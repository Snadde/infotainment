var COMMAND_ADD = "add";
var COMMAND_ADD_ALL = "add_all";
var COMMAND_NEXT = "next";
var COMMAND_PREV = "prev";
var COMMAND_PLAY = "play";
var COMMAND_PAUSE = "pause";
var COMMAND_SEEK = "seek";
var PRIVATE_CHANNEL = "/playlist";
var SENSOR_CHANNEL = "/sensor/infotainment"
var debug = true;
var playing = false;
var playlist = [];
var currentTrack = 0;
var currentTime = 0;
var totalTime = 0;
var meter;

$(document).ready(function () {
    meter = $('#meter span');
    $('#search-form').submit(function () {
        $.spotifyTrackSearch($('#search-input').val(), function (data) {
            var html = '';
            var result = $.filterTracksByCountryCode(data.tracks, "SE", 1);
            log(result);
            var data = {
                artist: result[0].artists[0].name,
                track: result[0].name
            };
            updateList(data);
        });
        return false;
    });

    $('.music-control').click(function () {
        var message = {
            action : this.id
        }
        publish(PRIVATE_CHANNEL, JSON.stringify(message));
    });
    
    meter.timer({
        callback: function() { 
            setMeter(++currentTime / totalTime);
        },
        delay: 1000,
        repeat: true,
        autostart: false
    });
    
    subscribe(PRIVATE_CHANNEL);
    subscribe(SENSOR_CHANNEL);
});

function setMeter(fraction) {
    var percent = fraction * 100;
    if(percent <= 100) {
        meter.css('width', percent + '%');
    }
}

function subscribe(topic) {
    WebApp.subscribe(topic);
}

function publish(topic, message) {
    log("publish: " + topic + " message " + message);    
    WebApp.publish(topic, message);
}

function onMessage(topic, payload) {
    log("onMessage: " + topic + " payload " + JSON.stringify(payload));
    if (topic == PRIVATE_CHANNEL || topic == SENSOR_CHANNEL) {
        handleMessagePayload(payload);
    }
}

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
    }
    
}

function resetPlayingInfo() {
    currentTime = 0;
    totalTime = playlist[currentTrack].tracklength;
    meter.css('width', '0%');
}

function updatePlayingInfo() {    
    $('#current-song-section h2').html(
        $('#options-list li:first span.artist').text() + " &ndash; " +
        $('#options-list li:first span.track').text()
    );
}

function toggleButtons() {
    if (playing) {
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
        html: '<span class="artist">' + payload.artist + '</span>' + "<br/>" + '<span class="track">' + payload.track + '</span>' + '<span class="tracklength"> &ndash; <span>' + payload.tracklength + '</span> s.</span>'
    }).hide().fadeIn());
}

function log(message) {
    if (debug) {
        console.log(message);
    }
}