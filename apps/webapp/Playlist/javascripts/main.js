/**
* This is the main javascript file for the web application. It communicates
* with the JavaScriptInterface "WebApp" in the webview through its defined methods.
*
* It also makes use of jquery and some plugins to animate the playlist,
* scrubber and update the playing information.
*
*/

// Define some constants
var COMMAND_ADD = "add";
var COMMAND_ADD_ALL = "add_all";
var COMMAND_NEXT = "next";
var COMMAND_PREV = "prev";
var COMMAND_PLAY = "play";
var COMMAND_PAUSE = "pause";
var COMMAND_SEEK = "seek";
var PRIVATE_CHANNEL = "/playlist";
var SENSOR_CHANNEL = "/sensor/infotainment"

// Define som global variables
var debug = true;
var playing = false;
var playlist = [];
var currentTrack = 0;
var currentTime = 0;
var totalTime = 0;
var meter;


$(document).ready(function () {
    
    // Get the div which will act as a parent to the scrubber
    // and initiate it.
    meter = $('#meter');
    meter.scrubber({
        callback : function (fraction){
                        currentTime = fraction * totalTime;
                        var message = {
                            action : 'seek',
                            data : fraction
                        };
                        publish(PRIVATE_CHANNEL, JSON.stringify(message));               
                    },
                    type : 'android'       
    });
    
    // Set a timer on the scrubber to update each second
    meter.timer({
        callback: function() { 
            setMeter(++currentTime / totalTime);
        },
        delay: 1000,
        repeat: true,
        autostart: false
    });

    // Listens for clicks on the music controls and asks the JavaScriptInterface
    // to publish an action message on the private channel.
    $('.music-control').click(function () {
        var message = {
            action : this.id
        }
        publish(PRIVATE_CHANNEL, JSON.stringify(message));
    });
    
    // Subscribe to the application and sensor topics
    subscribe(PRIVATE_CHANNEL);
    subscribe(SENSOR_CHANNEL);
});

// Sets the meter to the specified fraction,
// the fraction is multipled by 100 to give
// percent value before setting the progress.
function setMeter(fraction) {
    var percent = fraction * 100;
    if(percent <= 100) {
        meter.setProgress(percent);
    }
}

// Subscribes to the given topic by calling
// the JavaScriptInterface
function subscribe(topic) {
    WebApp.subscribe(topic);
}

// Publish a message to the specified topic through
// the JavaScriptInterface
function publish(topic, message) {
    log("publish: " + topic + " message " + message);    
    WebApp.publish(topic, message);
}

// Called through the JavaScriptInterface when a non-system
// message is received. Handles the topics the web app is interested in.
function onMessage(topic, payload) {
    log("onMessage: " + topic + " payload " + JSON.stringify(payload));
    if (topic == PRIVATE_CHANNEL || topic == SENSOR_CHANNEL) {
        handleMessagePayload(payload);
    }
}

// Parses the payload and controls which action to take depending
// on the incoming message content.
function handleMessagePayload(payload) {
    log("handleMessagePayload: " + payload);
    if (payload.action == COMMAND_ADD) {
        updateList(payload);
        playlist.push(payload);
    } else if(payload.action == COMMAND_ADD_ALL) {
        playlist.length = 0;
        playlist = payload.data;
        for(var i = 0; i < playlist.length; i++) {
            updateList(playlist[i]);
        }
    } else if (payload.action == COMMAND_NEXT) {
        currentTrack = ++currentTrack % playlist.length;
        var elem = $('#options-list li:first');
        while (elem.is(':animated')) {
            elem = elem.next('li');
        }
        elem.slideUp(function () {
            $(this).parent('ul').append($(this)).find('li:last').fadeIn();
            updatePlayingInfo();
        });
        resetPlayingInfo();
    } else if(payload.action == COMMAND_PREV) {
        currentTrack = --currentTrack;
        if(currentTrack < 0){
            currentTrack = playlist.length - 1;
        }
        var elem = $('#options-list li:last');
        while(elem.is(':animated')) {
            elem = elem.prev('li');
        }
        elem.fadeOut(function() {
            $(this).parent('ul').prepend($(this)).find('li:first').slideDown();
            updatePlayingInfo();
        });
        resetPlayingInfo();
    } else if (payload.action == COMMAND_PLAY) {
        playing = true;
        updatePlayingInfo();
        toggleButtons();
        totalTime = playlist[currentTrack].tracklength;
        meter.timer('start');
    } else if (payload.action == COMMAND_PAUSE) {
        playing = false;
        toggleButtons();
        meter.timer('stop');
    } else if (payload.action == COMMAND_SEEK) {
        var fraction = payload.data;
        currentTime = fraction * totalTime;
        setMeter(fraction);
    }
    
}

// Resets the player information
function resetPlayingInfo() {
    currentTime = 0;
    totalTime = playlist[currentTrack].tracklength;
    setMeter(0);
}

// Updates the playing information
function updatePlayingInfo() {    
    $('#current-song-section h2').html(
        $('#options-list li:first span.artist').text() + " &ndash; " +
        $('#options-list li:first span.track').text()
    );
}

// Toggles the play/pause buttons
function toggleButtons() {
    if (playing) {
        $('#play').hide();
        $('#pause').show();
    } else {
        $('#play').show();
        $('#pause').hide();
    }
}

// Updates the playlist with the new track in the payload
function updateList(payload) {
    $('#options-list').append(
        $('<li/>', {
        html: '<span class="artist">' + payload.artist + '</span>' + "<br/>" + '<span class="track">' + payload.track + '</span>' + '<span class="tracklength"> &ndash; <span>' + payload.tracklength + '</span> s.</span>'
    }).hide().fadeIn());
}

// Logs to the console if debug mode is on
function log(message) {
    if (debug) {
        console.log(message);
    }
}