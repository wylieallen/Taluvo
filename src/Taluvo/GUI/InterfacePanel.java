package Taluvo.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InterfacePanel extends JPanel
{
    private Uberstate activeUberstate;

    private Point camera = new Point(0, 0);

    private Point prevPt = new Point(this.getWidth() / 2, this.getHeight() / 2);

    public InterfacePanel(Uberstate initialUberstate)
    {
        this.activeUberstate = initialUberstate;
        centerCamera();

        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent event)
            {
                Point point = event.getPoint();
                prevPt = point;
                //point.translate(-camera.x, -camera.y);
                //System.out.println("Press detected " + point);
                activeUberstate.checkForPress(point, camera);
                activeUberstate.update();
                repaint();
            }
        });

        this.addMouseMotionListener(new MouseMotionListener()
        {
            public void mouseMoved(MouseEvent event)
            {
                Point point = event.getPoint();
                prevPt = point;
                //point.translate(-camera.x, -camera.y);
                activeUberstate.checkForHover(point, camera);
                activeUberstate.update();
                repaint();
            }

            public void mouseDragged(MouseEvent event)
            {
                Point point = event.getPoint();
                int dx = point.x - prevPt.x;
                int dy = point.y - prevPt.y;
                prevPt = point;
                translateOffset(dx, dy);
                //point.translate(-camera.x, -camera.y);
                //System.out.println("new camerapt " + camera);

                activeUberstate.checkForHover(point, camera);
                activeUberstate.update();
                repaint();
            }
        });

        //activeUberstate.update();
    }

    public void translateOffset(int dx, int dy)
    {
        camera.translate(dx, dy);
    }

    public Uberstate getActiveUberstate() {return activeUberstate;}

    public Point getCamera()
    {
        return camera;
    }

    public void centerCamera()
    {
        //System.out.println("Setting: " + -getWidth()/2 + " " + -getHeight()/2);
        Dimension size = getSize();
        camera.setLocation(size.width/2, size.height/2);
    }

    public void setActiveUberstate(Uberstate uberstate)
    {
        activeUberstate = uberstate;
        activeUberstate.update();
        changeSize();
        repaint();
    }

    public void changeSize()
    {
        // Recenter camera:
        Dimension prev = activeUberstate.getSize();
        Dimension next = this.getSize();
        Point translate = new Point((next.width - prev.width) / 2, (next.height - prev.height) / 2);
        camera.translate(translate.x, translate.y);

        // Update Uberstate:
        activeUberstate.changeSize(this.getSize());
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        activeUberstate.drawWithOffset((Graphics2D) g, camera);
    }
}
