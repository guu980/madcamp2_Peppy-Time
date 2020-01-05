var express = require('express');
var router = express.Router();

// Connection URL
const url = 'mongodb://localhost:27017';
// Database Name
const dbName = 'contacts';
const MongoClient = require('mongodb').MongoClient;
var bodyParser = require('body-parser');

const assert = require('assert');

/* GET home page. */
router.get('/all_data/:id', function(req, res, next) {

    // Use connect method to connect to the server
    MongoClient.connect(url, function(err, client) {
        assert.equal(null, err);
        console.log("Connected successfully to server");
    
        const db = client.db(dbName);
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

module.exports = router;