package Taluvo.Game.PlayerControllers;

import Taluvo.Game.GameModel.Player;

import java.awt.*;

public abstract class AbstractPlayerController implements PlayerController
{
        private Color mainColor, altColor;
        private String name;

        public AbstractPlayerController(String name, Color mainColor, Color altColor)
        {
            this.name = name;
            this.mainColor = mainColor;
            this.altColor = altColor;
        }

        public Player makeNewPlayer()
        {
            return new Player(name, mainColor, altColor);
        }
}
