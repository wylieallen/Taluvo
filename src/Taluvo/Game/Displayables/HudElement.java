package Taluvo.Game.Displayables;

import Taluvo.GUI.Displayables.Displayable;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class HudElement implements Displayable
{
    private Point origin;

    private BufferedImage background;

    protected HudElement(Point origin, BufferedImage background)
    {
        this.origin = origin;
        this.background = background;
    }

    public Point getOrigin() { return origin;}


    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        g2d.drawImage(background, drawPt.x, drawPt.y, null);
        drawContents(g2d, drawPt);
    }

    public abstract void drawContents(Graphics2D g2d, Point drawPt);
}
