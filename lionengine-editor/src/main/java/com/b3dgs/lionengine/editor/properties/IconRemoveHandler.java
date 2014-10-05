/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.properties;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Remove icon handler.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class IconRemoveHandler
{
    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     */
    @Execute
    public void execute(EPartService partService)
    {
        final PropertiesPart part = UtilEclipse.getPart(partService, PropertiesPart.ID, PropertiesPart.class);
        final Configurer configurer = (Configurer) part.properties.getData();
        final XmlNode root = configurer.getRoot();
        final XmlNode surfaceNode = root.getChild(ConfigSurface.SURFACE);
        surfaceNode.removeAttribute(ConfigSurface.SURFACE_ICON);
        configurer.save();
        for (final TreeItem item : part.properties.getItems())
        {
            if (ConfigSurface.SURFACE_ICON.equals(item.getData()))
            {
                part.clear(item);
            }
        }
    }
}
