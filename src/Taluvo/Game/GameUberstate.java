package Taluvo.Game;

import Taluvo.GUI.Camera;
import Taluvo.GUI.Clickables.Buttons.ButtonGroup;
import Taluvo.GUI.Clickables.Buttons.RadialButton;
import Taluvo.GUI.Clickables.Buttons.RadialButtonGroup;
import Taluvo.GUI.Clickables.Overlay;
import Taluvo.Game.GameModel.*;
import Taluvo.Game.Overlays.*;
import Taluvo.Game.PlayerControllers.AbstractPlayerController;
import Taluvo.Game.PlayerControllers.PlayerController;
import Taluvo.Util.AbstractFunction;
import Taluvo.Util.ImageFactory;
import Taluvo.Game.Clickables.HexButton;
import Taluvo.GUI.Clickables.Buttons.Button;
import Taluvo.GUI.Uberstate;

import java.awt.*;
import java.util.*;

public class GameUberstate extends Uberstate
{
    private Game game;
    private MoveTabulator tabulator;

    private Map<Point, HexButton> buttonMap = new HashMap<>();
    private Set<HexButton> hexButtons = new HashSet<>();

    private PlayerController activeController;

    private TurnPhase activePhase;
    private BuildMode activeBuildAction;

    private Hex.Building activeBuilding = Hex.Building.VILLAGE;
    private Hex.Terrain activeTerrain = Hex.Terrain.GRASS;

    private Map<Player, PlayerController> controllerMap = new HashMap<>();

    private AbstractFunction repainter;

    private boolean locked = true;

    private ButtonGroup postgameButtons;

    public GameUberstate(Point origin, Dimension size, Camera camera,
                         AbstractFunction repainter, AbstractFunction rematcher, AbstractFunction resetter,
                         PlayerController... playerControllers)
    {
        super(origin, size);

        this.repainter = repainter;

        addDisplays(hexButtons);

        activePhase = new TilePlacementPhase();
        activeBuildAction = new PlaceBuilding();

        Player[] players = new Player[playerControllers.length];
        for(int i = 0; i < players.length; i++)
        {
            players[i] = playerControllers[i].makeNewPlayer();
        }

        game = new Game(players);
        tabulator = new MoveTabulator();

        // Deck GUI elements:
        Overlay deckOverlay = OverlayMaker.makeDeckOverlay(game.getDeck());
        addRightOverlay(deckOverlay);

        // Hex Detail GUI element:
        Overlay hexDetailOverlay = OverlayMaker.makeHexDetailOverlay(this);
        addRightOverlay(hexDetailOverlay);

        // Turn Status GUI element:
        Overlay turnStatusOverlay = OverlayMaker.makeTurnStatusOverlay(this);
        addRightOverlay(turnStatusOverlay);

        // Building selection radial buttons:
        RadialButton villagerButton = makeBuildingSelector(new Point(33, 0), Hex.Building.VILLAGE);
        RadialButton templeButton = makeBuildingSelector(new Point(0, 34), Hex.Building.TEMPLE);
        RadialButton towerButton = makeBuildingSelector(new Point(66, 34), Hex.Building.TOWER);

        RadialButton grassButton = makeTerrainSelector(new Point(0, 76), Hex.Terrain.GRASS);
        RadialButton jungleButton = makeTerrainSelector(new Point(66, 76), Hex.Terrain.JUNGLE);
        RadialButton lakeButton = makeTerrainSelector(new Point(0, 110), Hex.Terrain.LAKE);
        RadialButton rockyButton = makeTerrainSelector(new Point(66, 110), Hex.Terrain.ROCKY);

        RadialButtonGroup buildingSelectGroup = new RadialButtonGroup(new Point(0, 0),
                villagerButton, templeButton, towerButton, grassButton, jungleButton, lakeButton, rockyButton);

        addRightOverlay(buildingSelectGroup);

        // Player GUI elements:

        int playerMidpoint = players.length / 2;

        Player[] playersNorth = Arrays.copyOf(players, playerMidpoint);
        Player[] playersSouth = Arrays.copyOfRange(players, playerMidpoint, players.length);

        Overlay playersOverlay = OverlayMaker.makePlayersPiecesOverlay(playersNorth);
        addCenterOverlay(playersOverlay);

        Overlay otherPlayersOverlay = OverlayMaker.makePlayersPiecesOverlay(playersSouth);
        addCenterOverlay(otherPlayersOverlay);

        // Endgame buttons:

        Button rematchButton = Button.makeLabeledButton(new Point(0, 0), new Dimension(96, 32), "REMATCH", rematcher);
        Button mainMenuButton = Button.makeLabeledButton(new Point(0, 36), new Dimension(96, 32), "MAIN MENU", resetter);

        postgameButtons = new ButtonGroup(new Point(), rematchButton, mainMenuButton);

        // AI action buttons:

        Button aiButton = Button.makeLabeledButton(new Point(0, 0), new Dimension(64, 32), "AI MOVE", () -> tabulator.playNextTurn(game));

        Button resolveButton = Button.makeLabeledButton(new Point(80, 0), new Dimension(64, 32), "RESOLVE",
                () ->
                {
                    resolveGame();

                    int minX = 999999, maxX = -999999, minY = 999999, maxY = -999999;
                    for(HexButton hexButton : hexButtons)
                    {
                        Point hexPt = hexButton.getOrigin();

                        if(hexPt.x < minX)
                        {
                            minX = hexPt.x;
                        }
                        if(hexPt.x + 40 > maxX)
                        {
                            maxX = hexPt.x + 40;
                        }
                        if(hexPt.y < minY)
                        {
                            minY = hexPt.y ;
                        }
                        if(hexPt.y + 40> maxY)
                        {
                            maxY = hexPt.y + 40;
                        }
                    }

                    camera.centerOn(getSize(), new Point((maxX + minX)/2, (maxY + minY)/2));

                    // This Displayable shows the Board's bounding box
                    //addUnderlay(new SimpleDisplayable(new Point(minX, minY), ImageFactory.makeBorderedRect(maxX - minX, maxY - minY, Color.WHITE, Color.BLACK)));

                });

        ButtonGroup aiGroup = new ButtonGroup(new Point(0, 0), aiButton, resolveButton);

        // Settlements GUI element:

        Overlay settlementOverlay = OverlayMaker.makeSettlementsOverlay(this, game.getBoard(), overlayManager);//new SettlementOverlay(this, game.getBoard(), overlayManager);
        addLeftOverlay(settlementOverlay);

        //addLeftOverlay(aiGroup);

        // Initialize starting HexButtons:
        for(Hex hex : game.getNewHexes())
        {
            emplaceHexButton(new HexButton(hex, () -> {if(!locked) { executeGameAction(hex.getOrigin()); }}));
        }

        // Initialize controllers:
        for(int i = 0; i < playerControllers.length; i++)
        {
            controllerMap.put(players[i], playerControllers[i]);
        }

        activeController = controllerMap.get(game.getActivePlayer());
        activeController.activate(this);
    }

    private RadialButton makeBuildingSelector(Point point, Hex.Building building)
    {
        return new RadialButton(point,
                ImageFactory.makeCenterLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, building.toString()),
                ImageFactory.makeCenterLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, building.toString()),
                ImageFactory.makeCenterLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, building.toString()),
                () -> {activeBuilding = building; activeBuildAction = new PlaceBuilding();});
    }

    private RadialButton makeTerrainSelector(Point point, Hex.Terrain terrain)
    {
        Color terrainColor = ImageFactory.getTerrainColor(terrain);
        return new RadialButton(point,
                ImageFactory.makeCenterLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, terrain.toString()),
                ImageFactory.makeCenterLabeledRect(64, 32, terrainColor, Color.GRAY, Color.BLACK, terrain.toString()),
                ImageFactory.makeCenterLabeledRect(64, 32, terrainColor, Color.GRAY, Color.BLACK, terrain.toString()),
                () -> {activeTerrain = terrain; activeBuildAction = new ExpandSettlement();});
    }

    // Is locking necessary? ...
    public void lockGameInput() {locked = true;}
    public void unlockGameInput() {locked = false;}
    public boolean isLocked() {return locked;}

    public void executeGameAction(Point point)
    {
        activePhase.performAction(point);
    }

    @Override
    public void update()
    {
        for(Hex hex : game.getNewHexes())
        {
            if(!buttonMap.containsKey(hex.getOrigin()))
            {
                emplaceHexButton(new HexButton(hex, () -> {if(!locked) { executeGameAction(hex.getOrigin()); }}));
            }
            else
            {
                HexButton hexButton = buttonMap.get(hex.getOrigin());
                hexButton.changeHex(hex);
            }
        }

        super.update();
    }

    public void resolveGame()
    {
        long gameStart = System.nanoTime();
        int turnCount = 0;
        while(game.getEndCondition() == Game.EndCondition.ACTIVE)
        {
            //long startTime = System.nanoTime();
            playNextTurn();
            ++turnCount;
            //long endTime = System.nanoTime();
            //System.out.println("Time elapsed during turn: " + ((double) (endTime - startTime)) / 1000000 + " ms");
        }
        long gameTotal = System.nanoTime() - gameStart;
        System.out.println("Total time elapsed: " + ((double) gameTotal) / 1000000 + " ms, " + turnCount + " turns taken, avg turn time: " + ((double) gameTotal) / turnCount / 1000000 + " ms");

        update();
    }

    private void emplaceHexButton(HexButton button)
    {
        super.addClickable(button);
        hexButtons.add(button);
        buttonMap.put(button.getOrigin(), button);
    }

    public void repaint()
    {
        repainter.execute();
    }

    //private void emplaceButton(Button button)
    //{
    //    super.addFixedClickable(button);
    //    super.addOverlay(button, true);
    //}

    public HexButton getHexButton(Point point)
    {
        return buttonMap.getOrDefault(point, HexButton.getNullButton());
    }

    public Hex.Building getActiveBuilding() { return activeBuilding; }
    public TurnPhase getActivePhase() { return activePhase; }
    public Player getActivePlayer() { return game.getActivePlayer(); }
    public Hex.Terrain getActiveTerrain() {
        return activeTerrain;
    }

    public Game.EndCondition getEndCondition() { return game.getEndCondition(); }

    public MoveTabulator getTabulator() { return tabulator; }

    public void playNextTurn()
    {
        tabulator.playNextTurn(game);
        endTurn();
    }

    public void endTurn()
    {
        if(game.getEndCondition() == Game.EndCondition.ACTIVE)
        {
            activeController.deactivate(this);
            activeController = controllerMap.get(game.getActivePlayer());
            activeController.activate(this);
        }
        else
        {
            super.addLeftOverlay(postgameButtons);
            super.overlayManager.resetLeftOverlays();
        }
    }

    // Turn Phases:
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
            endTurn();
        }

        public String toString() {return "BUILD";}
    }


    // Build Action modes:
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
