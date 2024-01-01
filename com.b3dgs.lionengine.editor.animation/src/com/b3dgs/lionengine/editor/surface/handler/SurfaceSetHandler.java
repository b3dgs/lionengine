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
package com.b3dgs.lionengine.editor.surface.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Tree;

import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.surface.properties.PropertiesSurface;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.SurfaceConfig;
import com.b3dgs.lionengine.graphic.drawable.ImageHeader;
import com.b3dgs.lionengine.graphic.drawable.ImageInfo;

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
        super();
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
        UtilDialog.selectResourceFile(properties.getShell(), true, UtilDialog.getImageFilter()).ifPresent(media ->
        {
            final Xml root = configurer.getRoot();
            if (!root.hasNode(SizeConfig.NODE_SIZE))
            {
                final ImageHeader info = ImageInfo.get(media);

                final Xml size = root.createChild(SizeConfig.NODE_SIZE);
                size.writeInteger(SizeConfig.ATT_WIDTH, info.getWidth());
                size.writeInteger(SizeConfig.ATT_HEIGHT, info.getHeight());
            }

            final Xml surfaceNode = root.createChild(SurfaceConfig.NODE_SURFACE);
            surfaceNode.writeString(SurfaceConfig.ATT_IMAGE, media.getPath());

            configurer.save();
            PropertiesSurface.createAttributeSurface(properties, configurer);
            part.setInput(properties, configurer);
        });
    }
}
