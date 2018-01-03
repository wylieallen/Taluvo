package Taluvo.GUI.Displayables;

import Taluvo.Util.TypedAbstractFunction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class FunctorDisplayable implements Displayable
{
    private Dimension size;

    private TypedAbstractFunction<BufferedImage> imageGenerator;
    private TypedAbstractFunction<Point> originGetter;

    public FunctorDisplayable(TypedAbstractFunction<Point> originGetter, Dimension size, TypedAbstractFunction<BufferedImage> imageGenerator)
    {
        this.originGetter = originGetter;
        this.size = size;
        this.imageGenerator = imageGenerator;
    }

    public Point getOrigin() {return originGetter.execute();}
    public Dimension getSize() {return size;}

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        g2d.drawImage(imageGenerator.execute(), drawPt.x, drawPt.y, null);
    }
}
