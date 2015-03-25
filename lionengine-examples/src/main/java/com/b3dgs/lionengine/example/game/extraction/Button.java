/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.extraction;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.Updatable;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.map.MapTilePath;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurface;
import com.b3dgs.lionengine.game.trait.actionable.Action;
import com.b3dgs.lionengine.game.trait.actionable.Actionable;
import com.b3dgs.lionengine.game.trait.actionable.ActionableModel;
import com.b3dgs.lionengine.game.trait.assignable.Assign;
import com.b3dgs.lionengine.game.trait.assignable.Assignable;
import com.b3dgs.lionengine.game.trait.assignable.AssignableModel;
import com.b3dgs.lionengine.game.trait.extractable.Extractable;
import com.b3dgs.lionengine.game.trait.extractable.Extractor;
import com.b3dgs.lionengine.game.trait.layerable.Layerable;
import com.b3dgs.lionengine.game.trait.layerable.LayerableModel;

/**
 * Resources button action.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
class Button
        extends ObjectGame
        implements Action, Assign, Updatable, Renderable
{
    /** Extract media. */
    public static final Media EXTRACT = Medias.create("Extract.xml");
    /** Carry media. */
    public static final Media CARRY = Medias.create("Carry.xml");

    /** Button image. */
    private final Image image;
    /** Text reference. */
    private final Text text;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Handler reference. */
    private final Handler handler;
    /** Map path reference. */
    private final MapTilePath mapPath;
    /** Actionable model. */
    private Actionable actionable;
    /** Assignable model. */
    private Assignable assignable;
    /** Current action state. */
    private Updatable state;

    /**
     * Create build button action.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     */
    public Button(SetupSurface setup, Services services)
    {
        super(setup, services);

        image = Drawable.loadImage(setup.surface);

        text = services.get(Text.class);
        cursor = services.get(Cursor.class);
        handler = services.get(Handler.class);
        mapPath = services.get(MapTilePath.class);

        addTrait(ActionableModel.class);
        addTrait(AssignableModel.class);
        addTrait(LayerableModel.class);
    }

    @Override
    protected void prepareTraits()
    {
        actionable = getTrait(Actionable.class);
        actionable.setClickAction(Mouse.LEFT);
        actionable.setAction(this);
        state = actionable;

        assignable = getTrait(Assignable.class);
        assignable.setClickAssign(Mouse.LEFT);
        assignable.setAssign(this);

        final Layerable layerable = getTrait(Layerable.class);
        layerable.setLayer(Integer.valueOf(3));
    }

    @Override
    public void execute()
    {
        cursor.setSurfaceId(1);
        state = assignable;
    }

    @Override
    public void assign()
    {
        for (final Extractor extractor : handler.get(Extractor.class))
        {
            for (final Integer id : mapPath.getObjectsId(cursor.getInTileX(), cursor.getInTileY()))
            {
                final ObjectGame object = handler.get(id);
                if (object.hasTrait(Extractable.class))
                {
                    extractor.setResource(object.getTrait(Extractable.class));
                    extractor.startExtraction();
                }
            }
        }
        cursor.setSurfaceId(0);
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
    }

    @Override
    public void render(Graphic g)
    {
        image.render(g);
    }
}
