package Taluvo.GUI.Clickables.Buttons;

import Taluvo.GUI.Clickables.Clickable;
import Taluvo.GUI.Displayables.CompositeDisplayable;

import java.awt.*;

public class ButtonGroup extends CompositeDisplayable implements Clickable
{
    private Button[] buttons;

    protected Button lastMousedButton = RadialButton.getNullButton();
    private Button hoveredButton = RadialButton.getNullButton();

    public ButtonGroup(Point origin, Button... buttons)
    {
        super(origin, buttons);

        this.buttons = buttons;
    }

    public boolean pointIsOn(Point point)
    {
        Point offset = new Point(point.x - getOrigin().x, point.y - getOrigin().y);
        for(Button button : buttons)
        {
            if(button.pointIsOn(offset))
            {
                lastMousedButton = button;
                this.enter();
                return true;
            }
        }
        return false;
    }

    public boolean expired() {return false;}

    public void press()
    {
        lastMousedButton.press();
    }

    public void enter()
    {
        hoveredButton.exit();
        hoveredButton = lastMousedButton;
        hoveredButton.enter();
    }

    public void exit()
    {
        hoveredButton.exit();
    }
}
