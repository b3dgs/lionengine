/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

/**
 * State object representation which allows to represent a gameplay as a finite state machine.
 * <p>
 * A {@link com.b3dgs.lionengine.game.State} is created by the
 * {@link com.b3dgs.lionengine.game.StateFactory}, and is handled by the
 * {@link com.b3dgs.lionengine.game.state.StateHandler}, which checks
 * {@link com.b3dgs.lionengine.game.StateTransition} depending of the
 * {@link com.b3dgs.lionengine.game.state.StateInputUpdater}.
 * </p>
 */
package com.b3dgs.lionengine.game.state;
