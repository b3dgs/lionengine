/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.network.client.InfoGet;

/**
 * Network utility.
 */
public final class UtilNetwork
{
    /** Header first byte. */
    public static final int HEADER_START_VALUE = 0x96;
    /** Header bytes number. */
    public static final int HEADER_BYTES_NUMBER = 3;

    /** Header start index. */
    public static final int HEADER_INDEX_START = 0;
    /** Header packet number index. */
    public static final int HEADER_INDEX_SEQUENCE = HEADER_INDEX_START + 1;
    /** Header total message bytes index. */
    public static final int HEADER_INDEX_SIZE = HEADER_INDEX_SEQUENCE + 1;

    /** Header message type index. */
    public static final int INDEX_TYPE = 0;
    /** Header client id index. */
    public static final int INDEX_CLIENT_ID = INDEX_TYPE + 1;
    /** Header mode index. */
    public static final int INDEX_MODE = INDEX_CLIENT_ID + 1;
    /** Header client source id index. */
    public static final int INDEX_CLIENT_SRC_ID = INDEX_MODE + 1;
    /** Header data id index. */
    public static final int INDEX_DATA_ID = INDEX_CLIENT_SRC_ID + 1;

    /** Mode direct. */
    public static final int MODE_DIRECT = 0;
    /** Mode data. */
    public static final int MODE_DATA = 1;
    /** Mode disconnect. */
    public static final int MODE_DISCONNECT = MODE_DATA + 1;

    /** Max packet buffer. */
    private static final int MAX_SIZE = 256;

    /** Server id. */
    public static final Integer SERVER_ID = Integer.valueOf(0);

    /**
     * Decode expected message.
     * 
     * @param socket The socket to read.
     * @return The packet read.
     * @throws IOException If invalid.
     */
    public static DatagramPacket receive(DatagramSocket socket) throws IOException
    {
        final byte[] data = new byte[MAX_SIZE];
        final DatagramPacket packet = new DatagramPacket(data, data.length);
        socket.receive(packet);

        if (UtilConversion.toUnsignedByte(data[UtilNetwork.HEADER_INDEX_START]) != UtilNetwork.HEADER_START_VALUE
            || UtilConversion.toUnsignedByte(data[UtilNetwork.HEADER_INDEX_SEQUENCE]) != 0)
        {
            throw new IOException("Invalid message: "
                                  + UtilConversion.toUnsignedByte(data[UtilNetwork.HEADER_INDEX_START])
                                  + " "
                                  + UtilConversion.toUnsignedByte(data[UtilNetwork.HEADER_INDEX_SEQUENCE])
                                  + "/"
                                  + 0);
        }

        return packet;
    }

    /**
     * Get the packet data.
     * 
     * @param packet The packet reference.
     * @return The packet data.
     */
    public static ByteBuffer getBuffer(DatagramPacket packet)
    {
        final byte[] data = packet.getData();
        final int size = UtilConversion.toUnsignedByte(data[UtilNetwork.HEADER_INDEX_SIZE]);
        final ByteBuffer buffer = ByteBuffer.allocate(size);
        buffer.put(data, HEADER_BYTES_NUMBER, size);

        return buffer;
    }

    /**
     * Get the server info.
     * 
     * @param ip The server ip.
     * @param port The server port.
     * @return The info data.
     * @throws IOException If error.
     */
    public static ByteBuffer getInfo(String ip, int port) throws IOException
    {
        try (DatagramSocket socket = new DatagramSocket())
        {
            final ByteBuffer buffer = ByteBuffer.allocate(1);
            buffer.put(UtilNetwork.toByte(MessageType.INFO));
            final ByteBuffer send = UtilNetwork.createPacket(buffer);
            socket.send(new DatagramPacket(send.array(), send.capacity(), InetAddress.getByName(ip), port));

            return InfoGet.decode(socket);
        }
    }

    /**
     * Create full packet from data with header.
     * 
     * @param buffer The packet content.
     * @return The packet with header and its content.
     */
    public static ByteBuffer createPacket(ByteBuffer buffer)
    {
        final int size = buffer.capacity();
        final ByteBuffer send = ByteBuffer.allocate(size + UtilNetwork.HEADER_BYTES_NUMBER);

        send.put(UtilConversion.fromUnsignedByte(UtilNetwork.HEADER_START_VALUE));
        send.put(UtilConversion.fromUnsignedByte(0));
        send.put(UtilConversion.fromUnsignedByte(size));
        send.put(buffer.array());

        return send;
    }

    /**
     * Send message.
     * 
     * @param socket The socket reference.
     * @param address The address destination.
     * @param port The port destination.
     * @param message The message reference.
     * @throws IOException If error.
     */
    public static void send(DatagramSocket socket, InetAddress address, int port, Message message) throws IOException
    {
        final ByteBuffer send = UtilNetwork.createPacket(message.create());
        socket.send(new DatagramPacket(send.array(), send.capacity(), address, port));
    }

    /**
     * Await task.
     * 
     * @param thread The task to await for.
     */
    public static void await(Thread thread)
    {
        try
        {
            thread.join();
        }
        catch (@SuppressWarnings("unused") final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Check expected buffer value.
     * 
     * @param buffer The buffer reference.
     * @param position The byte position.
     * @param expected The expected value at position.
     * @return <code>true</code> if equals, <code>false</code> else.
     */
    public static boolean equalsByte(ByteBuffer buffer, int position, int expected)
    {
        return UtilConversion.toUnsignedByte(buffer.get(position)) != expected;
    }

    /**
     * Check expected buffer value.
     * 
     * @param buffer The buffer reference.
     * @param position The byte position.
     * @param expected The expected value at position.
     * @return <code>true</code> if equals, <code>false</code> else.
     */
    public static boolean equalsByte(ByteBuffer buffer, int position, Enum<?> expected)
    {
        return UtilConversion.toUnsignedByte(buffer.get(position)) != expected.ordinal();
    }

    /**
     * Check expected buffer value.
     * 
     * @param buffer The buffer reference.
     * @param position The integer position.
     * @param expected The expected value at position.
     * @return <code>true</code> if equals, <code>false</code> else.
     */
    public static boolean equalsInt(ByteBuffer buffer, int position, int expected)
    {
        return buffer.getInt(position) != expected;
    }

    /**
     * Convert enum to byte ordinal.
     * 
     * @param value The enum value.
     * @return The enum ordinal as byte.
     */
    public static byte toByte(Enum<?> value)
    {
        return UtilConversion.fromUnsignedByte(value.ordinal());
    }

    /**
     * Show ip and port.
     * 
     * @param packet The packet.
     * @return The info.
     */
    public static String toString(DatagramPacket packet)
    {
        return toString(packet.getAddress().toString(), packet.getPort());
    }

    /**
     * Show ip and port.
     * 
     * @param ip The ip address.
     * @param port The port value.
     * @return The info.
     */
    public static String toString(String ip, int port)
    {
        return ip + Constant.DOUBLE_DOT + port;
    }

    /**
     * Private.
     */
    private UtilNetwork()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
