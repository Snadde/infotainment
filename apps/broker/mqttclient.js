(function() {
    var mqtt = require('./node_modules/mqttjs');
    var debug = require('./debug.js');
	/*
    ** Exports the init function that connects a specified websocket-client to a unique mqtt-client
    **/	
    module.exports.init = function(socketclient){
        var mqttclient = mqtt.createClient(1883, 'localhost', function(err, client) {	
			/*
            ** Connects the client to the broker with a unique client-id
            **/	
    		client.connect({ "version": "MQIsdp",
    		  "versionNum": 3,
    		  "keepalive": 60,
    		  "client": "mqtt_1"+Math.random(),
    		});
			/*
            ** Closes the connection to the broker
            **/		
    	    client.on('close', function() {
    	      process.exit(0);
    	    });
			/*
            ** If an error has occured log the error and stop the process
            **/		
    	    client.on('error', function(e) {
    	      debug.log('error %s', e);
    	      process.exit(-1);
    	    });
			/*
            ** When the broker has forward a packet that the client is subscribing
            ** on, create a JSON-object and forward it to the websocket-client
            **/	
    		client.on('publish', function(packet) {
    			var message = {
    				topic : packet.topic,
    				payload : packet.payload
    			};
    			var json = JSON.stringify(message);
    			debug.log("Sending to websocket : "+json);
    	  	  	socketclient.send(json);
    		});
		
    	});
        return mqttclient;
    };
}());