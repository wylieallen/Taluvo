package Taluvo.GUI.Displayables;

import Taluvo.Util.TypedAbstractFunction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class StringDisplayable implements Displayable
{
    private Point origin;
    private FontMetrics fontMetrics;
    private Color color;
    private TypedAbstractFunction<String> stringGenerator;

    public StringDisplayable(Point origin, Color color, TypedAbstractFunction<String> stringGenerator)
    {
        this.origin = origin;
        this.color = color;
        this.stringGenerator = stringGenerator;
        this.fontMetrics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics().getFontMetrics();
    }

    public StringDisplayable(Point origin, TypedAbstractFunction<String> stringGenerator)
    {
        this(origin, Color.BLACK, stringGenerator);
    }

    public Point getOrigin()
    {
        return origin;
    }

    public Dimension getSize()
    {
        int maxWidth = 0, heightMultiplier = 0;
        for(String line : stringGenerator.execute().split("\n"))
        {
            heightMultiplier++;
            int lineWidth = fontMetrics.stringWidth(line);
            if(lineWidth > maxWidth) maxWidth = lineWidth;
        }
        return new Dimension(maxWidth, fontMetrics.getHeight() * heightMultiplier);
    }

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        g2d.setColor(color);
        //System.out.println("Printing string at " + (drawPt.x + origin.x) + "," + (drawPt.y + origin.y + fontMetrics.getHeight()));
        //System.out.println("drawPt: " + drawPt + " origin: " + origin + " fontmetrics height: " + fontMetrics.getHeight());
        int y = drawPt.y + fontMetrics.getHeight();
        for(String line : stringGenerator.execute().split("\n"))
        {
            g2d.drawString(line, drawPt.x, y);
            y += fontMetrics.getHeight() + 4;
        }
    }
}
