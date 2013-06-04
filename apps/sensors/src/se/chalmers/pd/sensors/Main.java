package se.chalmers.pd.sensors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is the main class of the sensor application. It has three buttons to
 * trigger play, next and pause actions. This class contains the basic GUI
 * constructs such as the frame and buttons itself. It also has an action
 * listener that tells the application controller to perform an action when a
 * button is clicked.
 */
public class Main implements ActionListener {

    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 500;

    private SensorButton sensor1;
    private SensorButton sensor2;
    private SensorButton sensor3;
    private SensorButton sensor4;
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
        sensor1 = new SensorButton(Action.play.toString());
        sensor1.setActionCommand(Action.play.toString());
        sensor1.setLocation(500, 210);
        sensor2 = new SensorButton(Action.next.toString());
        sensor2.setActionCommand(Action.next.toString());
        sensor2.setLocation(500, 240);
        sensor3 = new SensorButton(Action.prev.toString());
        sensor3.setActionCommand(Action.prev.toString());
        sensor3.setLocation(500, 270);
        sensor4 = new SensorButton(Action.pause.toString());
        sensor4.setActionCommand(Action.pause.toString());
        sensor4.setLocation(500, 300);
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
        panel.add(sensor4);
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
        sensor4.addActionListener(this);
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
            case next:
            case prev:
            case pause:
                controller.performAction(action);
                break;
            case NONE:
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
