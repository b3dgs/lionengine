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
package com.b3dgs.lionengine.game.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Renderer component implementation which render {@link Displayable} elements with {@link Layerable} support to order
 * rendering. If there is not {@link Layerable} feature, {@link #LAYER_DEFAULT} will be used as default layer value.
 * The lower layer index is handled first.
 */
public class ComponentDisplayable implements ComponentRenderer, HandlerListener, LayerableListener
{
    /** Default layer value. */
    private static final Integer LAYER_DEFAULT = Integer.valueOf(0);

    /**
     * Get the featurable layer.
     * 
     * @param featurable The featurable reference.
     * @return The featurable layer if is {@link Layerable}, {@link #LAYER_DEFAULT} else.
     */
    private static Integer getLayer(Featurable featurable)
    {
        if (featurable.hasFeature(Layerable.class))
        {
            final Layerable layerable = featurable.getFeature(Layerable.class);
            return layerable.getLayerDisplay();
        }
        return LAYER_DEFAULT;
    }

    /** Sorted layers index. */
    private final List<Integer> indexs = new ArrayList<>();
    /** Sorted layers index. */
    private final Set<Integer> indexsSet = new HashSet<>();
    /** Layers to render. */
    private final Map<Integer, List<Displayable>> layers = new HashMap<>();
    /** Layer to update. */
    private final List<LayerUpdate> toUpdate = new ArrayList<>();
    /** Update flag. */
    private boolean updateRequested;

    /**
     * Create component.
     */
    public ComponentDisplayable()
    {
        super();
    }

    /**
     * Get the layer set at the specified index. Creates an empty set if no set already defined.
     * 
     * @param layer The layer index.
     * @return The layer set reference.
     */
    private List<Displayable> getLayer(Integer layer)
    {
        final List<Displayable> displayables;
        if (!layers.containsKey(layer))
        {
            displayables = new ArrayList<>();
            layers.put(layer, displayables);
        }
        else
        {
            displayables = layers.get(layer);
        }
        return displayables;
    }

    /**
     * Remove displayable and its layer.
     * 
     * @param layer The layer index.
     * @param displayable The displayable to remove.
     */
    private void remove(Integer layer, Displayable displayable)
    {
        final Collection<Displayable> displayables = getLayer(layer);
        displayables.remove(displayable);
        if (displayables.isEmpty())
        {
            indexs.remove(layer);
            indexsSet.remove(layer);
            Collections.sort(indexs);
        }
    }

    /*
     * ComponentRenderer
     */

    @Override
    public void render(Graphic g, Handlables featurables)
    {
        for (int l = 0; l < indexs.size(); l++)
        {
            final List<Displayable> displayables = layers.get(indexs.get(l));
            final int count = displayables.size();
            for (int i = 0; i < count; i++)
            {
                displayables.get(i).render(g);
            }
        }
        if (updateRequested)
        {
            final int count = toUpdate.size();
            for (int i = 0; i < count; i++)
            {
                final LayerUpdate update = toUpdate.get(i);

                getLayer(update.layerOld).remove(update.displayable);
                getLayer(update.layerNew).add(update.displayable);
                indexs.add(update.layerNew);
                indexsSet.add(update.layerNew);
            }
            Collections.sort(indexs);
            toUpdate.clear();
            updateRequested = false;
        }
    }

    /*
     * HandlerListener
     */

    @Override
    public void notifyHandlableAdded(Featurable featurable)
    {
        if (featurable.hasFeature(Displayable.class))
        {
            final Displayable displayable = featurable.getFeature(Displayable.class);
            final Integer layer = getLayer(featurable);
            final Collection<Displayable> displayables = getLayer(layer);
            displayables.add(displayable);
            if (!indexsSet.contains(layer))
            {
                indexs.add(layer);
                indexsSet.add(layer);
                Collections.sort(indexs);
            }
        }
        if (featurable.hasFeature(Layerable.class))
        {
            featurable.getFeature(Layerable.class).addListener(this);
        }
    }

    @Override
    public void notifyHandlableRemoved(Featurable featurable)
    {
        if (featurable.hasFeature(Displayable.class))
        {
            final Displayable displayable = featurable.getFeature(Displayable.class);
            final Integer layer = getLayer(featurable);
            remove(layer, displayable);
        }
        if (featurable.hasFeature(Layerable.class))
        {
            featurable.getFeature(Layerable.class).removeListener(this);
        }
    }

    /*
     * LayerableListener
     */

    @Override
    public void notifyLayerChanged(FeatureProvider provider,
                                   Integer layerRefreshOld,
                                   Integer layerRefreshNew,
                                   Integer layerDisplayOld,
                                   Integer layerDisplayNew)
    {
        if (provider.hasFeature(Displayable.class))
        {
            final Displayable displayable = provider.getFeature(Displayable.class);
            toUpdate.add(new LayerUpdate(displayable, layerDisplayOld, layerDisplayNew));
            updateRequested = true;
        }
    }

    /**
     * Layer update data.
     */
    private static final class LayerUpdate
    {
        /** Displayable reference. */
        private final Displayable displayable;
        /** Old layer. */
        private final Integer layerOld;
        /** New layer. */
        private final Integer layerNew;

        /**
         * Create data.
         * 
         * @param displayable The displayable reference.
         * @param layerOld The old layer.
         * @param layerNew The new layer.
         */
        private LayerUpdate(Displayable displayable, Integer layerOld, Integer layerNew)
        {
            super();

            this.displayable = displayable;
            this.layerOld = layerOld;
            this.layerNew = layerNew;
        }
    }
}
