package Taluvo.Game;

import Taluvo.GUI.Clickables.Buttons.Button;
import Taluvo.GUI.Clickables.Buttons.RadialButton;
import Taluvo.GUI.Clickables.Buttons.RadialButtonGroup;
import Taluvo.GUI.Clickables.Overlay;
import Taluvo.GUI.Uberstate;
import Taluvo.Game.PlayerControllers.AgentPlayerController;
import Taluvo.Game.PlayerControllers.LocalPlayerController;
import Taluvo.Game.PlayerControllers.PlayerController;
import Taluvo.Util.ImageFactory;

import java.awt.*;
import java.util.Arrays;


public class SetupUberstate extends Uberstate
{
    private int playerCount = 2;

    private PlayerController[] playerControllers = new PlayerController[4];

    private RadialButtonGroup[] playerSelectors = new RadialButtonGroup[4];

    private Color[][] playerColors;
    private String[] playerNames;

    private GamePanel panel;

    public SetupUberstate(Dimension size, GamePanel panel)
    {
        super(new Point(0, 0), size);

        this.panel = panel;

        addCenterOverlay(Button.getNullButton());

        playerColors = new Color[4][2];

        playerNames = new String[]{"One", "Two", "Three", "Four"};
        playerColors[0] = new Color[]{Color.BLACK, Color.WHITE};
        playerColors[1] = new Color[]{Color.WHITE, Color.BLACK};
        playerColors[2] = new Color[]{Color.BLUE, Color.RED};
        playerColors[3] = new Color[]{Color.MAGENTA, Color.YELLOW};

        for(int i = 0; i < 4; i++)
        {
            playerControllers[i] = new LocalPlayerController(playerNames[i], playerColors[i][0], playerColors[i][1]);
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
                ImageFactory.makeCenterLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, "START"),
                ImageFactory.makeCenterLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "START"),
                panel::setGameUberstate);

        addCenterOverlay(startGameButton);

        addCenterOverlay(Button.getNullButton());
    }

    private RadialButton makePlayerCountSelector(Point point, int number)
    {
        return new RadialButton(point,
                ImageFactory.makeCenterLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, "" + number),
                ImageFactory.makeCenterLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "" + number),
                ImageFactory.makeCenterLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "" + number),
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
                ImageFactory.makeCenterLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, "Local"),
                ImageFactory.makeCenterLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "Local"),
                ImageFactory.makeCenterLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "Local"),
                () -> {playerControllers[index] = new LocalPlayerController(playerNames[index], playerColors[index][0], playerColors[index][1]);});

        RadialButton remoteButton = new RadialButton(new Point(96, 0),
                ImageFactory.makeCenterLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, "Agent"),
                ImageFactory.makeCenterLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "Agent"),
                ImageFactory.makeCenterLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, "Agent"),
                () -> {playerControllers[index] = new AgentPlayerController(playerNames[index], playerColors[index][0], playerColors[index][1]);});

        localButton.press();

        return new RadialButtonGroup(new Point(0, y), localButton, remoteButton);
    }

    public GameUberstate makeGame()
    {
        return new GameUberstate(new Point(0, 0), panel.getSize(), panel.getCamera(),
                panel::repaint, panel::setGameUberstate, panel::reset,
                Arrays.copyOf(playerControllers, playerCount));
    }
}
