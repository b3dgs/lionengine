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
package com.b3dgs.lionengine.editor.formula.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormula;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormulaConfig;
import com.b3dgs.lionengine.game.feature.tile.map.collision.MapTileCollision;
import com.b3dgs.lionengine.io.Xml;

/**
 * Represents the formulas edition dialog.
 */
public class FormulasEditDialog extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "edit.png");

    /** Formulas properties. */
    private final FormulaProperties properties = new FormulaProperties();
    /** Formulas list. */
    private final FormulaList list = new FormulaList(properties);
    /** Formulas media. */
    private final Media formulas;

    /**
     * Create a formulas edit dialog.
     * 
     * @param parent The parent shell.
     * @param formulas The formulas media.
     */
    public FormulasEditDialog(Shell parent, Media formulas)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON);
        this.formulas = formulas;
        dialog.setMinimumSize(128, 320);
        createDialog();
        finish.setEnabled(true);
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        content.setLayout(new GridLayout(2, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        list.create(content);
        list.addListener(list);

        properties.create(content);
        list.addListener(properties);
        list.loadFormulas(formulas);
    }

    @Override
    protected void onFinish()
    {
        list.save();
        final Xml root = new Xml(CollisionFormulaConfig.FORMULAS);
        root.writeString(Constant.XML_HEADER, Constant.ENGINE_WEBSITE);

        for (final TreeItem item : list.getTree().getItems())
        {
            final CollisionFormula formula = (CollisionFormula) item.getData();
            CollisionFormulaConfig.exports(root, formula);
        }
        root.save(formulas);

        final MapTile map = WorldModel.INSTANCE.getMap();
        if (map.hasFeature(MapTileCollision.class))
        {
            final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
            mapCollision.loadCollisions(mapCollision.getFormulasConfig(), mapCollision.getCollisionsConfig());
        }
    }
}
