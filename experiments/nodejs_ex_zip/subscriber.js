var mqtt = require('./node_modules/mqttjs');
var sys = require('sys');
var net = require('net');
var io  = require('socket.io').listen(5000);
var fs = require('fs');
  
 
mqtt.createClient(1883, 'localhost', function(err, client) {
	if (err) process.exit(1);
    client.connect({keepalive: 3000});

	client.on('connack', function(packet) {
      if (packet.returnCode === 0) {
        //client.subscribe({topic: 'hej'});
        //client.disconnect();
      } else {
        console.log('connack error %d', packet.returnCode);
        process.exit(-1);
      }
    });

    client.on('close', function() {
      process.exit(0);
    });

    client.on('error', function(e) {
      console.log('error %s', e);
      process.exit(-1);
    });
	
	client.on('publish', function(packet) {
		console.log('%s\t%s', packet.topic, packet.payload);
		var zip = new require('node-zip')(packet.payload, {base64: false, checkCRC32: true}); 
		console.log(zip.files['test.file']); // hello there
		fs.writeFile("./tst", zip.files['test.file'].data, function(err) {
		    if(err) {
		        console.log(err);
		    } else {
		        console.log("The file was saved!");
		    }
		}); 
		//var jsondata = JSON.parse(packet.payload);
		//console.log(jsondata.message);
  	  	//sys.puts(packet.topic+'='+packet.payload);
  	  	//io.sockets.emit('mqtt', packet.payload);
	});
	
	io.sockets.on('connection', function (socket) {
	  socket.on('subscribe', function (data) {
	    console.log('Subscribing to '+data.topic);
	    client.subscribe({'topic': 'hej'});
	  });
	});

	client.addListener('hej', function(topic, payload){
		console.log("received data");
	  sys.puts(topic+'='+payload);
	  io.sockets.emit('mqtt',payload);
	}); 
	
}); 



