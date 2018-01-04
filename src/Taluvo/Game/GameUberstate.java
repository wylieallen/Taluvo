package Taluvo.Game;

import Taluvo.GUI.Camera;
import Taluvo.GUI.Clickables.Buttons.ButtonGroup;
import Taluvo.GUI.Clickables.Buttons.RadialButton;
import Taluvo.GUI.Clickables.Buttons.RadialButtonGroup;
import Taluvo.GUI.Clickables.Clickable;
import Taluvo.GUI.Clickables.Overlay;
import Taluvo.GUI.Displayables.CompositeDisplayable;
import Taluvo.GUI.Displayables.Displayable;
import Taluvo.GUI.Displayables.SimpleDisplayable;
import Taluvo.Game.GameModel.*;
import Taluvo.Game.Overlays.*;
import Taluvo.Util.ImageFactory;
import Taluvo.Game.Clickables.HexButton;
import Taluvo.GUI.Clickables.Buttons.Button;
import Taluvo.GUI.Uberstate;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameUberstate extends Uberstate
{
    private Game game;
    private MoveTabulator tabulator;

    private Map<Point, HexButton> buttonMap = new HashMap<>();
    private Set<HexButton> hexButtons = new HashSet<>();

    private TurnPhase activePhase;
    private BuildMode activeBuildAction;

    private Hex.Building activeBuilding = Hex.Building.VILLAGE;
    private Hex.Terrain activeTerrain = Hex.Terrain.GRASS;

    public GameUberstate(Point origin, Dimension size, Camera camera)
    {
        super(origin, size);

        addDisplays(hexButtons);

        activePhase = new TilePlacementPhase();
        activeBuildAction = new PlaceBuilding();

        game = new Game();
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
        RadialButton villagerButton = makeBuildingSelector(new Point(32, 0), Hex.Building.VILLAGE);
        RadialButton templeButton = makeBuildingSelector(new Point(0, 32), Hex.Building.TEMPLE);
        RadialButton towerButton = makeBuildingSelector(new Point(64, 32), Hex.Building.TOWER);

        RadialButton grassButton = makeTerrainSelector(new Point(0, 68), Hex.Terrain.GRASS);
        RadialButton jungleButton = makeTerrainSelector(new Point(64, 68), Hex.Terrain.JUNGLE);
        RadialButton lakeButton = makeTerrainSelector(new Point(0, 100), Hex.Terrain.LAKE);
        RadialButton rockyButton = makeTerrainSelector(new Point(64, 100), Hex.Terrain.ROCKY);

        RadialButtonGroup buildingSelectGroup = new RadialButtonGroup(new Point(0, 0),
                villagerButton, templeButton, towerButton, grassButton, jungleButton, lakeButton, rockyButton);

        Overlay buildingSelectOverlay = new Overlay(new Point());
        buildingSelectOverlay.add(buildingSelectGroup);
        buildingSelectOverlay.addClickable(buildingSelectGroup);

        addRightOverlay(buildingSelectOverlay);

        // Player GUI elements:

        Overlay playersOverlay = OverlayMaker.makePlayersPiecesOverlay(game.getPlayer1(), game.getPlayer2());
        addCenterOverlay(playersOverlay);

        // AI action button:

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

        Overlay aiButtonOverlay = new Overlay(new Point());
        aiButtonOverlay.add(aiGroup);
        aiButtonOverlay.addClickable(aiGroup);

        // Settlements GUI element:

        Overlay settlementOverlay = OverlayMaker.makeSettlementsOverlay(this, game.getBoard(), overlayManager);//new SettlementOverlay(this, game.getBoard(), overlayManager);
        addLeftOverlay(settlementOverlay);
        addLeftOverlay(aiButtonOverlay);

        // Initialize starting HexButtons:
        for(Hex hex : game.getNewHexes())
        {
            emplaceHexButton(new HexButton(hex, () -> {executeGameAction(hex.getOrigin());}));
        }
    }

    private RadialButton makeBuildingSelector(Point point, Hex.Building building)
    {
        return new RadialButton(point,
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, building.toString(), new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, building.toString(), new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, Color.GRAY, Color.GRAY, Color.BLACK, building.toString(), new Point(8, 18)),
                () -> {activeBuilding = building; activeBuildAction = new PlaceBuilding();});
    }

    private RadialButton makeTerrainSelector(Point point, Hex.Terrain terrain)
    {
        Color terrainColor = ImageFactory.getTerrainColor(terrain);
        return new RadialButton(point,
                ImageFactory.makeLabeledRect(64, 32, Color.WHITE, Color.GRAY, Color.BLACK, terrain.toString(), new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, terrainColor, Color.GRAY, Color.BLACK, terrain.toString(), new Point(8, 18)),
                ImageFactory.makeLabeledRect(64, 32, terrainColor, Color.GRAY, Color.BLACK, terrain.toString(), new Point(8, 18)),
                () -> {activeTerrain = terrain; activeBuildAction = new ExpandSettlement();});
    }

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
                emplaceHexButton(new HexButton(hex, () ->
                {
                    System.out.println("Legal? " + game.tilePlacementIsLegal(game.getTilePlacementAction(hex, game.getDeck().peek())));
                    executeGameAction(hex.getOrigin());
                }));
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
            tabulator.playNextTurn(game);
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
