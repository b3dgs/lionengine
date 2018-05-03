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
package com.b3dgs.lionengine.game.feature.assignable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;

/**
 * Test {@link AssignableModel}.
 */
public final class AssignableModelTest
{
    private final AtomicBoolean clicked = new AtomicBoolean();
    private final AtomicInteger clickNumber = new AtomicInteger();
    private final AtomicBoolean assigned = new AtomicBoolean();
    private final Services services = UtilAssignable.createServices(clicked, clickNumber);
    private final AssignableModel assignable = UtilAssignable.createAssignable(services);

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        assignable.getFeature(Identifiable.class).notifyDestroyed();
    }

    /**
     * Test assigning when clicking.
     */
    @Test
    public void testClick()
    {
        assignable.update(1.0);

        assertFalse(assigned.get());

        assignable.setAssign(UtilAssignable.createAssign(assigned));
        assignable.setClickAssign(1);
        assigned.set(false);
        clicked.set(true);
        clickNumber.set(1);
        assignable.update(1.0);

        assertEquals(1, clickNumber.get());
        assertTrue(assigned.get());
    }

    /**
     * Test assigning when no clicking.
     */
    @Test
    public void testNoClick()
    {
        assignable.setAssign(UtilAssignable.createAssign(assigned));
        assigned.set(false);
        clickNumber.set(0);
        assignable.update(1.0);

        assertEquals(0, clickNumber.get());
        assertFalse(assigned.get());
    }

    /**
     * Test assigning when clicking outside.
     */
    @Test
    public void testClickOutside()
    {
        clicked.set(true);
        services.get(Cursor.class).setLocation(64, 1);

        assignable.setAssign(UtilAssignable.createAssign(assigned));
        assignable.update(1.0);

        assertFalse(assigned.get());

        services.get(Cursor.class).setLocation(1, 64);
        assignable.update(1.0);

        assertFalse(assigned.get());
    }

    /**
     * Test execution when object is an assign itself.
     */
    @Test
    public void testObjectIsAssign()
    {
        clicked.set(true);

        final ObjectAssign object = new ObjectAssign(assigned);
        final AssignableModel assignable = new AssignableModel(services);
        assignable.prepare(object);
        assignable.update(1.0);

        assertTrue(assigned.get());
    }
}
