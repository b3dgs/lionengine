/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.trait;

import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.factory.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.handler.ObjectGame;

/**
 * Default rasterable implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class RasterableModel
        extends TraitModel
        implements Rasterable
{
    /** Localizable reference. */
    private final Localizable localizable;
    /** Mirrorable reference. */
    private final Mirrorable mirrorable;
    /** Animator reference. */
    private final Animator animator;
    /** The viewer reference. */
    private final Viewer viewer;
    /** List of rastered frames. */
    private final List<SpriteAnimated> rastersAnim;
    /** Rastered flag. */
    private final boolean rastered;
    /** Smooth raster flag. */
    private final boolean smooth;
    /** Tile height. */
    private final int tileHeight;
    /** Last raster. */
    private SpriteAnimated raster;
    /** Index. */
    private int index;

    /**
     * Create a rasterable model. The owner must have the following traits:
     * <ul>
     * <li>{@link Localizable}</li>
     * <li>{@link Mirrorable}</li>
     * <li>{@link Animator}</li>
     * </ul>
     * 
     * @param owner The owner reference.
     * @param context The context reference.
     * @param setup The setup reference.
     * @param tileHeight The tile height value (must be strictly positive).
     * @throws LionEngineException If missing {@link Trait}.
     */
    public RasterableModel(ObjectGame owner, Services context, SetupSurfaceRastered setup, int tileHeight)
            throws LionEngineException
    {
        super(owner);

        Check.superiorStrict(tileHeight, 0);
        this.tileHeight = tileHeight;
        localizable = owner.getTrait(Localizable.class);
        mirrorable = owner.getTrait(Mirrorable.class);
        animator = owner.getTrait(Animator.class);
        viewer = context.get(Viewer.class);
        rastersAnim = setup.rastersAnim;
        rastered = setup.rasterFile != null;
        smooth = setup.smoothRaster;
    }

    /*
     * Rasterable
     */

    @Override
    public void update(double extrp)
    {
        index = getRasterIndex(localizable.getY());
        raster = getRasterAnim(index);
        if (raster != null)
        {
            raster.setFrame(animator.getFrame());
            raster.setMirror(mirrorable.getMirror());
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (raster != null)
        {
            final double x = viewer.getViewpointX(localizable.getX() - raster.getFrameWidth() / 2);
            final double y = viewer.getViewpointY(localizable.getY() + raster.getFrameHeight());
            raster.setLocation(x, y);
            raster.render(g);
        }
    }

    @Override
    public int getRasterIndex(double y)
    {
        final double value = y / tileHeight;
        final int i = (int) value % Rasterable.MAX_RASTERS_R;
        int index = i;

        if (!smooth && index > Rasterable.MAX_RASTERS_M)
        {
            index = Rasterable.MAX_RASTERS_M - (index - Rasterable.MAX_RASTERS);
        }
        return UtilMath.fixBetween(index, 0, Rasterable.MAX_RASTERS);
    }

    @Override
    public SpriteAnimated getRasterAnim(int rasterIndex)
    {
        Check.superiorOrEqual(rasterIndex, 0);
        return rastersAnim.get(rasterIndex);
    }

    @Override
    public boolean isRastered()
    {
        return rastered;
    }
}
