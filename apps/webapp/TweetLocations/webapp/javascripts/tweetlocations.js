Ext.setup({
	tabletStartupScreen: 'tablet_splash.png',
	phoneStartupScreen: 'phone_splash.png',
	icon: 'icon.png',
	glossOnIcon: true,
	onReady: function() {

		var timeline, panel, mapPanel, tabBar, refresh, addMarker, tweetBubble;

		timeline = new Ext.Component({
			title: 'Timeline',
			cls: 'timeline',
			scroll: 'vertical',
			tpl: [
				'<tpl for=".">',
					'<div class="tweet">',
							'<div class="profile"><img src="{profile_image_url}" /></div>',
							'<div class="content">',
								'<h2>{from_user}</h2>',
								'<p>{text}</p>',
							'</div>',
					'</div>',
				'</tpl>'
			]
		});

		mapPanel = new Ext.Map({
			title: 'Map',
			useCurrentLocation: true,
			mapOptions: {
				zoom: 11
			}
		});

		panel = new Ext.TabPanel({
			fullscreen: true,
			cardSwitchAnimation: 'slide',
			ui: 'light',
			items: [mapPanel, timeline]
		});

		panel.getTabBar().add([ {
                xtype: 'spacer'
            },  {
				xtype: 'button',
				iconMask: true,
				iconCls: 'refresh',
				ui: 'plain',
				style: 'margin: 0;',
				handler: refresh
			}
		]);
		panel.getTabBar().doLayout();

		refresh = function() {
			var coords = mapPanel.geo.coords;

			Ext.util.JSONP.request({
				url: 'http://search.twitter.com/search.json',
				callbackKey: 'callback',
				params: {
					geocode: coords.latitude + ',' + coords.longitude + ',' + '10mi',
					rpp: 30
				},
				callback: function(data) {
					var tweetList = data.results;
					timeline.update(tweetList);

					for (var index = 0, tweets = tweetList.length; index < tweets; index++) {
						var tweet = tweetList[index];
						if (tweet.geo && tweet.geo.coordinates) {
							addMarker(tweet);
						}
					}
				}
			});
		};

		addMarker = function(tweet) {
            var latIndex = 0;
            var longIndex = 1;
			var latLong = new google.maps.LatLng(tweet.geo.coordinates[latIndex], tweet.geo.coordinates[longIndex]);

			var marker = new google.maps.Marker({
				map: mapPanel.map,
				position: latLong
			});

			google.maps.event.addListener(marker, "click", function() {
				tweetBubble.setContent(tweet.text);
				tweetBubble.open(mapPanel.map, marker);
			});
		};

		tweetBubble = new google.maps.InfoWindow();
		mapPanel.geo.on('update', refresh);
	}
});
