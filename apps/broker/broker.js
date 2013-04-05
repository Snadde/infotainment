var mqtt = require('./node_modules/mqttjs');
var socket = require('./node_modules/websocket.io')
var server = socket.listen(3000);
//var server = require('./node_modules/socket.io').listen(80);
var debug = true;
function log(message){
	if(debug){	
		console.log(message);
	}
}

server.on('connection', function (socketclient) {
	
	var mqttclient = mqtt.createClient(1883, 'localhost', function(err, client2) {	
		var privatetopic;
		
		client2.connect({ "version": "MQIsdp",
		  "versionNum": 3,
		  "keepalive": 60,
		  "client": "mqtt_1"+Math.random(),
		});
		//client2.subscribe({'topic': '/min/privata/kanal'});
		
	    client2.on('close', function() {
	      process.exit(0);
	    });

	    client2.on('error', function(e) {
	      log('error %s', e);
	      process.exit(-1);
	    });
	
		client2.on('publish', function(packet) {
			log('sending to websocekt!!   %s\t%s', packet.topic, packet.payload);
	  	  	socketclient.send('{"topic":"'+packet.topic+'","payload":'+packet.payload+"}");
		});
		
	});
	
	socketclient.on('message', function (packet) { 
		var message = JSON.parse(packet);
		action = message.action;
		
		if(action == undefined){
			mqttclient.publish({topic: privatetopic, payload: packet});
		}
		else if(action == 'publish'){
			mqttclient.publish({topic: message.topic, payload: message.data});
		}
		else if(action == 'subscribe'){
			mqttclient.subscribe({topic: message.topic});
		}
		else if(action == 'init'){
			privatetopic = message.data;
			mqttclient.subscribe({topic: privatetopic});
			mqttclient.publish({topic: '/system', payload: packet});
		}
		else if(action == 'install'){
			var location = message.url;
			var buffer;
			var http = require('http');	
			var request = http.request(location, function (res) {
		   	 	var data = '';
		    	res.on('data', function (chunk) {
		        	data += chunk;
					buffer = new Buffer(data).toString('base64');
		    	});
		    	res.on('end', function () {
		        	log(buffer);
					mqttclient.publish({topic: '/system', payload: '{"action":"install","data":"'+buffer+'"}'})
		    	});
			});
			request.on('error', function (e) {
		    	console.log("ERRORR  "+e.message);
			});
			request.end();
		}
		else{
			mqttclient.publish({topic: '/system', payload: packet});
		}
		
		log("message from websocket: "+packet) 
	});
    socketclient.on('close', function () {
		log('Closing socket') 
	});
});

mqtt.createServer(function (client) {
	var self = this;
	
	if(!self.clients) {
		self.clients = {};
	}

	client.on('connect', function (packet) {
	    self.clients[packet.client] = client;
	    client.id = packet.client;
	    log("CONNECT: client id: " + client.id);
	    client.subscriptions = [];
	    client.connack({returnCode: 0});
	});
	
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
			log("publish to client: "+c.id+" with topic: "+ packet.topic +" payload: "+packet.payload);
	      }
	    }
	  });
	  
	  client.on('subscribe', function(packet) {
	    var granted = [];

	    for (var i = 0; i < packet.subscriptions.length; i++) {
	      var qos = packet.subscriptions[i].qos
	        , topic = packet.subscriptions[i].topic
	        , reg = new RegExp(topic.replace('+', '[^\/]+').replace('#', '.+$'));

	      granted.push(qos);
	      client.subscriptions.push(reg);
		  log("client: "+client.id+" subscribes to topic: "+ topic); 
	    }

	    client.suback({messageId: packet.messageId, granted: granted});
	  });
	
	client.on('pingreq', function(packet) {
		client.pingresp();
		log('Ping from client ' + client.id);
	});

	client.on('disconnect', function(packet) {
	    client.stream.end();
		log('disconnect client ' + client.id);
	});

	client.on('close', function(err) {
	    delete self.clients[client.id];
		log("close " + client.id);
	});

	client.on('error', function(err) {
	    client.stream.end();
	    util.log('error!');
	});	
}).listen(1883);

