package Taluvo.GUI.Displayables;

import java.awt.*;

public interface Displayable
{
    Point getOrigin();
    Dimension getSize();

    default boolean expired() {return false;}
    default void update() {}

    default void draw(Graphics2D g2d)
    {
        drawAt(g2d, getOrigin());
    }

    default void drawWithOffset(Graphics2D g2d, Point offset)
    {
        Point origin = getOrigin();
        drawAt(g2d, new Point(origin.x + offset.x, origin.y + offset.y));
    }

    void drawAt(Graphics2D g2d, Point drawPt);
}
