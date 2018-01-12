package Taluvo.GUI.Clickables.Buttons;

import java.awt.*;

public class RadialButtonGroup extends ButtonGroup
{
    private RadialButton depressedButton;

    public RadialButtonGroup(Point origin, RadialButton... buttons)
    {
        super(origin, buttons);
        depressedButton = buttons[0];
        depressedButton.press();
    }

    @Override
    public void press()
    {
        depressedButton.release();
        lastMousedButton.press();
        depressedButton = (RadialButton) lastMousedButton;
    }

}
