/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Optional;
import java.util.OptionalInt;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Network data.
 */
public class Network
{
    /** No network. */
    public static final Network NONE = new Network(NetworkType.NONE);

    private static final int ARG_TYPE = 0;
    private static final int ARG_IP = 1;
    private static final int ARG_PORT = 2;
    private static final int ARG_NAME = 3;

    /**
     * Create network from arguments.
     * 
     * @param args The arguments.
     * @return The network.
     */
    public static Network from(String[] args)
    {
        final Network network;
        final int length = args.length - 1;
        if (length == ARG_TYPE)
        {
            network = new Network(NetworkType.from(args[ARG_TYPE]));
        }
        else if (length == ARG_IP)
        {
            network = new Network(NetworkType.from(args[ARG_TYPE]), args[ARG_IP]);
        }
        else if (length == ARG_PORT)
        {
            network = new Network(NetworkType.from(args[ARG_TYPE]), args[ARG_IP], Integer.parseInt(args[ARG_PORT]));
        }
        else if (length >= ARG_NAME)
        {
            network = new Network(NetworkType.from(args[ARG_TYPE]),
                                  args[ARG_IP],
                                  Integer.parseInt(args[ARG_PORT]),
                                  args[ARG_NAME]);
        }
        else
        {
            throw new LionEngineException("Invalid network !");
        }
        return network;
    }

    private final NetworkType type;
    private final Optional<String> ip;
    private final OptionalInt port;
    private final Optional<String> name;

    /**
     * Create network.
     * 
     * @param type The type.
     */
    public Network(NetworkType type)
    {
        super();

        this.type = type;
        ip = Optional.empty();
        port = OptionalInt.empty();
        name = Optional.empty();
    }

    /**
     * Create network.
     * 
     * @param type The type.
     * @param ip The ip.
     */
    public Network(NetworkType type, String ip)
    {
        super();

        this.type = type;
        this.ip = Optional.ofNullable(ip);
        port = OptionalInt.empty();
        name = Optional.empty();
    }

    /**
     * Create network.
     * 
     * @param type The type.
     * @param ip The ip.
     * @param port The port.
     */
    public Network(NetworkType type, String ip, int port)
    {
        super();

        this.type = type;
        this.ip = Optional.ofNullable(ip);
        this.port = OptionalInt.of(port);
        name = Optional.empty();
    }

    /**
     * Create network.
     * 
     * @param type The type.
     * @param ip The ip.
     * @param port The port.
     * @param name The name.
     */
    public Network(NetworkType type, String ip, int port, String name)
    {
        super();

        this.type = type;
        this.ip = Optional.ofNullable(ip);
        this.port = OptionalInt.of(port);
        this.name = Optional.ofNullable(name);
    }

    /**
     * Check if is type.
     * 
     * @param type The type to check.
     * @return <code>true</code> if type, <code>false</code> else.
     */
    public boolean is(NetworkType type)
    {
        return this.type.equals(type);
    }

    /**
     * Get the type.
     * 
     * @return The type.
     */
    public NetworkType getType()
    {
        return type;
    }

    /**
     * Get the ip.
     * 
     * @return The ip.
     */
    public Optional<String> getIp()
    {
        return ip;
    }

    /**
     * Get the port.
     * 
     * @return The port.
     */
    public OptionalInt getPort()
    {
        return port;
    }

    /**
     * Get the name.
     * 
     * @return The name.
     */
    public Optional<String> getName()
    {
        return name;
    }
}
