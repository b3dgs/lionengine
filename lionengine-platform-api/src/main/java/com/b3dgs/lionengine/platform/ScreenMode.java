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
 * How the output surface occupies the screen (ADR-0002, ADR-0006, REQ-DISPLAY-001, REQ-DISPLAY-002).
 *
 * <p>
 * A request for an unsupported mode falls back, and the effective mode and reason are reported, never thrown:
 * {@link #FULL_SCREEN} then {@link #FULL_WINDOW} then {@link #WINDOWED}; {@link #WINDOWED} then {@link #FULL_WINDOW}
 * then {@link #FULL_SCREEN}; {@link #FULL_WINDOW} then {@link #WINDOWED} then {@link #FULL_SCREEN}.
 * </p>
 *
 * @since 11.0.0
 */
public enum ScreenMode
{
    /** A decorated window. */
    WINDOWED,
    /** A borderless window covering the screen, without changing the display mode. */
    FULL_WINDOW,
    /** Exclusive full screen, which may change the display mode. */
    FULL_SCREEN
}
