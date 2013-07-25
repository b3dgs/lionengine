package com.b3dgs.lionengine.network.message;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Message chat implementation.
 */
public class NetworkMessageChat
        extends NetworkMessage
{
    /** The message. */
    private String message;

    /**
     * Default constructor.
     */
    public NetworkMessageChat()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param type The message type.
     * @param clientId The client id.
     * @param message The message content.
     */
    public NetworkMessageChat(Enum<?> type, byte clientId, String message)
    {
        this(type, clientId, (byte) -1, message);
    }

    /**
     * Constructor.
     * 
     * @param type The message type.
     * @param clientId The client id.
     * @param clientDestId The client destination.
     * @param message The message content.
     */
    public NetworkMessageChat(Enum<?> type, byte clientId, byte clientDestId, String message)
    {
        super(type, clientId, clientDestId);
        this.message = message;
    }

    /**
     * Get the message.
     * 
     * @return The message reference.
     */
    public String getMessage()
    {
        return message;
    }

    @Override
    protected void encode(ByteArrayOutputStream buffer) throws IOException
    {
        buffer.write(message.getBytes());
    }

    @Override
    protected void decode(DataInputStream buffer) throws IOException
    {
        final byte[] msg = new byte[buffer.available()];
        buffer.readFully(msg);
        message = new String(msg);
    }
}
