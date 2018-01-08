package Taluvo.GUI.Clickables;

import java.awt.*;

public interface Clickable
{
    Point getOrigin();
    Dimension getSize();
    default boolean pointIsOn(Point point)
    {
        Point origin = getOrigin();
        Dimension size = getSize();
        return point.x > origin.x && point.y > origin.y && point.x < origin.x + size.width && point.y < origin.y + size.height;
    }
    default void press() {}
    default void enter() {}
    default void exit() {}

    static Clickable getUnclickable()
    {
        return new Clickable()
        {
            private Point origin = new Point(-1, -1);
            private Dimension size = new Dimension(0, 0);

            public Point getOrigin() { return origin; }
            public Dimension getSize()
            {
                return size;
            }
            public boolean pointIsOn(Point point)
            {
                return false;
            }

        };
    }

    static Clickable makeNullClickable(Point origin, Dimension size)
    {
        return new Clickable()
        {
            public Point getOrigin() { return origin; }
            public Dimension getSize()
            {
                return size;
            }
        };
    }
}
