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
package com.b3dgs.lionengine.example.game.production;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerListener;
import com.b3dgs.lionengine.game.feature.rasterable.Rasterable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;

/**
 * Soldier entity implementation.
 */
@FeatureInterface
public final class Soldier extends FeatureModel implements ProducerListener
{
    /** Media reference. */
    public static final Media MEDIA = Medias.create("OrcSoldier.xml");

    private final MapTile map = services.get(MapTile.class);

    private final Rasterable rasterable;
    private final Pathfindable pathfindable;

    /**
     * Create a peon.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param rasterable The rasterable feature.
     * @param producer The producer feature.
     * @param pathfindable The pathfindable feature.
     */
    public Soldier(Services services, Setup setup, Rasterable rasterable, Producer producer, Pathfindable pathfindable)
    {
        super(services, setup);

        this.rasterable = rasterable;
        this.pathfindable = pathfindable;

        producer.setStepsSpeed(0.02);
        producer.setChecker(featurable -> pathfindable.isDestinationReached());

        pathfindable.setLocation(6, 5);
    }

    @Override
    public void notifyCanNotProduce(Featurable featurable)
    {
        // Nothing to do
    }

    @Override
    public void notifyStartProduction(Featurable featurable)
    {
        rasterable.setVisibility(false);
    }

    @Override
    public void notifyProducing(Featurable featurable)
    {
        // Nothing to do
    }

    @Override
    public void notifyProduced(Featurable featurable)
    {
        final Pathfindable source = featurable.getFeature(Pathfindable.class);
        final CoordTile coord = map.getFeature(MapTilePath.class).getFreeTileAround(pathfindable, source);
        pathfindable.setLocation(coord);

        rasterable.setVisibility(true);
    }
}
