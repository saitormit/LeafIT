from flask import Flask, render_template
from flask import request
from flask import jsonify
from flask_ngrok import run_with_ngrok

app = Flask(__name__)
run_with_ngrok(app)

@app.route("/")
def hello():
    intro = {"Test": "Hello world"}
    return jsonify(intro)
    #return render_template("home.html")

@app.route("/pots-config", methods=["GET","POST"])
def json_pots():
    garden = {
                "1": {"Plant": None, "Stage": None}, 
                "2": {"Plant": None, "Stage": None}
                }

    if request.method == "POST":
        garden = request.get_json()
        if garden:
            return garden

    return jsonify(garden)

if __name__ == "__main__":
    app.run()
