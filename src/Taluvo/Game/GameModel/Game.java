package Taluvo.Game.GameModel;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class Game
{
    private Board board;
    private Deck deck;
    private Rules rules;

    private EndCondition endCondition;

    private Player activePlayer;

    private Player player1;
    private Player player2;

    public Game()
    {
        board = new Board();
        deck = new Deck();
        rules = new Rules();

        endCondition = EndCondition.ACTIVE;

        activePlayer = player1 = new Player("One", Color.BLACK, Color.WHITE);
        player2 = new Player("Two", Color.WHITE, Color.BLACK);
    }

    public void placeTile(Point point, Tile tile)
    {
        board.placeTile(point, tile);
    }

    public void placeBuilding(Point point, Hex.Building building)
    {
        board.placeBuilding(point, building, activePlayer);
        activePlayer.decrementBuilding(building);
        endTurn();
    }

    public void endTurn()
    {
        // Check for endgame
        if(activePlayer.declaresVictory())
        {
            endCondition = EndCondition.NO_BUILDINGS;
            return;
        }

        if(deck.getTileCount() >= 48)
        {
            endCondition = EndCondition.NO_TILES;
            return;
        }


        // Toggle player
        activePlayer = activePlayer == player1 ? player2 : player1;

        // if active player has no legal moves, endCondition = EndCondition.NO_MOVES;
    }

    public void forfeit()
    {
        endCondition = EndCondition.NO_MOVES;
    }

    public boolean tilePlacementIsLegal(TilePlacementAction action) { return rules.tilePlacementIsLegal(action.getTarget().getOrigin(), action.getTile()); }
    public boolean buildingPlacementIsLegal(Point point, Hex.Building building, Player player) { return rules.buildingPlacementIsLegal(point, building, player);}
    public boolean settlementExpansionIsLegal(Settlement.Expansion expansion) { return rules.settlementExpansionIsLegal(expansion);}

    public Board getBoard() {
        return board;
    }
    public List<Hex> getNewHexes() { return board.getNewHexes(); }
    public Collection<Hex> getHexes()
    {
        return board.getHexes();
    }

    public Deck getDeck() {return deck;}

    public Player getActivePlayer() {return activePlayer;}
    public Player getPlayer1() {return player1;}
    public Player getPlayer2() {return player2;}

    public EndCondition getEndCondition() { return endCondition; }


    public Settlement.Expansion getSettlementExpansion(Point point, Hex.Terrain terrain)
    {
        Settlement settlement = board.getSettlement(board.getHex(point));
        return board.getExpansion(settlement, terrain);
    }

    public void expandSettlement(Settlement.Expansion expansion)
    {
        for(Hex hex : expansion.getHexes())
        {
            board.placeBuilding(hex.getOrigin(), Hex.Building.VILLAGE, activePlayer);
        }

        for(int i = 0; i < expansion.getCost(); i++)
        {
            activePlayer.decrementBuilding(Hex.Building.VILLAGE);
        }

        endTurn();
    }

    // Action vending methods:
    public TilePlacementAction getTilePlacementAction(Hex target, Tile tile)
    {
        return new TilePlacementAction(target, tile);
    }

    public BuildingPlacementAction getBuildingPlacementAction(Hex target, Hex.Building building)
    {
        return new BuildingPlacementAction(target, building);
    }

    public SettlementExpansionAction getSettlementExpansionAction(Settlement.Expansion expansion)
    {
        return new SettlementExpansionAction(expansion);
    }

    public DeclareForfeitAction getDeclareForfeitAction()
    {
        return new DeclareForfeitAction();
    }

    // Actions:
    public interface Action { void execute();}

    public class TilePlacementAction implements Action
    {
        private Hex target;
        private Tile tile;
        private Tile.Orientation orientation;

        public TilePlacementAction(Hex target, Tile tile)
        {
            this.target = target;
            this.tile = tile;
            this.orientation = tile.getOrientation();
        }

        public void execute()
        {
            Tile deckTile = deck.getNextTile();
            deckTile.setOrientation(orientation);
            placeTile(target.getOrigin(), deckTile);
        }

        public Hex getTarget() {return target;}
        public Tile getTile() {return tile;}
    }

    public class BuildingPlacementAction implements Action
    {
        private Hex target;
        private Hex.Building building;

        public BuildingPlacementAction(Hex target, Hex.Building building)
        {
            this.target = target;
            this.building = building;
        }

        public void execute() { placeBuilding(target.getOrigin(), building);}

        public Hex getTarget() {return target;}
        public Hex.Building getBuilding() {return building;}
    }

    public class SettlementExpansionAction implements Action
    {
        private Settlement.Expansion expansion;

        public SettlementExpansionAction(Settlement.Expansion expansion)
        {
            this.expansion = expansion;
        }

        public void execute() { expandSettlement(expansion);}
    }

    public class DeclareForfeitAction implements Action
    {
        public void execute() {forfeit();}
    }

    private class Rules
    {
        // Legality is defined as anything that does not explicitly violate a rule.

        public boolean tilePlacementIsLegal(Point point, Tile tile) {

            // Initialize data:

            Point offsetA = Hex.neighborOffsets[tile.getIndexA()];
            Point offsetB = Hex.neighborOffsets[tile.getIndexB()];

            Point originA = new Point(point.x + offsetA.x, point.y + offsetA.y);
            Point originB = new Point(point.x + offsetB.x, point.y + offsetB.y);

            //System.out.println("Tile pts: " + point + ", " + originA + ", " + originB);

            Hex targetA = board.getHex(originA);
            Hex targetB = board.getHex(originB);
            Hex targetV = board.getHex(point);

            //System.out.println("Actual hex origins: " + targetV.getOrigin() + ", " + targetA.getOrigin() + ", " + targetB.getOrigin());

            // Test for illegality:

            return  endCondition == EndCondition.ACTIVE
                    && !targetHexTerrainNotVolcanoOrEmpty(targetV)
                    && !targetNonEmptyHexesShareTileID(targetA, targetB, targetV)
                    && (tile.getTileID() <= 0 || !targetHexesHaveOnlyEmptyNeighbors(targetA, targetB, targetV))
                    && !targetHexHasPermanentBuildings(targetA, targetB)
                    && !targetHexesHaveUnevenLevels(targetA, targetB, targetV)
                    && !nukeWillDestroySettlement(targetA, targetB);
        }

        private boolean targetHexTerrainNotVolcanoOrEmpty(Hex hex)
        {
            Hex.Terrain terrain = hex.getTerrain();
            return terrain != Hex.Terrain.VOLCANO && terrain != Hex.Terrain.EMPTY;
        }

        private boolean targetNonEmptyHexesShareTileID(Hex hex1, Hex hex2, Hex hex3)
        {
            return hex1.getTileID() == hex2.getTileID() && hex2.getTileID() == hex3.getTileID() && hex1.getTerrain() != Hex.Terrain.EMPTY;
        }

        private boolean targetHexesHaveOnlyEmptyNeighbors(Hex... hexes)
        {
            for(Hex hex : hexes)
            {
                for(Hex neighbor : board.getNeighbors(hex.getOrigin()))
                {
                    if(neighbor.getTerrain() != Hex.Terrain.EMPTY)
                    {
                        //System.out.println("nonempty neighbor found at " + neighbor.getOrigin());
                        return false;
                    }
                }
            }
            //System.out.println("no nonempty neighbors");
            return true;
        }

        private boolean targetHexHasPermanentBuildings(Hex... hexes)
        {
            for(Hex hex : hexes)
            {
                if(hex.getBuilding().permanent)
                {
                    return true;
                }
            }
            return false;
        }

        private boolean targetHexesHaveUnevenLevels(Hex hex1, Hex hex2, Hex hex3)
        {
            return !(hex1.getLevel() == hex2.getLevel() && hex2.getLevel() == hex3.getLevel());
        }

        private boolean nukeWillDestroySettlement(Hex hexA, Hex hexB)
        {
            Settlement settlementA = null;
            Settlement settlementB = null;

            for(Settlement settlement : board.getSettlements())
            {
                if(settlement.contains(hexA))
                {
                    settlementA = settlement;
                    if(settlementA.getSize() == 1) return true;
                }

                if(settlement.contains(hexB))
                {
                    settlementB = settlement;
                    if(settlementB.getSize() == 1) return true;
                }
            }

            if(settlementA != null && settlementB != null)
            {
                if(settlementA == settlementB && settlementA.getSize() == 2) return true;
            }

            return false;
        }


        public boolean buildingPlacementIsLegal(Point point, Hex.Building building, Player player)
        {
            Hex targetHex = board.getHex(point);

            return building != Hex.Building.NONE
                    && !playerBuildingsExhausted(player, building)
                    && targetHex.buildable()
                    && (building != Hex.Building.VILLAGE || targetHex.getLevel() == 1)
                    && (building != Hex.Building.TOWER || targetHex.getLevel() >= 3)
                    && (building != Hex.Building.TOWER || adjacentToTowerlessSettlement(point, player))
                    && (building != Hex.Building.TEMPLE || adjacentToTemplelessSettlementOfSize3(point, player));
        }

        private boolean playerBuildingsExhausted(Player player, Hex.Building building)
        {
            return player.getBuildingCount(building) <= 0;
        }

        private boolean adjacentToTowerlessSettlement(Point point, Player player)
        {
            for(Hex neighbor : board.getNeighbors(point))
            {
                if(neighbor.getOwner() == player)
                {
                    Settlement settlement = board.getSettlement(neighbor);
                    if(!settlement.hasTower())
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        private boolean adjacentToTemplelessSettlementOfSize3(Point point, Player player)
        {
            for(Hex neighbor : board.getNeighbors(point))
            {
                if(neighbor.getOwner() == player)
                {
                    Settlement settlement = board.getSettlement(neighbor);
                    if(!settlement.hasTemple() && settlement.getSize() >= 3)
                    {
                        return true;
                    }
                }
            }

            return false;
        }

        public boolean settlementExpansionIsLegal(Settlement.Expansion expansion)
        {

            return expansion.getOwner() == activePlayer
                    && expansion.getCost() <= activePlayer.getBuildingCount(Hex.Building.VILLAGE)
                    && expansion.getCost() > 0
                    && expansion.getTerrain().buildable;
        }
    }

    public enum EndCondition
    {
        ACTIVE, NO_BUILDINGS, NO_TILES, NO_MOVES;
    }
}
