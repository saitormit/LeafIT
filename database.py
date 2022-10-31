import pymongo
from pymongo import MongoClient
#Setup MongoDB
client = MongoClient("mongodb+srv://saitormit:Ourplantech@cluster0.cwdv9fx.mongodb.net/?retryWrites=true&w=majority")
plantechDB = client["Plantech"]
#client = MongoClient('localhost', 27017)
#plantechDB = client.get_database("Plantech")
