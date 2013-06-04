(function($) {
    var meter;
    var scrubber;
    var parent;
    var options;
    
    /**
     ** The inital methods that sets the options and calls init
     ** There are currently 2 types supported, 'android' and 'web'
     ** 'web' is used for the Web browsers on your computer and
     ** 'android' is used for Androids WebView
    **/
    $.fn.scrubber = function(option) {
        var defaults = {
                        type: 'web',
                        callback: null,
                    };
         options = $.extend(defaults, option);
         parent = $(this);
         init(option.callback);
    };
    
    /**
     ** The init function that build up the scrubber by appending 2 additional elements,
     ** Uses The css in jquery.scrubber.css file, pending on what type is set touch or mouse
     ** events will be bound.
     ** Uses the callback method to return the fraction of how much the scrubber moved when
     ** releasing touch our mouse click.
    **/
    function init(callback){
        var meterDown = 'touchstart';
        var meterMove = 'touchmove';
        var meterUp = 'touchend';
        if(options.type == 'web'){
            meterDown = 'mousedown'
            meterMove = 'mousemove'
            meterUp = 'mouseup'
        }
        /* Known issue: parseInt does not work with android webView, please change the value manually if
         * you have updated the css border radius */
        var BORDER_RADIUS = 16 ;//parseInt($("#meter span#ring").css('border-radius'));
        
        parent.append('<span id=\'ring\'></span>');
        parent.append('<span id=\'bar\' style="width: 0%"></span>');
        
        meter = $('#meter span#bar');
        scrubber = $('#meter span#ring');
        
        scrubber.bind(meterDown, function(e){    
            var x = e.pageX;
            var offset = scrubber.parent().offset().left;
            var maxX = $('#meter').width() - scrubber.width();
            $(document).bind(meterMove, function(e){
                if(options.type == 'android'){
                    e.preventDefault();
                    var event = window.event;     
                    var pageX = event.touches[0].pageX;    
                }
                else {
                    var pageX = e.pageX;
                }
                var newX = Math.min(maxX, Math.max(-16, pageX - offset - BORDER_RADIUS));
                scrubber.css('left', newX);
                var percent = ((newX + BORDER_RADIUS) / meter.parent().width()) * 100;
                meter.css('width', percent + '%');
            });
            $(document).bind(meterUp, function (e) {
                $(document).unbind(meterMove);
                $(document).unbind(meterMove);
                var fraction = ($('#meter span#ring').position().left + BORDER_RADIUS) / $('#meter').width();
                
                if ( $.isFunction( callback ) ) {
                        callback(fraction);
                    }
            });
        });
    }
    /**
     ** Sets the progress of the scrubber in percent.
     **
    **/
    $.fn.setProgress = function(percent){
        meter.css('width', percent + '%');
        scrubber.css('left', meter.width() - 16);
    }  
}(jQuery));