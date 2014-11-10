/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.platform.entity;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.purview.Rasterable;
import com.b3dgs.lionengine.game.purview.model.RasterableModel;

/**
 * Rastered version of an EntityPlatform.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
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
     * Constructor base.
     * 
     * @param setup The setup reference.
     * @param tileHeight The tile height value.
     * @throws LionEngineException If there is more than {@link Integer#MAX_VALUE} objects at the same time or invalid
     *             setup.
     */
    public EntityPlatformRastered(SetupSurfaceRasteredGame setup, int tileHeight) throws LionEngineException
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
                anim.setFrame(getFrame());
                anim.setMirror(getMirror());
            }
        }
    }

    @Override
    public void render(Graphic g, CameraGame camera)
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
