/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.IOException;
import java.util.Optional;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Persistable;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Handle the {@link Handler} persistence by providing saving and loading function.
 * The following data are used when features are available:
 * <ul>
 * <li>{@link Featurable#getMedia()} as {@link String}</li>
 * <li>{@link Transformable#getX()} as {@link Integer}</li>
 * <li>{@link Transformable#getY()} as {@link Integer}</li>
 * </ul>
 */
public class HandlerPersister implements Persistable
{
    /** Factory reference. */
    protected final Factory factory;
    /** Handler reference. */
    protected final Handler handler;
    /** Map reference. */
    protected final Optional<MapTile> map;

    /**
     * Create a handler persister.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link Factory}</li>
     * <li>{@link Handler}</li>
     * </ul>
     * <p>
     * The {@link Services} may provide:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     * 
     * @param services The services reference.
     */
    public HandlerPersister(Services services)
    {
        super();

        factory = services.get(Factory.class);
        handler = services.get(Handler.class);
        map = services.getOptional(MapTile.class);
    }

    /**
     * Create featurable from media.
     * 
     * @param media The media reference.
     * @return The created featurable.
     */
    protected Featurable create(Media media)
    {
        return factory.create(media);
    }

    /**
     * Save transformable feature if has.
     * 
     * @param featurable The featurable reference.
     * @param output The output writing.
     * @throws IOException If error.
     */
    private void saveTransformable(Featurable featurable, FileWriting output) throws IOException
    {
        if (featurable.hasFeature(Transformable.class))
        {
            final Transformable transformable = featurable.getFeature(Transformable.class);
            final int x;
            final int y;
            if (map.isPresent())
            {
                x = map.get().getInTileX(transformable);
                y = map.get().getInTileY(transformable);
            }
            else
            {
                x = (int) Math.floor(transformable.getX());
                y = (int) Math.floor(transformable.getY());
            }
            output.writeInteger(x);
            output.writeInteger(y);
        }
    }

    /**
     * Load transformable feature if has.
     * 
     * @param featurable The featurable reference.
     * @param input The input reading.
     * @throws IOException If error.
     */
    private void loadTransformable(Featurable featurable, FileReading input) throws IOException
    {
        if (featurable.hasFeature(Transformable.class))
        {
            final Transformable transformable = featurable.getFeature(Transformable.class);
            final double x = input.readInteger();
            final double y = input.readInteger();
            if (map.isPresent())
            {
                transformable.teleport(x * map.get().getTileWidth(), y * map.get().getTileHeight());
            }
            else
            {
                transformable.teleport(x, y);
            }
        }
    }

    /*
     * Persistable
     */

    @Override
    public void save(FileWriting writing) throws IOException
    {
        writing.writeInteger(handler.size());
        for (final Featurable featurable : handler.values())
        {
            writing.writeString(featurable.getMedia().getPath());
            saveTransformable(featurable, writing);
        }
    }

    @Override
    public void load(FileReading reading) throws IOException
    {
        final int count = reading.readInteger();
        for (int i = 0; i < count; i++)
        {
            final Media media = Medias.create(reading.readString());
            final Featurable featurable = create(media);
            loadTransformable(featurable, reading);
            handler.add(featurable);
        }
    }
}
