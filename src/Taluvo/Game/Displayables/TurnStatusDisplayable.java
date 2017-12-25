package Taluvo.Game.Displayables;

import Taluvo.Game.GameUberstate;
import Taluvo.Util.ImageFactory;

import java.awt.*;

public class TurnStatusDisplayable extends HudElement
{
    private GameUberstate gameUberstate;

    private static final Dimension size = new Dimension(144, 104);

    public TurnStatusDisplayable(Point origin, GameUberstate gameUberstate)
    {
        super(origin, ImageFactory.makeBorderedRect(size.width, size.height, Color.WHITE, Color.BLACK));
        this.gameUberstate = gameUberstate;
    }

    public Dimension getSize() {return size;}

    public void drawContents(Graphics2D g2d, Point drawPt)
    {
        g2d.setColor(Color.BLACK);
        g2d.drawString("TurnPhase: " + gameUberstate.getActivePhase().toString(), drawPt.x + 4, drawPt.y + 16);
        g2d.drawString("Player: " + gameUberstate.getActivePlayer(), drawPt.x + 4, drawPt.y + 36);
        g2d.drawString("Building: " + gameUberstate.getActiveBuilding(), drawPt.x + 4, drawPt.y + 56);
        g2d.drawString("Terrain: " + gameUberstate.getActiveTerrain(), drawPt.x + 4, drawPt.y + 76);
        g2d.drawString("State: " + gameUberstate.getEndCondition(), drawPt.x + 4, drawPt.y + 96);
    }
}
