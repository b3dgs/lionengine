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
package com.b3dgs.lionengine.editor.map.group.editor;

import java.util.Collection;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.ObjectPropertiesAbstract;
import com.b3dgs.lionengine.editor.utility.control.UtilCombo;
import com.b3dgs.lionengine.game.feature.tile.TileGroup;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;

/**
 * Represents the tile group properties edition view.
 */
public class GroupProperties extends ObjectPropertiesAbstract<TileGroup> implements ObjectListListener<TileGroup>
{
    /** Associated tiles. */
    private final Collection<Integer> tiles = new TreeSet<>();
    /** Group type flag. */
    private Combo type;

    /**
     * Create a group properties.
     */
    public GroupProperties()
    {
        super();
    }

    @Override
    protected void createTextFields(Composite parent)
    {
        final Composite fields = new Composite(parent, SWT.NONE);
        fields.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        fields.setLayout(new GridLayout(1, false));

        type = UtilCombo.create(Messages.TileGroupType, fields, false, TileGroupType.values());
    }

    @Override
    protected TileGroup createObject(String name)
    {
        return new TileGroup(name, (TileGroupType) type.getData(), new TreeSet<>(tiles));
    }

    @Override
    public void notifyObjectSelected(TileGroup group)
    {
        tiles.clear();
        tiles.addAll(group.getTiles());
        setValueDefault(type, group.getType().name());
    }

    @Override
    public void notifyObjectDeleted(TileGroup collision)
    {
        tiles.clear();
        setValueDefault(type, "");
    }
}
