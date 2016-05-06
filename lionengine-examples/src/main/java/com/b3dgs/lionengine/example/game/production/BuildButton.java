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
package com.b3dgs.lionengine.example.game.production;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.layer.Layerable;
import com.b3dgs.lionengine.game.layer.LayerableModel;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.object.SizeConfig;
import com.b3dgs.lionengine.game.object.trait.actionable.Action;
import com.b3dgs.lionengine.game.object.trait.actionable.Actionable;
import com.b3dgs.lionengine.game.object.trait.actionable.ActionableModel;
import com.b3dgs.lionengine.game.object.trait.assignable.Assign;
import com.b3dgs.lionengine.game.object.trait.assignable.Assignable;
import com.b3dgs.lionengine.game.object.trait.assignable.AssignableModel;
import com.b3dgs.lionengine.game.object.trait.producible.Producer;
import com.b3dgs.lionengine.game.object.trait.producible.Producible;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Build button action.
 */
class BuildButton extends ObjectGame implements Action, Assign, Updatable, Renderable
{
    /** Build farm media. */
    public static final Media FARM = Medias.create("BuildFarm.xml");
    /** Build barracks media. */
    public static final Media BARRACKS = Medias.create("BuildBarracks.xml");

    /** Actionable model. */
    private final Actionable actionable = addTrait(new ActionableModel());
    /** Assignable model. */
    private final Assignable assignable = addTrait(new AssignableModel());
    /** Layerable model. */
    private final Layerable layerable = addTrait(new LayerableModel());
    /** Button image. */
    private final Image image;
    /** Text reference. */
    private final Text text;
    /** Viewer reference. */
    private final Viewer viewer;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Factory reference. */
    private final Factory factory;
    /** Handler reference. */
    private final Handler handler;
    /** Media target. */
    private final Media target;
    /** Current action state. */
    private Updatable state;
    /** Building area. */
    private Rectangle area;

    /**
     * Create build button action.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public BuildButton(SetupSurface setup, Services services)
    {
        super(setup, services);
        text = services.get(Text.class);
        viewer = services.get(Viewer.class);
        cursor = services.get(Cursor.class);
        factory = services.get(Factory.class);
        handler = services.get(Handler.class);

        image = Drawable.loadImage(setup.getSurface());
        target = Medias.create(setup.getConfigurer().getText("media"));

        actionable.setClickAction(Mouse.LEFT);
        assignable.setClickAssign(Mouse.LEFT);
        layerable.setLayer(Integer.valueOf(3));
        state = actionable;
    }

    @Override
    protected void onPrepared()
    {
        actionable.setAction(this);
        assignable.setAssign(this);
    }

    @Override
    public void execute()
    {
        state = assignable;
        final SizeConfig size = SizeConfig.imports(Xml.load(target));
        area = Geom.createRectangle(UtilMath.getRounded(cursor.getX(), cursor.getWidth()),
                                    UtilMath.getRounded(cursor.getY(), cursor.getHeight()),
                                    size.getWidth(),
                                    size.getHeight());
    }

    @Override
    public void assign()
    {
        for (final Producer producer : handler.get(Producer.class))
        {
            final ObjectGame farm = factory.create(target);
            final Producible producible = farm.getTrait(Producible.class);
            producible.setLocation(UtilMath.getRounded(cursor.getX(), cursor.getWidth()),
                                   UtilMath.getRounded(cursor.getY(), cursor.getHeight()));
            producer.addToProductionQueue(producible);

            final int x = (int) (producible.getX() + producible.getWidth() / 2) / cursor.getWidth();
            final int y = (int) (producible.getY() - producible.getHeight() / 2) / cursor.getHeight();
            producer.getOwner().getTrait(Pathfindable.class).setDestination(x, y);
        }
        area = null;
        state = actionable;
    }

    @Override
    public void update(double extrp)
    {
        image.setLocation(actionable.getButton().getX(), actionable.getButton().getY());
        if (actionable.isOver())
        {
            text.setText(actionable.getDescription());
        }
        state.update(extrp);
        if (area != null)
        {
            area.set(UtilMath.getRounded(cursor.getX(), cursor.getWidth()),
                     UtilMath.getRounded(cursor.getY(), cursor.getHeight()),
                     area.getWidth(),
                     area.getHeight());
        }
    }

    @Override
    public void render(Graphic g)
    {
        image.render(g);
        if (area != null)
        {
            g.setColor(ColorRgba.GREEN);
            g.drawRect(viewer,
                       Origin.TOP_LEFT,
                       area.getX(),
                       area.getY(),
                       (int) area.getWidth(),
                       (int) area.getHeight(),
                       false);
        }
    }
}
