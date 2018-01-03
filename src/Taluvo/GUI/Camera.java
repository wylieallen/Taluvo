package Taluvo.GUI;

import java.awt.*;

public class Camera
{
    private Point offset;

    public Camera()
    {
        offset = new Point(0, 0);
    }

    public Point getDrawOffset() { return offset; }

    public void center(Dimension dimension) { offset.setLocation(dimension.width/2, dimension.height/2); }

    public void centerOn(Dimension dimension, Point centerPt)
    {
        this.center(dimension);
        offset.translate(-centerPt.x, -centerPt.y);
    }

    public void translate(int dx, int dy)
    {
        offset.translate(dx, dy);
    }
}

