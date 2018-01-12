package Taluvo.GUI.Displayables;

import Taluvo.Util.TypedAbstractFunction;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ConditionalDisplayable implements Displayable
{
    private Point origin;

    private Displayable defaultDisplayable;
    private Map<TypedAbstractFunction<Boolean>, Displayable> conditionalMap;

    public ConditionalDisplayable(Point origin, Displayable defaultDisplayable, Map<TypedAbstractFunction<Boolean>, Displayable> conditionalMap)
    {
        this.origin = origin;
        this.defaultDisplayable = defaultDisplayable;
        this.conditionalMap = conditionalMap;
    }

    public ConditionalDisplayable(Point origin, Displayable defaultDisplayable)
    {
        this(origin, defaultDisplayable, new HashMap<>());
    }

    public void add(TypedAbstractFunction<Boolean> condition, Displayable displayable)
    {
        conditionalMap.put(condition, displayable);
    }

    public Point getOrigin() {return origin;}
    public Dimension getSize() {return defaultDisplayable.getSize();}


    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        for(TypedAbstractFunction<Boolean> condition : conditionalMap.keySet())
        {
            if(condition.execute())
            {
                conditionalMap.get(condition).drawAt(g2d, drawPt);
                return;
            }
        }
        defaultDisplayable.drawAt(g2d, drawPt);
    }
}
