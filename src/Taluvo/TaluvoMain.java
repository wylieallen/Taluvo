package Taluvo;

import Taluvo.GUI.InterfacePanel;
import Taluvo.Game.GameUberstate;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class TaluvoMain
{
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
                createAndShowGUI();
            }
        });
    }

    public static void createAndShowGUI()
    {
        JFrame frame = new JFrame();

        frame.setTitle("Taluvo v0.0");
        frame.setSize(WIDTH + 36, HEIGHT + 82);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        InterfacePanel panel = new InterfacePanel(new GameUberstate(new Point(0, 0), frame.getSize()));

        panel.setBackground(Color.BLUE);

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        JMenu fileMenu = new JMenu("File");

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(event -> System.exit(0));

        JMenuItem reset = new JMenuItem("Reset");
        reset.addActionListener(event -> {panel.setActiveUberstate(new GameUberstate(new Point(0, 0), panel.getSize()));});

        fileMenu.add(reset);
        fileMenu.add(exit);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        frame.addComponentListener(new ComponentListener()
        {
            public void componentResized(ComponentEvent evt)
            {
                //Component c = (Component) evt.getSource();

                panel.changeSize();
            }

            public void componentMoved(ComponentEvent e) { }

            public void componentHidden(ComponentEvent e) { }

            public void componentShown(ComponentEvent e) { }
        });

        frame.validate();
        //frame.setUndecorated(true);
        frame.setVisible(true);
    }
}
