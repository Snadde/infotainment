$(document).ready(function() {
    var COMMAND_ADD = "add"
    var PRIVATE_CHANNEL = "/webapp/playlist";
    var debug = true;
    
    /*
     *  Dev tests
     */
    
    function testAddMessage() {
        var testMessage = {
            topic : PRIVATE_CHANNEL,
            payload : {
                action : 'add',
                data : {
                    artist : 'Nirvana',
                    track : 'About a girl'
                }
            }
        }
        onMessage(JSON.stringify(testMessage));
    }
    
    function testNextMessage() {
        var testMessage = {
            topic : PRIVATE_CHANNEL,
            payload : {
                action : 'next'
            }
        }
        onMessage(JSON.stringify(testMessage));
    }
    
    /*
     *  Implementation
     */
    
    // Uncommented until it can be run in the proper environment
    //subscribe(PRIVATE_CHANNEL);
    
    function subscribe(topic) {
        WebApp.publish(topic);        
    }
    
    function publish(topic, message) {
        WebApp.publish(topic, message);
    }

	function onMessage(message) {
		log("onMessage: " + message);
        var json_message = JSON.parse(message);
        if(json_message.topic == PRIVATE_CHANNEL) {
            handleMessagePayload(json_message.payload);
        }
	}
    
    function handleMessagePayload(payload) {
		log("handleMessagePayload: " + payload);
        if(payload.action == COMMAND_ADD) {
            updateList(payload.data);
        } else if (payload.action == 'next') {
            $('#options-list li:first').remove();
        }
    }
    
    function updateList(data) {
        $('#options-list').append(
            $('<li/>', {
                html: '<span class="artist">' + data.artist + '</span>' + "<br/>" + '<span class="track">' + data.track + '</span>'
            })
        );
    }

	function log(message) {
		if (debug) {
			console.log(message);
		}
	}
    
    $('#search-form').submit(function() {
        $.spotifyTrackSearch($('#search-input').val(), function(data) {
            var html = '';
            var result = $.filterTracksByCountryCode(data.tracks, "SE", 1);
            log(result);
            var data = {
                artist : result[0].artists[0].name ,
                track : result[0].name
            };
            updateList(data);
        });
        return false;
    });
});