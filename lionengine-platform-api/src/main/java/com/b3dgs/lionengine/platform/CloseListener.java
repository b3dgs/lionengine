/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.platform;

/**
 * Listener of close requests, for example to ask the player for confirmation (ADR-0031, REQ-PLATFORM-002).
 *
 * <p>
 * A close request stops the loop unless a listener vetoes it. Thread: runtime thread, at inbox delivery.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface CloseListener
{
    /**
     * Called when the user or the system asks to close the application.
     *
     * @return {@code true} to accept the close, {@code false} to veto it.
     */
    boolean onCloseRequested();
}
