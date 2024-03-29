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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.UpdatableVoid;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.ActionerModel;
import com.b3dgs.lionengine.game.feature.AnimatableModel;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.MirrorableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.RoutineRender;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.attackable.AttackerModel;
import com.b3dgs.lionengine.game.feature.body.BodyModel;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.collidable.framed.CollidableFramedModel;
import com.b3dgs.lionengine.game.feature.collidable.selector.SelectableModel;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableModel;
import com.b3dgs.lionengine.game.feature.launchable.LauncherModel;
import com.b3dgs.lionengine.game.feature.networkable.NetworkableModel;
import com.b3dgs.lionengine.game.feature.producible.ProducerModel;
import com.b3dgs.lionengine.game.feature.producible.ProducibleModel;
import com.b3dgs.lionengine.game.feature.rasterable.RasterableModel;
import com.b3dgs.lionengine.game.feature.rasterable.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.feature.state.State;
import com.b3dgs.lionengine.game.feature.state.StateHandler;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollidableModel;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractableModel;
import com.b3dgs.lionengine.game.feature.tile.map.extractable.ExtractorModel;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathfindableModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.fog.FovableModel;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.RenderableVoid;

/**
 * Entity helper base.
 */
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

    /** Updatable list. */
    private final List<RoutineUpdate> updatables = new ArrayList<>();
    /** Renderable list. */
    private final List<RoutineRender> renderables = new ArrayList<>();
    /** Updating routine. */
    private final Updatable updating;
    /** Rendering routine. */
    private final Renderable rendering;
    /** Current updating. */
    private Updatable updatingCurrent;
    /** Current rendering. */
    private Renderable renderingCurrent;

    /**
     * Create entity.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    // CHECKSTYLE IGNORE LINE: C
    public EntityHelper(Services services, SetupSurfaceRastered setup)
    {
        super(services, setup);

        updating = extrp ->
        {
            final int n = updatables.size();
            for (int i = 0; i < n; i++)
            {
                updatables.get(i).updateBefore();
            }
            for (int i = 0; i < n; i++)
            {
                updatables.get(i).update(extrp);
            }
            for (int i = 0; i < n; i++)
            {
                updatables.get(i).updateAfter();
            }
        };
        rendering = g ->
        {
            final int n = renderables.size();
            for (int i = 0; i < n; i++)
            {
                renderables.get(i).render(g);
            }
        };

        updatingCurrent = updating;
        renderingCurrent = rendering;

        final EntityChecker checker = addFeature(new EntityChecker());
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

        addFeature(new RefreshableModel(extrp ->
        {
            checker.update(extrp);
            updatingCurrent.update(extrp);
        }));
        addFeature(new DisplayableModel(renderingCurrent::render));
        addFeature(NetworkableModel.class, services, setup);
        addFeature(LayerableModel.class, services, setup);
        addFeature(TransformableModel.class, services, setup);
        addFeature(FovableModel.class, services, setup);
        addFeature(ActionerModel.class, services, setup);
        addFeature(BodyModel.class, services, setup);
        addFeature(MirrorableModel.class, services, setup);
        addFeature(AnimatableModel.class, services, setup);
        addFeature(RasterableModel.class, services, setup);
        addFeature(PathfindableModel.class, services, setup);
        addFeature(AttackerModel.class, services, setup);
        addFeature(ExtractorModel.class, services, setup);
        addFeature(ProducerModel.class, services, setup);
        addFeature(LaunchableModel.class, services, setup);
        addFeature(LauncherModel.class, services, setup);
        addFeature(ProducibleModel.class, services, setup);
        addFeature(ExtractableModel.class, services, setup);
        addFeature(SelectableModel.class, services, setup);

        final StateHandler state = addFeature(new StateHandler(EntityHelper::getAnimationName, services, setup));

        final TileCollidable tileCollidable = addFeature(TileCollidableModel.class, services, setup);
        tileCollidable.addListener((r, c) ->
        {
            final State current = state.getCurrent();
            if (current instanceof final StateHelper<?> s)
            {
                s.notifyTileCollided(r, c);
            }
        });

        final Collidable collidable = addFeature(CollidableModel.class, services, setup);
        collidable.addListener((c, w, b) ->
        {
            final State current = state.getCurrent();
            if (current instanceof final StateHelper<?> s)
            {
                s.notifyCollided(c, w, b);
            }
        });
        addFeature(CollidableFramedModel.class, services, setup);
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
    public void prepare()
    {
        for (final Feature feature : getFeatures())
        {
            if (feature instanceof final RoutineUpdate routine)
            {
                updatables.add(routine);
            }
            if (feature instanceof final RoutineRender routine)
            {
                renderables.add(routine);
            }
        }
        Collections.sort(updatables, RoutineUpdate::compare);
        Collections.sort(renderables, RoutineRender::compare);
    }
}
