package Taluvo.GUI.Clickables.Buttons;

import Taluvo.GUI.Clickables.Clickable;
import Taluvo.GUI.Displayables.CompositeDisplayable;
import Taluvo.GUI.Displayables.Displayable;

import java.awt.*;

public class RadialButtonGroup extends ButtonGroup
{
    private RadialButton depressedButton = RadialButton.getNullButton();

    public RadialButtonGroup(Point origin, RadialButton... buttons)
    {
        super(origin, buttons);
    }

    @Override
    public void press()
    {
        depressedButton.release();
        lastMousedButton.press();
        depressedButton = (RadialButton) lastMousedButton;
    }

}
