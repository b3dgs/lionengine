package com.b3dgs.lionengine.example.d_rts.e_skills;

import java.awt.Color;
import java.awt.Font;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.drawable.Bar;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.e_skills.skill.Skill;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Control panel implementation.
 */
final class ControlPanel
        extends ControlPanelModel<Entity>
{
    /** Text. */
    private final Text text;
    /** Surface. */
    private final Sprite sprite;
    /** Entity stats. */
    private final Sprite entityStats;
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
        text = new Text(Font.DIALOG, 9, Text.NORMAL);
        sprite = Drawable.loadSprite(Media.get("hud.png"));
        entityStats = Drawable.loadSprite(Media.get("entity_stats.png"));
        sprite.load(false);
        entityStats.load(false);
        lastSingleSelection = null;
    }

    @Override
    public void update(double extrp, CameraRts camera, CursorRts cursor, Keyboard keyboard)
    {
        super.update(extrp, camera, cursor, keyboard);

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

    /**
     * Render panel.
     * 
     * @param g The graphics output.
     * @param cursor The cursor reference.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CursorRts cursor, CameraRts camera)
    {
        sprite.render(g, 0, 0);

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
    private void updateSingleEntity(Entity entity, CursorRts cursor, double extrp)
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
    private void renderSingleEntity(Graphic g, int x, int y, Entity entity, CursorRts cursor, CameraRts camera)
    {
        // Entity stats
        entityStats.render(g, x, y);
        entity.getIcon().render(g, x + 4, y + 4);
        text.draw(g, x + 4, y + 25, entity.getName());
        final int life = entity.life.getPercent();
        Color color = Color.GREEN;
        if (life <= 50)
        {
            color = Color.YELLOW;
        }
        if (life < 25)
        {
            color = Color.RED;
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

    @Override
    public void notifyUpdatedSelection(Set<Entity> selection)
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
    protected void onStartOrder()
    {
        if (cursor.getType() != TypeCursor.BOX)
        {
            cursor.setType(TypeCursor.CROSS);
        }
    }

    @Override
    protected void onTerminateOrder()
    {
        cursor.setType(TypeCursor.POINTER);
    }
}
