/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable.selector;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Action;
import com.b3dgs.lionengine.game.ActionRef;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.feature.Actionable;
import com.b3dgs.lionengine.game.feature.Actioner;
import com.b3dgs.lionengine.game.feature.Displayable;
import com.b3dgs.lionengine.game.feature.DisplayableModel;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Feature;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.RefreshableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Hud featurable implementation, containing a surface image, a {@link Selector} and menus handling.
 */
public class Hud extends FeaturableModel
{
    /**
     * Get all actions in common.
     * 
     * @param selection The current selection.
     * @return The actions in common.
     */
    private static Collection<ActionRef> getActionsInCommon(List<Selectable> selection)
    {
        final Collection<ActionRef> actions = new HashSet<ActionRef>();
        final int n = selection.size();
        for (int i = 0; i < n; i++)
        {
            final Selectable selectable = selection.get(i);
            if (selectable.hasFeature(Actioner.class))
            {
                final Actioner actioner = selectable.getFeature(Actioner.class);
                checkActionsInCommon(actioner, actions);
            }
        }
        return actions;
    }

    /**
     * Get all actions in common.
     * 
     * @param actioner The current selectable.
     * @param actions The collected actions in common.
     */
    private static void checkActionsInCommon(Actioner actioner, Collection<ActionRef> actions)
    {
        if (actions.isEmpty())
        {
            actions.addAll(actioner.getActions());
        }
        else
        {
            actions.retainAll(actioner.getActions());
        }
    }

    /** Selector reference. */
    protected final Selector selector;
    /** Hud surface. */
    protected final SpriteAnimated surface;
    /** Current active menus. */
    private final Collection<Featurable> menus = new ArrayList<Featurable>();
    /** Previous menus. */
    private final Map<ActionRef, Collection<ActionRef>> previous = new HashMap<ActionRef, Collection<ActionRef>>();
    /** Handler reference. */
    private final Handler handler;
    /** Factory reference. */
    private final Factory factory;

    /**
     * Create the HUD.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Hud(Services services, Setup setup)
    {
        super(services, setup);

        handler = services.get(Handler.class);
        factory = services.get(Factory.class);

        selector = services.add(new Selector(services));
        selector.addListener(new SelectorListener()
        {
            @Override
            public void notifySelectionStarted(Rectangle selection)
            {
                clearMenus();
            }

            @Override
            public void notifySelectionDone(Rectangle selection)
            {
                // Nothing to do
            }
        });
        selector.addListener(new SelectionListener()
        {
            @Override
            public void notifySelected(List<Selectable> selection)
            {
                createMenus(new ArrayList<ActionRef>(0), getActionsInCommon(selection));
            }
        });
        handler.add(selector);

        addFeature(new LayerableModel(services, setup));

        final int h;
        final int v;
        if (setup.hasNode(FramesConfig.NODE_FRAMES))
        {
            final FramesConfig config = FramesConfig.imports(setup);
            h = config.getHorizontal();
            v = config.getVertical();
        }
        else
        {
            h = 1;
            v = 1;
        }
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), h, v);
        surface.prepare();

        addFeature(new RefreshableModel(new Updatable()
        {
            @Override
            public void update(double extrp)
            {
                selector.update(extrp);
            }
        }));
        addFeature(new DisplayableModel(new Renderable()
        {
            @Override
            public void render(Graphic g)
            {
                surface.render(g);
                for (final Feature feature : getFeatures())
                {
                    if (feature instanceof Renderable && !(feature instanceof Displayable))
                    {
                        ((Renderable) feature).render(g);
                    }
                }
            }
        }));
    }

    /**
     * Clear current menus.
     */
    private void clearMenus()
    {
        for (final Featurable menu : menus)
        {
            menu.getFeature(Identifiable.class).destroy();
            handler.remove(menu);
        }
        menus.clear();
    }

    /**
     * Create menus from actions.
     * 
     * @param parents The parents menu.
     * @param actions The actions to create as menu.
     */
    private void createMenus(Collection<ActionRef> parents, Collection<ActionRef> actions)
    {
        for (final ActionRef action : actions)
        {
            final Featurable menu = createMenu(action);
            if (!action.getRefs().isEmpty())
            {
                generateSubMenu(actions, action, menu);
            }
            else if (action.getCancel())
            {
                previous.put(action, parents);
                generateCancel(action, menu);
            }
        }
        for (final Featurable current : menus)
        {
            handler.add(current);
        }
    }

    /**
     * Create menu from action and add to active menus.
     * 
     * @param action The action used.
     * @return The created menu.
     */
    private Featurable createMenu(ActionRef action)
    {
        final Featurable menu = factory.create(Medias.create(action.getPath().split(File.pathSeparator)));
        menus.add(menu);
        return menu;
    }

    /**
     * Generate sub menu creation if menu contains sub menu.
     * 
     * @param parents The parents menu.
     * @param action The current action.
     * @param menu The current menu to check.
     */
    private void generateSubMenu(final Collection<ActionRef> parents, final ActionRef action, Featurable menu)
    {
        menu.getFeature(Actionable.class).setAction(new Action()
        {
            @Override
            public void execute()
            {
                clearMenus();
                createMenus(parents, action.getRefs());
            }
        });
    }

    /**
     * Generate cancel to go back.
     * 
     * @param action The associated action.
     * @param menu The current menu to check.
     */
    private void generateCancel(final ActionRef action, Featurable menu)
    {
        menu.getFeature(Actionable.class).setAction(new Action()
        {
            @Override
            public void execute()
            {
                clearMenus();
                final Collection<ActionRef> parents = previous.get(action);
                createMenus(parents, parents);
            }
        });
    }

    /*
     * FeaturableModel
     */

    @Override
    public void checkListener(Object listener)
    {
        if (listener instanceof SelectionListener)
        {
            selector.addListener((SelectionListener) listener);
        }
    }
}
