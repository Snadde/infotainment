package se.chalmers.pd.sensors;

import javax.swing.*;
import java.awt.*;

/**
 * Custom class that extends JPanel to allow the panel to have a background image.
 */
public class ImagePanel extends JPanel {

    static final long serialVersionUID = 6982999219598677965L;
    private Image image;

    /**
     * Creates an ImagePanel from the image path given as a paramter.
     *
     * @param path to the image, relative to the project.
     */
    public ImagePanel(String path) {
        this(new ImageIcon(path).getImage());
    }

    /**
     * Creates an ImagePanel with the Image passed in
     *
     * @param image
     */
    public ImagePanel(Image image) {
        this.image = image;
        Dimension size = new Dimension(image.getWidth(null), image.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }

    /**
     * Draws the image.
     *
     * @param graphics
     */
    public void paintComponent(Graphics graphics) {
        graphics.drawImage(image, 0, 0, null);
    }
}