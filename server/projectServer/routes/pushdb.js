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
router.post('/phonecontacts/:id', function(req, res, next) {
    console.log("route in");

    const deviceID = req.params.id;
    console.log(deviceID);

    // Use connect method to connect to the server
    MongoClient.connect(url, function(err, client) {
        assert.equal(null, err);
        console.log("Connected successfully to server");
    
        const db = client.db(dbNameContacts);
        //global.db = db;

        var data = req.body;
        var host = req.host;
        //global.data = data; 

        console.log(data);

        findContactsByID(db, data, deviceID, function(docsLength) {
            insertOrUpdateContacts(db, data, docsLength, deviceID, function(){client.close();});
        });
        console.log("insert finally success");
    });
    
    //res.sendStatus(); 

    res.end();
});

router.post('/walking', function(req, res, next) {
    
    // Use connect method to connect to the server
    MongoClient.connect(url, function(err, client) {
        assert.equal(null, err);
        console.log("Connected successfully to server");
    
        const db = client.db(dbNameWalking);

        var data = req.body;
        const deviceID = req.query.id;

        console.log(data);
        console.log(deviceID);

        insertWalkingDatas(db, data, deviceID, function(){client.close();});

        console.log("insert finally success");

        res.send(data);
    });

})

const findContactsByID = function(db, data, deviceID, callback){
    const collection = db.collection(deviceID);

    collection.find({"contactID": data.id}).toArray(function(err, docs) {
        assert.equal(err, null);
        console.log("Found the following contacts by id");
        var docsLength = docs.length;
        callback(docsLength);
    })
}

const insertOrUpdateContacts = function(db, data, docsLength, deviceID, callback){
    const collection = db.collection(deviceID);

    if(docsLength == 0)
    {
        collection.insertMany([
            {"contactID": data.id, "contacts": data}
        ], function(err, result) {
            assert.equal(err, null);
            assert.equal(1, result.result.n);
            assert.equal(1, result.ops.length);
            console.log("Inserted phone's contacts into the contacts DB's collection deviceID");
            callback(result);
        });
    }
    else
    {
        collection.updateOne({ "contactID": data.id }
            , { $set: {"contactID": data.id, "contacts": data} }, function(err, result) {
            assert.equal(err, null);
            assert.equal(1, result.result.n);
            console.log("Updated the document with the field a equal to contact id");
            callback(result);
          });
    }
}

const insertWalkingDatas = function(db, data, devideID, callback) {
    const collection = db.collection(devideID);
    
    collection.insertMany([
        {"keyYear": data.date.year, "keyMonth": data.date.month, "keyDay": data.date.day, "walkingData": data}
    ], function(err, result) {
        assert.equal(err, null);
        assert.equal(1, result.result.n);
        assert.equal(1, result.ops.length);
        console.log("Inserted walking data into the walking DB's collection deviceID");
        console.log(data.date.year);
        console.log({"keyYear": data.date.year, "keyMonth": data.date.month, "keyDay": data.date.day, "walkingData": data});
        callback(result);
    });
}

module.exports = router;