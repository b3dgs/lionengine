/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.actionable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.ActionableModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.geom.Geom;

/**
 * Test {@link ActionableModel}.
 */
public final class ActionableModelTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(null);
    }

    private final Area area = Geom.createArea(0, 1, 16, 32);
    private final Media media = UtilActionnable.createAction("description", area);
    private final AtomicBoolean clicked = new AtomicBoolean();
    private final AtomicInteger clickNumber = new AtomicInteger();
    private final AtomicBoolean executed = new AtomicBoolean();
    private final Services services = UtilActionnable.createServices(clicked, clickNumber);
    private final ActionableModel actionable = UtilActionnable.createActionable(media, services);

    /**
     * Clean test.
     */
    @AfterEach
    public void clean()
    {
        actionable.getFeature(Identifiable.class).notifyDestroyed();

        assertTrue(media.getFile().delete());
    }

    /**
     * Test the description configuration.
     */
    @Test
    public void testDescription()
    {
        assertEquals("description", actionable.getDescription());
    }

    /**
     * Test the button configuration.
     */
    @Test
    public void testButton()
    {
        final Area boutton = actionable.getButton();

        assertEquals(area.getX(), boutton.getX());
        assertEquals(area.getY(), boutton.getY());
        assertEquals(area.getWidthReal(), boutton.getWidthReal());
        assertEquals(area.getHeightReal(), boutton.getHeightReal());
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

        assertFalse(executed.get());
        assertEquals(0, clickNumber.get());
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

        assertTrue(executed.get());
        assertEquals(2, clickNumber.get());
    }

    /**
     * Test execution without click.
     */
    @Test
    public void testNoClick()
    {
        actionable.update(1.0);

        assertEquals(0, clickNumber.get());

        actionable.setAction(UtilActionnable.createAction(executed));
        actionable.setClickAction(1);
        actionable.update(1.0);

        assertFalse(executed.get());
        assertEquals(1, clickNumber.get());
    }

    /**
     * Test execution when object is an action itself.
     */
    @Test
    public void testObjectIsAction()
    {
        clicked.set(true);

        final Setup setup = new Setup(media);
        final ObjectAction object = new ObjectAction(services, setup, executed);
        final ActionableModel actionable = new ActionableModel(services, setup);
        actionable.prepare(object);
        actionable.update(1.0);

        assertTrue(executed.get());
    }

    /**
     * Test enabled flag.
     */
    @Test
    public void testEnabled()
    {
        assertTrue(actionable.isEnabled());

        actionable.setEnabled(false);

        assertFalse(actionable.isEnabled());
    }
}
