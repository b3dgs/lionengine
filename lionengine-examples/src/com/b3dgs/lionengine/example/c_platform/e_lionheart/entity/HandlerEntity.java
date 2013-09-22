package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item.EntityItem;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.monster.EntityMonster;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.scenery.EntityScenery;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.platform.HandlerEntityPlatform;

/**
 * Handle all entity on the map.
 */
public class HandlerEntity
        extends HandlerEntityPlatform<Entity>
{
    /** The camera reference. */
    public final CameraPlatform camera;
    /** The entity factory reference. */
    private final FactoryEntity factoryEntity;
    /** The player reference. */
    private Valdyn player;

    /**
     * Constructor.
     * 
     * @param camera The camera reference.
     * @param factoryEntity The entity factory reference.
     */
    public HandlerEntity(CameraPlatform camera, FactoryEntity factoryEntity)
    {
        this.camera = camera;
        this.factoryEntity = factoryEntity;
    }

    /**
     * Save entities to an existing file.
     * 
     * @param file The level file.
     * @throws IOException If error.
     */
    public void save(FileWriting file) throws IOException
    {
        file.writeShort((short) size());
        for (final Entity entity : list())
        {
            file.writeByte((byte) entity.type.getIndex());
            entity.save(file);
        }
    }

    /**
     * Load entities from an existing file.
     * 
     * @param file The level file.
     * @throws IOException If error.
     */
    public void load(FileReading file) throws IOException
    {
        removeAll();
        updateRemove();
        final int entitiesNumber = file.readShort();
        for (int i = 0; i < entitiesNumber; i++)
        {
            final Entity entity = factoryEntity.createEntity(EntityType.get(file.readByte()));
            entity.load(file);
            add(entity);
        }
        updateAdd();
    }

    /**
     * Prepare entities.
     */
    public void prepare()
    {
        for (final Entity entity : list())
        {
            entity.prepare();
        }
    }

    /**
     * Set the player reference.
     * 
     * @param player The player reference.
     */
    public void setPlayer(Valdyn player)
    {
        this.player = player;
    }

    /*
     * HandlerEntityPlatform
     */

    @Override
    protected boolean canUpdateEntity(Entity entity)
    {
        return camera.isVisible(entity);
    }

    @Override
    protected boolean canRenderEntity(Entity entity)
    {
        return camera.isVisible(entity);
    }

    @Override
    protected void updatingEntity(Entity entity, double extrp)
    {
        // Scenery interaction (leg)
        if (entity instanceof EntityScenery)
        {
            if (player.getCollisionLeg().collide(entity))
            {
                entity.hitThat(player);
            }
            if (player.getCollisionAttack().collide(entity))
            {
                entity.hitBy(player);
            }
        }
        // Item interaction (player)
        else if (entity instanceof EntityItem)
        {
            if (player.collide(entity))
            {
                entity.hitBy(player);
            }
        }
        // Monster interaction (player)
        else if (entity instanceof EntityMonster)
        {
            if (player.collide(entity))
            {
                entity.hitThat(player);
                player.hitBy(entity);
            }
            if (player.getCollisionAttack().collide(entity))
            {
                player.hitThat(entity);
                entity.hitBy(player);
            }
        }
        entity.onUpdated();
    }

    @Override
    protected void renderingEntity(Entity entity, Graphic g, CameraPlatform camera)
    {
        if (AppLionheart.SHOW_COLLISIONS)
        {
            entity.renderCollision(g, camera);
        }
    }
}
