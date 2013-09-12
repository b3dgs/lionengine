package com.b3dgs.lionengine.game.entity;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.purview.Mirrorable;
import com.b3dgs.lionengine.game.purview.model.BodyModel;
import com.b3dgs.lionengine.game.purview.model.CollidableModel;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;
import com.b3dgs.lionengine.game.purview.model.MirrorableModel;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Main object that can be used by any higher level object for a game. It supports external configuration, collision,
 * and mirror. You can override this function: {@link #createConfigurable()} to create your own {@link Configurable}
 * type, if needed.
 */
public abstract class EntityGame
        extends BodyModel
        implements Configurable, Collidable, Mirrorable
{
    /** Id used. */
    private static final Set<Integer> IDS = new HashSet<>(16);
    /** Last id used. */
    private static int lastId = 1;

    /**
     * Get the next unused id.
     * 
     * @return The next unused id.
     */
    private static Integer getFreeId()
    {
        while (EntityGame.IDS.contains(Integer.valueOf(EntityGame.lastId)))
        {
            EntityGame.lastId++;
        }
        return Integer.valueOf(EntityGame.lastId);
    }

    /** Entity id. */
    private final Integer id;
    /** Configurable object reference. */
    private final Configurable configurable;
    /** Collidable object reference. */
    private final Collidable collidable;
    /** Mirrorable object reference. */
    private final Mirrorable mirrorable;
    /** Destroyed flag; true will remove it from the handler. */
    private boolean destroy;

    /**
     * Create a new entity.
     */
    public EntityGame()
    {
        this(null);
    }

    /**
     * Create a new entity from an existing configuration. The configuration will be shared; this will reduce memory
     * usage.
     * 
     * @param configurable The configuration reference.
     */
    public EntityGame(Configurable configurable)
    {
        super(null);
        if (configurable != null)
        {
            this.configurable = configurable;
        }
        else
        {
            this.configurable = createConfigurable();
        }
        mirrorable = new MirrorableModel();
        collidable = new CollidableModel(this);
        destroy = false;
        id = EntityGame.getFreeId();
        EntityGame.IDS.add(id);
    }

    /**
     * Get the entity id (unique).
     * 
     * @return The entity id.
     */
    public final Integer getId()
    {
        return id;
    }

    /**
     * Remove entity from handler, and free memory.
     */
    public void destroy()
    {
        destroy = true;
        EntityGame.IDS.remove(getId());
    }

    /**
     * Get the distance between the entity and the specified other entity.
     * 
     * @param entity The entity to compare to.
     * @return The distance value.
     */
    public double getDistance(EntityGame entity)
    {
        return UtilityMath.getDistance(getLocationX(), getLocationY(), entity.getLocationX(), entity.getLocationY());
    }

    /**
     * Check if entity is going to be removed.
     * 
     * @return <code>true</code> if going to be removed, <code>false</code> else.
     */
    public boolean isDestroyed()
    {
        return destroy;
    }

    /**
     * Create configurable.
     * 
     * @return The configurable instance.
     */
    protected Configurable createConfigurable()
    {
        return new ConfigurableModel();
    }

    /*
     * Configurable
     */

    @Override
    public void loadData(Media media)
    {
        configurable.loadData(media);
    }

    @Override
    public XmlNode getDataRoot()
    {
        return configurable.getDataRoot();
    }

    @Override
    public String getDataString(String attribute, String... path)
    {
        return configurable.getDataString(attribute, path);
    }

    @Override
    public int getDataInteger(String attribute, String... path)
    {
        return configurable.getDataInteger(attribute, path);
    }

    @Override
    public boolean getDataBoolean(String attribute, String... path)
    {
        return configurable.getDataBoolean(attribute, path);
    }

    @Override
    public double getDataDouble(String attribute, String... path)
    {
        return configurable.getDataDouble(attribute, path);
    }

    @Override
    public Animation getDataAnimation(String name)
    {
        return configurable.getDataAnimation(name);
    }

    @Override
    public CollisionData getDataCollision(String name)
    {
        return configurable.getDataCollision(name);
    }

    /*
     * Collidable
     */

    @Override
    public void updateCollision(boolean mirror)
    {
        collidable.updateCollision(mirror);
    }

    @Override
    public void setCollision(CollisionData collision)
    {
        collidable.setCollision(collision);
    }

    @Override
    public boolean collide(Collidable entity)
    {
        return collidable.collide(entity);
    }

    @Override
    public boolean collide(Rectangle2D area)
    {
        return collidable.collide(area);
    }

    @Override
    public void renderCollision(Graphic g, CameraGame camera)
    {
        collidable.renderCollision(g, camera);
    }

    @Override
    public CollisionData getCollisionData()
    {
        return collidable.getCollisionData();
    }

    @Override
    public Rectangle2D getCollisionBounds()
    {
        return collidable.getCollisionBounds();
    }

    @Override
    public Line2D getCollisionRay()
    {
        return collidable.getCollisionRay();
    }

    /*
     * Mirrorable
     */

    @Override
    public void mirror(boolean state)
    {
        mirrorable.mirror(state);
    }

    @Override
    public void updateMirror()
    {
        mirrorable.updateMirror();
    }

    @Override
    public void setMirrorCancel(boolean state)
    {
        mirrorable.setMirrorCancel(state);
    }

    @Override
    public boolean getMirrorCancel()
    {
        return mirrorable.getMirrorCancel();
    }

    @Override
    public boolean getMirror()
    {
        return mirrorable.getMirror();
    }
}
