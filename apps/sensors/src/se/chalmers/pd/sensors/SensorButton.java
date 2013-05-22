package se.chalmers.pd.sensors;

import javax.swing.JButton;

public class SensorButton extends JButton {

	private static final long serialVersionUID = -3807718038347771145L;
	private static final int BUTTON_WIDTH = 75;
	private static final int BUTTON_HEIGHT = 30;
	
	public SensorButton(String text) {
		super(text);
		this.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
		/*this.setOpaque(false);
		this.setContentAreaFilled(false);
		this.setBorderPainted(false);*/
	}
}
