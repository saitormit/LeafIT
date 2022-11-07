#include <ArduinoHttpClient.h>
#include <SPI.h>
#include <WiFiNINA.h>
#include <ArduinoJson.h>
//define sensorPin A5
char ssid[] = "iPhone de Mitsuaki Saito";
char pass[] = "Parkpark";

int status = WL_IDLE_STATUS;

char server[] = "https://9ec8-128-210-107-129.ngrok.io";

String postData;
String postVariable = "temp=";

WiFiClient client;
HTTPClient http;

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
  IPAddress ip = WiFi.localIP();
  IPAddress gateway = WiFi.gatewayIP();
  Serial.print("IP Address: ");
  Serial.println(ip);  

  Serial.println("Initializing connection to server...");
  
  if (client.connect(server, 80)) {
    Serial.println("Connected to the server");
    // Make a HTTP request:
    client.println("GET /pots-config HTTP/1.1");
    client.println("Host:  9ec8-128-210-107-129.ngrok.io");
    client.println("Content-Type: application/json");
    client.println("Connection: close");
    client.println();
    Serial.println(client);
  }

}

void loop() {
  // put your main code here, to run repeatedly:
    while (client.available()) {
    char c = client.read();
    Serial.write(c);
  }

  // if the server's disconnected, stop the client:
  if (!client.connected()) {
    Serial.println();
    Serial.println("disconnecting from server.");
    client.stop();

    // do nothing forevermore:
    while (true);
  }
}
