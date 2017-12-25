package Taluvo.GUI.Displayables;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CompositeDisplayable implements Displayable
{
    private Point origin;
    private Dimension size;
    private Set<Displayable> components;

    public CompositeDisplayable(Point origin, Displayable... components)
    {
        this.origin = origin;
        this.components = new HashSet<>();
        this.components.addAll(Arrays.asList(components));

        recalculateSize();
    }

    private void recalculateSize()
    {
        int maxX = 0, maxY = 0;

        for(Displayable component : components)
        {
            Point buttonPt = component.getOrigin();
            Dimension buttonSize = component.getSize();
            int pointEndX = buttonPt.x + buttonSize.width;
            if(maxX < pointEndX) maxX = pointEndX;
            int pointEndY = buttonPt.y + buttonSize.height;
            if(maxY < pointEndY) maxY = pointEndY;
        }

        this.size = new Dimension(maxX, maxY);
    }

    public Point getOrigin() {return origin;}
    public Dimension getSize() {return size; }

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        for(Displayable component : components)
        {
            component.drawWithOffset(g2d, drawPt);
        }
    }

    public void update()
    {
        Set<Displayable> expiredComponents = new HashSet<>();
        for(Displayable component : components)
        {
            component.update();
            if(component.expired())
            {
                expiredComponents.add(component);
            }
        }
        components.removeAll(expiredComponents);
    }

    public void add(Displayable displayable)
    {
        components.add(displayable);
    }
    public void remove(Displayable displayable) { components.remove(displayable); }
}
