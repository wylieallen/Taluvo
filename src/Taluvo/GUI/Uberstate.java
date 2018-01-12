package Taluvo.GUI;

import Taluvo.GUI.Clickables.Clickable;
import Taluvo.GUI.Clickables.Detectors.NaiveClickDetector;
import Taluvo.GUI.Clickables.Overlay;
import Taluvo.GUI.Displayables.Displayable;
import Taluvo.GUI.Clickables.Detectors.ClickDetector;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Uberstate implements Displayable, Clickable
{
    private Set<Collection<? extends Displayable>> displayables;
    private Set<Displayable> underlays;
    private Set<Displayable> overlays;

    private ClickDetector clickDetector;
    private Set<Clickable> clickables;
    private Set<Clickable> fixedClickables;
    private Clickable activeClickable;

    protected OverlayManager overlayManager;

    private Point origin;
    private Dimension size;

    public Uberstate(Point origin, Dimension size)
    {
        this.origin = origin;
        this.size = size;

        underlays = new HashSet<>();
        overlays = Collections.newSetFromMap(new ConcurrentHashMap<>());

        // Collections of intermediate displayables are stored in a LinkedHashSet to preserve insertion order
        displayables = new LinkedHashSet<>();

        clickables = new HashSet<>();
        fixedClickables = new HashSet<>();
        clickDetector = new NaiveClickDetector();
        activeClickable = Clickable.getUnclickable();

        overlayManager = new OverlayManager();
    }

    // Displayable interface:

    public Point getOrigin() { return origin;}
    public Dimension getSize() { return size; }

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        underlays.forEach(displayable -> displayable.drawWithOffset(g2d, drawPt));

        for(Collection<? extends Displayable> set : displayables)
        {
            set.forEach(displayable -> displayable.drawWithOffset(g2d, drawPt));
        }

        overlays.forEach(displayable -> displayable.draw(g2d));
    }


    // GUI interface:

    public boolean expired() {return false;}

    public Set<Displayable> getUnderlays() {return underlays;}
    public Set<Displayable> getOverlays() {return overlays;}
    public Set<Collection<? extends Displayable>> getDisplayables() {return displayables;}
    public Clickable getActiveClickable() { return activeClickable; }

    public void clearHover()
    {
        activeClickable.exit();
        activeClickable = Clickable.getUnclickable();
    }

    public void addUnderlay(Displayable underlay) { underlays.add(underlay); }

    public <T extends Displayable & Clickable> void addLeftOverlay(T overlay)
    {
        overlays.add(overlay);
        overlayManager.addLeft(overlay);
        addFixedClickable(overlay);
    }

    public <T extends Displayable & Clickable> void addCenterOverlay(T overlay)
    {
        overlays.add(overlay);
        overlayManager.addCenter(overlay);
        addFixedClickable(overlay);
    }

    public <T extends Displayable & Clickable> void addRightOverlay(T overlay)
    {
        overlays.add(overlay);
        overlayManager.addRight(overlay);
        addFixedClickable(overlay);
    }

    public void addDisplays(Collection<? extends Displayable> newDisplays) { displayables.add(newDisplays); }

    public void addClickable(Clickable clickable)
    {
        clickables.add(clickable);
        clickDetector.add(clickable);
    }

    public void addFixedClickable(Clickable clickable)
    {
        fixedClickables.add(clickable);
        clickDetector.addStatic(clickable);
    }

    public void update()
    {
        updateDisplays(underlays);

        for(Collection<? extends Displayable> collection : displayables)
        {
            updateDisplays(collection);
        }

        updateDisplays(overlays);

    }

    private static void updateDisplays(Collection<? extends Displayable> displayables)
    {
        Set<Displayable> expiredDisplayables = new HashSet<>();

        for(Displayable displayable : displayables)
        {
            displayable.update();
            if(displayable.expired())
            {
                expiredDisplayables.add(displayable);
            }
        }

        displayables.removeAll(expiredDisplayables);
    }

    // Clickable interface:

    public void checkForPress(Point point, Point offset)
    {
        clickDetector.getClickable(point, offset).press();
    }

    public void checkForHover(Point point, Point offset)
    {
        Clickable clickable = clickDetector.getClickable(point, offset);
        changeActiveClickable(clickable);
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
        changeActiveClickable(clickable);
    }

    private void changeActiveClickable(Clickable clickable)
    {
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
        this.size.setSize(newDimension);
        overlayManager.resetOverlays();
    }

    public class OverlayManager
    {
        private List<Displayable> leftOverlays;
        private List<Displayable> centerOverlays;
        private List<Displayable> rightOverlays;

        private int edgeBuffer = 12;

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
            double y = edgeBuffer;

            int maxHeight = size.height;
            int usedSpace = edgeBuffer * 2;
            for(Displayable overlay : overlays)
            {
                usedSpace += overlay.getSize().height;
            }
            int freeSpace = maxHeight - usedSpace;
            double elementBuffer =  overlays.size() > 1 ? freeSpace / (overlays.size() - 1) : freeSpace;

            //System.out.println("maxHeight " + maxHeight + ", usedSpace = " + usedSpace + ", bufferSize = " + elementBuffer);

            for(Displayable overlay : overlays)
            {
                Dimension size = overlay.getSize();

                //System.out.println("Next overlay: " + overlay.getClass() + " at " + (centerX - (size.width / 2)) + ", " + y);

                overlay.getOrigin().setLocation(centerX - (size.width / 2), y);

                y += elementBuffer + size.height; //componentBuffer + size.height;
            }
        }

        public void resetLeftOverlays()
        {
            updateLocations(edgeBuffer + (getMaxX(leftOverlays) / 2), leftOverlays);
        }

        public void resetCenterOverlays() { updateLocations((size.width / 2), centerOverlays); }
    }
}
