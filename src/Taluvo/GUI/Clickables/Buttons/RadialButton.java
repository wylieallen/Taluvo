package Taluvo.GUI.Clickables.Buttons;

import Taluvo.Util.AbstractFunction;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RadialButton extends Button
{
    private BufferedImage pressImage;
    private BufferedImage baseImage;
    private BufferedImage hoverImage;

    public RadialButton(Point origin, BufferedImage baseImage, BufferedImage hoverImage, BufferedImage pressImage, AbstractFunction pressFunction)
    {
        super(origin, baseImage, hoverImage, pressFunction);
        this.baseImage = baseImage;
        this.hoverImage = hoverImage;
        this.pressImage = pressImage;
    }

    @Override
    public void press()
    {
        super.setBaseImage(pressImage);
        super.setHoverImage(pressImage);
        super.press();
    }

    public void release()
    {
        super.setBaseImage(baseImage);
        super.setHoverImage(hoverImage);
    }

    public static RadialButton getNullButton()
    {
        BufferedImage nullImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        return new RadialButton(new Point(0, 0), nullImg, nullImg, nullImg, () -> {});
    }
}
