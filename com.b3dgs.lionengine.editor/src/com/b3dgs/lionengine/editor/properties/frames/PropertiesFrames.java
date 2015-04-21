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
package com.b3dgs.lionengine.editor.properties.frames;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Element properties part.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesFrames
        implements PropertiesProviderObject
{
    /** Icon frames. */
    private static final Image ICON_FRAMES = UtilEclipse.getIcon("properties", "frames.png");

    /**
     * Create the frames attribute.
     * 
     * @param properties The properties tree reference.
     * @param configurer The configurer reference.
     */
    static void createAttributeFrames(Tree properties, Configurer configurer)
    {
        final TreeItem iconItem = new TreeItem(properties, SWT.NONE);
        iconItem.setText(Messages.Properties_Frames);
        iconItem.setData(ConfigFrames.FRAMES);
        iconItem.setImage(PropertiesFrames.ICON_FRAMES);

        final ConfigFrames configFrames = ConfigFrames.create(configurer);

        final TreeItem framesHorizontal = new TreeItem(iconItem, SWT.NONE);
        PropertiesPart.createLine(framesHorizontal, "Horizontal", String.valueOf(configFrames.getHorizontal()));
        framesHorizontal.setData(ConfigFrames.FRAMES_HORIZONTAL);

        final TreeItem framesVertical = new TreeItem(iconItem, SWT.NONE);
        PropertiesPart.createLine(framesVertical, "Vertical", String.valueOf(configFrames.getVertical()));
        framesVertical.setData(ConfigFrames.FRAMES_VERTICAL);
    }

    /**
     * Update the frames.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    private static boolean updateFrames(TreeItem item, Configurer configurer)
    {
        final InputDialog frames = new InputDialog(item.getParent().getShell(), "Frames", "Frames number", "1",
                new InputValidator(InputValidator.INTEGER_POSITIVE_MATCH, "Invalid frames number !"));
        if (frames.open() == Window.OK)
        {
            final XmlNode root = configurer.getRoot();
            final XmlNode surfaceNode = root.getChild(ConfigSurface.SURFACE);
            surfaceNode.writeString((String) item.getData(), frames.getValue());
            item.setText(frames.getValue());
            return true;
        }
        return false;
    }

    /*
     * PropertiesProvider
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final XmlNode root = configurer.getRoot();
        if (root.hasChild(ConfigFrames.FRAMES))
        {
            createAttributeFrames(properties, configurer);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        boolean updated = false;
        if (ConfigFrames.FRAMES_HORIZONTAL.equals(data) || ConfigFrames.FRAMES_VERTICAL.equals(data))
        {
            updated = updateFrames(item, configurer);
        }
        return updated;
    }
}
