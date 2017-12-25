package Taluvo.Game.GameModel;

import java.awt.*;

public class Hex
{
    public static final Point[] neighborOffsets = {
            new Point(-20, -30),
            new Point(20, -30),
            new Point(40, 0),
            new Point(20, 30),
            new Point(-20, 30),
            new Point(-40, 0)
    };

    private Point origin;
    private int level;
    private Terrain terrain;
    private int tileID;
    private Settlement settlement;
    private Building building;
    private Player owner;

    public Hex(Point origin, int level, Terrain terrain, int tileID)
    {
        this.origin = origin;
        this.level = level;
        this.terrain = terrain;
        this.tileID = tileID;
        this.settlement = Settlement.getNullSettlement();
        this.building = Building.NONE;
        this.owner = Player.NONE;
    }

    public Point getOrigin()
    {
        return origin;
    }
    public int getLevel()
    {
        return level;
    }
    public Terrain getTerrain()
    {
        return terrain;
    }
    public int getTileID() { return tileID; }
    public int getSettlementID() { return settlement.getSettlementID(); }
    public Settlement getSettlement() {return settlement;}
    public Building getBuilding() { return building; }
    public Player getOwner() { return owner; }

    public static Hex getNullHex()
    {
        return new Hex(new Point(0, 0), 0, Terrain.EMPTY, -1);
    }

    public void setBuilding(Building building, Player owner)
    {
        this.building = building;
        this.owner = owner;
    }

    //public void setSettlementID(int settlementID) { this.settlementID = settlementID; }

    public void setSettlement(Settlement settlement) { this.settlement = settlement; }

    public void translate(int dx, int dy)
    {
        origin.translate(dx, dy);
    }

    public void setOrigin(Point origin)
    {
        this.origin = origin;
    }

    public boolean buildable() { return terrain.buildable && !building.occupiesHex; }

    public enum Building
    {
        VILLAGE(true, false), TOWER(true, true), TEMPLE(true, true), NONE(false, false);

        public final boolean occupiesHex;
        public final boolean permanent;

        Building(boolean occupiesHex, boolean permanent)
        {
            this.occupiesHex = occupiesHex;
            this.permanent = permanent;
        }
    }

    public enum Terrain
    {
        EMPTY(false), VOLCANO(false), ROCKY(true), LAKE(true), JUNGLE(true), GRASS(true);

        public final boolean buildable;

        Terrain(boolean buildable)
        {
            this.buildable = buildable;
        }
    }
}
