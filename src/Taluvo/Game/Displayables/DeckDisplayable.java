package Taluvo.Game.Displayables;

import Taluvo.Game.GameModel.Deck;
import Taluvo.Game.GameModel.Hex;
import Taluvo.Util.ImageFactory;

import java.awt.*;
import java.awt.image.BufferedImage;

public class DeckDisplayable extends HudElement
{
    private Deck deck;

    private static Dimension size = new Dimension(128, 128);

    private static final Point centerHexOrigin = new Point(44, 44);

    public DeckDisplayable(Point origin, Deck deck)
    {
        super(origin, ImageFactory.makeBorderedRect(size.width, size.height, Color.WHITE, Color.BLACK));
        this.deck = deck;
    }

    public Dimension getSize() {return size;}

    public void drawContents(Graphics2D g2d, Point drawPt)
    {
        BufferedImage hexImageA = ImageFactory.getBaseHex((deck.peek().getTerrainA()));
        BufferedImage hexImageB = ImageFactory.getBaseHex((deck.peek().getTerrainB()));

        Point pointA = Hex.neighborOffsets[deck.peek().getIndexA()];
        Point pointB = Hex.neighborOffsets[deck.peek().getIndexB()];

        g2d.drawImage(ImageFactory.getBaseHex(Hex.Terrain.VOLCANO), centerHexOrigin.x + drawPt.x, centerHexOrigin.y + drawPt.y, null);
        g2d.drawImage(hexImageA, centerHexOrigin.x + pointA.x + drawPt.x, centerHexOrigin.y + pointA.y + drawPt.y, null);
        g2d.drawImage(hexImageB, centerHexOrigin.x + pointB.x + drawPt.x, centerHexOrigin.y + pointB.y + drawPt.y, null);

        g2d.setColor(Color.BLACK);
        g2d.drawString("#" + deck.peek().getTileID(), drawPt.x + 4, drawPt.y + 124);
    }
}
