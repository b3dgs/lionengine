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
package com.b3dgs.lionengine.editor.properties.surface.handler;

import java.io.File;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Tree;

import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.surface.PropertiesSurface;
import com.b3dgs.lionengine.game.configurer.ConfigSize;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Set surface handler.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class SurfaceSetHandler
{
    /**
     * Create handler.
     */
    public SurfaceSetHandler()
    {
        // Nothing to do
    }

    /**
     * Execute the handler.
     */
    @Execute
    @SuppressWarnings("static-method")
    public void execute()
    {
        final PropertiesPart part = UtilEclipse.getPart(PropertiesPart.ID, PropertiesPart.class);
        final Tree properties = part.getTree();
        final Configurer configurer = (Configurer) properties.getData();
        final String file = Tools.selectFile(properties.getShell(), configurer.getPath(), true);
        if (file != null)
        {
            final XmlNode root = configurer.getRoot();
            if (!root.hasChild(ConfigSize.SIZE))
            {
                final File surface = new File(configurer.getPath(), file);
                final ImageInfo info = ImageInfo.get(Project.getActive().getResourceMedia(surface));

                final XmlNode size = root.createChild(ConfigSize.SIZE);
                size.writeInteger(ConfigSize.SIZE_WIDTH, info.getWidth());
                size.writeInteger(ConfigSize.SIZE_HEIGHT, info.getHeight());
            }

            final XmlNode surfaceNode = root.createChild(ConfigSurface.SURFACE);
            surfaceNode.writeString(ConfigSurface.SURFACE_IMAGE, file);

            configurer.save();
            PropertiesSurface.createAttributeSurface(properties, configurer);
            part.setInput(properties, configurer);
        }
    }
}
