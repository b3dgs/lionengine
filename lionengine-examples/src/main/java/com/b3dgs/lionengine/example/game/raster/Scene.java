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
package com.b3dgs.lionengine.example.game.raster;

import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Resolution;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.awt.Keyboard;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.animatable.AnimatableModel;
import com.b3dgs.lionengine.game.feature.displayable.DisplayableModel;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;
import com.b3dgs.lionengine.game.feature.mirrorable.MirrorableModel;
import com.b3dgs.lionengine.game.feature.refreshable.RefreshableModel;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.game.handler.ComponentDisplayable;
import com.b3dgs.lionengine.game.handler.ComponentRefreshable;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewer;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.game.raster.MapTileRastered;
import com.b3dgs.lionengine.game.raster.MapTileRasteredModel;
import com.b3dgs.lionengine.game.raster.Rasterable;
import com.b3dgs.lionengine.game.raster.RasterableModel;
import com.b3dgs.lionengine.game.raster.SetupSurfaceRastered;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Game loop designed to handle our world.
 * 
 * @see com.b3dgs.lionengine.example.core.minimal
 */
class Scene extends Sequence
{
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    private final Services services = new Services();
    private final Camera camera = services.create(Camera.class);
    private final Handler handler = services.create(Handler.class);
    private final MapTile map = services.create(MapTileGame.class);
    private final Mouse mouse;

    private boolean useRaster = true;
    private int count;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public Scene(Context context)
    {
        super(context, NATIVE);

        getInputDevice(Keyboard.class).addActionPressed(Keyboard.ESCAPE, () -> end());
        mouse = getInputDevice(Mouse.class);

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
    }

    @Override
    public void load()
    {
        final MapTileViewer mapViewer = map.addFeatureAndGet(new MapTileViewerModel());
        final MapTileRastered mapRaster = map.addFeatureAndGet(new MapTileRasteredModel());
        handler.add(map);
        map.create(Medias.create("level.png"), 16, 16, 16);
        mapRaster.loadSheets(Medias.create("raster.xml"), false);
        mapViewer.addRenderer(mapRaster);

        camera.setView(0, 0, getWidth(), getHeight(), getHeight());
        camera.setLimits(map);

        final SetupSurfaceRastered setup = new SetupSurfaceRastered(Medias.create("object.xml"),
                                                                    Medias.create("raster.xml"));
        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 4, 4);
        final Featurable featurable = new FeaturableModel();
        featurable.addFeature(new LayerableModel(1));
        featurable.addFeature(new MirrorableModel());
        featurable.addFeature(new AnimatableModel(surface));

        final Transformable transformable = featurable.addFeatureAndGet(new TransformableModel());
        final Rasterable rasterable = new RasterableModel(setup);
        featurable.addFeature(rasterable);
        featurable.addFeature(new RefreshableModel(extrp ->
        {
            transformable.setLocationY(UtilMath.sin(count) * 120 + 160);
            surface.setLocation(camera, transformable);
            rasterable.update(extrp);
            surface.update(extrp);
        }));
        featurable.addFeature(new DisplayableModel(g ->
        {
            if (useRaster)
            {
                rasterable.render(g);
            }
            else
            {
                surface.render(g);
            }
        }));

        transformable.setLocationX(120);
        handler.add(featurable);
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
        count++;
        if (mouse.hasClickedOnce(Mouse.LEFT))
        {
            useRaster = !useRaster;
            if (useRaster)
            {
                map.getFeature(MapTileViewer.class).addRenderer(map.getFeature(MapTileRastered.class));
            }
            else
            {
                map.getFeature(MapTileViewer.class).removeRenderer(map.getFeature(MapTileRastered.class));
            }
        }
    }

    @Override
    public void render(Graphic g)
    {
        g.clear(0, 0, getWidth(), getHeight());
        handler.render(g);
    }

    @Override
    public void onTerminated(boolean hasNextSequence)
    {
        Engine.terminate();
    }
}
