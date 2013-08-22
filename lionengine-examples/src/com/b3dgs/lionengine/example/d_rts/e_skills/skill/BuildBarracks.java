package com.b3dgs.lionengine.example.d_rts.e_skills.skill;

import java.awt.Color;

import com.b3dgs.lionengine.example.d_rts.e_skills.Cursor;
import com.b3dgs.lionengine.example.d_rts.e_skills.FactoryProduction;
import com.b3dgs.lionengine.example.d_rts.e_skills.ProducibleEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeCursor;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeEntity;
import com.b3dgs.lionengine.example.d_rts.e_skills.TypeSkill;
import com.b3dgs.lionengine.example.d_rts.e_skills.entity.UnitWorker;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.rts.ControlPanelModel;
import com.b3dgs.lionengine.game.rts.CursorRts;

/**
 * Build skill implementation
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
        super(TypeSkill.build_barracks_orc, setup);
        this.cursor = cursor;
        factoryProduction = setup.factoryProduction;
        final Configurable config = factoryProduction.getConfig(TypeEntity.barracks_orc);
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
            final ProducibleEntity producible = factoryProduction.createProducible(TypeEntity.barracks_orc, destX,
                    destY);
            ((UnitWorker) owner).addToProductionQueue(producible);
        }
    }

    @Override
    public void onClicked(ControlPanelModel<?> panel)
    {
        cursor.setType(TypeCursor.BOX);
        cursor.setBoxColor(Color.GREEN);
        cursor.setBoxSize(width, height);
    }
}
