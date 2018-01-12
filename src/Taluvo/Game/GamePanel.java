package Taluvo.Game;

import Taluvo.GUI.InterfacePanel;
import Taluvo.Game.GameModel.Game;
import Taluvo.Game.GameModel.MoveTabulator;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends InterfacePanel
{
    private StartUberstate startUberstate;
    private SetupUberstate setupUberstate;

    public GamePanel()
    {
        super(null);
        super.setBackground(Color.BLUE);
        super.setActiveUberstate(startUberstate = new StartUberstate(this));
        setupUberstate = new SetupUberstate(this.getSize(), this);
        centerCamera();
    }

    public void reset() { setStartUberstate(); }

    public void setStartUberstate()
    {
        super.setActiveUberstate(startUberstate);
    }

    public void setSetupUberstate()
    {
        super.setActiveUberstate(setupUberstate);
    }

    public void setGameUberstate()
    {
        super.setActiveUberstate(setupUberstate.makeGame());
    }
}
