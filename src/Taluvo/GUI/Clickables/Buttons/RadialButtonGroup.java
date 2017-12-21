package Taluvo.GUI.Clickables.Buttons;

import Taluvo.GUI.Clickables.Clickable;
import Taluvo.GUI.Displayables.Displayable;

import java.awt.*;

public class RadialButtonGroup implements Clickable, Displayable
{
    private Point origin;
    private Dimension size;

    private RadialButton[] buttons;

    private RadialButton lastMousedButton = RadialButton.getNullButton();
    private RadialButton hoveredButton = RadialButton.getNullButton();
    private RadialButton depressedButton = RadialButton.getNullButton();

    public RadialButtonGroup(Point origin, Dimension size, RadialButton... buttons)
    {
        this.origin = origin;
        this.size = size;
        this.buttons = buttons;
    }

    public Point getOrigin() {return origin;}
    public Dimension getSize() {return size;}

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        for(Button button : buttons)
        {
            button.drawWithOffset(g2d, drawPt);
        }
    }

    public boolean pointIsOn(Point point)
    {
        Point offset = new Point(point.x - origin.x, point.y - origin.y);
        for(RadialButton button : buttons)
        {
            if(button.pointIsOn(offset))
            {
                lastMousedButton = button;
                this.enter();
                return true;
            }
        }
        return false;
    }

    @Override
    public void press()
    {
        depressedButton.release();
        lastMousedButton.press();
        depressedButton = lastMousedButton;
    }

    public void enter()
    {
        hoveredButton.exit();
        hoveredButton = lastMousedButton;
        hoveredButton.enter();
    }

    public void exit()
    {
        hoveredButton.exit();
    }

    public boolean expired() {return false;}
}
