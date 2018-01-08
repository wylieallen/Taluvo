package Taluvo.Game.PlayerControllers;

import Taluvo.Game.GameModel.Game;
import Taluvo.Game.GameUberstate;

public interface PlayerController
{
    void activate(GameUberstate gameUberstate);
    void deactivate();
}
