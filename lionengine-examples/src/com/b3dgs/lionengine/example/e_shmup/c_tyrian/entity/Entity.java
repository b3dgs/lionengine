package com.b3dgs.lionengine.example.e_shmup.c_tyrian.entity;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.CollisionData;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.entity.EntityGame;

/**
 * Entity base implementation.
 */
public class Entity
        extends EntityGame
{
    /** Entity surface. */
    private final SpriteTiled sprite;
    /** Tile offset. */
    private int tileOffset;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Entity(SetupSurfaceGame setup)
    {
        super(setup.configurable);
        final int width = setup.configurable.getDataInteger("width", "size");
        final int height = setup.configurable.getDataInteger("height", "size");
        sprite = Drawable.loadSpriteTiled(setup.surface, width, height);
        setSize(width, height);
        setCollision(new CollisionData(width / 2, -height, width, height, false));
    }

    /**
     * Render the entity.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraGame camera)
    {
        final int x = camera.getViewpointX(getLocationIntX()) + getLocationOffsetX();
        final int y = camera.getViewpointY(getLocationIntY()) - getLocationOffsetY();
        sprite.render(g, tileOffset, x, y);
    }

    /**
     * Set the tile offset.
     * 
     * @param offset The tile offset.
     */
    protected void setTileOffset(int offset)
    {
        tileOffset = offset;
    }
    
    /*
     * EntityGame
     */
    
    @Override
    public void update(double extrp)
    {
        updateCollision();
    }
}
