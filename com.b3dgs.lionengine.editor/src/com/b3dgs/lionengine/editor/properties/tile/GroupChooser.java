/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.properties.tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.project.dialog.group.GroupsEditDialog;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.collision.TileGroup;
import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Represents the tile group chooser.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class GroupChooser
        extends AbstractDialog
{
    /** Groups values. */
    final String[] groups;
    /** Combo box. */
    private Combo combo;
    /** Choice value. */
    private String choice;

    /**
     * Create the group chooser.
     * 
     * @param parent The parent reference.
     * @param groups The groups values.
     */
    public GroupChooser(Shell parent, Collection<String> groups)
    {
        super(parent, Messages.GroupChooser_Title, Messages.GroupChooser_HeaderTitle, Messages.GroupChooser_HeaderDesc,
                GroupsEditDialog.ICON);
        this.groups = groups.toArray(new String[groups.size()]);
        createDialog();
        dialog.setMinimumSize(256, 64);
        finish.setEnabled(true);
    }

    /**
     * Get the current choice.
     * 
     * @return The choice value.
     */
    public String getChoice()
    {
        return choice;
    }

    /**
     * Load groups.
     * 
     * @param groups The groups to load.
     */
    void loadGroups(String[] groups)
    {
        Arrays.sort(groups);
        combo.setItems(groups);
        if (groups.length > 0)
        {
            combo.setText(groups[0]);
        }
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        final Composite composite = new Composite(content, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(3, false));

        final Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.GroupChooser_Choice);

        combo = new Combo(composite, SWT.SINGLE | SWT.READ_ONLY);
        loadGroups(groups);

        final Button add = new Button(composite, SWT.PUSH);
        add.setText(Messages.GroupChooser_Add);
        add.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                final MapTile map = WorldViewModel.INSTANCE.getMap();
                final GroupsEditDialog dialog = new GroupsEditDialog(add.getShell(), map.getGroupsConfig());
                dialog.open();

                final Collection<TileGroup> groups = map.getGroups();
                final Collection<String> values = new ArrayList<>();
                values.add(ConfigTileGroup.REMOVE_GROUP_NAME);
                for (final TileGroup group : groups)
                {
                    values.add(group.getName());
                }
                loadGroups(values.toArray(new String[values.size()]));
            }
        });
    }

    @Override
    protected void onFinish()
    {
        choice = combo.getText();
    }
}
