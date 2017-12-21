package Taluvo;

import Taluvo.GUI.InterfacePanel;
import Taluvo.Game.GameUberstate;

import javax.swing.*;
import java.awt.*;

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
        frame.setSize(WIDTH + 16, HEIGHT + 62);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        InterfacePanel panel = new InterfacePanel(new GameUberstate(new Point(0, 0), new Dimension(WIDTH, HEIGHT)));

        panel.setBackground(Color.BLUE);

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        JMenu fileMenu = new JMenu("File");

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(event -> System.exit(0));

        fileMenu.add(exit);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);

        frame.validate();
        //frame.setUndecorated(true);
        frame.setVisible(true);
    }
}
