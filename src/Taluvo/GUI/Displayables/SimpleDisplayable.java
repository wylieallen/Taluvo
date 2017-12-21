package Taluvo.GUI.Displayables;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SimpleDisplayable implements Displayable {
    private Point position;
    private BufferedImage image;
    private Dimension dimension;

    public SimpleDisplayable(Point position, BufferedImage image)
    {
        this.position = position;
        this.image = image;
        this.dimension = new Dimension(image.getWidth(), image.getHeight());
    }

    protected void setImage(BufferedImage image) {this.image = image;}

    public BufferedImage getImage() {return image;}

    @Override
    public Dimension getSize() {return dimension;}

    @Override
    public Point getOrigin() {return position;}

    public void drawAt(Graphics2D g2d, Point drawPt)
    {
        g2d.drawImage(image, drawPt.x, drawPt.y, null);
    }
}