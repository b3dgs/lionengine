package com.b3dgs.lionengine.game.platform;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.purview.Rasterable;
import com.b3dgs.lionengine.game.purview.model.RasterableModel;

/**
 * Rastered version of an EntityPlatform.
 */
public abstract class EntityPlatformRastered
        extends EntityPlatform
        implements Rasterable
{
    /** Raster model. */
    private final Rasterable rasterable;
    /** Index. */
    private int index;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param tileHeight The tile height value.
     */
    public EntityPlatformRastered(SetupSurfaceRasteredGame setup, int tileHeight)
    {
        super(setup);
        rasterable = new RasterableModel(setup, tileHeight);
    }

    /*
     * EntityPlatform
     */

    @Override
    public void updateAnimation(double extrp)
    {
        super.updateAnimation(extrp);
        if (rasterable.isRastered())
        {
            index = rasterable.getRasterIndex(getLocationY());
            final SpriteAnimated anim = getRasterAnim(index);
            if (anim != null)
            {
                anim.setFrame(sprite.getFrame());
                anim.setMirror(getMirror());
            }
        }
    }

    @Override
    public void render(Graphic g, CameraPlatform camera)
    {
        if (rasterable.isRastered())
        {
            final SpriteAnimated anim = getRasterAnim(index);
            renderAnim(g, anim, camera);
        }
        else
        {
            super.render(g, camera);
        }
    }

    /*
     * Rasterable
     */

    @Override
    public int getRasterIndex(double y)
    {
        return rasterable.getRasterIndex(y);
    }

    @Override
    public SpriteAnimated getRasterAnim(int rasterIndex)
    {
        return rasterable.getRasterAnim(rasterIndex);
    }

    @Override
    public boolean isRastered()
    {
        return rasterable.isRastered();
    }
}
