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
package com.b3dgs.lionengine.editor.animation.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.properties.PropertiesModel;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Disable animations handler.
 */
public final class AnimationsDisableHandler
{
    /**
     * Create handler.
     */
    public AnimationsDisableHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     */
    @Execute
    public void execute()
    {
        final Tree tree = PropertiesModel.INSTANCE.getTree();
        final Configurer configurer = (Configurer) tree.getData();
        configurer.getRoot().removeChildren(AnimationConfig.NODE_ANIMATION);
        configurer.save();

        final PropertiesPart part = UtilPart.getPart(PropertiesPart.ID, PropertiesPart.class);
        for (final TreeItem item : tree.getItems())
        {
            if (AnimationConfig.NODE_ANIMATION.equals(item.getData()))
            {
                part.clear(item);
            }
        }
        final Tree properties = part.getTree();
        part.setInput(properties, configurer);
    }
}
