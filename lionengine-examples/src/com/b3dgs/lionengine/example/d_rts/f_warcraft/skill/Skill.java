package com.b3dgs.lionengine.example.d_rts.f_warcraft.skill;

import java.awt.Font;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.ResourcesLoader;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.entity.Entity;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeSkill;
import com.b3dgs.lionengine.game.TimedMessage;
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
    /** Text. */
    protected final Text text;
    /** Id. */
    private final TypeSkill id;
    /** Sprite. */
    private final SpriteTiled icon;
    /** Background. */
    private final SpriteTiled background;
    /** Gold icon. */
    private final Sprite gold;
    /** Wood icon. */
    private final Sprite wood;
    /** Timed message handler. */
    private final TimedMessage message;
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
        message = setup.message;
        text = new Text(Font.DIALOG, 10, Text.NORMAL);
        icon = setup.icon;
        background = setup.background;
        gold = ResourcesLoader.GOLD;
        wood = ResourcesLoader.WOOD;
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
     * Abstract skill
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
        if (isOver() && !message.hasMessage())
        {
            text.draw(g, 72, 191, getDescription());
            if (this instanceof SkillProduceBuilding || this instanceof SkillProduceEntity)
            {
                final int width = text.getStringWidth(g, super.getDescription()) + 76;
                gold.render(g, width, 191);
                wood.render(g, width + 40, 191);
            }
        }
    }

    @Override
    public void onClicked(ControlPanelModel<?> panel)
    {
        // Nothing to do
    }

    @Override
    public boolean isOver(CursorRts cursor)
    {
        return cursor.getScreenX() >= x && cursor.getScreenX() <= x + icon.getTileWidth() && cursor.getScreenY() >= y
                && cursor.getScreenY() <= y + icon.getTileHeight();
    }

    @Override
    public TypeSkill getId()
    {
        return id;
    }
}
