package Taluvo.Game.GameModel;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Settlement
{
    private Set<Hex> hexes;
    private Player owner;

    private int settlementID;

    private boolean hasTemple;
    private boolean hasTower;

    public Settlement(Hex initialHex, int settlementID)
    {
        hexes = new HashSet<>();
        hexes.add(initialHex);

        this.settlementID = settlementID;

        initialHex.setSettlement(this);

        owner = initialHex.getOwner();

        hasTemple = initialHex.getBuilding() == Hex.Building.TEMPLE;
        hasTower = initialHex.getBuilding() == Hex.Building.TOWER;
    }

    public boolean hasTemple() {return hasTemple;}
    public boolean hasTower() {return hasTower;}
    public boolean contains(Hex hex) { return hexes.contains(hex); }

    public Player getOwner() {return owner;}
    public int getSettlementID() {return settlementID;}
    public int getSize() {return hexes.size();}
    public Set<Hex> getHexes() {return hexes;}

    public void incorporateAdjacentHexes(Board board)
    {
        Set<Hex> newHexes = new HashSet<>(hexes);

        while(!newHexes.isEmpty())
        {
            Set<Hex> newNewHexes = new HashSet<>();

            for(Hex hex : newHexes)
            {
                for(Hex neighbor : board.getNeighbors(hex.getOrigin()))
                {
                    if(neighbor.getOwner() == this.owner && neighbor.getSettlementID() != settlementID)
                    {
                        neighbor.setSettlement(this);
                        newNewHexes.add(neighbor);
                        hexes.add(neighbor);
                        if(neighbor.getBuilding() == Hex.Building.TOWER) hasTower = true;
                        if(neighbor.getBuilding() == Hex.Building.TEMPLE) hasTemple = true;
                    }
                }
            }

            newHexes = newNewHexes;
        }

    }

    private static final Settlement nullSettlement = new Settlement(new Hex(new Point(-1, -1), 0, Hex.Terrain.EMPTY, -1), -1);

    public static Settlement getNullSettlement() { return nullSettlement; }

    public static class Expansion
    {
        private Settlement settlement;
        private Hex.Terrain terrain;
        private Set<Hex> hexes;
        private int cost;

        public Expansion(Settlement settlement, Hex.Terrain terrain, Set<Hex> hexes)
        {
            this.settlement = settlement;
            this.terrain = terrain;
            this.hexes = new HashSet<>(hexes);
            this.cost = 0;

            for(Hex hex : hexes)
            {
                this.cost += hex.getLevel();
            }
        }

        public Set<Hex> getHexes() {return hexes;}
        public int getCost() {return cost;}
        public Player getOwner() {return settlement.getOwner();}
        public Hex.Terrain getTerrain() {return terrain;}
    }
}
