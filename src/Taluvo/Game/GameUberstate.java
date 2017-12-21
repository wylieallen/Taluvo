package Taluvo.Game;

import Taluvo.GUI.Clickables.Buttons.RadialButton;
import Taluvo.GUI.Clickables.Buttons.RadialButtonGroup;
import Taluvo.Game.Displayables.*;
import Taluvo.Game.GameModel.*;
import Taluvo.Util.ImageFactory;
import Taluvo.Game.Clickables.HexButton;
import Taluvo.GUI.Clickables.Buttons.Button;
import Taluvo.GUI.Displayables.SimpleDisplayable;
import Taluvo.GUI.Uberstate;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameUberstate extends Uberstate
{
    //private Board board;

    private Game game;
    private MoveTabulator tabulator;

    private Map<Point, HexButton> buttonMap = new HashMap<>();

    private Set<HexButton> hexButtons = new HashSet<>();

    private TurnPhase activePhase;
    private BuildMode activeBuildAction;

    private Hex.Building activeBuilding = Hex.Building.VILLAGE;
    private Hex.Terrain activeTerrain = Hex.Terrain.GRASS;

    public GameUberstate(Point origin, Dimension size)
    {
        super(origin, size);

        super.addDisplays(hexButtons);

        super.addUnderlay(new SimpleDisplayable(new Point(0, 0),  ImageFactory.makeFilledRect(1280, 720, Color.BLUE)));

        activePhase = new TilePlacementPhase();
        activeBuildAction = new PlaceBuilding();

        //board = new Board();
        game = new Game();
        tabulator = new MoveTabulator();
        tabulator.analyze(game);

        // Deck GUI elements:
        super.addOverlay(new DeckDisplayable(new Point(1136, 16), game.getDeck()));

        emplaceButton(new Button(new Point(1136, 144),
                ImageFactory.makeBorderedRect(64, 64, Color.WHITE, Color.BLACK),
                ImageFactory.makeBorderedRect(64, 64, Color.RED, Color.BLACK),
                () -> game.getDeck().rotLeft()));

        emplaceButton(new Button(new Point(1200, 144),
                ImageFactory.makeBorderedRect(64, 64, Color.WHITE, Color.BLACK),
                ImageFactory.makeBorderedRect(64, 64, Color.RED, Color.BLACK),
                () -> game.getDeck().rotRight()));

        // Hex Detail GUI element:
        super.addOverlay(new HexDetailDisplayable(new Point(1120, 224), this));

        // Turn Status GUI element:
        super.addOverlay(new TurnStatusDisplayable(new Point(1120, 376), this));

        // Building selection radial buttons:
        // Todo: get buttons from a factory or something to tidy these constructors up
        RadialButton villagerButton = new RadialButton(new Point(0, 0),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.BLACK, Color.BLACK, "VILLAGE", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.RED, Color.BLACK, Color.BLACK, "VILLAGE", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.ORANGE, Color.BLACK, Color.BLACK, "VILLAGE", new Point(8, 18)),
                () -> {activeBuilding = Hex.Building.VILLAGE; activeBuildAction = new PlaceBuilding();});

        RadialButton templeButton = new RadialButton(new Point(64, 0),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.BLACK, Color.BLACK, "TEMPLE", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.RED, Color.BLACK, Color.BLACK, "TEMPLE", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.ORANGE, Color.BLACK, Color.BLACK, "TEMPLE", new Point(8, 18)),
                () -> {activeBuilding = Hex.Building.TEMPLE; activeBuildAction = new PlaceBuilding();});

        RadialButton towerButton = new RadialButton(new Point(128, 0),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.BLACK, Color.BLACK, "TOWER", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.RED, Color.BLACK, Color.BLACK, "TOWER", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.ORANGE, Color.BLACK, Color.BLACK, "TOWER", new Point(8, 18)),
                () -> {activeBuilding = Hex.Building.TOWER; activeBuildAction = new PlaceBuilding();});

        RadialButton grassButton = new RadialButton(new Point(32, 32),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.BLACK, Color.BLACK, "GRASS", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.YELLOW, Color.BLACK, Color.BLACK, "GRASS", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.YELLOW, Color.BLACK, Color.BLACK, "GRASS", new Point(8, 18)),
                () -> {activeTerrain = Hex.Terrain.GRASS; activeBuildAction = new ExpandSettlement();});

        RadialButton jungleButton = new RadialButton(new Point(96, 32),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.BLACK, Color.BLACK, "JUNGLE", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GREEN, Color.BLACK, Color.BLACK, "JUNGLE", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GREEN, Color.BLACK, Color.BLACK, "JUNGLE", new Point(8, 18)),
                () -> {activeTerrain = Hex.Terrain.JUNGLE; activeBuildAction = new ExpandSettlement();});

        RadialButton lakeButton = new RadialButton(new Point(32, 64),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.BLACK, Color.BLACK, "LAKE", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.CYAN, Color.BLACK, Color.BLACK, "LAKE", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.CYAN, Color.BLACK, Color.BLACK, "LAKE", new Point(8, 18)),
                () -> {activeTerrain = Hex.Terrain.LAKE; activeBuildAction = new ExpandSettlement();});

        RadialButton rockyButton = new RadialButton(new Point(96, 64),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.BLACK, Color.BLACK, "ROCKY", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.BLACK, Color.BLACK, "ROCKY", new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.BLACK, Color.BLACK, "ROCKY", new Point(8, 18)),
                () -> {activeTerrain = Hex.Terrain.ROCKY; activeBuildAction = new ExpandSettlement();});

        RadialButtonGroup buildingSelectGroup = new RadialButtonGroup(new Point(1088, 480),
                new Dimension(192, 32),
                villagerButton, templeButton, towerButton, grassButton, jungleButton, lakeButton, rockyButton);

        addClickable(buildingSelectGroup);
        addOverlay(buildingSelectGroup);

        // Player GUI elements:
        super.addOverlay(new PlayerPiecesDisplayable(new Point(384, 16), Player.ONE));
        super.addOverlay(new PlayerPiecesDisplayable(new Point(672, 16), Player.TWO));

        // Settlements GUI element:

        super.addOverlay(new SettlementsDisplayable(new Point(16, 16), game.getBoard()));

        // AI action button:

        Button aiButton = new Button(new Point(16, 680),
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.BLACK, Color.BLACK, "AI MOVE", new Point(6, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.BLACK, Color.BLACK, "AI MOVE", new Point(6, 18)),
                () -> {tabulator.analyze(game); tabulator.playNextTurn(game);});

        emplaceButton(aiButton);

                // Initialize starting HexButtons:
        for(Hex hex : game.getNewHexes())
        {
            emplaceHexButton(new HexButton(hex, () -> {executeGameAction(hex.getOrigin());}));
        }
    }

    public void executeGameAction(Point point)
    {
        activePhase.performAction(point);
    }

    @Override
    public void update()
    {
        super.update();

        for(Hex hex : game.getNewHexes())
        {
            if(!buttonMap.containsKey(hex.getOrigin()))
            {
                emplaceHexButton(new HexButton(hex, () -> {executeGameAction(hex.getOrigin());}));
            }
            else
            {
                HexButton hexButton = buttonMap.get(hex.getOrigin());
                hexButton.changeHex(hex);
            }
        }
    }

    private void emplaceHexButton(HexButton button)
    {
        super.addClickable(button);
        hexButtons.add(button);
        buttonMap.put(button.getOrigin(), button);
    }

    private void emplaceButton(Button button)
    {
        super.addClickable(button);
        super.addOverlay(button);
    }

    public Hex.Building getActiveBuilding() { return activeBuilding; }
    public TurnPhase getActivePhase() { return activePhase; }
    public Player getActivePlayer() { return game.getActivePlayer(); }

    public Hex.Terrain getActiveTerrain() {
        return activeTerrain;
    }

    public Game.EndCondition getEndCondition() { return game.getEndCondition(); }

    public interface TurnPhase
    {
        void performAction(Point point);
    }

    private class TilePlacementPhase implements TurnPhase
    {
        public void performAction(Point point)
        {
            if(game.tilePlacementIsLegal(game.getTilePlacementAction(game.getBoard().getHex(point), game.getDeck().peek())))
            {
                game.placeTile(point, game.getDeck().getNextTile());
                activePhase = new BuildingPlacementPhase();
            }
        }

        public String toString() {return "TILE";}
    }

    private class BuildingPlacementPhase implements TurnPhase
    {
        public void performAction(Point point)
        {
            activeBuildAction.performAction(point);
        }

        public String toString() {return "BUILD";}
    }

    public interface BuildMode
    {
        void performAction(Point point);
    }

    private class PlaceBuilding implements BuildMode
    {
        public void performAction(Point point)
        {
            if(game.buildingPlacementIsLegal(point, activeBuilding, game.getActivePlayer()))
            {
                game.placeBuilding(point, activeBuilding);
                activePhase = new TilePlacementPhase();
            }
        }
    }

    private class ExpandSettlement implements BuildMode
    {
        public void performAction(Point point)
        {
            Settlement.Expansion expansion = game.getSettlementExpansion(point, activeTerrain);
            if(game.settlementExpansionIsLegal(expansion))
            {
                game.expandSettlement(expansion);
                activePhase = new TilePlacementPhase();
            }
        }
    }

}
