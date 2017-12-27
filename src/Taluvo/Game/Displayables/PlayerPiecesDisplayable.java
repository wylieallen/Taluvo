package Taluvo.Game.Displayables;

import Taluvo.Game.GameModel.Hex;
import Taluvo.Game.GameModel.Player;
import Taluvo.Util.ImageFactory;

import java.awt.*;

public class PlayerPiecesDisplayable extends HudElement
{
    private static final Dimension size = new Dimension(224, 64);

    private Player player;

    public PlayerPiecesDisplayable(Point origin, Player player)
    {
        super(origin, ImageFactory.makeBorderedRect(size.width, size.height, Color.WHITE, Color.BLACK));
        this.player = player;
    }

    public Dimension getSize() {return size;}

    public void drawContents(Graphics2D g2d, Point drawPt)
    {
        g2d.setColor(Color.BLACK);
        g2d.drawString(player.getName(), drawPt.x + 4, drawPt.y + 16);
        g2d.drawImage(ImageFactory.getBuilding(player, Hex.Building.VILLAGE), drawPt.x + 64, drawPt.y + 8, null);
        g2d.drawImage(ImageFactory.getBuilding(player, Hex.Building.TEMPLE), drawPt.x + 112, drawPt.y + 8, null);
        g2d.drawImage(ImageFactory.getBuilding(player, Hex.Building.TOWER), drawPt.x + 160, drawPt.y + 8, null);
        g2d.drawString("" + player.getVillagers(), drawPt.x + 64, drawPt.y + 54);
        g2d.drawString("" + player.getTemples(), drawPt.x + 112, drawPt.y + 54);
        g2d.drawString("" + player.getTowers(), drawPt.x + 160, drawPt.y + 54);
    }
}
