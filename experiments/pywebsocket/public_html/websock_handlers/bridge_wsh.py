import mosquitto
from mod_pywebsocket import msgutil


_GOODBYE_MESSAGE = u'Goodbye'

# Create global variable for the request object
# (Holds details of ws connection)
g_request = ""

# Define callbacks for mosquitto
def on_connect(mosq, obj, rc):
    print "connected"
    
def on_disconnect(mosq, obj, rc):
    print("Disconnected successfully.")

def on_message(mosq, obj, msg):
    global g_request
    #send mqtt message to socket using the connection specified in g_request
    msgutil.send_message(g_request, "<strong>" + msg.topic + "</strong> - " + msg.payload + "")

def on_publish(mosq, obj, mid):
    print("Message " + str(mid) + " published.")
    
def on_subscribe(mosq, obj, mid, qos_list):
    print "subscribe"

# Define callback for pywebsocket
# allows for extra handshaking
def web_socket_do_extra_handshake(request):
    print "shakeyshake"
    pass

# main web socket function called on starting connection
def web_socket_transfer_data(request):
    global g_request
    g_request = request
    
    print "transfer data"
	
    #create mosquitto object
    mqttc = mosquitto.Mosquitto("python_sub")

	#assign callbacks
    mqttc.on_message = on_message
    mqttc.on_connect = on_connect
    mqttc.on_disconnect = on_disconnect
    mqttc.on_publish = on_publish
    mqttc.on_subscribe = on_subscribe
    
	#connect to mqtt broker on localhost
    mqttc.connect("127.0.0.1", 1883, 60)

	#subscribe to topic "test"
    mqttc.subscribe("test", 2)

    # Try to receive message
    try:
        receive_request(request, mqttc)
    except Exception, e:
        print "no luck trying!"
        raise e

        
def receive_request(request, mqttc):
    
    def handle(message):
        if message is None:
            return
        if isinstance(message, unicode):
            print "handling " + message
            mqttc.publish("test", message.encode('utf-8'))
            if message == _GOODBYE_MESSAGE:
                return
        else:
            # request.ws_stream.send_message(line, binary = True)
            # binary data, could be file transfer? need to check json first and prepare?
            pass
        
    receiver = msgutil.MessageReceiver(request, handle)
    
	#keep web socket connected while mqtt is connected
    while mqttc.loop() == 0:
        pass
