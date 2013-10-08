/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.lionheart.entity.player;

import java.util.Locale;

import com.b3dgs.lionengine.example.lionheart.entity.State;

/**
 * List of entity states.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
enum ValdynState implements State
{
    /** Crouch state. */
    CROUCH,
    /** Border state. */
    BORDER,
    /** Slide state. */
    SLIDE,
    /** Slide fast state. */
    SLIDE_FAST,
    /** Slide slow state. */
    SLIDE_SLOW,
    /** Liana idle. */
    LIANA_IDLE,
    /** Liana walk. */
    LIANA_WALK,
    /** Liana slide. */
    LIANA_SLIDE,
    /** Lian soar. */
    LIANA_SOAR,
    /** Preparing attack. */
    ATTACK_PREPARING,
    /** Preparing attack down. */
    ATTACK_PREPARING_DOWN,
    /** Preparing attack. */
    ATTACK_PREPARED,
    /** Preparing attack down. */
    ATTACK_PREPARED_DOWN,
    /** Attack up. */
    ATTACK_UP,
    /** Attack horizontal. */
    ATTACK_HORIZONTAL,
    /** Attack turning. */
    ATTACK_TURNING,
    /** Attack down leg. */
    ATTACK_DOWN_LEG,
    /** Attack while jumping. */
    ATTACK_JUMP,
    /** Attack while falling. */
    ATTACK_FALL,
    /** Attack on liana. */
    ATTACK_LIANA,
    /** Attack while sliding. */
    ATTACK_SLIDE;

    /** Animation name. */
    private final String animationName;

    /**
     * Constructor.
     */
    private ValdynState()
    {
        animationName = name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public String getAnimationName()
    {
        return animationName;
    }
}
