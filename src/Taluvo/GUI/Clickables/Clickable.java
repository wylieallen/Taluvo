package Taluvo.GUI.Clickables;

import java.awt.*;

public interface Clickable
{
    Point getOrigin();
    Dimension getSize();
    boolean pointIsOn(Point point);
    default void press() {}
    default void enter() {}
    default void exit() {}
    default boolean expired() { return false; }

    static Clickable getNullClickable()
    {
        return new Clickable()
        {
            public Point getOrigin()
            {
                return new Point(0, 0);
            }

            public Dimension getSize()
            {
                return new Dimension(0, 0);
            }

            public boolean pointIsOn(Point point)
            {
                return false;
            }
        };

    }
}
