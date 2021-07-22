/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.OriginConfig;
import com.b3dgs.lionengine.game.SurfaceConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.raster.RasterImage;

/**
 * Rasterable model implementation.
 */
public class RasterableModel extends FeatureModel implements Rasterable, Recyclable
{
    /** List of rastered frames. */
    private final List<SpriteAnimated> rastersAnim = new ArrayList<>(RasterImage.MAX_RASTERS);
    /** The viewer reference. */
    private final Viewer viewer;
    /** Setup raster. */
    private final SetupSurfaceRastered setupRastered;
    /** Frame transform. */
    private FrameTransform transform;
    /** Raster media. */
    private Optional<Media> media = Optional.empty();
    /** The updater. */
    private Updatable updater;
    /** Raster height. */
    private int rasterHeight;
    /** Last raster. */
    private SpriteAnimated raster;
    /** Raster count. */
    private int count;
    /** Origin value. */
    private Origin origin;
    /** Anim offset. */
    private int animOffset;
    /** Anim offset 2. */
    private int animOffset2;
    /** Frame offsets x. */
    private int frameOffsetX;
    /** Frame offsets y. */
    private int frameOffsetY;
    /** Enabled flag. */
    private boolean enabled = true;
    /** Visibility flag. */
    private boolean visible = true;

    /** Transformable reference. */
    private Transformable transformable;
    /** Mirrorable reference. */
    private Mirrorable mirrorable;
    /** Animatable reference. */
    private Animatable animatable;

    /**
     * Create feature.
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
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public RasterableModel(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        setupRastered = setup;
        viewer = services.get(Viewer.class);

        origin = OriginConfig.imports(setup);

        final FramesConfig framesData = FramesConfig.imports(setup);
        final int hf = framesData.getHorizontal();
        final int vf = framesData.getVertical();

        if (setup.hasNode(SurfaceConfig.NODE_SURFACE))
        {
            rastersAnim.add(Drawable.loadSpriteAnimated(setup.getSurface(), hf, vf));
        }
        else
        {
            visible = false;
            enabled = false;
        }
        rasterHeight = setup.getRasterHeight();

        count = -1;
        final List<ImageBuffer> buffers = setup.getRasters();
        final int n = buffers.size();
        for (int i = 0; i < n; i++)
        {
            final SpriteAnimated sprite = Drawable.loadSpriteAnimated(buffers.get(i), hf, vf);
            rastersAnim.add(sprite);
            count++;
        }
        if (rastersAnim.size() < 2)
        {
            updater = UpdatableVoid.getInstance();
        }
        else
        {
            updater = extrp -> updateRasterAnim();
        }
        frameOffsetX = framesData.getOffsetX();
        frameOffsetY = framesData.getOffsetY();

        if (visible)
        {
            raster = rastersAnim.get(0);
        }
    }

    /**
     * Update raster sprite with current vertical location.
     */
    private void updateRasterAnim()
    {
        if (enabled)
        {
            final int index = getRasterIndex((transformable.getY() - transformable.getHeight()) / rasterHeight);
            raster = rastersAnim.get(UtilMath.clamp(index, 0, count));
        }
        else
        {
            raster = rastersAnim.get(0);
        }
    }

    /**
     * Update raster.
     */
    private void updateRaster()
    {
        if (transform != null)
        {
            final Animation anim = animatable.getAnim();
            if (anim != null)
            {
                raster.setFrame(transform.transform(anim.getName(), animatable.getFrameAnim()));
            }
        }
        else
        {
            raster.setFrame(animatable.getFrame() + animOffset + animOffset2);
        }
        raster.setMirror(mirrorable.getMirror());
        raster.setOrigin(origin);
        raster.setFrameOffsets(frameOffsetX, frameOffsetY);
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

        if (visible)
        {
            updateRaster();
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (visible)
        {
            raster.setLocation(viewer, transformable);
            raster.render(g);
        }
    }

    @Override
    public void setAnimTransform(FrameTransform transform)
    {
        this.transform = transform;
    }

    @Override
    public int getRasterIndex(double y)
    {
        int index = (int) y / RasterImage.LINES_PER_RASTER;
        if (index > RasterImage.MAX_RASTERS - 1)
        {
            index = RasterImage.MAX_RASTERS - 1;
        }
        return index + 1;
    }

    @Override
    public SpriteAnimated getRasterAnim(int rasterIndex)
    {
        Check.superiorOrEqual(rasterIndex, 0);

        return rastersAnim.get(rasterIndex);
    }

    @Override
    public int getAnimOffset()
    {
        return animOffset;
    }

    @Override
    public int getAnimOffset2()
    {
        return animOffset2;
    }

    @Override
    public void setRaster(boolean save, Media media, int rasterHeight)
    {
        setRaster(save, media, rasterHeight, Collections.emptyList());
    }

    @Override
    public void setRaster(boolean save, Media media, int rasterHeight, Collection<Integer> allowed)
    {
        if (setupRastered.isExtern())
        {
            Check.notNull(media);
            Check.superiorStrict(rasterHeight, 0);

            this.media = Optional.of(media);
            this.rasterHeight = rasterHeight;

            setupRastered.load(save, media, allowed);
            rastersAnim.clear();

            count = -1;
            final List<ImageBuffer> buffers = setupRastered.getRasters();
            final int n = buffers.size();
            for (int i = 0; i < n; i++)
            {
                final SpriteAnimated sprite = Drawable.loadSpriteAnimated(buffers.get(i),
                                                                          raster.getFramesHorizontal(),
                                                                          raster.getFramesVertical());
                rastersAnim.add(sprite);
                count++;
            }
            if (rastersAnim.size() < 2)
            {
                updater = UpdatableVoid.getInstance();
            }
            else
            {
                updater = extrp -> updateRasterAnim();
            }
        }
    }

    @Override
    public Optional<Media> getMedia()
    {
        return media;
    }

    @Override
    public void setAnimOffset(int offset)
    {
        animOffset = offset;
    }

    @Override
    public void setAnimOffset2(int offset)
    {
        animOffset2 = offset;
    }

    @Override
    public void setFrameOffsets(int offsetX, int offsetY)
    {
        frameOffsetX = offsetX;
        frameOffsetY = offsetY;
    }

    @Override
    public void setOrigin(Origin origin)
    {
        Check.notNull(origin);

        this.origin = origin;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public void setVisibility(boolean visible)
    {
        this.visible = visible;
    }

    @Override
    public boolean isVisible()
    {
        return visible;
    }

    /*
     * Recyclable
     */

    @Override
    public void recycle()
    {
        animOffset = 0;
        animOffset2 = 0;
    }
}
