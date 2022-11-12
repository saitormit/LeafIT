import firebase_admin
from firebase_admin import credentials, messaging
from pathlib import Path
cred = credentials.Certificate("adminKeys/firebase_serviceAccountKey.json")
firebase_admin.initialize_app(cred)

registation_token = "djVNXfHFTH-K93sxiBWj0p:APA91bHDXjWTEN8yUyjSPSw5vi0kv1CGuGKMOE2JYQD1GevEZFa5yf_V1qE-5lFuKuovXXsbOb485OKBTCN3FxDCwTJY33VFv8KI3KJuN_Z95h_1vKWk9-5sSCWMz8u8e5VfZ6FrwMZp"
def pushMessage(msg, recipient=registation_token):
    message = messaging.Message(
        data = msg,
        token = recipient
    )
    messaging.send(message)