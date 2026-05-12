const functions = require("firebase-functions");
const admin = require("firebase-admin");
const axios = require("axios");

admin.initializeApp();

exports.procesarPagoAlToque = functions.https.onCall(async (data, context) => {
    try {
        // 1. ABRIMOS EL TEXTO PLANO
        let payload = data;
        if (typeof data === 'string') {
            payload = JSON.parse(data);
        }

        let userId = null;
        if (context.auth && context.auth.uid) {
            userId = context.auth.uid;
        } else if (payload && payload.uid) {
            userId = payload.uid;
        }

        if (!userId) {
            return JSON.stringify({ exito: false, mensaje: "Falta UID" });
        }

        const montoPagar = payload.monto;
        const descripcion = payload.descripcion;

        const userDoc = await admin.firestore().collection('usuarios').doc(userId).get();
        if (!userDoc.exists) {
            return JSON.stringify({ exito: false, mensaje: "Usuario no existe" });
        }

        const cardToken = userDoc.data().card_token;
        if (!cardToken) {
            return JSON.stringify({ exito: false, mensaje: "No hay tarjeta vinculada" });
        }

        const userEmail = userDoc.data().email || "alumno@ucol.mx";
        const ACCESS_TOKEN = "TEST-5274885548194765-041905-2ab93399cf879f1f6a2de1078fe249fd-2922185240";

        const response = await axios.post('https://api.mercadopago.com/v1/payments', {
            transaction_amount: montoPagar,
            token: cardToken,
            description: descripcion,
            installments: 1,
            payment_method_id: "visa",
            payer: { email: userEmail }
        }, {
            headers: {
                'Authorization': `Bearer ${ACCESS_TOKEN}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.data.status === "approved") {
            return JSON.stringify({ exito: true, mensaje: "Pago aprobado en El Naranjo", id_pago: response.data.id });
        } else {
            return JSON.stringify({ exito: false, mensaje: `Rechazado: ${response.data.status}` });
        }

    } catch (error) {
        let mensajeError = error.message;
        if (error.response && error.response.data) {
            mensajeError = error.response.data.message || error.response.data.error;
        }
        return JSON.stringify({ exito: false, mensaje: `Error MP: ${mensajeError}` });
    }
});