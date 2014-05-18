/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.purview.Configurable;

/**
 * Game object minimal representation. Defined by a unique ID, the object is designed to be handled by a
 * {@link HandlerObjectGame}. To remove it from the handler, a simple call to {@link #destroy()} is needed. An object
 * can also be externally configured by using a {@link Configurable}, filled by an XML file.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurable
 * @see HandlerObjectGame
 */
public abstract class ObjectGame
        implements Configurable
{
    /** Setup error. */
    private static final String ERROR_SETUP = "Setup must not be null !";
    /** Id used. */
    private static final Set<Integer> IDS = new HashSet<>(16);
    /** Last id used. */
    private static int lastId = 1;

    /**
     * Get the next unused id.
     * 
     * @return The next unused id.
     */
    private static Integer getFreeId()
    {
        while (ObjectGame.IDS.contains(Integer.valueOf(ObjectGame.lastId)))
        {
            ObjectGame.lastId++;
        }
        return Integer.valueOf(ObjectGame.lastId);
    }

    /** Configurable object reference. */
    private final Configurable configurable;
    /** Entity id. */
    private final Integer id;
    /** Destroyed flag; true will remove it from the handler. */
    private boolean destroy;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public ObjectGame(SetupGame setup)
    {
        Check.notNull(setup, ObjectGame.ERROR_SETUP);
        configurable = setup.configurable;
        destroy = false;
        id = ObjectGame.getFreeId();
        ObjectGame.IDS.add(id);
    }

    /**
     * Get the entity id (unique).
     * 
     * @return The entity id.
     */
    public final Integer getId()
    {
        return id;
    }

    /**
     * Remove object from handler, and free memory.
     */
    public void destroy()
    {
        destroy = true;
        ObjectGame.IDS.remove(getId());
    }

    /**
     * Check if entity is going to be removed.
     * 
     * @return <code>true</code> if going to be removed, <code>false</code> else.
     */
    public boolean isDestroyed()
    {
        return destroy;
    }

    /*
     * Configurable
     */

    @Override
    public void loadData(Media media)
    {
        configurable.loadData(media);
    }

    @Override
    public XmlNode getDataRoot()
    {
        return configurable.getDataRoot();
    }

    @Override
    public String getText(String... path)
    {
        return configurable.getText(path);
    }

    @Override
    public String getDataString(String attribute, String... path)
    {
        return configurable.getDataString(attribute, path);
    }

    @Override
    public int getDataInteger(String attribute, String... path)
    {
        return configurable.getDataInteger(attribute, path);
    }

    @Override
    public boolean getDataBoolean(String attribute, String... path)
    {
        return configurable.getDataBoolean(attribute, path);
    }

    @Override
    public double getDataDouble(String attribute, String... path)
    {
        return configurable.getDataDouble(attribute, path);
    }

    @Override
    public Animation getDataAnimation(String name)
    {
        return configurable.getDataAnimation(name);
    }

    @Override
    public CollisionData getDataCollision(String name)
    {
        return configurable.getDataCollision(name);
    }
}
