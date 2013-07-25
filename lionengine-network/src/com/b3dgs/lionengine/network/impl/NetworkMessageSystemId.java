package com.b3dgs.lionengine.network.impl;

/**
 * List of messages system flag. This allows to filter data on message received depending of the case.
 */
interface NetworkMessageSystemId
{
    /** First connection step (client if preparing its connection with the server). */
    final byte CONNECTING = -120;
    /** Last connection step (client is now connected to the server properly). */
    final byte CONNECTED = -110;
    /** Ping message. */
    final byte PING = -105;
    /** Client is kicked. */
    final byte KICKED = -100;
    /** Inform about a new connected client. */
    final byte OTHER_CLIENT_CONNECTED = -90;
    /** Disconnect other client. */
    final byte OTHER_CLIENT_DISCONNECTED = -80;
    /** Other client Name changes. */
    final byte OTHER_CLIENT_RENAMED = -70;
    /** User message. */
    final byte USER_MESSAGE = -60;
}
