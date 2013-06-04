(function($) {
    var meter;
    var scrubber;
    var parent;
    var options;
    
    $.fn.scrubber = function(option) {
        var defaults = {
                        type: 'web',
                        callback: null,
                    };
         options = $.extend(defaults, option);
         parent = $(this);
         init(option.callback);
    };
   
    function init(callback){
        var meterDown = 'touchstart';
        var meterMove = 'touchmove';
        var meterUp = 'touchend';
        if(options.type == 'web'){
            meterDown = 'mousedown'
            meterMove = 'mousemove'
            meterUp = 'mouseup'
        }
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

    $.fn.setProgress = function(percent){
        meter.css('width', percent + '%');
        scrubber.css('left', meter.width() - 16);
    }  
}(jQuery));