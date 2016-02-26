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
package com.b3dgs.lionengine.game.object.trait;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.game.object.trait.actionable.Action;
import com.b3dgs.lionengine.game.object.trait.actionable.ActionConfig;
import com.b3dgs.lionengine.game.object.trait.actionable.ActionableModel;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the actionnable model trait.
 */
public class ActionnableModelTest
{
    /** Temp file name. */
    private static final String ACTION_XML = "action.xml";

    /**
     * Create a default action.
     * 
     * @param description The description.
     * @param rectangle The button.
     * @return The temp media.
     */
    private static Media createAction(String description, Rectangle rectangle)
    {
        final Media media = Medias.create(ACTION_XML);

        final String name = "name";
        final ActionConfig action = new ActionConfig(name,
                                                     description,
                                                     (int) rectangle.getX(),
                                                     (int) rectangle.getY(),
                                                     (int) rectangle.getWidth(),
                                                     (int) rectangle.getHeight());
        final XmlNode root = Xml.create("test");
        root.add(ActionConfig.exports(action));
        Xml.save(root, media);

        return media;
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
        final Cursor cursor = new Cursor()
        {
            @Override
            public boolean hasClickedOnce(int click)
            {
                clickNumber.set(click);
                return clicked.get();
            }
        };
        cursor.setArea(0, 0, 32, 32);
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
    private static ActionableModel createActionable(Media media, Services services)
    {
        final ObjectGame object = new ObjectGame(new Setup(media), services);
        final ActionableModel actionable = new ActionableModel();
        actionable.prepare(object, services);
        return actionable;
    }

    /**
     * Add a default action.
     * 
     * @param actionable The trait.
     * @param executed The execution flag.
     */
    private static void addAction(ActionableModel actionable, final AtomicBoolean executed)
    {
        actionable.setAction(new Action()
        {
            @Override
            public void execute()
            {
                executed.set(true);
            }
        });
    }

    /**
     * Test the description configuration.
     */
    @Test
    public void testDescription()
    {
        final String description = "description";
        final Media media = createAction(description, Geom.createRectangle());
        final Services services = createServices(new AtomicBoolean(), new AtomicInteger());
        final ActionableModel actionable = createActionable(media, services);
        Assert.assertEquals(description, actionable.getDescription());

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test the button configuration.
     */
    @Test
    public void testButton()
    {
        final Rectangle rectangle = Geom.createRectangle(0, 1, 16, 32);
        final Media media = createAction("description", rectangle);
        final Services services = createServices(new AtomicBoolean(), new AtomicInteger());
        final ActionableModel actionable = createActionable(media, services);

        final Rectangle boutton = actionable.getButton();
        Assert.assertEquals(rectangle.getX(), boutton.getX(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getY(), boutton.getY(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getWidth(), boutton.getWidth(), UtilTests.PRECISION);
        Assert.assertEquals(rectangle.getHeight(), boutton.getHeight(), UtilTests.PRECISION);

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test execution when clicking outside button.
     */
    @Test
    public void testClickOutside()
    {
        final Media media = createAction("description", Geom.createRectangle(0, 1, 16, 32));
        final AtomicInteger clickNumber = new AtomicInteger();
        final Services services = createServices(new AtomicBoolean(), clickNumber);
        final ActionableModel actionable = createActionable(media, services);

        final AtomicBoolean executed = new AtomicBoolean();
        addAction(actionable, executed);
        services.get(Cursor.class).setLocation(64, 64);

        executed.set(false);
        clickNumber.set(0);
        actionable.update(1.0);

        Assert.assertEquals(0, clickNumber.get());
        Assert.assertFalse(executed.get());

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test execution when clicking inside button.
     */
    @Test
    public void testClickInside()
    {
        final Media media = createAction("description", Geom.createRectangle(0, 1, 16, 32));
        final AtomicBoolean clicked = new AtomicBoolean();
        final AtomicInteger clickNumber = new AtomicInteger();
        final Services services = createServices(clicked, clickNumber);
        final ActionableModel actionable = createActionable(media, services);

        final AtomicBoolean executed = new AtomicBoolean();
        addAction(actionable, executed);

        clicked.set(true);
        actionable.setClickAction(2);
        actionable.update(1.0);

        Assert.assertEquals(2, clickNumber.get());
        Assert.assertTrue(executed.get());

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test execution without click.
     */
    @Test
    public void testNoClick()
    {
        final Media media = createAction("description", Geom.createRectangle(0, 1, 16, 32));
        final AtomicInteger clickNumber = new AtomicInteger();
        final Services services = createServices(new AtomicBoolean(), clickNumber);
        final ActionableModel actionable = createActionable(media, services);

        actionable.update(1.0);
        Assert.assertEquals(0, clickNumber.get());

        final AtomicBoolean executed = new AtomicBoolean();
        addAction(actionable, executed);

        actionable.setClickAction(1);
        actionable.update(1.0);

        Assert.assertEquals(1, clickNumber.get());
        Assert.assertFalse(executed.get());

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Test execution when object is an action itself.
     */
    @Test
    public void testObjectIsAction()
    {
        final Media media = createAction("description", Geom.createRectangle(0, 1, 16, 32));
        final AtomicBoolean clicked = new AtomicBoolean();
        final Services services = createServices(clicked, new AtomicInteger());
        final AtomicBoolean executed = new AtomicBoolean();
        final ObjectAction object = new ObjectAction(new Setup(media), services, executed);
        final ActionableModel actionable = new ActionableModel();
        actionable.prepare(object, services);

        clicked.set(true);
        actionable.update(1.0);

        Assert.assertTrue(executed.get());

        Assert.assertTrue(media.getFile().delete());
    }

    /**
     * Object containing action.
     */
    private static class ObjectAction extends ObjectGame implements Action
    {
        /** Action executed flag. */
        private final AtomicBoolean executed;

        /**
         * Constructor.
         * 
         * @param setup The setup reference.
         * @param services The services.
         * @param executed The executed flag.
         */
        public ObjectAction(Setup setup, Services services, AtomicBoolean executed)
        {
            super(setup, services);
            this.executed = executed;
        }

        @Override
        public void execute()
        {
            executed.set(true);
        }
    }
}
