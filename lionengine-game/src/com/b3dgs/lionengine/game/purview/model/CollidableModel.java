package com.b3dgs.lionengine.game.purview.model;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Default collidable model implementation.
 */
public class CollidableModel
        implements Collidable
{
    /** Entity owning this model. */
    private final Localizable entity;
    /** Entity collision representation. */
    private final Polygon coll;
    /** Ray cast representation. */
    private final Line2D ray;
    /** Temp entity bounding box from polygon. */
    private Rectangle2D box;

    /**
     * Create a collidable model.
     * 
     * @param entity The entity owning this model.
     */
    public CollidableModel(Localizable entity)
    {
        this.entity = entity;
        coll = new Polygon();
        ray = new Line2D.Double();
        box = coll.getBounds2D();
    }

    /*
     * CollidableModel
     */

    @Override
    public void updateCollision(int x, int y, int width, int height)
    {
        final int xCur = entity.getLocationIntX();
        final int yCur = entity.getLocationIntY() - entity.getHeight();
        final int xOld = (int) entity.getLocationOldX();
        final int yOld = (int) entity.getLocationOldY() - entity.getHeight();

        coll.reset();
        coll.addPoint(xCur + x, yCur + y);
        coll.addPoint(xCur + x, yCur + y + height);
        coll.addPoint(xCur + x + width, yCur + y + height);
        coll.addPoint(xCur + x + width, yCur + y);

        coll.addPoint(xOld + x, yOld + y);
        coll.addPoint(xOld + x, yOld + y + height);
        coll.addPoint(xOld + x + width, yOld + y + height);
        coll.addPoint(xOld + x + width, yOld + y);

        box = coll.getBounds2D();
        ray.setLine(xOld + x + width / 2, yOld + y + height / 2, xCur + x + width / 2, yCur + y + height / 2);
    }

    @Override
    public boolean collide(Collidable entity)
    {
        return box.intersects(entity.getCollision()) || box.contains(entity.getCollision());
    }

    @Override
    public boolean collide(Rectangle2D area)
    {
        return coll.intersects(area) || coll.contains(area);
    }

    @Override
    public void renderCollision(Graphic g, CameraGame camera)
    {
        final int x = camera.getViewpointX((int) box.getX() - (int) box.getWidth() / 2);
        final int y = camera.getViewpointY((int) (box.getY() + box.getHeight()));
        g.drawRect(x, y, (int) box.getWidth(), (int) box.getHeight(), false);

        final int x1 = camera.getViewpointX((int) ray.getX1());
        final int y1 = camera.getViewpointY((int) ray.getY1());
        final int x2 = camera.getViewpointX((int) ray.getX2());
        final int y2 = camera.getViewpointY((int) ray.getY2());
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public Rectangle2D getCollision()
    {
        return box;
    }

    @Override
    public Line2D getCollisionRay()
    {
        return ray;
    }
}
