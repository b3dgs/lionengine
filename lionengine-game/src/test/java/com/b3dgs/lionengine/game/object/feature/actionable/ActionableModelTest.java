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
package com.b3dgs.lionengine.game.object.feature.actionable;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the actionable model.
 */
public class ActionableModelTest
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

    private final Rectangle rectangle = Geom.createRectangle(0, 1, 16, 32);
    private final Media media = UtilActionnable.createAction("description", rectangle);
    private final AtomicBoolean clicked = new AtomicBoolean();
    private final AtomicInteger clickNumber = new AtomicInteger();
    private final AtomicBoolean executed = new AtomicBoolean();
    private final Services services = UtilActionnable.createServices(clicked, clickNumber);
    private final ActionableModel actionable = UtilActionnable.createActionable(media, services);

    /**
     * Clean test.
     */
    @After
    public void clean()
    {
        actionable.getOwner().getFeature(Identifiable.class).notifyDestroyed();
        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the description configuration.
     */
    @Test
    public void testDescription()
    {
        Assert.assertEquals("description", actionable.getDescription());
    }

    /**
     * Test the button configuration.
     */
    @Test
    public void testButton()
    {
        final Rectangle boutton = actionable.getButton();
        Assert.assertEquals(rectangle.getX(), boutton.getX(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getY(), boutton.getY(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getWidth(), boutton.getWidth(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getHeight(), boutton.getHeight(), UtilTests.PRECISION);
    }

    /**
     * Test execution when clicking outside button.
     */
    @Test
    public void testClickOutside()
    {
        actionable.setAction(UtilActionnable.createAction(executed));
        services.get(Cursor.class).setLocation(64, 64);

        executed.set(false);
        clickNumber.set(0);
        actionable.update(1.0);

        Assert.assertEquals(0, clickNumber.get());
        Assert.assertFalse(executed.get());
    }

    /**
     * Test execution when clicking inside button.
     */
    @Test
    public void testClickInside()
    {
        clicked.set(true);
        actionable.setAction(UtilActionnable.createAction(executed));
        actionable.setClickAction(2);
        actionable.update(1.0);

        Assert.assertEquals(2, clickNumber.get());
        Assert.assertTrue(executed.get());
    }

    /**
     * Test execution without click.
     */
    @Test
    public void testNoClick()
    {
        actionable.update(1.0);

        Assert.assertEquals(0, clickNumber.get());

        actionable.setAction(UtilActionnable.createAction(executed));
        actionable.setClickAction(1);
        actionable.update(1.0);

        Assert.assertEquals(1, clickNumber.get());
        Assert.assertFalse(executed.get());
    }

    /**
     * Test execution when object is an action itself.
     */
    @Test
    public void testObjectIsAction()
    {
        clicked.set(true);

        final Setup setup = new Setup(media);
        final ObjectAction object = new ObjectAction(executed);
        final ActionableModel actionable = new ActionableModel(setup);
        actionable.prepare(object, services);
        actionable.update(1.0);

        Assert.assertTrue(executed.get());
    }
}
