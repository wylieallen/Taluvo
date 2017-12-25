package Taluvo.GUI;

import Taluvo.Game.GameModel.Game;
import Taluvo.Game.GameModel.Player;
import Taluvo.Game.GameModel.Settlement;
import Taluvo.Game.GameUberstate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class InterfacePanel extends JPanel
{
    private Uberstate activeUberstate;

    private Point offset = new Point(0, 0);

    private Point prevPt = new Point(this.getWidth() / 2, this.getHeight() / 2);

    public InterfacePanel(Uberstate initialUberstate)
    {
        this.activeUberstate = initialUberstate;

        this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent event)
            {
                Point point = event.getPoint();
                prevPt = point;
                //point.translate(-offset.x, -offset.y);
                activeUberstate.checkForPress(point, offset);
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
                //point.translate(-offset.x, -offset.y);
                activeUberstate.checkForHover(point, offset);
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
                //point.translate(-offset.x, -offset.y);

                activeUberstate.checkForHover(point, offset);
                activeUberstate.update();
                repaint();
            }
        });

        //activeUberstate.update();
    }

    public void translateOffset(int dx, int dy)
    {
        offset.translate(dx, dy);
    }

    public void setActiveUberstate(Uberstate uberstate)
    {
        activeUberstate = uberstate;
        activeUberstate.update();
        Player.ONE.reset();
        Player.TWO.reset();
        changeSize();
        repaint();
    }

    public void changeSize()
    {
        // Recenter camera:
        Dimension prev = activeUberstate.getSize();
        Dimension next = this.getSize();
        Point translate = new Point((next.width - prev.width) / 2, (next.height - prev.height) / 2);
        offset.translate(translate.x, translate.y);

        // Update Uberstate:
        activeUberstate.changeSize(this.getSize());
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        activeUberstate.drawWithOffset((Graphics2D) g, offset);
    }
}
