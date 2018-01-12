package Taluvo.Game.PlayerControllers;

import Taluvo.Game.GameModel.Player;
import Taluvo.Game.GameUberstate;

import java.awt.*;

public class LocalPlayerController extends AbstractPlayerController
{

    public LocalPlayerController(String name, Color mainColor, Color altColor)
    {
        super(name, mainColor, altColor);
    }

    public void activate(GameUberstate uberstate)
    {
        uberstate.unlockGameInput();
    }

    public void deactivate(GameUberstate uberstate)
    {
        uberstate.lockGameInput();
    }
}
