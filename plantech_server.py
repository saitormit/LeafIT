from flask import Flask, render_template
from flask import request
from flask import jsonify
from flask_ngrok import run_with_ngrok
import database as db
import firebase as fcm
import datetime

app = Flask(__name__)
run_with_ngrok(app)

@app.route("/")
def hello():
    intro = {"Message": "Welcome to the Plantech server"}
    return jsonify(intro)
    #return render_template("home.html")

@app.route("/pots-config", methods=["GET","POST","DELETE"])
def configure_pots():
    #MongoDB Collections
    userCollection = db.plantechDB["UserPlants"]
    infoCollection = db.plantechDB["PlantsInfo"]
    readingCollection = db.plantechDB["MoistureReadings"]

    if request.method == "GET":
        moistConfig = {}

        for id in range(1, db.potsNum+1):
            if userCollection.count_documents({"_id": id}):
                plant = userCollection.find_one({"_id": id}).get("Plant")
                moisture = infoCollection.find_one({"Plant": plant}).get("Moisture")
                moistConfig["pot"+str(id)] = moisture
            else:
                moistConfig["pot"+str(id)] = 0

        return jsonify(moistConfig)
    
    elif request.method == "POST":
        #Input received from Android
        userInput = request.get_json()
        # "userInput["Pot Number"] is an integer
        pot = userInput["Pot Number"]
        plant = userInput["Plant"]
        stage = userInput["Stage"]

        if userCollection.count_documents({"_id": pot}) == 0:
            userCollection.insert_one({"_id": pot, "Plant": plant, "Stage": stage})
            return {"Message": "Added"}
        else:
            return {"Message": "Unavailable"}
    
    elif request.method == "DELETE":
        #Input from the user with the ID to be deleted
        empty_pot = int(request.args.get("empty"))

        if userCollection.count_documents({"_id": empty_pot}) > 0:
            userCollection.delete_one({"_id": empty_pot})
            readingCollection.delete_many({"pot": empty_pot})
            #return {"Message": "Pot " + str(empty_pot) + " is empty now"}
            return {"Message": "Now empty"}
        else:
            #return {"Message": "Pot " + str(empty_pot) + " is already empty"}
            return {"Message": "Already empty"}

@app.route("/moisture-data", methods=["GET","POST"])
def manage_moisture():
    readingCollection = db.plantechDB["MoistureReadings"]

    if request.method == "GET":
        pot = int(request.args.get("pot"))

        plotData = {
            "time": [],
            "moisture":[]
        }

        if readingCollection.count_documents({"pot": pot}) > 0:
            cursor = readingCollection.find({"pot": pot}).sort("timestamp",-1).limit(10)
            for doc in cursor:
                #Append timestamp into time array within plotData
                timestamp = doc.get("timestamp")
                timestampFmt = datetime.datetime.strftime(timestamp, "%Y/%m/%d %H:%M:%S")
                plotData.get("time").insert(0, timestampFmt)
                #Append moisture data into moisture array within plotData
                plotData.get("moisture").insert(0, doc.get("moisture"))
                 
        return plotData
        #Sort timestamps and group by pot -> get timestamp and moisture
        #Create time and moisture array with the timestamps and moisture and add into a new json file
    
    elif request.method == "POST":
        reading = request.get_json()
        reading["timestamp"] = datetime.datetime.utcnow()
        readingCollection.insert_one(reading)
        #Post Sensor readings in MongoDB database
        return jsonify({"Message": "Moisture data received"})

@app.route("/activate-water", methods=["GET","PUT"])
def manage_water():
    #waterCollection contains {"_id": 0, shouldWater1: false, shouldWater2: false} by default
    waterCollection = db.plantechDB["WaterCommand"]

    if request.method == "GET":
        #Consider not hardcoding pots to make it scalable for more pots
        document = waterCollection.find_one({"_id" : 0})
        return jsonify({"shouldWater1": document.get("shouldWater1"), "shouldWater2": document.get("shouldWater2")})

    elif request.method == "PUT":
        #For PUT method, if the client is Arduino,
        #Firebase Cloud Messaging should be called
        clientId = request.args.get("client")
        toWater = request.get_json()

        waterCollection.replace_one({"_id": 0}, toWater)
        if clientId == "arduino":
            try:
                fcm.pushMessage({"Message": "Successful override watering"})
            except:
                print("Not connected to Firebase")

            return jsonify({"Message": "Override watering set to false"})

        return jsonify(toWater)
            
if __name__ == "__main__":
    app.run()
