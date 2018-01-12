package Taluvo.GUI.Clickables.Buttons;

import Taluvo.GUI.Clickables.Clickable;
import Taluvo.GUI.Displayables.SimpleDisplayable;
import Taluvo.Util.AbstractFunction;
import Taluvo.Util.ImageFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Button extends SimpleDisplayable implements Clickable
{
    private BufferedImage baseImage;
    private BufferedImage hoverImage;

    private AbstractFunction pressFunction;

    public Button(Point point, BufferedImage baseImage, BufferedImage hoverImage, AbstractFunction pressFunction)
    {
        super(point, baseImage);
        this.baseImage = baseImage;
        this.hoverImage = hoverImage;
        this.pressFunction = pressFunction;
    }

    public static Button makeLabeledButton(Point origin, Dimension size, String label, AbstractFunction pressFunction)
    {
        return new Button(origin,
                ImageFactory.makeCenterLabeledRect(size.width, size.height, Color.WHITE, Color.GRAY, Color.BLACK, label),
                ImageFactory.makeCenterLabeledRect(size.width, size.height, Color.RED, Color.GRAY, Color.BLACK, label),
                pressFunction);
    }

    public boolean pointIsOn(Point point)
    {
        Point origin = super.getOrigin();
        Dimension dimension = super.getSize();

        return(point.x >= origin.x && point.x <= origin.x + dimension.width && point.y >= origin.y && point.y <= origin.y + dimension.height);
    }

    @Override
    public boolean expired()
    {
        return false;
    }

    @Override
    public void enter()
    {
        super.setImage(hoverImage);
    }

    @Override
    public void exit()
    {
        super.setImage(baseImage);
    }

    @Override
    public void press()
    {
        pressFunction.execute();
    }

    protected void setBaseImage(BufferedImage image)
    {
        if(super.getImage() == baseImage)
        {
            super.setImage(baseImage = image);
        }
        else
        {
            this.baseImage = image;
        }
    }

    protected void setHoverImage(BufferedImage image)
    {
        if(super.getImage() == hoverImage)
        {
            super.setImage(hoverImage = image);
        }
        else
        {
            this.hoverImage = image;
        }
    }

    public BufferedImage getBaseImage() {return baseImage;}
    public BufferedImage getHoverImage() {return hoverImage;}

    private static Button nullButton = new Button(new Point(-1, -1),
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
            () -> {});

    public static Button getNullButton() {return nullButton;}

}
