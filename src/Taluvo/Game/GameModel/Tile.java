package Taluvo.Game.GameModel;

public class Tile
{
    private Hex.Terrain terrainA;
    private Hex.Terrain terrainB;
    private Orientation orientation;
    private int tileID;

    public Tile(Hex.Terrain terrainA, Hex.Terrain terrainB, int tileID, Orientation orientation)
    {
        this.tileID = tileID;
        this.orientation = orientation;
        this.terrainA = terrainA;
        this.terrainB = terrainB;
    }

    public Tile(Tile tile)
    {
        this.tileID = tile.getTileID();
        this.orientation = tile.getOrientation();
        this.terrainA = tile.getTerrainA();
        this.terrainB = tile.getTerrainB();
    }

    public void setOrientation(Orientation orientation) {this.orientation = orientation;}

    public int getIndexA() {return orientation.ordinal();}
    public int getIndexB() {return orientation.getPlus(1).ordinal();}

    public Orientation getOrientation() {return orientation;}
    public Orientation getOrientationPlus(int number)
    {
        return orientation.getPlus(number);
    }

    public Hex.Terrain getTerrainA() {return terrainA;}
    public Hex.Terrain getTerrainB() {return terrainB;}

    public int getTileID() {return tileID;}

    enum Orientation
    {
        NW, NE, E, SE, SW, W;

        public Orientation rotRight()
        {
            int index = ordinal() + 1;
            return values()[wrapAround(index)];
        }

        public Orientation rotLeft()
        {
            int index = ordinal() - 1;
            return values()[wrapAround(index)];
        }

        public Orientation getPlus(int number)
        {
            int index = ordinal();
            int increment = (number < 0 ? -1 : 1);
            for(int i = 0; i < Math.abs(number); i++)
            {
                index = wrapAround(index + increment);
            }
            return values()[index];
        }

        public int wrapAround(int index)
        {
            if (index < 0) index = values().length - 1;
            if (index >= values().length) index = 0;
            return index;
        }
    }
}
