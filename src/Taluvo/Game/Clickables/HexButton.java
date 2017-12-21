package Taluvo.Game.Clickables;

import Taluvo.Game.GameModel.Hex;
import Taluvo.Util.ImageFactory;
import Taluvo.GUI.Clickables.Buttons.Button;
import Taluvo.Util.AbstractFunction;

import java.awt.*;

public class HexButton extends Button
{
    private Hex hex;

    private Shape hitboxHex;

    public HexButton(Hex hex, AbstractFunction action)
    {
        super(hex.getOrigin(), ImageFactory.getBaseHex(hex.getTerrain()), ImageFactory.getHoverHex(hex.getTerrain()), action);
        this.hex = hex;
        this.hitboxHex = makeHexagon(super.getOrigin());
    }

    private static Shape makeHexagon(Point origin)
    {
        Polygon hexagon = (Polygon) ImageFactory.makeHexShape();
        hexagon.translate(origin.x, origin.y);
        return hexagon;
    }

    public Hex getHex()
    {
        return hex;
    }

    public void changeHex(Hex hex)
    {
        //System.out.println("Changing hex");

        this.hex = hex;

        super.setBaseImage(ImageFactory.getBaseHex(hex.getTerrain()));
        super.setHoverImage(ImageFactory.getHoverHex(hex.getTerrain()));
    }

    @Override
    public boolean pointIsOn(Point point)
    {
        return hitboxHex.contains(point);
    }

    @Override
    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        super.drawAt(g2d, drawPt);
        g2d.setColor(Color.BLACK);
        if(hex.getLevel() >= 1)
        {
            g2d.drawString("" + hex.getLevel(), drawPt.x + 24, drawPt.y + 17);
        }
        g2d.drawImage(ImageFactory.getBuilding(hex.getOwner(), hex.getBuilding()), drawPt.x + 4, drawPt.y + 13, null);
    }

    public static HexButton makeNullButton() { return new HexButton(Hex.getNullHex(), () -> {}); }
}
