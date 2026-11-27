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
package com.b3dgs.lionengine;

/**
 * Severity of a log message, from the most to the least severe (ADR-0031).
 *
 * @since 11.0.0
 */
public enum LogLevel
{
    /** Failure that stops an operation or the engine. */
    ERROR,
    /** Unexpected situation the engine recovered from. */
    WARNING,
    /** Notable lifecycle information. */
    INFO,
    /** Detailed diagnostic information, disabled by default. */
    DEBUG
}
