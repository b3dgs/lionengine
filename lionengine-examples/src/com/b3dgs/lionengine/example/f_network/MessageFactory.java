package com.b3dgs.lionengine.example.f_network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.b3dgs.lionengine.network.message.NetworkMessageEntity;

/**
 * Factory network message description.
 */
class MessageFactory
        extends NetworkMessageEntity<TypeEntity>
{
    /**
     * Constructor.
     */
    public MessageFactory()
    {
        super();
    }

    /**
     * Constructor.
     * 
     * @param entityId The entity id.
     */
    public MessageFactory(short entityId)
    {
        super(TypeMessage.MESSAGE_FACTORY, entityId);
    }

    /**
     * Constructor.
     * 
     * @param entityId The entity id.
     * @param destId The client destination id.
     */
    public MessageFactory(short entityId, byte destId)
    {
        super(TypeMessage.MESSAGE_FACTORY, entityId, destId);
    }

    @Override
    protected void encode(ByteArrayOutputStream buffer, TypeEntity key) throws IOException
    {
        buffer.write((byte) key.ordinal());
    }

    @Override
    protected void decode(DataInputStream buffer, int i) throws IOException
    {
        final TypeEntity type = TypeEntity.fromOrdinal(buffer.readByte());
        this.addAction(type, true);
    }
}
