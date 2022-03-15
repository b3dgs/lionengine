/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.helper;

import java.util.Locale;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.feature.ActionerModel;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Mirrorable;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Routines;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.attackable.Attacker;
import com.b3dgs.lionengine.game.feature.attackable.AttackerModel;
import com.b3dgs.lionengine.game.feature.body.Body;
import com.b3dgs.lionengine.game.feature.body.BodyModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.framed.CollidableFramedModel;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectableModel;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableModel;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.launchable.LauncherModel;
import com.b3dgs.lionengine.game.feature.networkable.NetworkableModel;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerModel;
import com.b3dgs.lionengine.game.feature.producible.ProducibleModel;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.State;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableModel;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractableModel;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.Extractor;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FovableModel;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;

/**
 * Entity helper base.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling|FanOutComplexity
public class EntityHelper extends FeaturableModel
{
    private static final int PREFIX = State.class.getSimpleName().length();

    /**
     * Get animation name from state class.
     * 
     * @param state The state class.
     * @return The animation name.
     */
    public static String getAnimationName(Class<? extends State> state)
    {
        return state.getSimpleName().substring(PREFIX).toLowerCase(Locale.ENGLISH);
    }

    private final EntityChecker checker;
    private final Body body;
    private final StateHandler state;
    private final Mirrorable mirrorable;
    private final Animatable animatable;
    private final Rasterable rasterable;
    private final Collidable collidable;
    private final TileCollidable tileCollidable;
    private final Pathfindable pathfindable;
    private final Attacker attacker;
    private final Extractor extractor;
    private final Producer producer;
    private final Launchable launchable;
    private final Launcher launcher;

    private Updatable updating;
    private Renderable rendering;

    private Updatable updatingCurrent;
    private Renderable renderingCurrent;

    /**
     * Create entity.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public EntityHelper(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        addFeature(new NetworkableModel(services, setup));
        addFeature(new LayerableModel(services, setup));
        addFeature(new TransformableModel(services, setup));
        addFeature(new FovableModel(services, setup));
        addFeature(new ActionerModel(services, setup));

        state = addFeatureAndGet(new StateHandler(services, setup, EntityHelper::getAnimationName));
        body = addFeatureAndGet(new BodyModel(services, setup));
        mirrorable = addFeatureAndGet(new MirrorableModel(services, setup));
        animatable = addFeatureAndGet(new AnimatableModel(services, setup));
        rasterable = addFeatureAndGet(new RasterableModel(services, setup));
        collidable = addFeatureAndGet(new CollidableModel(services, setup));
        tileCollidable = addFeatureAndGet(new TileCollidableModel(services, setup));
        pathfindable = addFeatureAndGet(new PathfindableModel(services, setup));
        attacker = addFeatureAndGet(new AttackerModel(services, setup));
        extractor = addFeatureAndGet(new ExtractorModel(services, setup));
        producer = addFeatureAndGet(new ProducerModel(services, setup));
        launchable = addFeatureAndGet(new LaunchableModel(services, setup));
        launcher = addFeatureAndGet(new LauncherModel(services, setup));

        addFeature(new ProducibleModel(services, setup));
        addFeature(new ExtractableModel(services, setup));
        addFeature(new CollidableFramedModel(services, setup));
        addFeature(new SelectableModel(services, setup));

        checker = addFeatureAndGet(new EntityChecker());
        checker.addListener(new EntityCheckerListener()
        {
            @Override
            public void notifyCheckedUpdate(boolean checked)
            {
                onCheckedUpdate(checked);
            }

            @Override
            public void notifyCheckedRender(boolean checked)
            {
                onCheckedRender(checked);
            }
        });
    }

    /**
     * Called on checked update.
     * 
     * @param checked The flag.
     */
    private void onCheckedUpdate(boolean checked)
    {
        if (checked)
        {
            updatingCurrent = updating;
        }
        else
        {
            updatingCurrent = UpdatableVoid.getInstance();
        }
    }

    /**
     * Called on checked render.
     * 
     * @param checked The flag.
     */
    private void onCheckedRender(boolean checked)
    {
        if (checked)
        {
            renderingCurrent = rendering;
        }
        else
        {
            renderingCurrent = RenderableVoid.getInstance();
        }
    }

    /*
     * Featurable
     */

    @Override
    public void addAfter(Services services, Setup setup)
    {
        final Routines routines = addFeatureAndGet(new Routines(services, setup));

        updating = extrp ->
        {
            state.update(extrp);
            attacker.update(extrp);
            extractor.update(extrp);
            producer.update(extrp);
            launchable.update(extrp);
            launcher.update(extrp);
            routines.update(extrp);
            body.update(extrp);
            pathfindable.update(extrp);
            tileCollidable.update(extrp);
            state.postUpdate();
            mirrorable.update(extrp);
            animatable.update(extrp);
            rasterable.update(extrp);
        };

        rendering = g ->
        {
            pathfindable.render(g);
            rasterable.render(g);
            collidable.render(g);
            routines.render(g);
        };

        updatingCurrent = updating;
        renderingCurrent = rendering;

        addFeature(new RefreshableModel(extrp ->
        {
            checker.update(extrp);
            updatingCurrent.update(extrp);
        }));
        addFeature(new DisplayableModel(g ->
        {
            renderingCurrent.render(g);
        }));
    }
}
