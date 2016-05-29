/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.raster;

import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.animatable.Animatable;
import com.b3dgs.lionengine.game.feature.mirrorable.Mirrorable;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Default rasterable implementation.
 */
public class RasterableModel extends FeatureModel implements Rasterable
{
    /** List of rastered frames. */
    private final List<SpriteAnimated> rastersAnim;
    /** Smooth raster flag. */
    private final boolean smooth;
    /** Raster height. */
    private final int height;
    /** The viewer reference. */
    private Viewer viewer;
    /** Transformable reference. */
    private Transformable transformable;
    /** Mirrorable reference. */
    private Mirrorable mirrorable;
    /** Animator reference. */
    private Animator animator;
    /** Last raster. */
    private SpriteAnimated raster;
    /** Origin value. */
    private Origin origin = Origin.TOP_LEFT;

    /**
     * Create a rasterable model.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Viewer}</li>
     * </ul>
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link Transformable}</li>
     * <li>{@link Mirrorable}</li>
     * <li>{@link Animatable}</li>
     * </ul>
     * 
     * @param setup The setup reference.
     */
    public RasterableModel(SetupSurfaceRastered setup)
    {
        super();

        Check.notNull(setup);

        height = setup.getRasterHeight();
        rastersAnim = setup.getRasters();
        smooth = setup.hasSmooth();
    }

    /*
     * Rasterable
     */

    @Override
    public void prepare(Featurable owner, Services services)
    {
        transformable = owner.getFeature(Transformable.class);
        mirrorable = owner.getFeature(Mirrorable.class);
        animator = owner.getFeature(Animatable.class);
        viewer = services.get(Viewer.class);
    }

    @Override
    public void update(double extrp)
    {
        final int index = getRasterIndex(transformable.getY());
        raster = getRasterAnim(index);
        if (raster != null)
        {
            raster.setFrame(animator.getFrame());
            raster.setMirror(mirrorable.getMirror());
            raster.setOrigin(origin);
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (raster != null)
        {
            final double x = viewer.getViewpointX(origin.getX(transformable.getX(), transformable.getWidth()));
            final double y = viewer.getViewpointY(origin.getY(transformable.getY(), transformable.getHeight()));
            raster.setLocation(x, y);
            raster.render(g);
        }
    }

    @Override
    public int getRasterIndex(double y)
    {
        final double value = y / height;
        final int i = (int) value % Rasterable.MAX_RASTERS_R;
        int index = i;

        if (!smooth && index > Rasterable.MAX_RASTERS_M)
        {
            index = Rasterable.MAX_RASTERS_M - (index - Rasterable.MAX_RASTERS);
        }
        return UtilMath.clamp(index, 0, Rasterable.MAX_RASTERS);
    }

    @Override
    public SpriteAnimated getRasterAnim(int rasterIndex)
    {
        Check.superiorOrEqual(rasterIndex, 0);

        return rastersAnim.get(rasterIndex);
    }

    @Override
    public void setOrigin(Origin origin)
    {
        Check.notNull(origin);

        this.origin = origin;
    }
}
