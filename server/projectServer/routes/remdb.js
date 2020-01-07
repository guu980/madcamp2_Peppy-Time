var express = require('express');
var router = express.Router();

// Connection URL
const url = 'mongodb://localhost:27017';
// Database Name
const dbNameContacts = 'contacts';
const dbNameWalking = "walking";
const MongoClient = require('mongodb').MongoClient;
const {ObjectId} = require('mongodb');
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
    
        const db = client.db(dbNameContacts);

        var data = req.body;

        var realData = data.id;

        console.log(data.id);

        removeContact(db, realData, deviceID, function() {client.close();});

        console.log("Delete finally success");
    });
    
    //res.sendStatus(); 

    res.end();
});

router.get('/record', function(req, res, next){
  // Use connect method to connect to the server
  MongoClient.connect(url, function(err, client) {
      assert.equal(null, err);
      console.log("Connected successfully to server");
  
      const db = client.db(dbNameWalking);

      const deviceID = req.query.id;
      const dbID = req.query.dbid;

      //console.log(data);
      console.log(deviceID);
      console.log(dbID);
      // findWalkingByDbid(db, deviceID, dbID, function(){
        
      // });     
      
      removeWalkingRecord(db, deviceID, dbID, function() {client.close(); res.end();});

      console.log("Remove finally success");
  });
})

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

const removeWalkingRecord = function(db, deviceID, dbID, callback){
  const collection = db.collection(deviceID);

  collection.deleteOne({ "_id": ObjectId(dbID) }, function(err, result) {
    assert.equal(err, null);
    assert.equal(1, result.result.n);
    console.log("Removed the document with the field a equal to input contact id");
    callback(result);
  });
}

// const findWalkingByDbid = function(db, deviceID, dbID, callback){
//   const collection = db.collection(deviceID);

//   collection.find({_id: ObjectId(dbID)}).toArray(function(err, docs) {
//       assert.equal(err, null);
//       console.log("Found the following walking data by dbid!!");
//       console.log(docs);
//       //console.log(docs.get(0).get("walkingData").get("start"));
//       callback(docs);
//   })
// }
module.exports = router;