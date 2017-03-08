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
package com.b3dgs.lionengine.editor.animation.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.animation.editor.AnimationEditor;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.Xml;

/**
 * Element properties part.
 */
public class PropertiesAnimation implements PropertiesProviderObject
{
    /** Animations icon. */
    private static final Image ICON_ANIMATIONS = UtilIcon.get("properties", "animations.png");

    /**
     * Create the animations attribute.
     * 
     * @param properties The properties tree reference.
     */
    public static void createAttributeAnimations(Tree properties)
    {
        final TreeItem animationsItem = new TreeItem(properties, SWT.NONE);
        animationsItem.setText(Messages.Animations);
        animationsItem.setData(AnimationConfig.ANIMATION);
        animationsItem.setImage(ICON_ANIMATIONS);
    }

    /**
     * Create properties.
     */
    public PropertiesAnimation()
    {
        super();
    }

    /*
     * PropertiesProvider
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final Xml root = configurer.getRoot();
        if (root.hasChild(AnimationConfig.ANIMATION))
        {
            createAttributeAnimations(properties);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        if (AnimationConfig.ANIMATION.equals(data))
        {
            final AnimationEditor animationEditor = new AnimationEditor(item.getParent(), configurer);
            animationEditor.create();
            animationEditor.openAndWait();
        }
        return false;
    }
}
