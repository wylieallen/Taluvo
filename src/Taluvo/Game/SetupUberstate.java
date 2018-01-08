package Taluvo.Game;

import Taluvo.GUI.Clickables.Buttons.Button;
import Taluvo.GUI.Clickables.Buttons.RadialButton;
import Taluvo.GUI.Clickables.Buttons.RadialButtonGroup;
import Taluvo.GUI.Clickables.Overlay;
import Taluvo.GUI.InterfacePanel;
import Taluvo.GUI.Uberstate;
import Taluvo.Game.PlayerControllers.AgentPlayerController;
import Taluvo.Game.PlayerControllers.LocalPlayerController;
import Taluvo.Game.PlayerControllers.PlayerController;
import Taluvo.Util.ImageFactory;

import java.awt.*;


public class SetupUberstate extends Uberstate
{
    private int playerCount = 2;

    private PlayerController[] playerControllers = new PlayerController[4];

    private RadialButtonGroup[] playerSelectors = new RadialButtonGroup[4];

    private GamePanel panel;

    public SetupUberstate(Dimension size, GamePanel panel)
    {
        super(new Point(0, 0), size);

        this.panel = panel;

        addCenterOverlay(Button.getNullButton());

        for(int i = 0; i < 4; i++)
        {
            playerControllers[i] = new LocalPlayerController();
        }

        RadialButtonGroup playerCountSelectors = new RadialButtonGroup(new Point(),
                makePlayerCountSelector(new Point(0,0), 2),
                makePlayerCountSelector(new Point(64, 0), 3),
                makePlayerCountSelector(new Point(128, 0), 4)
        );

        addCenterOverlay(playerCountSelectors);


        for(int i = 0; i < 4; i++)
        {
            playerSelectors[i] = makePlayerControllerSelectors(i, i * 48);
        }

        addCenterOverlay(makePlayerAgentSelectorOverlay());

        Button startGameButton = new Button(new Point(),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, "START", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "START", new Point(8, 18)),
                panel::setGameUberstate);

        addCenterOverlay(startGameButton);

        addCenterOverlay(Button.getNullButton());
    }

    private RadialButton makePlayerCountSelector(Point point, int number)
    {
        return new RadialButton(point,
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, "" + number, new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "" + number, new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "" + number, new Point(8, 18)),
                () -> { playerCount = number; });
    }

    private Overlay makePlayerAgentSelectorOverlay()
    {
        return new Overlay(new Point())
        {
            private int prevCount = 0;

            @Override
            public void update()
            {
                if(prevCount != playerCount)
                {
                    for(int i = playerCount; i < 4; i++)
                    {
                        super.remove(playerSelectors[i]);
                        super.removeClickable(playerSelectors[i]);
                    }

                    for(int i = 0; i < playerCount; i++)
                    {
                        super.add(playerSelectors[i]);
                        super.addClickable(playerSelectors[i]);
                    }
                    prevCount = playerCount;
                    overlayManager.resetCenterOverlays();
                }
                super.update();
            }
        };
    }

    private RadialButtonGroup makePlayerControllerSelectors(int index, int y)
    {

        RadialButton localButton = new RadialButton(new Point(0, 0),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, "Local", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "Local", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "Local", new Point(8, 18)),
                () -> {playerControllers[index] = new LocalPlayerController();});

        RadialButton remoteButton = new RadialButton(new Point(96, 0),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, "Agent", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "Agent", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "Agent", new Point(8, 18)),
                () -> {playerControllers[index] = new AgentPlayerController();});

        return new RadialButtonGroup(new Point(0, y), localButton, remoteButton);
    }

    public GameUberstate makeGame()
    {
        return new GameUberstate(new Point(0, 0), panel.getSize(), panel.getCamera(), panel::repaint, playerControllers);
    }
}
