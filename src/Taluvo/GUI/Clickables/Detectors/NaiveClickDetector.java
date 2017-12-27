package Taluvo.GUI.Clickables.Detectors;

import Taluvo.GUI.Clickables.Clickable;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class NaiveClickDetector implements ClickDetector
{
    private Set<Clickable> clickables;
    private Set<Clickable> staticClickables;

    public NaiveClickDetector()
    {
        this.clickables = new HashSet<>();
        this.staticClickables = new HashSet<>();
    }

    public void reset()
    {
        clickables.clear();
        staticClickables.clear();
    }

    public void add(Clickable clickable)
    {
        clickables.add(clickable);
    }
    public void addStatic(Clickable clickable) { staticClickables.add(clickable);}

    public void initialize(Set<Clickable> clickables, Set<Clickable> staticClickables)
    {
        this.clickables.addAll(clickables);
        this.staticClickables.addAll(staticClickables);
    }

    public Clickable getClickable(Point point)
    {
        for(Clickable clickable : clickables)
        {
            if(clickable.pointIsOn(point))
            {
                return clickable;
            }
        }

        for(Clickable clickable : staticClickables)
        {
            if(clickable.pointIsOn(point))
            {
                return clickable;
            }
        }

        return Clickable.getNullClickable();
    }

    public Clickable getClickable(Point point, Point offset)
    {
        Point adjustedPt = new Point(point.x - offset.x, point.y - offset.y);

        for(Clickable clickable : staticClickables)
        {
            if(clickable.pointIsOn(point))
            {
                return clickable;
            }
        }

        for(Clickable clickable : clickables)
        {
            if(clickable.pointIsOn(adjustedPt))
            {
                return clickable;
            }
        }

        return Clickable.getNullClickable();
    }
}
