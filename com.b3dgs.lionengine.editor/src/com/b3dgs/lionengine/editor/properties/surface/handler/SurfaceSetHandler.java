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
package com.b3dgs.lionengine.editor.properties.surface.handler;

import java.io.File;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Tree;

import com.b3dgs.lionengine.ImageInfo;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.surface.PropertiesSurface;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.object.SurfaceConfig;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Set surface handler.
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
    public void execute()
    {
        final PropertiesPart part = UtilPart.getPart(PropertiesPart.ID, PropertiesPart.class);
        final Tree properties = part.getTree();
        final Configurer configurer = (Configurer) properties.getData();
        final String file = UtilDialog.selectFile(properties.getShell(), configurer.getPath(), true);
        if (file != null)
        {
            final XmlNode root = configurer.getRoot();
            if (!root.hasChild(SizeConfig.NODE_SIZE))
            {
                final File surface = new File(configurer.getPath(), file);
                final ImageInfo info = ImageInfo.get(Project.getActive().getResourceMedia(surface));

                final XmlNode size = root.createChild(SizeConfig.NODE_SIZE);
                size.writeInteger(SizeConfig.ATT_WIDTH, info.getWidth());
                size.writeInteger(SizeConfig.ATT_HEIGHT, info.getHeight());
            }

            final XmlNode surfaceNode = root.createChild(SurfaceConfig.NODE_SURFACE);
            surfaceNode.writeString(SurfaceConfig.ATT_IMAGE, file);

            configurer.save();
            PropertiesSurface.createAttributeSurface(properties, configurer);
            part.setInput(properties, configurer);
        }
    }
}
