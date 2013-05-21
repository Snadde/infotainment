package se.chalmers.pd.sensors;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


public class ImagePanel extends JPanel {

	static final long serialVersionUID = 6982999219598677965L;
	private Image image;

	public ImagePanel(String path) {
		this(new ImageIcon(path).getImage());
	}

	public ImagePanel(Image image) {
		this.image = image;
		Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
	}

	public void paintComponent(Graphics graphics) {
		graphics.drawImage(image, 0, 0, null);
	}
}