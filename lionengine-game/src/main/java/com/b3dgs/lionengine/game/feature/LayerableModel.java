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
package com.b3dgs.lionengine.game.feature;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Layerable model implementation.
 */
public class LayerableModel extends FeatureModel implements Layerable
{
    /** Layers listener. */
    private final Collection<LayerableListener> listeners = new ArrayList<LayerableListener>();
    /** Layer refresh value. */
    private Integer layerRefresh = Integer.valueOf(0);
    /** Layer display value. */
    private Integer layerDisplay = layerRefresh;

    /**
     * Create a layerable model.
     */
    public LayerableModel()
    {
        super();
    }

    /**
     * Create a layerable model.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link LayerableListener}</li>
     * </ul>
     * 
     * @param services The services reference.
     */
    public LayerableModel(Services services)
    {
        super();

        addListener(services.get(LayerableListener.class));
    }

    /**
     * Create a layerable model.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link LayerableListener}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public LayerableModel(Services services, Setup setup)
    {
        this(services);

        if (setup.hasNode(LayerableConfig.NODE_LAYERABLE))
        {
            final LayerableConfig config = LayerableConfig.imports(setup);
            layerRefresh = Integer.valueOf(config.getLayerRefresh());
            layerDisplay = Integer.valueOf(config.getLayerDisplay());
        }
    }

    /**
     * Create a layerable model.
     * 
     * @param layer The default layer refresh and display value.
     */
    public LayerableModel(int layer)
    {
        this(layer, layer);
    }

    /**
     * Create a layerable model.
     * 
     * @param layerRefresh The default layer refresh value.
     * @param layerDisplay The default layer display value.
     */
    public LayerableModel(int layerRefresh, int layerDisplay)
    {
        super();

        this.layerRefresh = Integer.valueOf(layerRefresh);
        this.layerDisplay = Integer.valueOf(layerDisplay);
    }

    /*
     * Layerable
     */

    @Override
    public final void addListener(LayerableListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void setLayer(int layer)
    {
        setLayer(layer, layer);
    }

    @Override
    public void setLayer(int layerRefresh, int layerDisplay)
    {
        setLayer(Integer.valueOf(layerRefresh), Integer.valueOf(layerDisplay));
    }

    @Override
    public void setLayer(Integer layerRefresh, Integer layerDisplay)
    {
        for (final LayerableListener listener : listeners)
        {
            listener.notifyLayerChanged(this, this.layerRefresh, layerRefresh, this.layerDisplay, layerDisplay);
        }
        this.layerRefresh = layerRefresh;
        this.layerDisplay = layerDisplay;
    }

    @Override
    public Integer getLayerRefresh()
    {
        return layerRefresh;
    }

    @Override
    public Integer getLayerDisplay()
    {
        return layerDisplay;
    }
}
