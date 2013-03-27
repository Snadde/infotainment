var mqtt = require('./node_modules/mqttjs');

mqtt.createServer(function (client) {
	var self = this;
	
	if(!self.clients) {
		self.clients = {};
	}
	client.on('connect', function (packet) {
		client.connack({returnCode: 0});
		client.id = packet.client;
		self.clients[client.id] = client;
		console.log('Connect client ' + client.id);
	});
	
	client.on('publish', function (packet) {
		for (var k in self.clients) {
			self.clients[k].publish({topic: packet.topic, payload: packet.payload});
		}
		console.log('Publish '+packet.topic + 'payload: ' + packet.payload + 'from client ' + client.id);
		//var jsondata = JSON.parse(packet.payload);
		//console.log(jsondata.message);
	});
	
	client.on('subscribe', function (packet) {
		var granted = [];
		for (var i=0; i < packet.subscriptions.length; i++) {
			granted.push(packet.subscriptions[i].qos);
		};
		
		client.suback({granted: granted});
		console.log('Subscribe client ' + client.id);
	});
	
	client.on('pingreq', function(packet) {
		client.pingresp();
		console.log('Ping from client ' + client.id);
	});

	client.on('disconnect', function(packet) {
	    client.stream.end();
		console.log('disconnect client ' + client.id);
	});

	client.on('close', function(err) {
	    delete self.clients[client.id];
	});

	client.on('error', function(err) {
	    client.stream.end();
	    util.log('error!');
	});	
}).listen(1883);

