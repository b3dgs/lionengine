package com.b3dgs.lionengine.example.f_network;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.b3dgs.lionengine.network.message.NetworkMessageEntity;
import com.b3dgs.lionengine.utility.UtilityConversion;

/**
 * Entity network message description.
 */
public class MessageEntity
        extends NetworkMessageEntity<MessageEntityElement>
{
    /**
     * Constructor (used in decoding case).
     */
    public MessageEntity()
    {
        super();
    }

    /**
     * Constructor (used in client case).
     * 
     * @param clientId The client id.
     */
    public MessageEntity(Byte clientId)
    {
        super(TypeMessage.MESSAGE_ENTITY, clientId.byteValue());
    }

    /**
     * Constructor (used in entity server case).
     * 
     * @param entityId The entity id.
     */
    public MessageEntity(short entityId)
    {
        super(TypeMessage.MESSAGE_ENTITY, entityId);
    }

    /**
     * Constructor (used in entity server case).
     * 
     * @param entityId The entity id.
     * @param destId The client destination id.
     */
    public MessageEntity(short entityId, byte destId)
    {
        super(TypeMessage.MESSAGE_ENTITY, entityId, destId);
    }

    @Override
    protected void encode(ByteArrayOutputStream buffer, MessageEntityElement key) throws IOException
    {
        buffer.write(MessageEntity.getKeyByte(key));
        // States
        if (key == MessageEntityElement.DOWN || key == MessageEntityElement.LEFT || key == MessageEntityElement.RIGHT
                || key == MessageEntityElement.UP)
        {
            buffer.write(getActionBoolean(key) ? 1 : 0);
        }
        // Location correction
        if (key == MessageEntityElement.LOCATION_X || key == MessageEntityElement.LOCATION_Y)
        {
            buffer.write(UtilityConversion.intToByteArray(getActionInteger(key)));
        }
    }

    @Override
    protected void decode(DataInputStream buffer, int i) throws IOException
    {
        final MessageEntityElement key = MessageEntity.getActionKey(buffer.readByte());

        // States
        if (key == MessageEntityElement.DOWN || key == MessageEntityElement.LEFT || key == MessageEntityElement.RIGHT
                || key == MessageEntityElement.UP)
        {
            this.addAction(key, buffer.readByte() == 0 ? false : true);
        }
        // Location correction
        if (key == MessageEntityElement.LOCATION_X || key == MessageEntityElement.LOCATION_Y)
        {
            this.addAction(key, buffer.readInt());
        }
    }

    /**
     * Get the value in byte of the enum.
     * 
     * @param value The enum value.
     * @return The byte value.
     */
    private static byte getKeyByte(MessageEntityElement value)
    {
        return (byte) value.ordinal();
    }

    /**
     * Get the key value from the byte value.
     * 
     * @param value The byte value.
     * @return The key value.
     */
    private static MessageEntityElement getActionKey(byte value)
    {
        return MessageEntityElement.fromOrdinal(value);
    }
}
