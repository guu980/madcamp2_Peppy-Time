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
router.post('/one/:id', function(req, res, next) {
    console.log("route in");

    const deviceID = req.params.id;
    console.log(deviceID);

    // Use connect method to connect to the server
    MongoClient.connect(url, function(err, client) {
        assert.equal(null, err);
        console.log("Connected successfully to server");
    
        const db = client.db(dbName);

        var data = req.body;

        var realData = data.id;

        console.log(data.id);

        removeContact(db, realData, deviceID, function() {client.close();});

        console.log("Delete finally success");
    });
    
    //res.sendStatus(); 

    res.end();
});

const removeContact = function(db, data, deviceID, callback) {
    // Get the documents collection
    const collection = db.collection(deviceID);
    // Delete document where a is 3
    collection.deleteOne({"contactID": String(data) }, function(err, result) {
      assert.equal(err, null);
      assert.equal(1, result.result.n);
      console.log("Removed the document with the field a equal to input contact id");
      callback(result);
    });
  }

module.exports = router;