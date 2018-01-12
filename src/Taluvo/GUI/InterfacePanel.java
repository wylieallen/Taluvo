package Taluvo.GUI;

import com.sun.corba.se.impl.orbutil.graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class InterfacePanel extends JPanel
{
    private Uberstate activeUberstate;

    private Camera camera;

    private Point prevPt = new Point(this.getWidth() / 2, this.getHeight() / 2);

    public InterfacePanel(Uberstate initialUberstate)
    {
        this.activeUberstate = initialUberstate;
        this.camera = new Camera();
        centerCamera();

        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent event)
            {
                Point point = event.getPoint();
                prevPt = point;
                activeUberstate.checkForPress(point, camera.getDrawOffset());
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
                activeUberstate.checkForHover(point, camera.getDrawOffset());
                activeUberstate.update();
                repaint();
            }

            public void mouseDragged(MouseEvent event)
            {
                Point point = event.getPoint();
                int dx = point.x - prevPt.x;
                int dy = point.y - prevPt.y;
                prevPt = point;
                translateCamera(dx, dy);

                activeUberstate.checkForHover(point, camera.getDrawOffset());
                activeUberstate.update();
                repaint();
            }
        });
    }


    public Uberstate getActiveUberstate() {return activeUberstate;}

    public Camera getCamera() {return camera;}

    public void centerCamera() { camera.center(getSize()); }
    public void translateCamera(int dx, int dy) { camera.translate(dx, dy); }

    protected void setActiveUberstate(Uberstate uberstate)
    {
        activeUberstate = uberstate;
        activeUberstate.clearHover();
        activeUberstate.update();
        changeSize();
        centerCamera();
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

        activeUberstate.drawWithOffset((Graphics2D) g, camera.getDrawOffset());
    }
}
