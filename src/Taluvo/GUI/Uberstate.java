package Taluvo.GUI;

import Taluvo.GUI.Clickables.Clickable;
import Taluvo.GUI.Clickables.Detectors.NaiveClickDetector;
import Taluvo.GUI.Displayables.Displayable;
import Taluvo.GUI.Clickables.Detectors.ClickDetector;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.*;

public class Uberstate implements Displayable, Clickable
{
    private Set<Collection<? extends Displayable>> displayables;
    private Set<Displayable> underlays;
    private Set<Displayable> overlays;

    private ClickDetector clickDetector;
    private Set<Clickable> clickables;
    private Set<Clickable> staticClickables;
    private Clickable activeClickable;

    protected OverlayManager overlayManager;

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
        staticClickables = new HashSet<>();
        clickDetector = new NaiveClickDetector();
        activeClickable = Clickable.getNullClickable();

        overlayManager = new OverlayManager();
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
            //overlay.drawWithOffset(g2d, drawPt);
            overlay.draw(g2d);
        }
    }


    // GUI interface:

    public boolean expired() {return false;}

    public Set<Displayable> getUnderlays() {return underlays;}
    public Set<Displayable> getOverlays() {return overlays;}
    public Set<Collection<? extends Displayable>> getDisplayables() {return displayables;}

    public Clickable getActiveClickable() { return activeClickable; }

    public void addUnderlay(Displayable underlay) { underlays.add(underlay); }

    public void addLeftOverlay(Displayable overlay)
    {
        overlays.add(overlay);
        overlayManager.addLeft(overlay);
    }

    public void addCenterOverlay(Displayable overlay)
    {
        overlays.add(overlay);
        overlayManager.addCenter(overlay);
    }

    public void addRightOverlay(Displayable overlay)
    {
        overlays.add(overlay);
        overlayManager.addRight(overlay);
    }

    public void addDisplays(Collection<? extends Displayable> newDisplays) { displayables.add(newDisplays); }

    public void addClickable(Clickable clickable)
    {
        clickables.add(clickable);
        clickDetector.add(clickable);
    }

    public void addStaticClickable(Clickable clickable)
    {
        staticClickables.add(clickable);
        clickDetector.addStatic(clickable);
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

        for(Clickable clickable : staticClickables)
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
            clickDetector.initialize(clickables, staticClickables);
        }
    }

    // Clickable interface:

    public void checkForPress(Point point, Point offset)
    {
        clickDetector.getClickable(point, offset).press();
    }

    public void checkForHover(Point point, Point offset)
    {
        Clickable clickable = clickDetector.getClickable(point, offset);
        if(activeClickable != clickable)
        {
            activeClickable.exit();
            activeClickable = clickable;
            activeClickable.enter();
        }
    }

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

    public void changeSize(Dimension newDimension)
    {
        this.size = newDimension;
        overlayManager.resetOverlays();
    }

    public class OverlayManager
    {
        private List<Displayable> leftOverlays;
        private List<Displayable> centerOverlays;
        private List<Displayable> rightOverlays;

        private int edgeBuffer = 12;
        private int componentBuffer = 16;

        public OverlayManager()
        {
            leftOverlays = new ArrayList<>();
            centerOverlays = new ArrayList<>();
            rightOverlays = new ArrayList<>();
        }

        public void addRight(Displayable displayable)
        {
            rightOverlays.add(displayable);
        }

        public void addCenter(Displayable displayable)
        {
            centerOverlays.add(displayable);
        }

        public void addLeft(Displayable displayable)
        {
            leftOverlays.add(displayable);
        }

        public void resetOverlays()
        {
            int centerX;

            centerX = size.width - edgeBuffer - (getMaxX(rightOverlays) / 2);
            updateLocations(centerX, rightOverlays);

            centerX = (size.width / 2);
            updateLocations(centerX, centerOverlays);

            centerX = edgeBuffer + (getMaxX(leftOverlays) / 2);
            updateLocations(centerX, leftOverlays);
        }

        private int getMaxX(Collection<Displayable> overlays)
        {
            int maxWidth = 0;

            for(Displayable overlay : overlays)
            {
                int overlayWidth = overlay.getSize().width;
                if(overlayWidth > maxWidth)
                {
                    maxWidth = overlayWidth;
                }
            }

            return maxWidth;
        }

        private void updateLocations(int centerX, Collection<Displayable> overlays)
        {
            int y = edgeBuffer;

            for(Displayable overlay : overlays)
            {
                Dimension size = overlay.getSize();

                overlay.getOrigin().setLocation(centerX - (size.width / 2), y);

                y += componentBuffer + size.height;
            }
        }

        public void resetLeftOverlays()
        {
            updateLocations(edgeBuffer + (getMaxX(leftOverlays) / 2), leftOverlays);
        }
    }
}
