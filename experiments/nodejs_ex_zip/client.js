var mqtt = require('./node_modules/mqttjs');

var argv = process.argv;

var zip = new require('./node_modules/node-zip')();

zip.file('test.file', 'hello world');

var fs = require("fs");


var port = 1883,
  host = 'localhost',
  topic = 'zip',
  payload = zip.generate({base64:false,compression:'DEFLATE'});
  
  console.log(payload);
  
fs.writeFileSync('test.zip', payload);  
  
mqtt.createClient(1883, 'localhost', function(err, client) {
	if (err) process.exit(1);
    client.connect({keepalive: 3000});

    client.on('connack', function(packet) {
      if (packet.returnCode === 0) {	
		client.publish({topic: topic, payload: payload});
        client.disconnect();
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
});  