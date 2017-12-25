package Taluvo.Game.Displayables;

import Taluvo.GUI.Uberstate;
import Taluvo.Game.GameModel.Board;
import Taluvo.Game.GameModel.Settlement;
import Taluvo.Util.ImageFactory;

import java.awt.*;

public class SettlementsDisplayable extends HudElement
{
    private static Dimension size = new Dimension(128, 21);

    private Board board;

    private Uberstate.OverlayManager overlayManager;

    public SettlementsDisplayable(Point origin, Board board, Uberstate.OverlayManager overlayManager)
    {
        super(origin, ImageFactory.makeBorderedRect(size.width, 21, Color.WHITE, Color.BLACK));
        this.board = board;
        this.overlayManager = overlayManager;
    }

    public Dimension getSize() {return size;}

    public void update()
    {
        int height = 21 + (16 * board.getSettlements().size());
        if(size.height != height)
        {
            size = new Dimension(size.width, height);
            overlayManager.resetLeftOverlays();
        }
    }

    public void drawContents(Graphics2D g2d, Point drawPt)
    {
        g2d.setColor(Color.BLACK);
        g2d.drawString("Settlements:", drawPt.x + 28, drawPt.y + 16);
        int y = 32;
        for(Settlement settlement : board.getSettlements())
        {
            g2d.setColor(settlement.getOwner().color1);
            g2d.fillRect(drawPt.x + 1, drawPt.y + y - 12, 126, 16);
            g2d.setColor(settlement.getOwner().color2);
            String string = "Settlement " + settlement.getSettlementID() + " Size: " + settlement.getSize();
            g2d.drawString(string, drawPt.x + 4, drawPt.y + y);
            g2d.setColor(Color.GRAY);
            g2d.drawRect(drawPt.x + 0, drawPt.y + y - 12, 127, 16);
            y += 16;
        }
    }
}
