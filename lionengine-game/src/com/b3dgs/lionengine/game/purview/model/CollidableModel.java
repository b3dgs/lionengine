package com.b3dgs.lionengine.game.purview.model;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.game.purview.Mirrorable;

/**
 * Default collidable model implementation.
 */
public class CollidableModel
        implements Collidable
{
    /** No collision. */
    private static final CollisionData NO_COLLISION = new CollisionData(0, 0, 0, 0, false);
    /** Entity owning this model. */
    private final Localizable entity;
    /** Entity collision representation. */
    private final Polygon coll;
    /** Ray cast representation. */
    private final Line2D ray;
    /** The collision used. */
    private CollisionData collision;
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
        collision = CollidableModel.NO_COLLISION;
    }

    /*
     * CollidableModel
     */

    @Override
    public void updateCollision()
    {
        final int xCur = entity.getLocationIntX() - entity.getWidth() / 2;
        final int yCur = entity.getLocationIntY();
        final int xOld = (int) entity.getLocationOldX() - entity.getWidth() / 2;
        final int yOld = (int) entity.getLocationOldY();

        boolean mirror = false;
        if (collision.getMirror() && entity instanceof Mirrorable)
        {
            mirror = ((Mirrorable) entity).getMirror();
        }
        final int offsetX = mirror ? -collision.getOffsetX() : collision.getOffsetX();
        final int offsetY = collision.getOffsetY();
        final int width = collision.getWidth();
        final int height = collision.getHeight();

        coll.reset();
        coll.addPoint(xCur + offsetX, yCur + offsetY);
        coll.addPoint(xCur + offsetX, yCur + offsetY + height);
        coll.addPoint(xCur + offsetX + width, yCur + offsetY + height);
        coll.addPoint(xCur + offsetX + width, yCur + offsetY);

        coll.addPoint(xOld + offsetX, yOld + offsetY);
        coll.addPoint(xOld + offsetX, yOld + offsetY + height);
        coll.addPoint(xOld + offsetX + width, yOld + offsetY + height);
        coll.addPoint(xOld + offsetX + width, yOld + offsetY);

        box = coll.getBounds2D();

        final int sx = xOld + offsetX + width / 2;
        final int sy = yOld + offsetY + height / 2;
        final int ex = xCur + offsetX + width / 2;
        final int ey = yCur + offsetY + height / 2;
        ray.setLine(sx, sy, ex, ey);
    }

    @Override
    public void setCollision(CollisionData collision)
    {
        if (collision == null)
        {
            this.collision = CollidableModel.NO_COLLISION;
        }
        else
        {
            this.collision = collision;
        }
    }

    @Override
    public boolean collide(Collidable entity)
    {
        return box.intersects(entity.getCollisionBounds()) || box.contains(entity.getCollisionBounds());
    }

    @Override
    public boolean collide(Rectangle2D area)
    {
        return coll.intersects(area) || coll.contains(area);
    }

    @Override
    public void renderCollision(Graphic g, CameraGame camera)
    {
        final int x = camera.getViewpointX((int) box.getX());
        final int y = camera.getViewpointY((int) (box.getY() + box.getHeight()));
        g.drawRect(x, y, (int) box.getWidth(), (int) box.getHeight(), false);

        final int x1 = camera.getViewpointX((int) ray.getX1());
        final int y1 = camera.getViewpointY((int) ray.getY1());
        final int x2 = camera.getViewpointX((int) ray.getX2());
        final int y2 = camera.getViewpointY((int) ray.getY2());
        g.drawLine(x1, y1, x2, y2);
    }

    @Override
    public CollisionData getCollisionData()
    {
        return collision;
    }

    @Override
    public Rectangle2D getCollisionBounds()
    {
        return box;
    }

    @Override
    public Line2D getCollisionRay()
    {
        return ray;
    }
}
