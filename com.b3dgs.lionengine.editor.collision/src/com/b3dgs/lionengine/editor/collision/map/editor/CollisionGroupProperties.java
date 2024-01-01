/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.editor.collision.map.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.ObjectPropertiesAbstract;
import com.b3dgs.lionengine.editor.formula.editor.FormulaList;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionFormula;
import com.b3dgs.lionengine.game.feature.tile.map.collision.CollisionGroup;

/**
 * Represents the collisions properties edition view.
 */
public class CollisionGroupProperties extends ObjectPropertiesAbstract<CollisionGroup>
                                      implements ObjectListListener<CollisionGroup>
{
    /** Formulas tree. */
    private final FormulaList tree = new FormulaList();

    /**
     * Create collisions properties.
     */
    public CollisionGroupProperties()
    {
        super();
    }

    /**
     * Create the formulas tree.
     * 
     * @param parent The parent composite.
     */
    private void createTreeFormulas(Composite parent)
    {
        tree.createToolBar(parent);
        tree.createTree(parent);
    }

    @Override
    protected void createTextFields(Composite parent)
    {
        final Group formulas = new Group(parent, SWT.NONE);
        formulas.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        formulas.setLayout(new GridLayout(1, false));
        formulas.setText(Messages.Formulas);
        createTreeFormulas(formulas);
    }

    @Override
    protected CollisionGroup createObject(String name)
    {
        return new CollisionGroup(name, tree.getObjects());
    }

    @Override
    public void notifyObjectSelected(CollisionGroup collision)
    {
        for (final TreeItem item : tree.getTree().getItems())
        {
            item.setData(null);
            item.dispose();
        }
        for (final CollisionFormula formula : collision.getFormulas())
        {
            final TreeItem item = new TreeItem(tree.getTree(), SWT.NONE);
            item.setText(formula.getName());
            item.setData(formula);
        }
    }

    @Override
    public void notifyObjectDeleted(CollisionGroup collision)
    {
        // Nothing to do
    }
}
