/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.collidable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Represents the collidable data.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @see Collidable
 */
public final class CollidableConfig
{
    /** Collidable node. */
    public static final String NODE_COLLIDABLE = Constant.XML_PREFIX + "collidable";
    /** Collidable group attribute name. */
    public static final String ATT_GROUP = "group";
    /** Collidable accepted groups attribute name. */
    public static final String ATT_ACCEPTED = "accepted";
    /** Default group. */
    public static final Integer DEFAULT_GROUP = Integer.valueOf(0);
    /** Error invalid group. */
    static final String ERROR_INVALID_GROUP = "Invalid group: ";
    /** Accepted separator. */
    private static final String ACCEPTED_SEPARATOR = Constant.PERCENT;
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 38;

    /**
     * Create the collidable data from node.
     * 
     * @param configurer The configurer reference (must not be <code>null</code>).
     * @return The config loaded.
     * @throws LionEngineException If unable to read node.
     */
    public static CollidableConfig imports(Configurer configurer)
    {
        Check.notNull(configurer);

        if (configurer.hasNode(NODE_COLLIDABLE))
        {
            try
            {
                final Integer group = Integer.valueOf(configurer.getIntegerDefault(DEFAULT_GROUP.intValue(),
                                                                                   ATT_GROUP,
                                                                                   NODE_COLLIDABLE));
                final String accepted = configurer.getStringDefault(Constant.EMPTY_STRING,
                                                                    ATT_ACCEPTED,
                                                                    NODE_COLLIDABLE);
                final Collection<Integer> acceptedGroups;
                if (accepted.isEmpty())
                {
                    acceptedGroups = new ArrayList<>();
                }
                else
                {
                    acceptedGroups = Arrays.asList(accepted.split(ACCEPTED_SEPARATOR))
                                           .stream()
                                           .map(Integer::valueOf)
                                           .collect(Collectors.toSet());
                }
                return new CollidableConfig(group, acceptedGroups);
            }
            catch (final NumberFormatException exception)
            {
                throw new LionEngineException(exception, ERROR_INVALID_GROUP);
            }
        }
        return new CollidableConfig(DEFAULT_GROUP, Collections.emptySet());
    }

    /**
     * Create an XML node from a collidable.
     * 
     * @param root The node root (must not be <code>null</code>).
     * @param collidable The collidable reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public static void exports(Xml root, Collidable collidable)
    {
        Check.notNull(root);
        Check.notNull(collidable);

        final Xml node = root.createChild(NODE_COLLIDABLE);
        node.writeInteger(ATT_GROUP, collidable.getGroup().intValue());

        final StringBuilder accepted = new StringBuilder();
        int count = collidable.getAccepted().size();
        for (final Integer group : collidable.getAccepted())
        {
            accepted.append(group);
            count--;
            if (count > 1)
            {
                accepted.append(ACCEPTED_SEPARATOR);
            }
        }
        node.writeString(ATT_ACCEPTED, accepted.toString());
    }

    /** The defined group. */
    private final Integer group;
    /** The accepted groups. */
    private final Collection<Integer> accepted;

    /**
     * Private constructor.
     * 
     * @param group The defined group.
     * @param accepted The accepted groups.
     */
    public CollidableConfig(Integer group, Collection<Integer> accepted)
    {
        super();

        this.group = group;
        this.accepted = accepted;
    }

    /**
     * Get the defined group.
     * 
     * @return The defined group.
     */
    public Integer getGroup()
    {
        return group;
    }

    /**
     * Get the accepted groups.
     * 
     * @return The accepted groups.
     */
    public Collection<Integer> getAccepted()
    {
        return accepted;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + group.hashCode();
        result = prime * result + accepted.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final CollidableConfig other = (CollidableConfig) object;
        return group.equals(other.getGroup())
               && accepted.containsAll(other.getAccepted())
               && other.getAccepted().containsAll(accepted);
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [group=")
                                            .append(group)
                                            .append(", accepted=")
                                            .append(accepted)
                                            .append("]")
                                            .toString();
    }
}
