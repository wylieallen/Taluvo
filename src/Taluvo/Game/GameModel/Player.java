package Taluvo.Game.GameModel;

import Taluvo.Util.AbstractFunction;
import Taluvo.Util.TypedAbstractFunction;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Player
{
    private static final Player nullPlayer = new Player("None", Color.LIGHT_GRAY, Color.DARK_GRAY);

    public static Player getNullPlayer() {return nullPlayer;}

    private String name;

    public final Color color1;
    public final Color color2;

    private int villagers = 20;
    private int temples = 3;
    private int towers = 2;

    private int score = 0;

    private boolean winner = false;
    private boolean loser = false;

    public Player(String name, Color color1, Color color2)
    {
        this.name = name;
        this.color1 = color1;
        this.color2 = color2;
    }

    private Map<Hex.Building, AbstractFunction> buildingDecrementers;
    {
        buildingDecrementers = new HashMap<>();
        buildingDecrementers.put(Hex.Building.VILLAGE, () -> --villagers);
        buildingDecrementers.put(Hex.Building.TEMPLE, () -> --temples);
        buildingDecrementers.put(Hex.Building.TOWER, () -> --towers);
    }

    private Map<Hex.Building, TypedAbstractFunction<Integer>> buildingAccessors;
    {
        buildingAccessors = new HashMap<>();
        buildingAccessors.put(Hex.Building.VILLAGE, this::getVillagers);
        buildingAccessors.put(Hex.Building.TEMPLE, this::getTemples);
        buildingAccessors.put(Hex.Building.TOWER, this::getTowers);
    }

    public boolean declaresVictory()
    {
        return (villagers + temples == 0 || villagers + towers == 0 || temples + towers == 0);
    }

    public void declareWinner() {winner = true;}
    public boolean isWinner() {return winner;}
    public void declareLoser() {loser = true;}
    public boolean isLoser() {return loser;}

    public int getBuildingCount(Hex.Building building) { return buildingAccessors.get(building).execute(); }

    public void decrementBuilding(Hex.Building building)
    {
        buildingDecrementers.get(building).execute();
        score += building.points;
    }

    public int getVillagers() { return villagers; }
    public int getTemples() { return temples; }
    public int getTowers() { return towers; }

    public int getScore() { return score; }

    public Color getColor1() { return color1; }
    public Color getColor2() { return color2; }

    public String getName() { return name; }
}
