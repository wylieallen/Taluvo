package Taluvo.Game.GameModel;

import java.util.Random;

public class Deck
{
    private Random rand = new Random();

    private int tileCount = 0;

    private Tile nextTile;

    private Tile.Orientation orientation;

    public Deck()
    {
        orientation = Tile.Orientation.NE;
        Hex.Terrain nextA = Hex.Terrain.values()[rand.nextInt(4) + 2];
        Hex.Terrain nextB = Hex.Terrain.values()[rand.nextInt(4) + 2];
        nextTile = new Tile(nextA, nextB, tileCount, orientation);
    }

    public Tile getNextTile()
    {
        Tile ret = nextTile;
        ++tileCount;
        Hex.Terrain nextA = Hex.Terrain.values()[rand.nextInt(4) + 2];
        Hex.Terrain nextB = Hex.Terrain.values()[rand.nextInt(4) + 2];
        nextTile = new Tile(nextA, nextB, tileCount, orientation);
        return ret;
    }

    public Tile peek()
    {
        return nextTile;
    }

    public void rotLeft()
    {
        orientation = orientation.rotLeft();
        nextTile.setOrientation(orientation);
    }

    public void rotRight()
    {
        orientation = orientation.rotRight();
        nextTile.setOrientation(orientation);
    }

    public int getTileCount()
    {
        return tileCount;
    }
}
