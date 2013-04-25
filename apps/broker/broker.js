var debug = require('./debug.js');
var mqtt = require('./node_modules/mqttjs');
//Initiates the websocket bridge

/*
**  Creates the MQTT server that will handle all communication such as 
**  publish and subscribing to different topics
**/
mqtt.createServer(function (client) {
    var self = this;
    if(!self.clients) {
	    self.clients = {};
    }
	/*
	**	Adds the client when it is connecting to the client list 
	**/
    client.on('connect', function (packet) {
        self.clients[packet.client] = client;
        client.id = packet.client;
        debug.log("CONNECT: client id: " + client.id);
        client.subscriptions = [];
        client.connack({returnCode: 0});
    });
	/*
	**	Publish a message to all clients that are subscribing to the specified topic
	**/
    client.on('publish', function(packet) {
        debug.log("publish to with topic: "+ packet.topic +" payload: "+packet.payload);
        for (var k in self.clients) {
            var c = self.clients[k]
            , publish = false;
            for (var i = 0; i < c.subscriptions.length; i++) {
                var s = c.subscriptions[i];
                if (s.test(packet.topic)) {
                    publish = true;
                }
            }
            if (publish) {
                var payload = JSON.parse(packet.payload);
                if(payload.action == 'install-url') {
                    debug.log('installing from url!')
    	            var buffer;
                    var location = payload.data;
    	            var http = require('http');	
    	            var request = http.request(location, function (res) {
                        var data = '';
                        res.setEncoding('binary');
    		            res.on('data', function (chunk) {
    		                data += chunk;
					    
    		            });
    		    	    res.on('end', function () {
                            buffer = new Buffer(data, 'binary').toString('base64');
    					    var json_data = {
    						    action : 'install',
    						    data : buffer
    					    };
    					    var json_payload = JSON.stringify(json_data);
                            c.publish({topic: packet.topic, payload: json_payload});
    		    	    });
    			    });
    			    request.on('error', function (e) {
    		            console.log("ERRORR  "+e.message);
    			    });
    			    request.end();
                } else {
                    c.publish({topic: packet.topic, payload: packet.payload});
                }
		        debug.log("publish to client: "+c.id+" with topic: "+ packet.topic +" payload: "+packet.payload);
            }
        }
    });
	/*
	**	Adds the topics specified in the packet to the clients subscriptions
	**/
    client.on('subscribe', function(packet) {
        var granted = [];
        for (var i = 0; i < packet.subscriptions.length; i++) {
            var qos = packet.subscriptions[i].qos
            , topic = packet.subscriptions[i].topic
            , reg = new RegExp(topic.replace('+', '[^\/]+').replace('#', '.+') + '$');
            granted.push(qos);
            client.subscriptions.push(reg);
	        debug.log("client: "+client.id+" subscribes to topic: "+ topic); 
        }
        client.suback({messageId: packet.messageId, granted: granted});
    });
	/*
	**  When there is a ping request, respond with a pingresponse
	**/
    client.on('pingreq', function(packet) {
	    client.pingresp();
	    debug.log('Ping from client ' + client.id);
    });
	/*
	**	Ends the stream to the client when a disconnect message is sent
	**/
	client.on('disconnect', function(packet) {
        client.stream.end();
	    debug.log('disconnect client ' + client.id);
    });
	/*
	**	Removes the client from the client list when the client 
	**  close the connection
	**/
    client.on('close', function(err) {
        delete self.clients[client.id];
	    debug.log("close " + client.id);
    });
	/*
	**	Ends the stream to the client when an error has occured and 
	**  log the error to the console
	**/
    client.on('error', function(err) {
        client.stream.end();
        debug.log('error! ' + err);
    });	
}).listen(1883);
