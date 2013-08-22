package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeSkill;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.Entity;
import com.b3dgs.lionengine.game.rts.CameraRts;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;
import com.b3dgs.lionengine.game.rts.skill.SkillRts;

/**
 * Default skill implementation.
 */
public abstract class Skill
        extends SkillRts<TypeSkill>
{
    /** Id. */
    private final TypeSkill id;
    /** Sprite. */
    private final SpriteTiled icon;
    /** Background. */
    private final SpriteTiled background;
    /** Owner. */
    protected Entity owner;
    /** Location x on panel. */
    private int x;
    /** Location y on panel. */
    private int y;

    /**
     * Constructor.
     * 
     * @param id The skill id.
     * @param setup The setup skill reference.
     */
    protected Skill(TypeSkill id, SetupSkill setup)
    {
        super(setup);
        this.id = id;
        background = setup.background;
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
        background.render(g, flag, x - 2, y - 2);
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
    public TypeSkill getId()
    {
        return id;
    }
}
