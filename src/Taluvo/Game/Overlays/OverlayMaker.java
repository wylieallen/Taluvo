package Taluvo.Game.Overlays;

import Taluvo.GUI.Clickables.Buttons.Button;
import Taluvo.GUI.Clickables.Buttons.ButtonGroup;
import Taluvo.GUI.Clickables.Clickable;
import Taluvo.GUI.Clickables.Overlay;
import Taluvo.GUI.Displayables.ConditionalDisplayable;
import Taluvo.GUI.Displayables.FunctorDisplayable;
import Taluvo.GUI.Displayables.SimpleDisplayable;
import Taluvo.GUI.Displayables.StringDisplayable;
import Taluvo.GUI.Uberstate;
import Taluvo.Game.Clickables.HexButton;
import Taluvo.Game.GameModel.*;
import Taluvo.Game.GameUberstate;
import Taluvo.Util.ImageFactory;

import java.awt.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class OverlayMaker
{
    public static Overlay makeDeckOverlay(Deck deck)
    {
        Overlay deckOverlay = new Overlay(new Point());

        Button rotLeft = Button.makeLabeledButton(new Point(0, 0), new Dimension(64, 32), "ROT. L", deck::rotLeft);
        Button rotRight = Button.makeLabeledButton(new Point(64, 0), new Dimension(64, 32), "ROT. R", deck::rotRight);
        ButtonGroup rots = new ButtonGroup(new Point(0,128), rotLeft, rotRight);

        Point volcanoOrigin = new Point(44, 44);

        deckOverlay.add(new SimpleDisplayable(new Point(0, 0), ImageFactory.getVolcanoHexOnBackground(128, 128)));

        deckOverlay.add(new FunctorDisplayable(
                () ->
                {
                    Point neighborOffset = Hex.neighborOffsets[deck.peek().getIndexA()];
                    return new Point(volcanoOrigin.x + neighborOffset.x, volcanoOrigin.y + neighborOffset.y);
                },
                new Dimension(40, 40), () -> ImageFactory.getBaseHex(deck.peek().getTerrainA()) ));

        deckOverlay.add(new FunctorDisplayable(
                () ->
                {
                    Point neighborOffset = Hex.neighborOffsets[deck.peek().getIndexB()];
                    return new Point(volcanoOrigin.x + neighborOffset.x, volcanoOrigin.y + neighborOffset.y);
                },
                new Dimension(40, 40), () -> ImageFactory.getBaseHex(deck.peek().getTerrainB()) ));

        deckOverlay.add(new StringDisplayable(new Point(2, 108), () -> "#" + deck.peek().getTileID()));
        deckOverlay.add(rots);

        deckOverlay.addClickable(rots);
        deckOverlay.addClickable(Clickable.makeNullClickable(new Point(0, 0), new Dimension(128, 128)));

        return deckOverlay;
    }

    public static Overlay makeHexDetailOverlay(GameUberstate gameUberstate)
    {
        Overlay hexDetailOverlay = new Overlay(new Point());

        hexDetailOverlay.add(new SimpleDisplayable(new Point(0, 0), ImageFactory.makeBorderedRect(144, 148, Color.WHITE, Color.GRAY)));

        Point hexImageOrigin = new Point(100, 4);

        hexDetailOverlay.add(new FunctorDisplayable(() -> hexImageOrigin, new Dimension(40, 40),
                () -> gameUberstate.getHexButton(gameUberstate.getActiveClickable().getOrigin()).getBaseImage()));

        hexDetailOverlay.add(new StringDisplayable(new Point(124, 5),
                () ->
                {
                    int level = gameUberstate.getHexButton(gameUberstate.getActiveClickable().getOrigin()).getHex().getLevel();
                    return level >= 1 ? "" + level : "";
                }));

        // Better solution: one big StringDisplayable using \n
        hexDetailOverlay.add(new StringDisplayable(new Point(4, 3),
                () ->
                {
                    Hex activeHex = gameUberstate.getHexButton(gameUberstate.getActiveClickable().getOrigin()).getHex();
                    Point origin = activeHex.getOrigin();

                    return activeHex == Hex.getNullHex() ? "" :
                            "TileID: " + activeHex.getTileID() + "\nLevel: " + activeHex.getLevel() + "\nBuilding: " + activeHex.getBuilding() +
                                    "\nPt: (" + origin.x + "," + origin.y + ")" + "\nTerrain: " + activeHex.getTerrain() +
                                    "\nOwner: " + activeHex.getOwner().getName() + "\nSettlement: " + activeHex.getSettlementID();

                }));

        hexDetailOverlay.addClickable(Clickable.makeNullClickable(new Point(0, 0), new Dimension(144, 148)));

        return hexDetailOverlay;
    }

    public static Overlay makeTurnStatusOverlay(GameUberstate gameUberstate)
    {
        Overlay turnStatusOverlay = new Overlay(new Point());

        turnStatusOverlay.add(new SimpleDisplayable(new Point(0, 0), ImageFactory.makeBorderedRect(144, 104, Color.WHITE, Color.GRAY)));
        turnStatusOverlay.add(new StringDisplayable(new Point(4, 0),
                () ->
                        "TurnPhase: " + gameUberstate.getActivePhase().toString() + "\nPlayer: " + gameUberstate.getActivePlayer().getName()
                                + "\nBuilding: " + gameUberstate.getActiveBuilding() + "\nTerrain: " + gameUberstate.getActiveTerrain() + "\nState: " + gameUberstate.getEndCondition()
        ));

        turnStatusOverlay.addClickable(Clickable.makeNullClickable(new Point(0, 0), new Dimension(144, 104)));

        return turnStatusOverlay;
    }

    public static Overlay makePlayersPiecesOverlay(Player... players)
    {
        Overlay playersOverlay = new Overlay(new Point());

        int x = 0;

        for(Player player : players)
        {
            Overlay playerOverlay = makePlayerPiecesOverlay(player, new Point(x, 0));
            playersOverlay.add(playerOverlay);
            playersOverlay.addClickable(playerOverlay);
            x += 288;
        }

        return playersOverlay;
    }

    private static Overlay makePlayerPiecesOverlay(Player player, Point origin)
    {
        Overlay playerOverlay = new Overlay(origin);

        //playerOverlay.add(new SimpleDisplayable(new Point(0, 0), ImageFactory.makeBorderedRect(224, 64, Color.WHITE, Color.GRAY)));

        ConditionalDisplayable background = new ConditionalDisplayable(new Point(0, 0),
                new SimpleDisplayable(new Point(0, 0), ImageFactory.makeBorderedRect(224, 64, Color.WHITE, Color.GRAY)));

        background.add(player::isLoser, new SimpleDisplayable(new Point(0, 0), ImageFactory.makeBorderedRect(224, 64, Color.RED, Color.GRAY)));
        background.add(player::isWinner,  new SimpleDisplayable(new Point(0, 0), ImageFactory.makeBorderedRect(224, 64, Color.GREEN, Color.GRAY)));

        playerOverlay.add(background);

        playerOverlay.add(new StringDisplayable(new Point(4, 0), player::getName));

        // Todo: prerender the buildings and background (and maybe player name) onto one image and combine into a single SimpleDisplayable
        playerOverlay.add(new SimpleDisplayable(new Point(64, 8), ImageFactory.getBuilding(player, Hex.Building.VILLAGE)));
        playerOverlay.add(new SimpleDisplayable(new Point(112, 8), ImageFactory.getBuilding(player, Hex.Building.TEMPLE)));
        playerOverlay.add(new SimpleDisplayable(new Point(160, 8), ImageFactory.getBuilding(player, Hex.Building.TOWER)));

        playerOverlay.add(new StringDisplayable(new Point(64, 36), () -> "" + player.getVillagers()));
        playerOverlay.add(new StringDisplayable(new Point(112, 36), () -> "" + player.getTemples()));
        playerOverlay.add(new StringDisplayable(new Point(160, 36), () -> "" + player.getTowers()));

        playerOverlay.addClickable(Clickable.makeNullClickable(new Point(0, 0), new Dimension(224, 64)));

        return playerOverlay;
    }

    public static Overlay makeSettlementsOverlay(GameUberstate gameUberstate, Board board, Uberstate.OverlayManager overlayManager)
    {
        Overlay settlementsOverlay = new Overlay(new Point())
        {
            private Set<SettlementButton> settlementClickables = new LinkedHashSet<>();

            private Dimension buttonSize = new Dimension(128, 16);

            public void update()
            {
                // Todo: move this flag to the GameUberstate instead of the Board
                if(board.settlementsHaveChanged())
                {
                    super.removeAll(settlementClickables);
                    super.removeAllClickables(settlementClickables);

                    settlementClickables.clear();

                    int y = 21;

                    // Todo: Not thread-safe, currently working around it by using Concurrent structures in Board
                    for(Settlement settlement : board.getSettlements())
                    {
                        Point origin = new Point(0, y);

                        SettlementButton settlementButton = new SettlementButton(origin, settlement);

                        settlementClickables.add(settlementButton);

                        y += buttonSize.height;
                    }

                    super.addAll(settlementClickables);
                    super.addAllClickables(settlementClickables);

                    overlayManager.resetLeftOverlays();
                }
            }

            class SettlementButton extends Button
            {
                private Set<HexButton> hexes;

                public SettlementButton(Point origin, Settlement settlement)
                {
                    super(origin,
                            ImageFactory.makeLeftLabeledRect(buttonSize.width, buttonSize.height, settlement.getOwner().getColor1(), Color.GRAY, settlement.getOwner().getColor2(),
                                    "Settlement " + settlement.getSettlementID() + " Size: " + settlement.getSize()),
                            ImageFactory.makeLeftLabeledRect(buttonSize.width, buttonSize.height, Color.PINK, Color.GRAY, Color.BLACK,
                                    "Settlement " + settlement.getSettlementID() + " Size: " + settlement.getSize()),
                            () -> {});

                    this.hexes = new HashSet<>();
                    for(Hex hex : settlement.getHexes())
                    {
                        //System.out.println("Adding " + hex.getOrigin() + " to settlement " + settlement.getSettlementID() );
                        hexes.add(gameUberstate.getHexButton(hex.getOrigin()));
                    }
                }

                public void enter()
                {
                    super.enter();
                    hexes.forEach(Button::enter);
                }

                public void exit()
                {
                    super.exit();
                    hexes.forEach(Button::exit);
                }
            }
        };

        settlementsOverlay.add(new SimpleDisplayable(new Point(0, 0),
                ImageFactory.makeCenterLabeledRect(128, 21, Color.WHITE, Color.GRAY, Color.BLACK, "Settlements:")));

        settlementsOverlay.addClickable(Clickable.makeNullClickable(new Point(0, 0), new Dimension(128, 21)));

        return settlementsOverlay;
    }
}
