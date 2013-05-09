package se.chalmers.pd.sensors;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


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
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JFrame jframe;
	private Controller controller;
	
	public Main() {
		createComponents();
		addComponents();
		addListeners();
		
		controller = new Controller();
		jframe.setVisible(true);
	}
	
	private void createComponents() {
		jframe = new JFrame("Sensors");
		jframe.setSize(600, 400);
		sensor1 = new JButton(SENSOR_PLAY);
		sensor1.setActionCommand(ACTION_PLAY);
		sensor2 = new JButton(SENSOR_NEXT);
		sensor2.setActionCommand(ACTION_NEXT);
		sensor3 = new JButton(SENSOR_PAUSE);
		sensor3.setActionCommand(ACTION_PAUSE);
		label1 = new JLabel("Label 1");
		label2 = new JLabel("Label 2");
		label3 = new JLabel("Label 3");
	}
	
	private void addComponents() {
		JPanel panel = new JPanel();
		GridLayout grid = new GridLayout(3,2);
		panel.setLayout(grid);
		panel.add(sensor1);
		panel.add(label1);
		panel.add(sensor2);
		panel.add(label2);
		panel.add(sensor3);		
		panel.add(label3);
		
		jframe.getContentPane().add(panel);
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void addListeners() {
		sensor1.addActionListener(this);
		sensor2.addActionListener(this);
		sensor3.addActionListener(this);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals(ACTION_PLAY)) {
			label1.setText("Clicked button play");
			controller.play();
		} else if(command.equals(ACTION_NEXT)) {
			label2.setText("Clicked button next");
			controller.next();
		} else if(command.equals(ACTION_PAUSE)) {
			label3.setText("Clicked button pause");
			controller.pause();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();
	}
	
}
