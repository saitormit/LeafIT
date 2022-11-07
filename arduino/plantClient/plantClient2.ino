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
char server[] = "b126-128-210-107-130.ngrok.io";
int port = 80;
HttpClient client = HttpClient(wifi, server, port);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);

  while (status != WL_CONNECTED) {
    Serial.print("Attempting to connect to Network named: ");
    Serial.println(ssid);
    status = WiFi.begin(ssid, pass);
    delay(10000);
  }

  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());
  // IPAddress ip = WiFi.localIP();
  // IPAddress gateway = WiFi.gatewayIP();
  // Serial.print("IP Address: ");
  // Serial.println(ip);  

  Serial.println("Initializing connection to server...");  
  Serial.println("Making GET request");
  client.get("/pots-config");

  // read the status code and body of the response
  int statusCode = client.responseStatusCode();
  String response = client.responseBody();

  Serial.print("Status code: ");
  Serial.println(statusCode);
  Serial.print("Response: ");
  Serial.println(response);
  //Serial.println("Wait five seconds");
  //delay(5000);
}

void loop() {
  // put your main code here, to run repeatedly:

}
