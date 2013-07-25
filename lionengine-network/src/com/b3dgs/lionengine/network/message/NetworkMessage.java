package com.b3dgs.lionengine.network.message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Network message description.
 */
public abstract class NetworkMessage
{
    /** The message type (should be an enum ordinal to make the id clean). */
    private byte type;
    /** Id of the client who sent this message. */
    private byte clientId;
    /** Id of the client to sent this message. */
    private byte clientDestId;

    /**
     * Default constructor.
     */
    public NetworkMessage()
    {
        // Nothing to do
    }

    /**
     * Constructor.
     * 
     * @param type the message type.
     * @param clientId The client id.
     */
    public NetworkMessage(Enum<?> type, byte clientId)
    {
        this(type, clientId, (byte) -1);
    }

    /**
     * Constructor.
     * 
     * @param type the message type.
     * @param clientId The client id.
     * @param clientDestId The client destination id (-1 if all).
     */
    public NetworkMessage(Enum<?> type, byte clientId, byte clientDestId)
    {
        this.type = (byte) type.ordinal();
        this.clientId = clientId;
        this.clientDestId = clientDestId;
    }

    /**
     * Get the message type.
     * 
     * @return The message type.
     */
    public final byte getType()
    {
        return type;
    }

    /**
     * Get the owner if of this message.
     * 
     * @return The owner id of this message.
     */
    public final byte getClientId()
    {
        return clientId;
    }

    /**
     * Get the destination of this message.
     * 
     * @return The destination of this message.
     */
    public final byte getClientDestId()
    {
        return clientDestId;
    }

    /**
     * Encode the message.
     * 
     * @return The encoded message data.
     * @throws IOException Error on writing.
     */
    public final ByteArrayOutputStream encode() throws IOException
    {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        buffer.write(type);
        buffer.write(clientId);
        buffer.write(clientDestId);
        this.encode(buffer);

        return buffer;
    }

    /**
     * Decode the message from the data.
     * 
     * @param type The message type.
     * @param from The client id from.
     * @param dest The client id destination.
     * @param buffer The data reference.
     * @throws IOException Error on reading.
     */
    public final void decode(byte type, byte from, byte dest, DataInputStream buffer) throws IOException
    {
        this.type = type;
        clientId = from;
        clientDestId = dest;
        this.decode(buffer);
    }

    /**
     * Encode the message.
     * 
     * @param buffer The current buffer.
     * @throws IOException Error on writing.
     */
    protected abstract void encode(ByteArrayOutputStream buffer) throws IOException;

    /**
     * Decode the message from the data.
     * 
     * @param buffer The data reference.
     * @throws IOException Error on reading.
     */
    protected abstract void decode(DataInputStream buffer) throws IOException;
}
