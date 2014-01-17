/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.rts.skills.skill;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.game.rts.skills.entity.Entity;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.skill.SkillRts;

/**
 * Default skill implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Skill
        extends SkillRts<SkillType>
{
    /** Id. */
    private final SkillType type;
    /** Sprite. */
    private final SpriteTiled icon;
    /** Owner. */
    protected Entity owner;
    /** Location x on panel. */
    private int x;
    /** Location y on panel. */
    private int y;

    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     */
    protected Skill(SetupSkill setup)
    {
        super(setup);
        type = setup.type;
        icon = setup.icon;
    }

    /**
     * Set the skill owner.
     * 
     * @param entity The skill owner.
     */
    public void setOwner(Entity entity)
    {
        owner = entity;
    }

    /**
     * Prepare skill location.
     */
    public void prepare()
    {
        final int i = getPriority();
        x = 4 + (icon.getTileWidth() + 5) * (i % 2);
        y = 114 + (icon.getTileHeight() + 5) * (i / 2);
    }

    /*
     * SkillRts
     */

    @Override
    public void updateOnMap(double extrp, CameraRts camera, CursorRts cursor)
    {
        // Nothing to do
    }

    @Override
    public void renderOnMap(Graphic g, CursorRts cursor, CameraRts camera)
    {
        // Nothing to do
    }

    @Override
    public void renderOnPanel(Graphic g)
    {
        final int flag = isSelected() ? 1 : 0;
        icon.render(g, getLevel() - 1, x, y + flag);
    }

    @Override
    public void onClicked(ControlPanelModel<?> panel)
    {
        // Nothing to do
    }

    @Override
    public boolean isOver(CursorRts cursor)
    {
        return cursor.getClick() > 0 && cursor.getScreenX() >= x && cursor.getScreenX() <= x + icon.getTileWidth()
                && cursor.getScreenY() >= y && cursor.getScreenY() <= y + icon.getTileHeight();
    }

    @Override
    public SkillType getType()
    {
        return type;
    }
}
