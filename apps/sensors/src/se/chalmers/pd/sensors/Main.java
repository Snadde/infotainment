package se.chalmers.pd.sensors;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * This is the main class of the sensor application. It has three buttons to
 * trigger play, next and pause actions. This class contains the basic GUI
 * constructs such as the frame and buttons itself. It also has an action
 * listener that tells the application controller to perform an action when a
 * button is clicked.
 * 
 */
public class Main implements ActionListener {

	private static final String SENSOR_PLAY = "Play sensor";
	private static final String SENSOR_NEXT = "Next sensor";
	private static final String SENSOR_PAUSE = "Pause sensor";

	private static final int PANEL_WIDTH = 800;
	private static final int PANEL_HEIGHT = 500;

	private SensorButton sensor1;
	private SensorButton sensor2;
	private SensorButton sensor3;
	private JFrame jframe;
	private Controller controller;

	/**
	 * Initiates the application and creates the basic components and adds the
	 * listeners.
	 */
	public Main() {
		createComponents();
		addComponents();
		addListeners();
		controller = new Controller();
		jframe.pack();
		jframe.setVisible(true);
	}

	/**
	 * Creates the specific components of the GUI
	 */
	private void createComponents() {
		jframe = new JFrame("Sensors");
		jframe.setSize(PANEL_WIDTH, PANEL_HEIGHT);
		sensor1 = new SensorButton(SENSOR_PLAY);
		sensor1.setLocation(500, 225);
		sensor1.setActionCommand(Action.play.toString());
		sensor2 = new SensorButton(SENSOR_NEXT);
		sensor2.setActionCommand(Action.next.toString());
		sensor2.setLocation(500, 275);
		sensor3 = new SensorButton(SENSOR_PAUSE);
		sensor3.setActionCommand(Action.pause.toString());
		sensor3.setLocation(500, 325);
	}

	/**
	 * Adds the components to a panel and jframe
	 */
	private void addComponents() {
		Image image = new ImageIcon("wheel.jpg").getImage();
		ImagePanel panel = new ImagePanel(image);
		panel.add(sensor1);
		panel.add(sensor2);
		panel.add(sensor3);
		jframe.getContentPane().add(panel);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Adds this class as listener to the buttons
	 */
	private void addListeners() {
		sensor1.addActionListener(this);
		sensor2.addActionListener(this);
		sensor3.addActionListener(this);
	}

	/**
	 * Executed when a button is clicked and tells the controller to perform the
	 * specific action that the button that was clicked represents.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Action action = Action.valueOf(e.getActionCommand());
		switch (action) {
		case play:
			controller.play();
			break;
		case next:
			controller.next();
			break;
		case pause:
			controller.pause();
			break;
		}
	}

	/**
	 * Instantiates the application
	 */
	public static void main(String[] args) {
		new Main();
	}

}
