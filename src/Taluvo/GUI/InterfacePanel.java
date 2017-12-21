package Taluvo.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InterfacePanel extends JPanel
{
    private Uberstate activeUberstate;

    public InterfacePanel(Uberstate activeUberstate)
    {
        //this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        this.activeUberstate = activeUberstate;

        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent event)
            {
                activeUberstate.checkForPress(event.getPoint());
                activeUberstate.update();
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionListener()
        {
            public void mouseMoved(MouseEvent event)
            {
                activeUberstate.checkForHover(event.getPoint());
                activeUberstate.update();
                repaint();
            }

            public void mouseDragged(MouseEvent event)
            {
                activeUberstate.checkForHover(event.getPoint());
                activeUberstate.update();
                repaint();
            }
        });

        //activeUberstate.update();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        activeUberstate.draw((Graphics2D) g);
    }
}
