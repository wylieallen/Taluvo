package Taluvo.Game.PlayerControllers;

import Taluvo.Game.GameModel.Player;
import Taluvo.Game.GameUberstate;

public interface PlayerController
{
    void activate(GameUberstate gameUberstate);
    void deactivate(GameUberstate gameUberstate);
    Player makeNewPlayer();
}
