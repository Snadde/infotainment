package se.chalmers.pd.playlistmanager;

public class ApplicationController implements MqttWorker.Callback {
	
	private MqttWorker mqttWorker;

	public ApplicationController() {
		mqttWorker = new MqttWorker(this);
		mqttWorker.start();
	}

	@Override
	public void onMessage(String topic, String payload) {
		
	}

	@Override
	public void onConnected(boolean connected) {
		
	}
}
