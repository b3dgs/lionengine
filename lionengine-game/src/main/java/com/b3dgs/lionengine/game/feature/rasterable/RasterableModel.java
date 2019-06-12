/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.rasterable;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.raster.RasterImage;

/**
 * Default rasterable implementation.
 */
@FeatureInterface
public class RasterableModel extends FeatureModel implements Rasterable
{
    /** List of rastered frames. */
    private final List<SpriteAnimated> rastersAnim = new ArrayList<>(RasterImage.MAX_RASTERS);
    /** Smooth raster flag. */
    private final boolean smooth;
    /** Raster height. */
    private final int height;
    /** The viewer reference. */
    private final Viewer viewer;
    /** The updater. */
    private final Updatable updater;
    /** Transformable reference. */
    private Transformable transformable;
    /** Mirrorable reference. */
    private Mirrorable mirrorable;
    /** Animatable reference. */
    private Animatable animatable;
    /** Last raster. */
    private SpriteAnimated raster;
    /** Origin value. */
    private Origin origin;
    /** Frame offsets x. */
    private int frameOffsetX;
    /** Frame offsets y. */
    private int frameOffsetY;

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
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public RasterableModel(Services services, SetupSurfaceRastered setup)
    {
        super();

        Check.notNull(setup);

        viewer = services.get(Viewer.class);

        height = setup.getRasterHeight();
        smooth = setup.hasSmooth();

        final FramesConfig framesData = FramesConfig.imports(setup);
        final int hf = framesData.getHorizontal();
        final int vf = framesData.getVertical();

        for (final ImageBuffer buffer : setup.getRasters())
        {
            final SpriteAnimated sprite = Drawable.loadSpriteAnimated(buffer, hf, vf);
            rastersAnim.add(sprite);
        }
        if (rastersAnim.isEmpty())
        {
            rastersAnim.add(Drawable.loadSpriteAnimated(setup.getSurface(), hf, vf));
            updater = UpdatableVoid.getInstance();
        }
        else
        {
            updater = extrp -> updateRasterAnim();
        }
        frameOffsetX = framesData.getOffsetX();
        frameOffsetY = framesData.getOffsetY();

        raster = rastersAnim.get(0);
    }

    @Override
    public void setFrameOffsets(int offsetX, int offsetY)
    {
        frameOffsetX = offsetX;
        frameOffsetY = offsetY;
    }

    /**
     * Update raster sprite with current vertical location.
     */
    private void updateRasterAnim()
    {
        final int index = getRasterIndex(transformable.getY());
        if (index >= 0)
        {
            raster = rastersAnim.get(index);
        }
    }

    /*
     * Rasterable
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        transformable = provider.getFeature(Transformable.class);
        mirrorable = provider.getFeature(Mirrorable.class);
        animatable = provider.getFeature(Animatable.class);
    }

    @Override
    public void update(double extrp)
    {
        updater.update(extrp);

        raster.setFrame(animatable.getFrame());
        raster.setMirror(mirrorable.getMirror());
        raster.setOrigin(origin);
        raster.setFrameOffsets(frameOffsetX, frameOffsetY);
    }

    @Override
    public void render(Graphic g)
    {
        raster.setLocation(viewer, transformable);
        raster.render(g);
    }

    @Override
    public int getRasterIndex(double y)
    {
        int index = (int) y / height % RasterImage.MAX_RASTERS_R;
        if (!smooth && index > RasterImage.MAX_RASTERS_M)
        {
            index = RasterImage.MAX_RASTERS_M - (index - RasterImage.MAX_RASTERS);
        }
        return index;
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
