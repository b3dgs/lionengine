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
package com.b3dgs.lionengine.editor.object.properties.setup;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.dialog.combo.ComboChooserDialog;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.object.ObjectConfig;
import com.b3dgs.lionengine.game.object.Setup;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Element properties part.
 */
public class PropertiesSetup implements PropertiesProviderObject
{
    /** Setup icon. */
    private static final Image ICON_SETUP = UtilIcon.get(FOLDER, "setup.png");

    /**
     * Create the attribute setup.
     * 
     * @param properties The properties tree reference.
     * @param configObject The configObject reference.
     */
    private static void createAttributeSetup(Tree properties, ObjectConfig configObject)
    {
        final TreeItem classItem = new TreeItem(properties, SWT.NONE);
        PropertiesPart.createLine(classItem, Messages.Setup, configObject.getSetupName());
        classItem.setData(ObjectConfig.SETUP);
        classItem.setImage(ICON_SETUP);
    }

    /**
     * Update the setup.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    private static boolean updateSetup(TreeItem item, Configurer configurer)
    {
        final ComboChooserDialog chooser = new ComboChooserDialog(item.getParent().getShell());
        final Collection<Class<? extends Setup>> setups = ProjectModel.INSTANCE.getProject()
                                                                               .getLoader()
                                                                               .getImplementing(Setup.class);
        setups.add(Setup.class);
        final String[] items = new String[setups.size()];
        int i = 0;
        for (final Class<? extends Setup> current : setups)
        {
            items[i] = current.getName();
            i++;
        }
        Arrays.sort(items);
        final String setup = chooser.open(items);
        if (setup != null)
        {
            final XmlNode root = configurer.getRoot();
            final XmlNode setupNode = root.getChild(ObjectConfig.SETUP);
            setupNode.setText(setup);
            item.setText(PropertiesPart.COLUMN_VALUE, setup);
            return true;
        }
        return false;
    }

    /**
     * Create properties.
     */
    public PropertiesSetup()
    {
        super();
    }

    /*
     * PropertiesListener
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final XmlNode root = configurer.getRoot();
        if (root.hasChild(ObjectConfig.SETUP))
        {
            createAttributeSetup(properties, ObjectConfig.imports(configurer));
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        if (ObjectConfig.SETUP.equals(data))
        {
            return updateSetup(item, configurer);
        }
        return false;
    }
}
