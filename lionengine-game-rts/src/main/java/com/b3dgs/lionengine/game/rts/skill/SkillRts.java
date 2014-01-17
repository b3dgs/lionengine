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
package com.b3dgs.lionengine.game.rts.skill;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionengine.game.ObjectType;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Abstract structure of a skill, used by any kind of entity. It already contains name, description and level. This
 * model has to be implemented in order to specify the action. To be created, it needs the filename containing the skill
 * data.
 * 
 * @param <T> Skill enum type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class SkillRts<T extends Enum<T> & ObjectType>
        extends ObjectGame
{
    /** Name. */
    private final String name;
    /** Description. */
    private final String description;
    /** Destination assignment x. */
    protected int destX;
    /** Destination assignment y. */
    protected int destY;
    /** Skill current level. */
    private int level;
    /** Priority used. */
    private int priority;
    /** Clicked flag. */
    private boolean clicked;
    /** Ignore flag. */
    private boolean ignore;
    /** Order flag. */
    private boolean order;
    /** Active flag. */
    private boolean active;
    /** Selected flag. */
    private boolean selected;
    /** Over flag. */
    private boolean over;

    /**
     * Constructor.
     * 
     * <pre>
     * {@code
     * <skill name="Name" description="Description">
     * </skill>
     * }
     * </pre>
     * 
     * @param setup The setup skill used.
     */
    public SkillRts(SetupGame setup)
    {
        super(setup);
        name = getDataString("name");
        description = getDataString("description");
        level = 1;
        priority = 0;
        ignore = false;
        order = false;
        active = false;
        selected = false;
    }

    /**
     * Update routine on map.
     * 
     * @param extrp The extrapolation value.
     * @param camera The camera reference.
     * @param cursor The cursor reference.
     */
    public abstract void updateOnMap(double extrp, CameraRts camera, CursorRts cursor);

    /**
     * Rendering routine on map.
     * 
     * @param g The graphic output.
     * @param cursor The cursor reference.
     * @param camera The camera reference.
     */
    public abstract void renderOnMap(Graphic g, CursorRts cursor, CameraRts camera);

    /**
     * Rendering routine on panel.
     * 
     * @param g The graphic output.
     */
    public abstract void renderOnPanel(Graphic g);

    /**
     * Action executed on cast. Effects has to be updated in update function.
     * 
     * @param panel The panel reference.
     * @param cursor The cursor reference.
     */
    public abstract void action(ControlPanelModel<?> panel, CursorRts cursor);

    /**
     * Check if the cursor is over the skill button.
     * 
     * @param cursor The cursor reference.
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public abstract boolean isOver(CursorRts cursor);

    /**
     * Action called when clicked on skill from panel.
     * 
     * @param panel The panel reference.
     */
    public abstract void onClicked(ControlPanelModel<?> panel);

    /**
     * Get the id.
     * 
     * @return The id.
     */
    public abstract T getType();

    /**
     * Update routine on panel.
     * 
     * @param cursor The cursor reference.
     * @param panel The control panel reference.
     */
    public void updateOnPanel(CursorRts cursor, ControlPanelModel<?> panel)
    {
        // Reset clicked flag
        if (cursor.getClick() == 0)
        {
            clicked = false;
        }

        // Over
        over = isOver(cursor);
        if (cursor.getClick() > 0 && over)
        {
            if (!clicked)
            {
                setSelected(true);
                clicked = true;
            }
        }
        // Released
        else
        {
            if (isSelected())
            {
                if (!isOrder())
                {
                    action(panel, cursor);
                }
                else
                {
                    setActive(true);
                    panel.ordered();
                    onClicked(panel);
                    clicked = false;
                }
                setSelected(false);
            }
        }

        // Order case
        if (isOrder() && isActive() && cursor.getClick() > 0 && !clicked && !panel.canClick(cursor))
        {
            setActive(false);
            destX = cursor.getLocationInTileX();
            destY = cursor.getLocationInTileY();
            action(panel, cursor);
        }
    }

    /**
     * Set skill level.
     * 
     * @param level The level to set.
     */
    public void setLevel(int level)
    {
        this.level = level;
    }

    /**
     * Set skill selection state.
     * 
     * @param state The selection state.
     */
    public void setSelected(boolean state)
    {
        selected = state;
    }

    /**
     * Set active state (true when using).
     * 
     * @param state The active state.
     */
    public void setActive(boolean state)
    {
        active = state;
    }

    /**
     * Set order state (an order will require a left click on map to assign it). Setting it to false means that a single
     * click on the icon will call action(). Setting it to true means that it will need another click to set the map
     * destination.
     * 
     * @param state The order state.
     */
    public void setOrder(boolean state)
    {
        order = state;
    }

    /**
     * Set the priority.
     * 
     * @param priority The priority number (0 = first rendered).
     */
    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    /**
     * Set ignorance state (it can be used to hide a certain part of skills).
     * 
     * @param state The ignorance state.
     */
    public void setIgnore(boolean state)
    {
        ignore = state;
    }

    /**
     * Set priority level. It is used during skill rendering, to know which is rendered first.
     * 
     * @return The priority level.
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * Get skill level.
     * 
     * @return The skill level.
     */
    public int getLevel()
    {
        return level;
    }

    /**
     * Get skill name.
     * 
     * @return The skill name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get skill description.
     * 
     * @return The skill description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Check if skill is an order or a simple button.
     * 
     * @return The order state.
     */
    public boolean isOrder()
    {
        return order;
    }

    /**
     * Get ignorance state.
     * 
     * @return The ignorance state.
     */
    public boolean isIgnored()
    {
        return ignore;
    }

    /**
     * Get active state.
     * 
     * @return <code>true</code> if currently in use, <code>false</code> else.
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Get skill selection state.
     * 
     * @return The skill selection state.
     */
    public boolean isSelected()
    {
        return selected;
    }

    /**
     * Check if cursor is over the skill.
     * 
     * @return <code>true</code> if over, <code>false</code> else.
     */
    public boolean isOver()
    {
        return over;
    }
}
