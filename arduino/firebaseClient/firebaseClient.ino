#include <SPI.h>
#include <WiFiNINA.h>
#include <Config.h>
#include <Firebase.h>
#include <Firebase_Arduino_WiFiNINA.h>
#include <Firebase_TCP_Client.h>
#include <WCS.h>
#include <ArduinoHttpClient.h>
#include <ArduinoJson.h>

//Wifi Parameters
//char ssid[] = "iPhone de Mitsuaki Saito";
//char pass[] = "Parkpark";
int status = WL_IDLE_STATUS;

#define FIREBASE_HOST "plantech-leafit-default-rtdb.firebaseio.com"
#define FIREBASE_AUTH "Qxd0jKdAK0qm75T9iN0vdmDsVrSAQ3M7QCa6bRJM"
#define WIFI_SSID "iPhone de Mitsuaki Saito"
#define WIFI_PASSWORD "Parkpark"

//String path = "/test";
FirebaseData fbdo;

int counter = 0;
float moisture = 0.1;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  
  while (status != WL_CONNECTED) {
    Serial.print("Attempting to connect to Network named: ");
    Serial.println(WIFI_SSID);
    status = WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
    delay(5000);
  }  
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH, WIFI_SSID, WIFI_PASSWORD);
  Firebase.reconnectWiFi(true);
}

void loop() {
  // put your main code here, to run repeatedly:
  pushJsonFirebase(1, moisture, counter);
  pushJsonFirebase(2, moisture, counter);
  counter++;
  moisture = moisture + 0.01;
  //setIntFirebase(4);
  delay(5000);  
}

void postSensorData(int pot, float moistData){  
  // Serial.println("Post sensor readings");
  // String contentType = "application/json";
  // StaticJsonDocument<256> doc;

  // // create a body object
  // JsonObject object = doc.to<JsonObject>();
  // object["pot"] = pot;
  // object["moisture"] = moistData;
  // String requestBody;
  // serializeJson(object, requestBody);
  // // http put method
  // client.post("/moisture-data", contentType, requestBody);

  // // read the status code and body of the response
  // int statusCode = client.responseStatusCode();
  // String response = client.responseBody();

  // Serial.print("Status code: ");
  // Serial.println(statusCode);
  // Serial.print("Response: ");
  // Serial.println(response);
}

void setIntFirebase(int input){
  if (Firebase.setInt(fbdo, "/test", input)){

    //Success, then read the payload value return from server
    //This confirmed that your data was set to database as float number

    if (fbdo.dataType() == "int"){
      Serial.println(fbdo.intData());            
    }
    
  } else {
    //Failed, then print out the error detail
      Serial.println(fbdo.errorReason());
  }  
} 

void pushJsonFirebase(int pot, float moistData, int counter){
  //Build Json body
  StaticJsonDocument<256> doc;
  JsonObject object = doc.to<JsonObject>();
  object["id"] = counter;
  object["moisture"] = moistData;
  String requestBody;
  serializeJson(object, requestBody);
  String endpoint = "/pot" + String(pot);
  if (Firebase.pushJSON(fbdo, endpoint, requestBody)){
    Serial.print("OK: ");
    Serial.println(fbdo.dataPath());
  }else{
    //Failed, then print out the error detail
    Serial.println(fbdo.errorReason());    
  }
}

