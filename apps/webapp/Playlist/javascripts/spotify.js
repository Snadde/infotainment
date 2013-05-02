(function($) {  
  $.grepWithLimit = function( elems, callback, limit, inv  ) {
		var ret = [], retVal;
		inv = !!inv;
 
		// Go through the array, only saving the items
		// that pass the validator function
		for ( var i = 0, length = elems.length; i < length; i++ ) {
			retVal = !!callback( elems[ i ], i );
			if ( inv !== retVal ) {
				ret.push( elems[ i ] );
			}
			// Break when the limit has been reached
			if ( limit >= ret.length) {
			  break;
			}
		}
 
		return ret;
	}
})(jQuery);

(function($) {
    
    var spotify = {
        spotifySearch: function(query, callback, options) {
            if (options == null) options = $.extend(options, {})
      
            if ($.isArray(query)) {
                return $.each(query, function(query, i) {
                    return $.spotifySearch(query, callback, options)
                })
              } else {
                var data = { q: query, page: options.page || 1 };    
                return $.get('http://ws.spotify.com/search/1/' + (options.method || 'track'), data, callback, options.dataType || 'json')
            }
        },
        
        filterTracksByCountryCode: function(tracks, twoLetterCountryCode, limit) {

            if (limit) {
                limit = 100;
            }
      
            return $.grepWithLimit(tracks, function(track, i) {
                return track.album.availability.territories.search(twoLetterCountryCode) >= 0;
            }, limit);
        },
    
        spotifyArtistSearch: function(query, data, callback) {
              return jQuery.spotifySearch(query, callback, $.extend(data, { method: 'artist' }));
        },
 
        spotifyAlbumSearch: function(query, data, callback) {
          return jQuery.spotifySearch(query, callback, $.extend(data, { method: 'album' }));
        }
    }
    
    $.extend(spotify);
    $.spotifyTrackSearch = $.spotifySearch;
})(jQuery);