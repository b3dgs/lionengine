/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.frame.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.io.Xml;

/**
 * Remove frames handler.
 */
public final class FramesRemoveHandler
{
    /**
     * Create handler.
     */
    public FramesRemoveHandler()
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
        final Xml root = configurer.getRoot();
        root.removeChild(FramesConfig.NODE_FRAMES);
        configurer.save();
        for (final TreeItem item : properties.getItems())
        {
            if (FramesConfig.NODE_FRAMES.equals(item.getData()) || AnimationConfig.ANIMATION.equals(item.getData()))
            {
                part.clear(item);
            }
        }
        part.setInput(properties, configurer);
    }
}
