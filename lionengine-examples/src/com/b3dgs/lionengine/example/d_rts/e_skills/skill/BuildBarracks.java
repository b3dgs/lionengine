package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import java.awt.Color;

import com.b3dgs.lionengine.example.d_rts.e_skills.Cursor;
import com.b3dgs.lionengine.example.d_rts.e_skills.CursorType;
import com.b3dgs.lionengine.example.d_rts.e_skills.EntityType;
import com.b3dgs.lionengine.example.d_rts.e_skills.FactoryProduction;
import com.b3dgs.lionengine.example.d_rts.e_skills.ProducibleEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.SkillType;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.UnitWorker;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Build skill implementation.
 */
final class BuildBarracks
        extends Skill
{
    /** Production factory. */
    private final FactoryProduction factoryProduction;
    /** Production width in tile. */
    private final int width;
    /** Production height in tile. */
    private final int height;
    /** Cursor reference. */
    private final Cursor cursor;

    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     * @param cursor The cursor reference.
     */
    BuildBarracks(SetupSkill setup, Cursor cursor)
    {
        super(SkillType.BUILD_BARRACKS_ORC, setup);
        this.cursor = cursor;
        factoryProduction = setup.factoryProduction;
        final Configurable config = factoryProduction.getConfig(EntityType.BARRACKS_ORC);
        width = config.getDataInteger("widthInTile", "size");
        height = config.getDataInteger("heightInTile", "size");
        setOrder(true);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorRts cursor)
    {
        if (owner instanceof UnitWorker)
        {
            final ProducibleEntity produce = factoryProduction.createProducible(EntityType.BARRACKS_ORC, destX, destY);
            ((UnitWorker) owner).addToProductionQueue(produce);
        }
    }

    @Override
    public void onClicked(ControlPanelModel<?> panel)
    {
        cursor.setType(CursorType.BOX);
        cursor.setBoxColor(Color.GREEN);
        cursor.setBoxSize(width, height);
    }
}
