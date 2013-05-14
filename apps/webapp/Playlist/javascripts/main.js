$(document).ready(function() {
    
    
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

 var COMMAND_ADD = "add"
 var COMMAND_NEXT = "next";
 var COMMAND_PLAY = "play";
 var COMMAND_PAUSE = "pause";
 var PRIVATE_CHANNEL = "/playlist";
 var SENSOR_CHANNEL = "/sensor/infotainment"
 var debug = true;
 var playing = false;
 
 /*
  *  Dev tests
  */
 
 function testAddMessage() {
     var testMessage = {
         action : 'add',
         artist : 'Nirvana',
         track : 'About a girl'
     }
     onMessage("/playlist", testMessage);
 }
 
 function testNextMessage() {
     var testMessage = {
         action : 'next'
     }
     onMessage("/playlist", testMessage);
 }
 
 function testPlayMessage() {
     var testMessage = {
         action : 'play'
     }
     onMessage("/playlist", testMessage);
 }
 
 function testPauseMessage() {
     var testMessage = {
         action : 'pause'
     }
     onMessage("/playlist", testMessage);
 }
 
 
 /*
  *  Implementation
  */
 
subscribe(PRIVATE_CHANNEL);
subscribe(SENSOR_CHANNEL);
 
 function subscribe(topic) {
     WebApp.subscribe(topic);        
 }
 
/* function publish(topic, message) {
     WebApp.publish(topic, message);
 }*/

function onMessage(topic, payload) {
	log("onMessage: " + topic + " payload " + JSON.stringify(payload));
     if(topic == PRIVATE_CHANNEL || topic == SENSOR_CHANNEL) {
         handleMessagePayload(payload);
     }
}
 
 function handleMessagePayload(payload) {
	log("handleMessagePayload: " + payload);
     if(payload.action == COMMAND_ADD) {
         updateList(payload);
     } else if (payload.action == COMMAND_NEXT) {
         var elem = $('#options-list li:first');
         while(elem.is(':animated')) {
             elem = elem.next('li');
         }
         elem.slideUp(function() {
             $(this).parent('ul').append($(this)).find('li:last').fadeIn();
             updatePlayingInfo();
         });
     } else if(payload.action == COMMAND_PLAY) {
         playing = true;
         updatePlayingInfo();
         toggleButtons();
     } else if(payload.action == COMMAND_PAUSE) {
         playing = false;
         toggleButtons();
     }
 }
 
 function updatePlayingInfo() {
     $('#current-song-section h2').html(
         $('#options-list li:first span.artist').text() + " &ndash; " +
         $('#options-list li:first span.track').text()
     );
 }
 
 function toggleButtons() {
     if(playing) {
         $('#play').hide();
         $('#pause').show();
     } else {
         $('#play').show();
         $('#pause').hide();
     }
 }
 
 function updateList(payload) {
     $('#options-list').append(
         $('<li/>', {
             html: '<span class="artist">' + payload.artist + '</span>' + "<br/>" + 
                     '<span class="track">' + payload.track + '</span>'
         }).hide().fadeIn()
     );
 }

function log(message) {
	if (debug) {
		console.log(message);
	}
}