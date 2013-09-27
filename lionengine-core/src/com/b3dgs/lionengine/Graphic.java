package com.b3dgs.lionengine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Main interface with the graphic output, representing the screen buffer.
 */
public class Graphic
{
    /** The graphic output. */
    private Graphics2D g;

    /**
     * Constructor.
     */
    public Graphic()
    {
        // Nothing to do
    }

    /**
     * Constructor.
     * 
     * @param g The graphics output.
     */
    public Graphic(Graphics2D g)
    {
        this.g = g;
    }

    /**
     * Clear the display.
     * 
     * @param resolution The resolution.
     */
    public void clear(Resolution resolution)
    {
        g.clearRect(0, 0, resolution.getWidth(), resolution.getHeight());
    }

    /**
     * Dispose the graphic and release associated resources.
     */
    public void dispose()
    {
        g.dispose();
    }

    /**
     * Copies an area of the component by a distance specified by <code>dx</code> and <code>dy</code>. From the point
     * specified by <code>x</code> and <code>y</code>, this method copies downwards and to the right.
     * 
     * @param x the <i>x</i> coordinate of the source rectangle.
     * @param y the <i>y</i> coordinate of the source rectangle.
     * @param width the width of the source rectangle.
     * @param height the height of the source rectangle.
     * @param dx the horizontal distance to copy the pixels.
     * @param dy the vertical distance to copy the pixels.
     */
    public void copyArea(int x, int y, int width, int height, int dx, int dy)
    {
        g.copyArea(x, y, width, height, dx, dy);
    }

    /**
     * Draw an image to the graphic.
     * 
     * @param image The image to draw.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void drawImage(Image image, int x, int y)
    {
        g.drawImage(image, x, y, null);
    }

    /**
     * Draw an image to the graphic.
     * 
     * @param image The image to draw.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void drawImage(BufferedImage image, int x, int y)
    {
        g.drawImage(image, null, x, y);
    }

    /**
     * Draw an image to the graphic.
     * 
     * @param image The image to draw.
     * @param op The affine transformation.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void drawImage(BufferedImage image, AffineTransformOp op, int x, int y)
    {
        g.drawImage(image, op, x, y);
    }

    /**
     * Draw a part of an image from a source rectangle to a destination rectangle.
     * 
     * @param img The image to draw.
     * @param dx1 The <i>x</i> coordinate of the first corner of the destination rectangle.
     * @param dy1 The <i>y</i> coordinate of the first corner of the destination rectangle.
     * @param dx2 The <i>x</i> coordinate of the second corner of the destination rectangle.
     * @param dy2 The <i>y</i> coordinate of the second corner of the destination rectangle.
     * @param sx1 The <i>x</i> coordinate of the first corner of the source rectangle.
     * @param sy1 The <i>y</i> coordinate of the first corner of the source rectangle.
     * @param sx2 The <i>x</i> coordinate of the second corner of the source rectangle.
     * @param sy2 The <i>y</i> coordinate of the second corner of the source rectangle.
     */
    public void drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2)
    {
        g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
    }

    /**
     * Draws the specified rectangle. The left and right edges of the rectangle are at <code>x</code> and
     * <code>x&nbsp;+&nbsp;width&nbsp;-&nbsp;1</code>. The top and bottom edges are at <code>y</code> and
     * <code>y&nbsp;+&nbsp;height&nbsp;-&nbsp;1</code>. The resulting rectangle covers an area <code>width</code> pixels
     * wide by <code>height</code> pixels tall.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The width.
     * @param height The height.
     * @param fill <code>true</code> to fill the rectangle, <code>false</code> to draw only its borders.
     */
    public void drawRect(int x, int y, int width, int height, boolean fill)
    {
        if (fill)
        {
            g.fillRect(x, y, width, height);
        }
        else
        {
            g.drawRect(x, y, width, height);
        }
    }

    /**
     * Draws a line, using the current color, between the points <code>(x1,&nbsp;y1)</code> and
     * <code>(x2,&nbsp;y2)</code> in this graphic.
     * 
     * @param x1 The first point's <i>x</i> coordinate.
     * @param y1 The first point's <i>y</i> coordinate.
     * @param x2 The second point's <i>x</i> coordinate.
     * @param y2 The second point's <i>y</i> coordinate.
     */
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        g.drawLine(x1, y1, x2, y2);
    }

    /**
     * Fills an oval bounded by the specified rectangle with the current color.
     * 
     * @param x the <i>x</i> coordinate of the upper left corner of the oval to be filled.
     * @param y the <i>y</i> coordinate of the upper left corner of the oval to be filled.
     * @param width the width of the oval to be filled.
     * @param height the height of the oval to be filled.
     * @param fill <code>true</code> to fill the rectangle, <code>false</code> to draw only its borders.
     */
    public void drawOval(int x, int y, int width, int height, boolean fill)
    {
        if (fill)
        {
            g.fillOval(x, y, width, height);
        }
        else
        {
            g.drawOval(x, y, width, height);
        }
    }

    /**
     * Set the current graphic color.
     * 
     * @param color The color.
     */
    public void setColor(Color color)
    {
        g.setColor(color);
    }

    /**
     * Set the graphics output.
     * 
     * @param g The graphics output.
     */
    public void setGraphics(Graphics2D g)
    {
        this.g = g;
    }

    /**
     * Get the current color.
     * 
     * @return The current graphic color.
     */
    public Color getColor()
    {
        return g.getColor();
    }

    /**
     * Get the graphics output.
     * 
     * @return The graphics output.
     */
    Graphics2D getGraphics()
    {
        return g;
    }
}
