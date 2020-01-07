var express = require('express');
var router = express.Router();

// Connection URL
const url = 'mongodb://localhost:27017';
// Database Name
const dbNameContacts = 'contacts';
const dbNameWalking = "walking";
const MongoClient = require('mongodb').MongoClient;
var bodyParser = require('body-parser');

const assert = require('assert');

/* GET home page. */
router.get('/all_data/:id', function(req, res, next) {

    // Use connect method to connect to the server
    MongoClient.connect(url, function(err, client) {
        assert.equal(null, err);
        console.log("Connected successfully to server");
    
        const db = client.db(dbNameContacts);
        // var data = req.body;
        
        const deviceid = req.params.id; 

        findDocuments(db, deviceid, function(docs) {
            console.log(docs);
            res.send(docs);
            client.close();
         });
        console.log("find finally success");
    });

});

router.get('/walking', function(req, res, next){
    // Use connect method to connect to the server
    MongoClient.connect(url, function(err, client) {
        assert.equal(null, err);
        console.log("Connected successfully to server");
    
        const db = client.db(dbNameWalking);

        const deviceID = req.query.id;
        const queryYear = req.query.year;
        const queryMonth = req.query.month;
        const queryDay = req.query.day

        //console.log(data);
        console.log(deviceID);

        findWalkingByDate(db, queryYear, queryMonth, queryDay, deviceID, function(docs){
            res.send(docs); //docs ==JsonArray
            client.close();            
        });

        console.log("insert finally success");
    });
})

const findDocuments = function(db, deviceid, callback) {
    const temp = undefined;
    const collection = db.collection(deviceid);
    collection.find({}).toArray(function (err, docs) {
        assert.equal(err, null);
        console.log("Found the following records");
        //const realDocs = JSON.parse(docs);
        console.log(docs);
        callback(docs);
    });
 }

 const findWalkingByDate = function(db, year, month, day, deviceID, callback){
    const collection = db.collection(deviceID);

    collection.find({"keyYear": year, "keyMonth": month, "keyDay": day}).toArray(function(err, docs) {
        assert.equal(err, null);
        console.log("Found the following walking data by date");
        console.log(docs);
        //console.log(docs.get(0).get("walkingData").get("start"));
        callback(docs);
    })
}

module.exports = router;