/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilReflection;

/**
 * Test {@link IdentifiableModel}.
 */
final class IdentifiableModelTest
{
    /** Object config test. */
    private static Media config;

    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        config = UtilSetup.createConfig(IdentifiableModelTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        assertTrue(config.getFile().delete());
        Medias.setResourcesDirectory(null);
    }

    private final Services services = new Services();
    private final Setup setup = new Setup(config);

    /**
     * Test the id.
     * 
     * @throws ReflectiveOperationException If error.
     */
    @Test
    void testId() throws ReflectiveOperationException
    {
        final Collection<Integer> ids = UtilReflection.getField(IdentifiableModel.class, "IDS");
        ids.clear();

        final Field field = IdentifiableModel.class.getDeclaredField("lastId");
        UtilReflection.setAccessible(field, true);
        field.set(IdentifiableModel.class, Integer.valueOf(0));

        final Collection<Identifiable> identifiables = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            final Featurable featurable = new FeaturableModel(services, setup);
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

        final Featurable featurable = new FeaturableModel(services, setup);
        final IdentifiableModel identifiable = featurable.getFeature(IdentifiableModel.class);
        identifiable.prepare(featurable);

        assertEquals(Integer.valueOf(10), identifiable.getId());

        identifiable.destroy();
        identifiable.notifyDestroyed();

        assertNull(identifiable.getId());

        identifiable.destroy();
        identifiable.notifyDestroyed();

        assertNull(identifiable.getId());

        identifiable.recycle();

        assertNotNull(identifiable.getId());
    }

    /**
     * Test the listener.
     */
    @Test
    void testListener()
    {
        final Identifiable identifiable = new IdentifiableModel();
        final AtomicBoolean destroyed = new AtomicBoolean();
        final IdentifiableListener listener = id -> destroyed.set(true);
        identifiable.prepare(new FeaturableModel(services, setup));
        identifiable.addListener(listener);
        identifiable.destroy();
        identifiable.removeListener(listener);
        identifiable.notifyDestroyed();

        assertTrue(destroyed.get());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final IdentifiableModel model = new IdentifiableModel();

        assertEquals(model, model);

        assertNotEquals(model, null);
        assertNotEquals(model, new Object());
        assertNotEquals(model, new IdentifiableModel());
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final IdentifiableModel model = new IdentifiableModel();

        assertHashEquals(model, model);

        assertHashNotEquals(model, new Object());
        assertHashNotEquals(model, new IdentifiableModel());
    }
}
