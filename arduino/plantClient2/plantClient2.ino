#include <WiFiNINA.h>
#include <SPI.h>
#include <ArduinoJson.h>
#include <ArduinoHttpClient.h>

//WiFi Parameters
char ssid[] = "iPhone de Mitsuaki Saito";
char pass[] = "Parkpark";
int status = WL_IDLE_STATUS;
WiFiClient wifi;
//HTTP Server Parameters
char server[] = "b655-128-210-107-131.ngrok.io";
int port = 80;
HttpClient client = HttpClient(wifi, server, port);

//Plant Parameters
float thresholdOne;
float thresholdTwo;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  while (status != WL_CONNECTED) {
    Serial.print("Attempting to connect to Network named: ");
    Serial.println(ssid);
    status = WiFi.begin(ssid, pass);
    delay(8000);
  }

  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  Serial.println("Initializing connection to server...");  
  Serial.println("Checking User Plants");
  client.get("/pots-config");
  // read the status code and body of the response
  int statusCode = client.responseStatusCode();
  String response = client.responseBody();

  Serial.print("Status code: ");
  Serial.println(statusCode);
  Serial.print("Response: ");
  Serial.println(response);
  
  StaticJsonDocument<256> jsonResp;
  deserializeJson(jsonResp, response);  
  
  thresholdOne = jsonResp["pot1"];
  thresholdTwo = jsonResp["pot2"];
  if (thresholdOne > 0){
    Serial.println(thresholdOne);
  }
  if (thresholdTwo > 0){
    Serial.println(thresholdTwo);
  }
  
  delay(2000);
}

void loop() {
  // put your main code here, to run repeatedly:
  // if (thresholdOne > 0 && moisture < thresholdOne){
  //   waterPlant()
  // }
  // if (thresholdTwo > 0 && moisture < thresholdTwo){
  //   waterPlant()
  // }
  Serial.println("Making GET request to check overide watering");
  client.get("/activate-water?client=arduino");  
  int statusCode = client.responseStatusCode();
  String response = client.responseBody();  
  Serial.print("Status code: ");
  Serial.println(statusCode);  
  
  StaticJsonDocument<256> jsonResp;
  deserializeJson(jsonResp, response); 
  bool shouldWater1 = jsonResp["shouldWater1"];
  bool shouldWater2 = jsonResp["shouldWater2"];
  if(shouldWater1 == true){
    Serial.println("Override autowater at Pot1");
    delay(5000);        
  }
  if(shouldWater2 == true){
    Serial.println("Override autowater at Pot2");
    delay(5000);
  }
  if(shouldWater1 || shouldWater2){
    setOverrideWaterFalse();
  }
  delay(2000);
}

void setOverrideWaterFalse(){  
  Serial.println("Make override watering false");
  String contentType = "application/json";
  StaticJsonDocument<256> doc;

  // create a body object
  JsonObject object = doc.to<JsonObject>();
  object["_id"] = 0;
  object["shouldWater1"] = false;
  object["shouldWater2"] = false;
  String requestBody;
  serializeJson(object, requestBody);
  // http put method
  client.put("/activate-water?client=arduino", contentType, requestBody);

  // read the status code and body of the response
  int statusCode = client.responseStatusCode();
  String response = client.responseBody();

  Serial.print("Status code: ");
  Serial.println(statusCode);
  Serial.print("Response: ");
  Serial.println(response);
}