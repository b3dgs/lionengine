/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.it.feature.rasterable;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.ComponentDisplayable;
import com.b3dgs.lionengine.game.feature.ComponentRefreshable;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.Sequence;

/**
 * Integration test for rasterable model.
 */
public final class SceneRasterable extends Sequence
{
    private final Tick tick = new Tick();
    private final Services services = new Services();
    private final Camera camera = services.create(Camera.class);
    private final Handler handler = services.create(Handler.class);

    private int count;

    /**
     * Constructor.
     * 
     * @param context The context reference.
     */
    public SceneRasterable(Context context)
    {
        super(context, new Resolution(320, 240, 60));

        handler.addComponent(new ComponentRefreshable());
        handler.addComponent(new ComponentDisplayable());
    }

    private void add(SetupSurfaceRastered setup, int offsetX)
    {
        final SpriteAnimated surface = Drawable.loadSpriteAnimated(setup.getSurface(), 4, 4);
        final Featurable featurable = new FeaturableModel(services, setup);
        final Transformable transformable = featurable.addFeature(TransformableModel.class, services, setup);
        final Mirrorable mirrorable = featurable.addFeature(MirrorableModel.class, services, setup);
        final Animatable animatable = featurable.addFeature(new AnimatableModel(surface, services, setup));

        final Rasterable rasterable = new RasterableModel(services, setup, transformable, mirrorable, animatable);
        rasterable.setOrigin(Origin.MIDDLE);
        rasterable.setFrameOffsets(1, 2);
        featurable.addFeature(rasterable);
        featurable.addFeature(new RefreshableModel(extrp ->
        {
            transformable.setLocationY(UtilMath.sin(count) * 240);
            surface.setLocation(camera, transformable);
            rasterable.update(extrp);
            surface.update(extrp);
        }));
        featurable.addFeature(new DisplayableModel(g -> rasterable.render(g)));

        transformable.setLocationX(120 + offsetX);
        handler.add(featurable);
    }

    @Override
    public void load()
    {
        add(new SetupSurfaceRastered(Medias.create("Object.xml"), Medias.create("tiles.png")), 0);
        add(new SetupSurfaceRastered(Medias.create("Object2.xml")), 64);
        add(new SetupSurfaceRastered(Medias.create("Object3.xml")), 128);
        camera.setView(0, 0, getWidth(), getHeight(), getHeight());

        tick.start();
    }

    @Override
    public void update(double extrp)
    {
        handler.update(extrp);
        tick.update(extrp);
        count += 6;
        if (tick.elapsed(20L))
        {
            end();
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
