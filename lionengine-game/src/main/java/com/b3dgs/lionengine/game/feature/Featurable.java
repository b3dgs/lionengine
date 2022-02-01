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
package com.b3dgs.lionengine.game.feature;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Represents something that can have a collection of {@link Feature}.
 * <p>
 * Such a system allows to reduce direct complexity by splitting object implementation into different classes. Each will
 * provide a specific sub system, called a {@link Feature}. They will compose a complex system, called a
 * {@link Featurable}.
 * </p>
 * <p>
 * <b>Problematic</b>
 * </p>
 * <p>
 * Implementation of an object, which may move and can collide other objects which may not all move.
 * </p>
 * <p>
 * <b>Naive solution</b>
 * </p>
 * <p>
 * One big object, implementing moving and colliding.
 * </p>
 * <p>
 * <b>Feature solution</b>
 * </p>
 * <p>
 * Sub system implementation, sub system composition:
 * </p>
 * <ul>
 * <li>Localization feature: <i>Simple representation of something which have a coordinate</i></li>
 * <li>Moving feature: <i>System dedicated to handle the movement of something</i></li>
 * <li>Colliding feature: <i>System dedicated to collision representation and detection</i></li>
 * <li>Little object: <i>Supporting moving and colliding feature independently</i></li>
 * </ul>
 * <p>
 * This way, our object is now very simple, as it only declares its required features. Implementations are localized in
 * specific classes, which helps to avoid <i>god class</i> if our object want to also jump and throw something.
 * </p>
 * <p>
 * <b>Caution</b>
 * </p>
 * <p>
 * The counterpart of such a system is the low typing of our final object, as it is only known at runtime, even if they
 * are statically declared. An object is just a set of <i>something</i>, which can lead to undesired exceptions if not
 * used correctly. Documentation must explicit the required {@link Feature}, in order to use an object properly.
 * </p>
 * <p>
 * An alternative could be direct {@link Feature} implementation, combined with simple delegate.
 * </p>
 */
public interface Featurable extends FeatureProvider
{
    /**
     * Check if features auto loading from configuration file is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    default boolean isLoadFeaturesEnabled()
    {
        return true;
    }

    /**
     * Add features after read from config.
     * Does nothing by default.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    default void addAfter(Services services, Setup setup)
    {
        // Nothing by default
    }

    /**
     * Add a feature.
     * 
     * @param feature The feature to add.
     */
    void addFeature(Feature feature);

    /**
     * Add a feature.
     * 
     * @param feature The feature to add.
     * @param overwrite <code>true</code> to overwrite existing feature, <code>false</code> else.
     */
    void addFeature(Feature feature, boolean overwrite);

    /**
     * Add a feature and retrieve it. Read all {@link FeaturableConfig#NODE_FEATURE} nodes.
     * 
     * @param <T> The feature type.
     * @param feature The feature to add.
     * @return The added feature (same as source).
     */
    <T extends Feature> T addFeatureAndGet(T feature);

    /**
     * Check object interface listening and add them automatically. If the {@link Feature} provide listeners, this will
     * allow to add them automatically.
     * 
     * @param listener The listener to check.
     */
    void checkListener(Object listener);

    /**
     * Get the associated media.
     * 
     * @return The associated media, <code>null</code> if none.
     */
    Media getMedia();
}
