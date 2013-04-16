(function() {
    var activated = true;
	/*
    ** Universal debug function that will log messages to the console
    ** if activated.
    **/	
    module.exports.log = function(message){
            if(activated){	
                console.log(message);
            }
        };
}());
