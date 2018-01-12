package Taluvo.Game;

import Taluvo.GUI.Clickables.Buttons.Button;
import Taluvo.GUI.Clickables.Overlay;
import Taluvo.GUI.Displayables.SimpleDisplayable;
import Taluvo.GUI.Uberstate;
import Taluvo.Util.ImageFactory;

import java.awt.*;


public class StartUberstate extends Uberstate
{
    public StartUberstate(GamePanel panel)
    {
        super(new Point(0, 0), panel.getSize());

        Overlay titleOverlay = new Overlay(new Point());
        titleOverlay.add(new SimpleDisplayable(new Point(0, 0), ImageFactory.makeText("Taluvo", new Font("Lucida-Grande", Font.BOLD, 172), Color.ORANGE)));

        super.addCenterOverlay(titleOverlay);

        Button newGameButton = new Button(
                new Point(0, 0),
                ImageFactory.makeCenterLabeledRect(256, 64, Color.WHITE, Color.GRAY, Color.BLACK, "New Game"),
                ImageFactory.makeCenterLabeledRect(256, 64, Color.RED, Color.GRAY, Color.BLACK, "New Game"),
                //() -> panel.setActiveUberstate(new GameUberstate(new Point(0, 0), panel.getSize(), panel.getCamera(), panel))
                panel::setSetupUberstate
        );

        super.addCenterOverlay(newGameButton);

        Button exitButton = new Button(
                new Point(0, 0),
                ImageFactory.makeCenterLabeledRect(256, 64, Color.WHITE, Color.GRAY, Color.BLACK, "Exit"),
                ImageFactory.makeCenterLabeledRect(256, 64, Color.RED, Color.GRAY, Color.BLACK, "Exit"),
                () -> System.exit(0)
        );

        super.addCenterOverlay(exitButton);

        super.addCenterOverlay(Button.getNullButton());
    }


}
