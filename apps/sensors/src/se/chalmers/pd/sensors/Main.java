package se.chalmers.pd.sensors;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class Main implements ActionListener {

	private JButton sensor1;
	private JButton sensor2;
	private JButton sensor3;
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JFrame jframe;
	
	public Main() {
		createComponents();
		addComponents();
		addListeners();
		
		jframe.setVisible(true);
	}
	
	private void createComponents() {
		jframe = new JFrame("Sensors");
		jframe.setSize(600, 400);
		sensor1 = new JButton("Sensor 1");
		sensor1.setActionCommand("1");
		sensor2 = new JButton("Sensor 2");
		sensor2.setActionCommand("2");
		sensor3 = new JButton("Sensor 3");
		sensor3.setActionCommand("3");
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
		if(command.equals("1")) {
			label1.setText("Clicked button 1");
		} else if(command.equals("2")) {
			label2.setText("Clicked button 2");
		} else if(command.equals("3")) {
			label3.setText("Clicked button 3");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Main();
	}
	
}
