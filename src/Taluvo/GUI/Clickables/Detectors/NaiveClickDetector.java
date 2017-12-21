package Taluvo.GUI.Clickables.Detectors;

import Taluvo.GUI.Clickables.Clickable;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class NaiveClickDetector implements ClickDetector
{
    private Set<Clickable> clickables;

    public NaiveClickDetector()
    {
        this.clickables = new HashSet<>();
    }

    public void reset()
    {
        clickables.clear();
    }

    public void add(Clickable clickable)
    {
        clickables.add(clickable);
    }

    public void initialize(Set<Clickable> clickables)
    {
        this.clickables.addAll(clickables);
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
        return Clickable.getNullClickable();
    }
}
