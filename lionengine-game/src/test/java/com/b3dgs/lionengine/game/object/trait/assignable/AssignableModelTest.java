/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.object.trait.assignable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.ObjectGameTest;
import com.b3dgs.lionengine.game.object.Setup;

/**
 * Test the assignable model trait.
 */
public class AssignableModelTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Create the services.
     * 
     * @param clicked The click flag.
     * @param clickNumber The click number recorded.
     * @return The services.
     */
    private static Services createServices(final AtomicBoolean clicked, final AtomicInteger clickNumber)
    {
        final Services services = new Services();
        final Camera camera = new Camera();
        camera.setView(0, 0, 32, 32);
        services.add(camera);
        final Cursor cursor = new Cursor()
        {
            @Override
            public boolean hasClickedOnce(int click)
            {
                clickNumber.set(click);
                return clicked.get();
            }
        };
        cursor.setArea(0, 0, 64, 64);
        cursor.setLocation(0, 1);
        services.add(cursor);
        return services;
    }

    /**
     * Create the trait.
     * 
     * @param media The media.
     * @param services The services.
     * @return The prepared trait.
     */
    private static AssignableModel createAssignable(Media media, Services services)
    {
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        final AssignableModel assignable = new AssignableModel();
        assignable.prepare(object, services);
        return assignable;
    }

    /**
     * Add a default assign.
     * 
     * @param assignable The trait.
     * @param assigned The assigned flag.
     */
    private static void addAssign(AssignableModel assignable, final AtomicBoolean assigned)
    {
        assignable.setAssign(new Assign()
        {
            @Override
            public void assign()
            {
                assigned.set(true);
            }
        });
    }

    /**
     * Test assigning when clicking.
     */
    @Test
    public void testClick()
    {
        final AtomicBoolean clicked = new AtomicBoolean();
        final AtomicInteger clickNumber = new AtomicInteger();
        final Services services = createServices(clicked, clickNumber);
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final AssignableModel assignable = createAssignable(media, services);

        final AtomicBoolean assigned = new AtomicBoolean();
        assignable.update(1.0);

        Assert.assertFalse(assigned.get());

        addAssign(assignable, assigned);

        assignable.setClickAssign(1);
        assigned.set(false);
        clicked.set(true);
        clickNumber.set(1);
        assignable.update(1.0);

        Assert.assertEquals(1, clickNumber.get());
        Assert.assertTrue(assigned.get());

        ObjectGameTest.freeId(assignable.getOwner());
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test assigning when no clicking.
     */
    @Test
    public void testNoClick()
    {
        final AtomicInteger clickNumber = new AtomicInteger();
        final Services services = createServices(new AtomicBoolean(), clickNumber);
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final AssignableModel assignable = createAssignable(media, services);

        final AtomicBoolean assigned = new AtomicBoolean();
        addAssign(assignable, assigned);

        assigned.set(false);
        clickNumber.set(0);
        assignable.update(1.0);

        Assert.assertEquals(0, clickNumber.get());
        Assert.assertFalse(assigned.get());

        ObjectGameTest.freeId(assignable.getOwner());
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test assigning when clicking outside.
     */
    @Test
    public void testClickOutside()
    {
        final AtomicBoolean clicked = new AtomicBoolean();
        final AtomicInteger clickNumber = new AtomicInteger();
        final Services services = createServices(clicked, clickNumber);
        final Media media = ObjectGameTest.createMedia(ObjectGame.class);
        final AssignableModel assignable = createAssignable(media, services);

        final AtomicBoolean assigned = new AtomicBoolean();
        addAssign(assignable, assigned);
        clicked.set(true);
        services.get(Cursor.class).setLocation(64, 1);
        assignable.update(1.0);

        Assert.assertFalse(assigned.get());

        services.get(Cursor.class).setLocation(1, 64);
        assignable.update(1.0);

        Assert.assertFalse(assigned.get());

        ObjectGameTest.freeId(assignable.getOwner());
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test execution when object is an assign itself.
     */
    @Test
    public void testObjectIsAssign()
    {
        final AtomicBoolean clicked = new AtomicBoolean();
        final Services services = createServices(clicked, new AtomicInteger());

        final AtomicBoolean assigned = new AtomicBoolean();
        final Media media = ObjectGameTest.createMedia(ObjectAssign.class);
        final ObjectAssign object = new ObjectAssign(new Setup(media), services, assigned);
        final AssignableModel assignable = new AssignableModel();
        assignable.prepare(object, services);

        clicked.set(true);
        assignable.update(1.0);

        Assert.assertTrue(assigned.get());

        ObjectGameTest.freeId(object);
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Object containing action.
     */
    public static class ObjectAssign extends ObjectGame implements Assign
    {
        /** Action assigned flag. */
        private final AtomicBoolean assigned;

        /**
         * Constructor.
         * 
         * @param setup The setup reference.
         * @param services The services.
         * @param assigned The assigned flag.
         */
        public ObjectAssign(Setup setup, Services services, AtomicBoolean assigned)
        {
            super(setup, services);
            this.assigned = assigned;
        }

        @Override
        public void assign()
        {
            assigned.set(true);
        }
    }
}
