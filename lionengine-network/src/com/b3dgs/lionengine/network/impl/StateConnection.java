package com.b3dgs.lionengine.network.impl;

/**
 * List of different connection state.
 */
enum StateConnection
{
    /** Connecting state (first exchanges between server and client). */
    CONNECTING,
    /** Connected state (server approved the new client). */
    CONNECTED,
    /** Disconnected state. */
    DISCONNECTED;
}
