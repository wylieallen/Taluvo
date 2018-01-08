package Taluvo.Game.GameModel;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class Board
{
    private Map<Point, Hex> hexMap;

    private List<Hex> newHexes;

    private Set<Settlement> settlements;

    private boolean settlementsChanged;

    public Board()
    {
        hexMap = new HashMap<>();
        newHexes = new CopyOnWriteArrayList<>();
        settlements = new ConcurrentSkipListSet<>();

        settlementsChanged = false;

        Hex hex = new Hex(new Point(0, 0), 0, Hex.Terrain.EMPTY, -1);
        emplaceHex(hex);
    }

    public void recalculateSettlements()
    {
        settlements.clear();

        int settlementCount = 0;

        Collection<Hex> hexes = hexMap.values();

        hexes.forEach(hex -> hex.setSettlement(Settlement.getNullSettlement()));

        for(Hex hex : hexes)
        {
            if(hex.getBuilding().occupiesHex && hex.getSettlementID() == -1)
            {
                Settlement settlement = new Settlement(hex, settlementCount++);
                settlements.add(settlement);
                settlement.incorporateAdjacentHexes(this);
            }
        }

        settlementsChanged = true;
    }

    public boolean settlementsHaveChanged()
    {
        if(settlementsChanged)
        {
            settlementsChanged = false;
            return true;
        }

        return false;
    }

    public void placeBuilding(Point point, Hex.Building building, Player player)
    {
        hexMap.get(point).setBuilding(building, player);

        // Recalculate Settlements:

        recalculateSettlements();
    }

    public void placeTile(Point point, Tile tile)
    {
        // Expand board if necessary:

        expandBoard(point, 2);

        // Place Volcano:

        Hex prevHex = hexMap.get(point);

        //Tile tile = deck.getNextTile();

        int nextLevel = prevHex.getLevel() + 1;

        emplaceHex(new Hex(point, nextLevel, Hex.Terrain.VOLCANO, tile.getTileID()));

        // Place A and B:

        int orientationA = tile.getOrientation().ordinal();
        int orientationB = tile.getOrientationPlus(1).ordinal();

        Point offsetA = Hex.neighborOffsets[orientationA];
        Point offsetB = Hex.neighborOffsets[orientationB];

        Point actualA = new Point(point.x + offsetA.x, point.y + offsetA.y);
        Point actualB = new Point(point.x + offsetB.x, point.y + offsetB.y);

        expandBoard(actualA, 2);
        expandBoard(actualB, 2);

        emplaceHex(new Hex(actualA, nextLevel, tile.getTerrainA(), tile.getTileID()));
        emplaceHex(new Hex(actualB, nextLevel, tile.getTerrainB(), tile.getTileID()));

        // Recalculate Settlements:

        recalculateSettlements();
    }

    private void expandBoard(Point point, int depth)
    {
        for(Point offset : Hex.neighborOffsets)
        {
            Point neighborPt = new Point(point.x + offset.x, point.y + offset.y);

            if(!hexMap.containsKey(neighborPt))
            {
                Hex newHex = new Hex(neighborPt, 0, Hex.Terrain.EMPTY, -1);
                emplaceHex(newHex);
            }

            if(depth > 1)
            {
                expandBoard(neighborPt, depth - 1);
            }
        }
    }

    private void emplaceHex(Hex hex)
    {
        hexMap.put(hex.getOrigin(), hex);
        newHexes.add(hex);
    }

    public Collection<Hex> getHexes()
    {
        return hexMap.values();
    }

    public Collection<Settlement> getSettlements()
    {
        return settlements;
    }

    public Settlement getSettlement(Hex hex)
    {
        for(Settlement settlement : settlements)
        {
            if(settlement.contains(hex))
            {
                return settlement;
            }
        }

        return Settlement.getNullSettlement();
    }

    public Hex getHex(Point point) { return hexMap.getOrDefault(point, Hex.getNullHex()); }

    public Set<Hex> getNeighbors(Point point)
    {
        //System.out.println("Getting neighbors of " + point);
        Set<Hex> neighbors = new HashSet<>();
        for(Point offset : Hex.neighborOffsets)
        {
            Point neighborPt = new Point(point.x + offset.x, point.y + offset.y);
            neighbors.add(getHex(neighborPt));
            //System.out.println("Neighbor found at: " + neighborPt);
        }
        return neighbors;
    }

    public Hex getNeighbor(Hex hex, int neighborIndex)
    {
        //System.out.println("Getting neighbor " + neighborIndex + " of " + hex.getOrigin());
        Point targetPt = hex.getOrigin();
        Point offsetPt = Hex.neighborOffsets[neighborIndex];
        Point neighborPt = new Point(targetPt.x + offsetPt.x, targetPt.y + offsetPt.y);
        //System.out.println("Neighborpt = " + neighborPt);
        return hexMap.get(neighborPt);
    }

    public List<Hex> getNewHexes()
    {
        List<Hex> retHexes = newHexes;
        newHexes = new ArrayList<>();
        return retHexes;
    }

    public Settlement.Expansion getExpansion(Settlement settlement, Hex.Terrain terrain)
    {
        Set<Hex> expansionHexes = new HashSet<>();

        for(Hex hex : settlement.getHexes())
        {
            for(Hex neighbor : getNeighbors(hex.getOrigin()))
            {
                if(neighbor.buildable() && neighbor.getTerrain() == terrain)
                {
                    expansionHexes.add(neighbor);
                }
            }
        }

        return new Settlement.Expansion(settlement, terrain, expansionHexes);
    }
}
