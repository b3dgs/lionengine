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
package com.b3dgs.lionengine.game.handler;

import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Feature;

/**
 * Handlable model implementation.
 */
public class HandlableModel implements Handlable
{
    /** The identifiable model. */
    private final Identifiable identifiable;
    /** The featurable model. */
    private final Featurable featurable;

    /**
     * Create model.
     * 
     * @param identifiable The identifiable reference.
     * @param featurable The featurable reference.
     */
    public HandlableModel(Identifiable identifiable, Featurable featurable)
    {
        this.identifiable = identifiable;
        this.featurable = featurable;
    }

    /*
     * Handlable
     */

    @Override
    public void addListener(IdentifiableListener listener)
    {
        identifiable.addListener(listener);
    }

    @Override
    public void removeListener(IdentifiableListener listener)
    {
        identifiable.removeListener(listener);
    }

    @Override
    public Integer getId()
    {
        return identifiable.getId();
    }

    @Override
    public void destroy()
    {
        identifiable.destroy();
    }

    @Override
    public void notifyDestroyed()
    {
        identifiable.notifyDestroyed();
    }

    @Override
    public void prepareFeatures(Handlable owner, Services services)
    {
        featurable.prepareFeatures(owner, services);
    }

    @Override
    public void addFeature(Feature feature)
    {
        featurable.addFeature(feature);
    }

    @Override
    public <C extends Feature> C getFeature(Class<C> feature)
    {
        return featurable.getFeature(feature);
    }

    @Override
    public Iterable<Feature> getFeatures()
    {
        return featurable.getFeatures();
    }

    @Override
    public Iterable<Class<? extends Feature>> getFeaturesType()
    {
        return featurable.getFeaturesType();
    }

    @Override
    public boolean hasFeature(Class<? extends Feature> feature)
    {
        return featurable.hasFeature(feature);
    }

}
