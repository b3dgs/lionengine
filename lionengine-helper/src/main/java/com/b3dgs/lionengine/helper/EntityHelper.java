/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.feature.Transformable;
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
    private final Transformable transformable;
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

        addFeature(NetworkableModel.class, services, setup);
        addFeature(LayerableModel.class, services, setup);
        transformable = addFeature(TransformableModel.class, services, setup);
        addFeature(FovableModel.class, services, setup);
        addFeature(ActionerModel.class, services, setup);

        state = addFeature(new StateHandler(EntityHelper::getAnimationName, services, setup));
        body = addFeature(BodyModel.class, services, setup);
        mirrorable = addFeature(MirrorableModel.class, services, setup);
        animatable = addFeature(AnimatableModel.class, services, setup);
        rasterable = addFeature(RasterableModel.class, services, setup);
        collidable = addFeature(CollidableModel.class, services, setup);
        tileCollidable = addFeature(TileCollidableModel.class, services, setup);
        pathfindable = addFeature(PathfindableModel.class, services, setup);
        attacker = addFeature(AttackerModel.class, services, setup);
        extractor = addFeature(ExtractorModel.class, services, setup);
        producer = addFeature(ProducerModel.class, services, setup);
        launchable = addFeature(LaunchableModel.class, services, setup);
        launcher = addFeature(LauncherModel.class, services, setup);

        addFeature(ProducibleModel.class, services, setup);
        addFeature(ExtractableModel.class, services, setup);
        addFeature(CollidableFramedModel.class, services, setup);
        addFeature(SelectableModel.class, services, setup);

        checker = addFeature(new EntityChecker());
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

        tileCollidable.addListener((r, c) ->
        {
            final State current = state.getCurrent();
            if (current instanceof final StateHelper<?> s)
            {
                s.notifyTileCollided(r, c);
            }
        });
        collidable.addListener((c, w, b) ->
        {
            final State current = state.getCurrent();
            if (current instanceof final StateHelper<?> s)
            {
                s.notifyCollided(c, w, b);
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

    @Override
    public void addAfter(Services services, Setup setup)
    {
        final Routines routines = addFeature(Routines.class, services, setup);

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
            transformable.check(false);
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
        addFeature(new DisplayableModel(renderingCurrent::render));
    }
}
