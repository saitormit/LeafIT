from flask import Flask, render_template
from flask import request
from flask import jsonify
from flask_ngrok import run_with_ngrok
import database as db

app = Flask(__name__)
#run_with_ngrok(app)
#client = MongoClient("mongodb+srv://saitormit:Ourplantech@cluster0.cwdv9fx.mongodb.net/?retryWrites=true&w=majority")
#collection = db.plantechDB["UserPlants"]
#collection.insert_one({"_id": 2, "Plant": "Test Plant", "Stage": "Baby"})
#Setup MongoDB

@app.route("/")
def hello():
    intro = {"Message": "Welcome to the Plantech server"}
    return jsonify(intro)
    #return render_template("home.html")

@app.route("/pots-config", methods=["GET","POST","DELETE"])
def json_pots():
    #Database Parameters
    collection = db.plantechDB["UserPlants"]
    #Input received from Android
    userInput = request.get_json()

    if request.method == "POST":
        # "userInput["Pot Number"] is an integer
        pot = userInput["Pot Number"]
        plant = userInput["Plant"]
        stage = userInput["Stage"]

        if collection.count_documents({"_id": pot}) == 0:
            collection.insert_one({"_id": pot, "Plant": plant, "Stage": stage})
            return jsonify({"Message": plant + " added at pot " + str(pot) + " succesfully"})
        else:
            return jsonify({"Message": "Pot " + str(pot) + " is already being used"})

    elif request.method == "GET":

        return jsonify({"Message": "GET request received"})
    
    elif request.method == "DELETE":
        empty_pot = userInput["Pot Number"]
        if collection.count_documents({"_id": empty_pot}) > 0:
            collection.delete_one({"_id": empty_pot})
            return jsonify({"Message": "Pot " + str(empty_pot) + " is now empty"})
        else:
            return jsonify({"Message": "Pot " + str(empty_pot) + " is already empty"})
            
if __name__ == "__main__":
    app.run()
