from flask import Flask, render_template
from flask import request
from flask import jsonify
from flask_ngrok import run_with_ngrok
import database as db

app = Flask(__name__)
run_with_ngrok(app)

@app.route("/")
def hello():
    intro = {"Message": "Welcome to the Plantech server"}
    return jsonify(intro)
    #return render_template("home.html")

@app.route("/pots-config", methods=["GET","POST","DELETE"])
def configure_pots():
    #Number of pots:
    potsNum = 2
    #Database Parameters
    userCollection = db.plantechDB["UserPlants"]

    if request.method == "GET":
        moistConfig = {}
        infoCollection = db.plantechDB["PlantsInfo"]
        for id in range(1, potsNum+1):
            if userCollection.count_documents({"_id": id}):
                plant = userCollection.find_one({"_id": id}).get("Plant")
                moisture = infoCollection.find_one({"Plant": plant}).get("Moisture")
                moistConfig["pot"+str(id)] = moisture
            else:
                moistConfig["pot"+str(id)] = None

        return jsonify(moistConfig)
        #return jsonify({"Message": "GET request received"})
    
    elif request.method == "POST":
        #Input received from Android
        userInput = request.get_json()
        # "userInput["Pot Number"] is an integer
        pot = userInput["Pot Number"]
        plant = userInput["Plant"]
        stage = userInput["Stage"]

        if userCollection.count_documents({"_id": pot}) == 0:
            userCollection.insert_one({"_id": pot, "Plant": plant, "Stage": stage})
            return jsonify({"Message": plant + " added at pot " + str(pot) + " succesfully"})
        else:
            return jsonify({"Message": "Pot " + str(pot) + " is already being used"})
    
    elif request.method == "DELETE":
        #Input from the user with the ID to be deleted
        empty_pot = int(request.args.get("empty"))

        if userCollection.count_documents({"_id": empty_pot}) > 0:
            userCollection.delete_one({"_id": empty_pot})
            return jsonify({"Message": "Pot " + str(empty_pot) + " is now empty"})
        else:
            return jsonify({"Message": "Pot " + str(empty_pot) + " is already empty"})

@app.route("/moisture-data", methods=["GET","POST"])
def manage_moisture():
    userCollection = db.plantechDB["MoistureReadings"]
    return jsonify({"Message": "Moisture data"})
            
if __name__ == "__main__":
    app.run()
