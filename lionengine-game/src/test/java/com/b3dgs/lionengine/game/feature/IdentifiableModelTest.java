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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilReflection;

/**
 * Test {@link IdentifiableModel}.
 */
public final class IdentifiableModelTest
{
    /**
     * Test the id.
     * 
     * @throws NoSuchFieldException If error.
     * @throws IllegalAccessException If error.
     */
    @Test
    public void testId() throws NoSuchFieldException, IllegalAccessException
    {
        final Collection<Integer> ids = UtilReflection.getField(IdentifiableModel.class, "IDS");
        ids.clear();

        final Collection<Integer> recycle = UtilReflection.getField(IdentifiableModel.class, "RECYCLE");
        recycle.clear();

        final Field field = IdentifiableModel.class.getDeclaredField("lastId");
        UtilReflection.setAccessible(field, true);
        field.set(IdentifiableModel.class, Integer.valueOf(0));

        final Collection<Identifiable> identifiables = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            final Featurable featurable = new FeaturableModel();
            final Identifiable identifiable = featurable.getFeature(Identifiable.class);
            identifiable.prepare(featurable);
            identifiables.add(identifiable);

            assertEquals(Integer.valueOf(i), identifiable.getId());
        }

        for (final Identifiable identifiable : identifiables)
        {
            identifiable.destroy();
            identifiable.notifyDestroyed();

            assertNull(identifiable.getId());
        }

        final Identifiable identifiable = new IdentifiableModel();
        identifiable.prepare(new FeaturableModel());
        assertEquals(Integer.valueOf(0), identifiable.getId());

        identifiable.destroy();
        identifiable.notifyDestroyed();

        assertNull(identifiable.getId());

        identifiable.destroy();

        assertNull(identifiable.getId());
    }

    /**
     * Test the listener.
     */
    @Test
    public void testListener()
    {
        final Identifiable identifiable = new IdentifiableModel();
        final AtomicBoolean destroyed = new AtomicBoolean();
        final IdentifiableListener listener = id -> destroyed.set(true);
        identifiable.addListener(listener);
        identifiable.removeListener(listener);
        identifiable.destroy();

        assertFalse(destroyed.get());
    }
}
