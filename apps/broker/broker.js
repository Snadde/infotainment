var debug = require('./debug.js');
var mqtt = require('./node_modules/mqttjs');
//Initiates the websocket bridge
var bridge = require("./websocketBridge");
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
                c.publish({topic: packet.topic, payload: packet.payload});
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
            , reg = new RegExp(topic.replace('+', '[^\/]+').replace('#', '.+$'));
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
        util.log('error!');
    });	
}).listen(1883);
