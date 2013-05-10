package se.chalmers.pd.sensors;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

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

	private static final String ACTION_PLAY = "play";
	private static final String ACTION_NEXT = "next";
	private static final String ACTION_PAUSE = "pause";

	private JButton sensor1;
	private JButton sensor2;
	private JButton sensor3;
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
		jframe.setVisible(true);
	}

	/**
	 * Creates the specific components of the GUI
	 */
	private void createComponents() {
		jframe = new JFrame("Sensors");
		jframe.setSize(600, 400);
		sensor1 = new JButton(SENSOR_PLAY);
		sensor1.setActionCommand(ACTION_PLAY);
		sensor2 = new JButton(SENSOR_NEXT);
		sensor2.setActionCommand(ACTION_NEXT);
		sensor3 = new JButton(SENSOR_PAUSE);
		sensor3.setActionCommand(ACTION_PAUSE);
	}

	/**
	 * Adds the components to a panel and jframe
	 */
	private void addComponents() {
		JPanel panel = new JPanel();
		GridLayout grid = new GridLayout(3, 2);
		panel.setLayout(grid);
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
		String command = e.getActionCommand();
		if (command.equals(ACTION_PLAY)) {
			controller.play();
		} else if (command.equals(ACTION_NEXT)) {
			controller.next();
		} else if (command.equals(ACTION_PAUSE)) {
			controller.pause();
		}
	}

	/**
	 * Instantiates the application
	 */
	public static void main(String[] args) {
		new Main();
	}

}
