package Taluvo.Game;

import Taluvo.GUI.Clickables.Buttons.Button;
import Taluvo.GUI.Clickables.Buttons.RadialButton;
import Taluvo.GUI.Clickables.Overlay;
import Taluvo.GUI.Displayables.SimpleDisplayable;
import Taluvo.GUI.Displayables.StringDisplayable;
import Taluvo.GUI.InterfacePanel;
import Taluvo.GUI.Uberstate;
import Taluvo.Util.ImageFactory;

import java.awt.*;


public class StartUberstate extends Uberstate
{
    public StartUberstate(GamePanel panel)
    {
        super(new Point(0, 0), panel.getSize());

        Overlay titleOverlay = new Overlay(new Point());
        titleOverlay.add(new SimpleDisplayable(new Point(0, 0), ImageFactory.makeText("Taluvo", new Font("Lucida-Grande", Font.BOLD, 96), Color.ORANGE)));

        super.addCenterOverlay(titleOverlay);

        Button newGameButton = new Button(
                new Point(0, 0),
                ImageFactory.makeLabeledRect(256, 64, Color.WHITE, Color.GRAY, Color.BLACK, "New Game", new Point(8, 12)),
                ImageFactory.makeLabeledRect(256, 64, Color.RED, Color.GRAY, Color.BLACK, "New Game", new Point(8, 12)),
                //() -> panel.setActiveUberstate(new GameUberstate(new Point(0, 0), panel.getSize(), panel.getCamera(), panel))
                panel::setSetupUberstate
        );

        super.addCenterOverlay(newGameButton);

        Button exitButton = new Button(
                new Point(0, 0),
                ImageFactory.makeLabeledRect(256, 64, Color.WHITE, Color.GRAY, Color.BLACK, "Exit", new Point(8, 12)),
                ImageFactory.makeLabeledRect(256, 64, Color.RED, Color.GRAY, Color.BLACK, "Exit", new Point(8, 12)),
                () -> System.exit(0)
        );

        super.addCenterOverlay(exitButton);

        super.addCenterOverlay(Button.getNullButton());
    }


}
