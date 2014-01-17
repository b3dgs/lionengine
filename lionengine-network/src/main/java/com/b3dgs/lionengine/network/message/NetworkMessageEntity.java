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
package com.b3dgs.lionengine.network.message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.b3dgs.lionengine.UtilityConversion;

/**
 * Standard entity message.
 * 
 * @param <M> The message entity element enum.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class NetworkMessageEntity<M extends Enum<M>>
        extends NetworkMessage
{
    /** List of actions. */
    private final HashMap<M, Object> actions = new HashMap<>(1);
    /** Entity number. */
    private short entityId;

    /**
     * Constructor (used in decoding case).
     */
    public NetworkMessageEntity()
    {
        super();
        this.entityId = -1;
    }

    /**
     * Constructor (used for the client case).
     * 
     * @param type The message type.
     * @param clientId The client id.
     */
    public NetworkMessageEntity(Enum<?> type, byte clientId)
    {
        super(type, clientId);
        this.entityId = -1;
    }

    /**
     * Constructor (used to identify an entity from the server).
     * 
     * @param type The message type.
     * @param entityId The entity id.
     */
    public NetworkMessageEntity(Enum<?> type, short entityId)
    {
        super(type, (byte) -1);
        this.entityId = entityId;
    }

    /**
     * Constructor (used to identify an entity from the server).
     * 
     * @param type The message type.
     * @param entityId The entity id.
     * @param destId The client destination.
     */
    public NetworkMessageEntity(Enum<?> type, short entityId, byte destId)
    {
        super(type, (byte) -1, destId);
        this.entityId = entityId;
    }

    /**
     * Encode function for the current key.
     * 
     * @param buffer The current buffer to write.
     * @param key The current key.
     * @throws IOException Exception in case of error.
     */
    protected abstract void encode(ByteArrayOutputStream buffer, M key) throws IOException;

    /**
     * Decode function for the current key number.
     * 
     * @param buffer The current buffer to read.
     * @param i The current key number.
     * @throws IOException Exception in case of error.
     */
    protected abstract void decode(DataInputStream buffer, int i) throws IOException;

    /**
     * Add an action.
     * 
     * @param element The action type.
     * @param value The action value.
     */
    public void addAction(M element, boolean value)
    {
        actions.put(element, Boolean.valueOf(value));
    }

    /**
     * Add an action.
     * 
     * @param element The action type.
     * @param value The action value.
     */
    public void addAction(M element, char value)
    {
        actions.put(element, Character.valueOf(value));
    }

    /**
     * Add an action.
     * 
     * @param element The action type.
     * @param value The action value.
     */
    public void addAction(M element, byte value)
    {
        actions.put(element, Byte.valueOf(value));
    }

    /**
     * Add an action.
     * 
     * @param element The action type.
     * @param value The action value.
     */
    public void addAction(M element, short value)
    {
        actions.put(element, Short.valueOf(value));
    }

    /**
     * Add an action.
     * 
     * @param element The action type.
     * @param value The action value.
     */
    public void addAction(M element, int value)
    {
        actions.put(element, Integer.valueOf(value));
    }

    /**
     * Add an action.
     * 
     * @param element The action type.
     * @param value The action value.
     */
    public void addAction(M element, double value)
    {
        actions.put(element, Double.valueOf(value));
    }

    /**
     * Get the action value.
     * 
     * @param element The action element.
     * @return The action value.
     */
    public boolean getActionBoolean(M element)
    {
        return ((Boolean) actions.get(element)).booleanValue();
    }

    /**
     * Get the action value.
     * 
     * @param element The action element.
     * @return The action value.
     */
    public byte getActionByte(M element)
    {
        return ((Byte) actions.get(element)).byteValue();
    }

    /**
     * Get the action value.
     * 
     * @param element The action element.
     * @return The action value.
     */
    public char getActionChar(M element)
    {
        return ((Character) actions.get(element)).charValue();
    }

    /**
     * Get the action value.
     * 
     * @param element The action element.
     * @return The action value.
     */
    public short getActionShort(M element)
    {
        return ((Short) actions.get(element)).shortValue();
    }

    /**
     * Get the action value.
     * 
     * @param element The action element.
     * @return The action value.
     */
    public int getActionInteger(M element)
    {
        return ((Integer) actions.get(element)).intValue();
    }

    /**
     * Get the action value.
     * 
     * @param element The action element.
     * @return The action value.
     */
    public double getActionDouble(M element)
    {
        return ((Double) actions.get(element)).doubleValue();
    }

    /**
     * Check if the action is contained.
     * 
     * @param element The action to check.
     * @return <code>true</code> if action is contained, <code>false</code> else.
     */
    public boolean hasAction(M element)
    {
        return actions.containsKey(element);
    }

    /**
     * Get the entity id (-1 if none).
     * 
     * @return The entity id.
     */
    public short getEntityId()
    {
        return entityId;
    }

    /*
     * NetworkMessage
     */

    /**
     * Retrieve the keys, store its total number in the buffer, and call {@link #encode(ByteArrayOutputStream, Enum) for
     * each key}.
     * 
     * @param buffer The current buffer to write.
     * @throws IOException Exception in case of error.
     */
    @Override
    protected void encode(ByteArrayOutputStream buffer) throws IOException
    {
        buffer.write(UtilityConversion.shortToByteArray(entityId));
        final Set<M> keys = actions.keySet();

        // Fill the data
        buffer.write((byte) keys.size());
        for (final M key : keys)
        {
            this.encode(buffer, key);
        }
    }

    /**
     * Read the first byte to retrieve the total number of key and call {@link #decode(DataInputStream, int) for each
     * key}.
     * 
     * @param buffer The current buffer to read.
     * @throws IOException Exception in case of error.
     */
    @Override
    protected void decode(DataInputStream buffer) throws IOException
    {
        entityId = buffer.readShort();
        final int number = buffer.readByte();
        for (int i = 0; i < number; i++)
        {
            decode(buffer, i);
        }
    }
}
