/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertCause;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Services}.
 */
final class ServicesTest
{
    /**
     * Test the service creation.
     */
    @Test
    void testCreateGet()
    {
        final Services services = new Services();
        final Factory factory = services.create(Factory.class);

        assertEquals(services, services.get(Services.class));
        assertEquals(factory, services.get(Factory.class));
    }

    /**
     * Test the service add.
     */
    @Test
    void testAddGet()
    {
        final Services services = new Services();
        final Factory factory = services.add(new Factory(services));

        assertEquals(factory, services.get(Factory.class));
    }

    /**
     * Test the service without constructor.
     */
    @Test
    void testServiceNoConstructor()
    {
        final Services services = new Services();

        assertCause(() -> services.create(NoConstructorService.class), IllegalAccessException.class);
    }

    /**
     * Test the service with invalid constructor.
     */
    @Test
    void testServiceInvalidConstructor()
    {
        final Services services = new Services();

        assertCause(() -> services.create(InvalidConstructorService.class), InstantiationException.class);
    }

    /**
     * Test the service not found.
     */
    @Test
    void testNotFound()
    {
        final Services services = new Services();

        assertThrows(() -> services.get(Camera.class), Services.ERROR_SERVICE_GET + Camera.class.getName());
    }

    /**
     * Test the service <code>null</code>.
     */
    @Test
    void testNull()
    {
        final Services services = new Services();

        assertThrows(() -> services.get(null), "Unexpected null argument !");
    }

    /**
     * Test the service as optional.
     */
    @Test
    void testOptional()
    {
        final Services services = new Services();
        final Factory factory = services.add(new Factory(services));

        assertEquals(services, services.getOptional(Services.class).get());
        assertEquals(factory, services.getOptional(Factory.class).get());
        assertFalse(services.getOptional(String.class).isPresent());
    }

    /**
     * Service without constructor.
     */
    public static final class NoConstructorService
    {
        /**
         * Private.
         */
        private NoConstructorService()
        {
            super();
        }
    }

    /**
     * Service without valid constructor.
     */
    public static abstract class InvalidConstructorService
    {
        /**
         * Private.
         */
        public InvalidConstructorService()
        {
            super();
        }
    }
}
