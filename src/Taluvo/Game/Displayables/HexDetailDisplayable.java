package Taluvo.Game.Displayables;

import Taluvo.Game.Clickables.HexButton;
import Taluvo.Game.GameModel.Hex;
import Taluvo.Game.GameUberstate;
import Taluvo.Util.ImageFactory;
import Taluvo.GUI.Clickables.Clickable;

import java.awt.*;

public class HexDetailDisplayable extends HudElement
{
    private static final Dimension size = new Dimension(144, 148);
    private HexButton activeHexButton = HexButton.makeNullButton();

    private GameUberstate gameUberstate;

    public HexDetailDisplayable(Point origin, GameUberstate gameUberstate)
    {
        super(origin, ImageFactory.makeBorderedRect(size.width, size.height, Color.WHITE, Color.BLACK));
        this.gameUberstate = gameUberstate;
    }

    public Dimension getSize() {return size;}

    public void drawContents(Graphics2D g2d, Point drawPt)
    {
        g2d.drawImage(activeHexButton.getBaseImage(), drawPt.x + 100, drawPt.y + 4, null);
        Hex hex = activeHexButton.getHex();
        g2d.setColor(Color.BLACK);
        if(hex.getLevel() >= 1)
        {
            g2d.drawString("" + hex.getLevel(), drawPt.x + 100 + 24, drawPt.y + 4 + 17);
        }
        g2d.drawImage(ImageFactory.getBuilding(hex.getOwner(), hex.getBuilding()), drawPt.x + 100 + 4, drawPt.y + 4 + 13, null);
        g2d.drawString("TileID: " + hex.getTileID(), drawPt.x + 4, drawPt.y + 19);
        g2d.drawString("Level: " + hex.getLevel(), drawPt.x + 4, drawPt.y + 39);
        g2d.drawString("Building: " + hex.getBuilding().toString(), drawPt.x + 4, drawPt.y + 59);
        g2d.drawString("Pt: (" + hex.getOrigin().x + ", " + hex.getOrigin().y + ")", drawPt.x + 4, drawPt.y + 79);
        g2d.drawString("Terrain: " + hex.getTerrain().toString(), drawPt.x + 4, drawPt.y + 99);
        g2d.drawString("Owner: " + hex.getOwner().toString(), drawPt.x + 4, drawPt.y + 119);
        g2d.drawString("Settlement: " + hex.getSettlementID(), drawPt.x + 4, drawPt.y + 139);
    }

    @Override
    public void update()
    {
        Clickable activeClickable = gameUberstate.getActiveClickable();
        if (activeClickable instanceof HexButton)
        {
            activeHexButton = (HexButton) activeClickable;
        }
    }
}
