/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.pathfinding.properties.editor;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.ObjectPropertiesAbstract;
import com.b3dgs.lionengine.editor.utility.control.UtilCombo;
import com.b3dgs.lionengine.editor.utility.control.UtilText;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.PathData;

/**
 * Represents the animation properties edition view.
 */
public class PathProperties extends ObjectPropertiesAbstract<PathData> implements ObjectListListener<PathData>
{
    /** Diagonal flag. */
    private final MovementList movements = new MovementList();
    /** Category combo. */
    private Combo category;
    /** Cost value. */
    private Text cost;
    /** Block flag. */
    private Combo block;

    /**
     * Create a path properties.
     */
    public PathProperties()
    {
        super();
    }

    /*
     * ObjectProperties
     */

    @Override
    protected void createTextFields(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        content.setLayout(new GridLayout(1, false));

        final MapTile map = WorldModel.INSTANCE.getMap();
        final MapTilePath mapPath = map.getFeature(MapTilePath.class);
        final Collection<String> categories = mapPath.getCategories();
        final String[] values = categories.toArray(new String[categories.size()]);

        category = UtilCombo.create(Messages.Category, content, true, values);
        cost = UtilText.create(Messages.Cost, content);
        block = UtilCombo.create(Messages.Block, content, false, Boolean.TRUE, Boolean.FALSE);
        movements.create(content);
    }

    @Override
    protected PathData createObject(String name)
    {
        return new PathData(name,
                            Double.parseDouble(cost.getText()),
                            Boolean.parseBoolean(block.getText()),
                            movements.getObjects());
    }

    /*
     * ObjectListListener
     */

    @Override
    public void notifyObjectSelected(PathData path)
    {
        setValueDefault(category, path.getName());
        setValueDefault(cost, String.valueOf(path.getCost()));
        setValueDefault(block, String.valueOf(path.isBlocking()));
        movements.loadMovements(path.getAllowedMovements());
    }

    @Override
    public void notifyObjectDeleted(PathData path)
    {
        // Nothing to do
    }
}
