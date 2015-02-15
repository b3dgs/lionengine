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
package com.b3dgs.lionengine.example.game.strategy.skills;

import java.util.Collection;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Text;
import com.b3dgs.lionengine.core.awt.Mouse;
import com.b3dgs.lionengine.example.game.strategy.skills.entity.Entity;
import com.b3dgs.lionengine.example.game.strategy.skills.skill.Skill;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.ControlPanelModel;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;
import com.b3dgs.lionengine.game.strategy.entity.EntityStrategyListener;

/**
 * Control panel implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.example.game.strategy.controlpanel
 */
public final class ControlPanel
        extends ControlPanelModel<Entity>
        implements EntityStrategyListener<Entity>
{
    /** Text. */
    private final Text text;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Last single selection. */
    private Entity lastSingleSelection;
    /** Entity life bar. */
    private final Bar barLife;

    /**
     * Constructor.
     * 
     * @param cursor The cursor reference.
     */
    ControlPanel(Cursor cursor)
    {
        super();
        this.cursor = cursor;
        barLife = new Bar(27, 3);
        text = Core.GRAPHIC.createText(Text.DIALOG, 9, TextStyle.NORMAL);
        lastSingleSelection = null;
        setClickSelection(Mouse.LEFT);
    }

    /**
     * Render panel.
     * 
     * @param g The graphics output.
     * @param cursor The cursor reference.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CursorStrategy cursor, CameraStrategy camera)
    {
        // Render the single selection if has
        if (lastSingleSelection != null)
        {
            renderSingleEntity(g, 2, 76, lastSingleSelection, cursor, camera);
        }
    }

    /**
     * Update a single entity and its skills.
     * 
     * @param entity The entity to update.
     * @param cursor The cursor reference.
     * @param extrp The extrapolation value.
     */
    private void updateSingleEntity(Entity entity, CursorStrategy cursor, double extrp)
    {
        for (final Skill skill : entity.getSkills(entity.getSkillPanel()))
        {
            if (skill.isIgnored())
            {
                continue;
            }
            skill.updateOnPanel(cursor, this);
        }
    }

    /**
     * Render a single entity with its details.
     * 
     * @param g The graphics output.
     * @param x The rendering location x.
     * @param y The rendering location y.
     * @param entity The entity to render.
     * @param cursor The cursor reference.
     * @param camera The camera reference.
     */
    private void renderSingleEntity(Graphic g, int x, int y, Entity entity, CursorStrategy cursor, CameraStrategy camera)
    {
        // Entity stats
        text.draw(g, x + 4, y + 25, entity.getName());
        final int life = entity.life.getPercent();
        ColorRgba color = ColorRgba.GREEN;
        if (life <= 50)
        {
            color = ColorRgba.YELLOW;
        }
        if (life < 25)
        {
            color = ColorRgba.RED;
        }
        barLife.setLocation(x + 35, y + 20);
        barLife.setWidthPercent(entity.life.getPercent());
        barLife.setColorForeground(color);
        barLife.render(g);

        // Entity skills
        for (final Skill skill : entity.getSkills(entity.getSkillPanel()))
        {
            if (skill.isIgnored())
            {
                continue;
            }
            skill.renderOnPanel(g);
        }
    }

    /*
     * EntityStrategyListener
     */

    @Override
    public void notifyUpdatedSelection(Collection<Entity> selection)
    {
        if (selection.size() == 1)
        {
            lastSingleSelection = selection.iterator().next();
        }
        else
        {
            lastSingleSelection = null;
        }
    }

    @Override
    public void entityMoved(Entity entity)
    {
        // Nothing to do
    }

    /*
     * ControlPanelModel
     */

    @Override
    public void update(double extrp, CameraStrategy camera, CursorStrategy cursor)
    {
        super.update(extrp, camera, cursor);

        // Update the single selection if has
        if (lastSingleSelection != null)
        {
            if (!lastSingleSelection.isSelected())
            {
                lastSingleSelection = null;
            }
            else
            {
                updateSingleEntity(lastSingleSelection, cursor, extrp);
            }
        }
    }

    @Override
    protected void onStartOrder()
    {
        if (cursor.getType() != CursorType.BOX)
        {
            cursor.setType(CursorType.CROSS);
        }
    }

    @Override
    protected void onTerminateOrder()
    {
        cursor.setType(CursorType.POINTER);
    }
}
