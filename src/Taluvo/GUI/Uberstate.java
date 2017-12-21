package Taluvo.GUI;

import Taluvo.GUI.Clickables.Clickable;
import Taluvo.GUI.Clickables.Detectors.NaiveClickDetector;
import Taluvo.GUI.Displayables.Displayable;
import Taluvo.GUI.Clickables.Detectors.ClickDetector;

import java.awt.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class Uberstate implements Displayable, Clickable
{
    private Set<Collection<? extends Displayable>> displayables;
    private Set<Displayable> underlays;
    private Set<Displayable> overlays;

    private ClickDetector clickDetector;
    private Set<Clickable> clickables;
    private Clickable activeClickable;

    private Point origin;
    private Dimension size;

    public Uberstate(Point origin, Dimension size)
    {
        this.origin = origin;
        this.size = size;
        underlays = new HashSet<>();
        overlays = new HashSet<>();
        displayables = new LinkedHashSet<>();
        clickables = new HashSet<>();
        clickDetector = new NaiveClickDetector();
        activeClickable = Clickable.getNullClickable();
    }

    // Displayable interface:

    public Point getOrigin() { return origin;}
    public Dimension getSize() { return size; }

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        for(Displayable underlay : underlays)
        {
            underlay.drawWithOffset(g2d, drawPt);
        }

        for(Collection<? extends Displayable> set : displayables)
        {
            for(Displayable displayable : set)
            {
                displayable.drawWithOffset(g2d, drawPt);
            }
        }

        for(Displayable overlay : overlays)
        {
            overlay.drawWithOffset(g2d, drawPt);
        }
    }


    // GUI interface:

    public boolean expired() {return false;}

    public Set<Displayable> getUnderlays() {return underlays;}
    public Set<Displayable> getOverlays() {return overlays;}
    public Set<Collection<? extends Displayable>> getDisplayables() {return displayables;}

    public Clickable getActiveClickable() { return activeClickable; }

    public void addUnderlay(Displayable underlay) { underlays.add(underlay); }
    public void addOverlay(Displayable overlay) { overlays.add(overlay); }
    public void addDisplays(Collection<? extends Displayable> newDisplays) { displayables.add(newDisplays); }

    public void addClickable(Clickable clickable)
    {
        clickables.add(clickable);
        clickDetector.add(clickable);
    }

    public void update()
    {
        Set<Displayable> expiredDisplays = new HashSet<>();

        for(Displayable underlay : underlays)
        {
            underlay.update();
            if(underlay.expired())
            {
                expiredDisplays.add(underlay);
            }
        }

        underlays.removeAll(expiredDisplays);

        for(Collection<? extends Displayable> collection : displayables)
        {
            expiredDisplays = new HashSet<>();
            for(Displayable displayable : collection)
            {
                displayable.update();
                if(displayable.expired())
                {
                    expiredDisplays.add(displayable);
                }

            }
            //noinspection SuspiciousMethodCalls
            collection.removeAll(expiredDisplays);
        }

        expiredDisplays = new HashSet<>();

        for(Displayable overlay : overlays)
        {
            overlay.update();
            if(overlay.expired())
            {
                expiredDisplays.add(overlay);
            }
        }

        overlays.removeAll(expiredDisplays);

        Set<Clickable> expiredClickables = new HashSet<>();

        for(Clickable clickable : clickables)
        {
            if(clickable.expired())
            {
                expiredClickables.add(clickable);
            }
        }

        clickables.removeAll(expiredClickables);

        if(!expiredClickables.isEmpty())
        {
            clickDetector.reset();
            clickDetector.initialize(clickables);
        }
    }

    // Clickable interface:

    public void checkForPress(Point point)
    {
        if(activeClickable.pointIsOn(point))
        {
            activeClickable.press();
        }
        else
        {
            clickDetector.getClickable(point).press();
        }
    }

    public void checkForHover(Point point)
    {
        Clickable clickable = clickDetector.getClickable(point);
        if(activeClickable != clickable)
        {
            activeClickable.exit();
            activeClickable = clickable;
            activeClickable.enter();
        }
    }

    public boolean pointIsOn(Point point)
    {
        return(point.x >= origin.x && point.x <= origin.x + size.width && point.y >= origin.y && point.y <= origin.y + size.height);
    }
}
