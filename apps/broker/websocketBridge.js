(function() {
    var debug = require('./debug.js');
    var socket = require('./node_modules/websocket.io');
    var server = socket.listen(3000);
    /*
    ** Connects a websocket client to a mqtt client for every new connection
    **/
    server.on('connection', function (socketclient) {
	
	    var privatetopic;
	    // Initiates the mqtt client that will be connected to the websocket client
        var mqttclient = require('./mqttclient.js').init(socketclient); 
	    
        /*
        **  Bridge all communication from the websocket to the MQTT client
        **  here are the different 'actions'
        **  undefined: 
        **          If the action value does not exist in the message it will 
        **          publish the message on the private topic
        **  init:   
        **          initiates the communication by subscribing on the private 
        **          channel and publish a init-message to the '/system' channel
        **          the application should then wait for the initresponse that
        **          the 'dashboard' will reply with
        **  install:
        **          Initiates the installation of the application (if it is not installed) 
        **          by downloading a zip file from the specified URL in the install message
        **          and store it in a buffer which will be published on the '/system' channel
        **          
        **  Subscribe:
        **          Subscribes on the topic that is specified in the message
        **  Publish:
        **          Publish a message to the topic both specified in the message
        **  Start, Stop, Uninstall: (The 'else' case)
        **          All theese actions is published to the '/system' topic for the
        **          Dashboard to interpret
        **/
        socketclient.on('message', function (packet) { 
	        var message = JSON.parse(packet);
	        var action = message.action;
	        var mqttmessage;
		
	        if(action == undefined){
	            mqttmessage = createJSON(privatetopic, packet);
	            mqttclient.publish(mqttmessage);
	        }
	        else if(action == 'publish'){
	            mqttmessage = createJSON(message.topic, message.data);
                debug.log('publish from websocket'+mqttmessage+" ej json : "+message);
	            mqttclient.publish(mqttmessage);
	        }
	        else if(action == 'subscribe'){
	            mqttclient.subscribe({topic: message.topic});
	        }
	        else if(action == 'init'){
	            privatetopic = message.data;
	            mqttclient.subscribe({topic: privatetopic});
	            //mqttmessage = createJSON('/system', packet);
	            //mqttclient.publish(mqttmessage);
                var initmessage = {
                    payload : '{"action":"init","type":"response","data":"success"}'
                };
                
                socketclient.send(JSON.stringify(initmessage));
	        }
	        else if(action == 'install'){
	            var buffer;
                var location = message.url;
	            var http = require('http');	
	            var request = http.request(location, function (res) {
                    var data = '';
                    res.setEncoding('binary');
		            res.on('data', function (chunk) {
		                data += chunk;
					    
		            });
		    	    res.on('end', function () {
                        buffer = new Buffer(data, 'binary').toString('base64');
		        	    debug.log(buffer);
					    var json_data = {
						    action : 'install',
						    data : buffer
					    };
					    var json_payload = JSON.stringify(json_data);
					    mqttmessage = createJSON('/system', json_payload);
					    mqttclient.publish(mqttmessage);
		    	    });
			    });
			    request.on('error', function (e) {
		            console.log("ERRORR  "+e.message);
			    });
			    request.end();
		    }
		    else{
			    mqttmessage = createJSON('/system', packet);
			    mqttclient.publish(mqttmessage);
		    }
		    debug.log("message as mqtt: "+mqttmessage); 
		    debug.log("message from websocket: "+packet); 
	    });
        /*
        ** Closes the websocket connection
        **/
        socketclient.on('close', function () {
	        debug.log('Closing socket') 
	    });
    });
    /*
    ** Help function that creates a MQTT message with topic and payload as a JSON object
    **/
    function createJSON(mqtt_topic, mqtt_payload){
	    var mqttmessage = {
		    topic : mqtt_topic,
		    payload : mqtt_payload
	    };
	    return mqttmessage;
    };
}());