
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;


public class MQTT {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MQTT mqtt = new MQTT();
	}
	
	public MQTT(){
		String serverURI = "tcp://localhost:1883";
		String clientId = "Patrik is awesome";
		String pathname = "./testzip.txt";
		File file = new File(pathname);
		
		
		
		try {
			Path path = Paths.get("./test.zip");
			//ZipFile zipfile = new ZipFile();
			RandomAccessFile f = new RandomAccessFile(file, "r");
			byte[] b = new byte[(int)f.length()];
			f.readFully(b);
			byte[] payload = Files.readAllBytes(path);
			
			FileInputStream reader = new FileInputStream(file);
			byte[] cbuf = new byte[1000];
			reader.read(cbuf);
			//reader.read(cbuf);
			
			
			System.out.println(cbuf);
			System.out.println(payload);
			System.out.println(file);
			System.out.println(path);
			
			
			MqttClient client = new MqttClient(serverURI, clientId);
			MqttTopic topic = client.getTopic("zip");
			MqttMessage message = new MqttMessage();
			String hej = "halla";
			message.setPayload(payload);
			client.connect();
			MqttDeliveryToken token = topic.publish(message);
			//token.waitForCompletion(5000);
			Thread.sleep(1500);
			System.out.println("Done!");
			client.disconnect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
