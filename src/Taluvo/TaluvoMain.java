package Taluvo;

import Taluvo.Game.GamePanel;
import Taluvo.Game.StartUberstate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class TaluvoMain
{
    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(TaluvoMain::createAndShowGUI);
    }

    public static void createAndShowGUI()
    {
        JFrame frame = new JFrame();

        frame.setTitle("Taluvo v0.0");
        frame.setSize(WIDTH + 36, HEIGHT + 82);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GamePanel panel = new GamePanel();

        frame.getContentPane().add(panel, BorderLayout.CENTER);

        frame.addComponentListener(new ComponentListener()
        {
            public void componentResized(ComponentEvent e)
            {
                panel.changeSize();
            }

            public void componentMoved(ComponentEvent e) { }

            public void componentHidden(ComponentEvent e) { }

            public void componentShown(ComponentEvent e) { }
        });

        frame.validate();
        frame.setVisible(true);
    }
}
