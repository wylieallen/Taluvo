package Taluvo.Util;

import Taluvo.Game.GameModel.Hex;
import Taluvo.Game.GameModel.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ImageFactory
{
    private static Shape hexagon = makeHexShape();

    private static BufferedImage emptyHex = makeHex(41, 41, Color.WHITE, Color.BLACK);
    private static BufferedImage hoverEmptyHex = makeHex(41, 41, Color.PINK, Color.BLACK);

    private static BufferedImage volcanoHex = makeHex(41, 41, Color.RED, Color.BLACK);
    private static BufferedImage hoverVolcanoHex = makeHex(41, 41, Color.PINK, Color.BLACK);

    private static BufferedImage rockyHex = makeHex(41, 41, Color.LIGHT_GRAY, Color.BLACK);
    private static BufferedImage hoverRockyHex = makeHex(41, 41, Color.PINK, Color.BLACK);

    private static BufferedImage lakeHex = makeHex(41, 41, Color.CYAN, Color.BLACK);
    private static BufferedImage hoverLakeHex = makeHex(41, 41, Color.PINK, Color.BLACK);

    private static BufferedImage jungleHex = makeHex(41, 41, Color.GREEN, Color.BLACK);
    private static BufferedImage hoverJungleHex = makeHex(41, 41, Color.PINK, Color.BLACK);

    private static BufferedImage grassHex = makeHex(41, 41, Color.YELLOW, Color.BLACK);
    private static BufferedImage hoverGrassHex = makeHex(41, 41, Color.PINK, Color.BLACK);


    private static final Map<Hex.Terrain, BufferedImage> baseTerrainHexes;
    static
    {
        baseTerrainHexes = new HashMap<>();
        baseTerrainHexes.put(Hex.Terrain.EMPTY, emptyHex);
        baseTerrainHexes.put(Hex.Terrain.VOLCANO, volcanoHex);
        baseTerrainHexes.put(Hex.Terrain.ROCKY, rockyHex);
        baseTerrainHexes.put(Hex.Terrain.LAKE, lakeHex);
        baseTerrainHexes.put(Hex.Terrain.JUNGLE, jungleHex);
        baseTerrainHexes.put(Hex.Terrain.GRASS, grassHex);
    }

    private static final Map<Hex.Terrain, BufferedImage> hoverTerrainHexes;
    static
    {
        hoverTerrainHexes = new HashMap<>();
        hoverTerrainHexes.put(Hex.Terrain.EMPTY, hoverEmptyHex);
        hoverTerrainHexes.put(Hex.Terrain.VOLCANO, hoverVolcanoHex);
        hoverTerrainHexes.put(Hex.Terrain.ROCKY, hoverRockyHex);
        hoverTerrainHexes.put(Hex.Terrain.LAKE, hoverLakeHex);
        hoverTerrainHexes.put(Hex.Terrain.JUNGLE, hoverJungleHex);
        hoverTerrainHexes.put(Hex.Terrain.GRASS, hoverGrassHex);
    }

    public static Shape makeHexShape()
    {
        int hexX[] = {0, 20, 40, 40, 20, 0};
        int hexY[] = {10, 0, 10, 30, 40, 30};
        return new Polygon(hexX, hexY, 6);
    }

    public static BufferedImage getBaseHex(Hex.Terrain terrain) {return baseTerrainHexes.get(terrain);}
    public static BufferedImage getHoverHex(Hex.Terrain terrain) {return hoverTerrainHexes.get(terrain);}

    private static BufferedImage noneBuilding = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    private static BufferedImage villageBuilding1 = makeBuildingImage(Color.BLACK, Color.WHITE, "Vi");
    private static BufferedImage templeBuilding1 = makeBuildingImage(Color.BLACK, Color.WHITE, "Te");
    private static BufferedImage towerBuilding1 = makeBuildingImage(Color.BLACK, Color.WHITE, "To");

    private static BufferedImage villageBuilding2 = makeBuildingImage(Color.WHITE, Color.BLACK, "Vi");
    private static BufferedImage templeBuilding2 = makeBuildingImage(Color.WHITE, Color.BLACK, "Te");;
    private static BufferedImage towerBuilding2 = makeBuildingImage(Color.WHITE, Color.BLACK, "To");;

    private static final Map<Hex.Building, BufferedImage> buildings1;
    static
    {
        buildings1 = new HashMap<>();
        buildings1.put(Hex.Building.NONE, noneBuilding);
        buildings1.put(Hex.Building.VILLAGE, villageBuilding1);
        buildings1.put(Hex.Building.TEMPLE, templeBuilding1);
        buildings1.put(Hex.Building.TOWER, towerBuilding1);
    }

    private static final Map<Hex.Building, BufferedImage> buildings2;
    static
    {
        buildings2 = new HashMap<>();
        buildings2.put(Hex.Building.NONE, noneBuilding);
        buildings2.put(Hex.Building.VILLAGE, villageBuilding2);
        buildings2.put(Hex.Building.TEMPLE, templeBuilding2);
        buildings2.put(Hex.Building.TOWER, towerBuilding2);
    }

    private static final Map<Hex.Building, BufferedImage> nonbuildings;
    static
    {
        nonbuildings = new HashMap<>();
        nonbuildings.put(Hex.Building.NONE, noneBuilding);
        nonbuildings.put(Hex.Building.VILLAGE, noneBuilding);
        nonbuildings.put(Hex.Building.TEMPLE, noneBuilding);
        nonbuildings.put(Hex.Building.TOWER, noneBuilding);
    }

    private static final Map<Player, Map<Hex.Building, BufferedImage>> playerToBuildings;
    static
    {
        playerToBuildings = new HashMap<>();
        playerToBuildings.put(Player.NONE, nonbuildings);
        playerToBuildings.put(Player.ONE, buildings1);
        playerToBuildings.put(Player.TWO, buildings2);
    }

    public static BufferedImage getBuilding(Player player, Hex.Building building)
    {
        return playerToBuildings.get(player).get(building);
    }

    private static BufferedImage makeBuildingImage(Color baseColor, Color textColor, String text)
    {
        BufferedImage image = new BufferedImage(21, 21, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(baseColor);
        g2d.fillOval(0, 0, 20, 20);
        g2d.setColor(Color.GRAY);
        g2d.drawOval(0, 0, 20, 20);
        g2d.setColor(textColor);
        g2d.drawString(text, 4, 15);
        return image;
    }
    public static BufferedImage makeFilledRect(int width, int height, Color color)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.fillRect(0, 0, width, height);
        return image;
    }

    public static BufferedImage makeBorderedRect(int width, int height, Color bodyColor, Color borderColor)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(bodyColor);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(borderColor);
        g2d.drawRect(0, 0, width - 1, height - 1);
        return image;
    }

    public static BufferedImage makeLabeledRect(int width, int height, Color bodyColor, Color borderColor, Color textColor, String text, Point textPt)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(bodyColor);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(borderColor);
        g2d.drawRect(0, 0, width - 1, height - 1);
        g2d.setColor(textColor);
        g2d.drawString(text, textPt.x, textPt.y);
        return image;
    }

    public static BufferedImage makeHex(int width, int height, Color bodyColor, Color borderColor)
    {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(bodyColor);
        g2d.fill(hexagon);
        g2d.setColor(borderColor);
        g2d.draw(hexagon);
        return image;
    }


}
