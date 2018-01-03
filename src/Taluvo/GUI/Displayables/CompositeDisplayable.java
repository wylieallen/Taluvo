package Taluvo.GUI.Displayables;

import java.awt.*;
import java.util.*;

public class CompositeDisplayable implements Displayable
{
    private Point origin;
    private Dimension size;
    private Set<Displayable> components;

    public CompositeDisplayable(Point origin, Displayable... components)
    {
        this.origin = origin;
        this.components = new LinkedHashSet<>();
        this.components.addAll(Arrays.asList(components));
        this.size = new Dimension();

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

        this.size.setSize(maxX, maxY);
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
        removeAll(expiredComponents);
    }

    public void add(Displayable displayable)
    {
        components.add(displayable);
        recalculateSize();
    }

    public void addAll(Collection<? extends Displayable> displayables)
    {
        components.addAll(displayables);
        recalculateSize();
    }

    public void remove(Displayable displayable)
    {
        components.remove(displayable);
        recalculateSize();
    }

    public void removeAll(Collection<? extends Displayable> displayables)
    {
        components.removeAll(displayables);
        recalculateSize();
    }

}
