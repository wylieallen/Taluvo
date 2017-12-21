package Taluvo.GUI.Clickables.Buttons;

import Taluvo.GUI.Clickables.Clickable;
import Taluvo.GUI.Displayables.SimpleDisplayable;
import Taluvo.Util.AbstractFunction;

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

}
