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
package com.b3dgs.lionengine.editor.frame.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.b3dgs.lionengine.editor.frame.properties.PropertiesFrames;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.io.Xml;

/**
 * Set frames handler.
 */
public class FramesSetHandler
{
    /** Default frames. */
    private static final String DEFAULT_FRAMES = "1";

    /**
     * Create handler.
     */
    public FramesSetHandler()
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
        final Shell shell = properties.getShell();

        final InputValidator validator = new InputValidator(InputValidator.INTEGER_POSITIVE_STRICT_MATCH,
                                                            Messages.Error);

        final InputDialog horizontalFrames = new InputDialog(shell,
                                                             Messages.Title,
                                                             Messages.NumberHorizontal,
                                                             DEFAULT_FRAMES,
                                                             validator);
        if (horizontalFrames.open() == Window.OK)
        {
            final InputDialog verticalFrames = new InputDialog(shell,
                                                               Messages.Title,
                                                               Messages.NumberVertical,
                                                               DEFAULT_FRAMES,
                                                               validator);
            if (verticalFrames.open() == Window.OK)
            {
                final Xml root = configurer.getRoot();
                final Xml frames = root.createChild(FramesConfig.NODE_FRAMES);
                frames.writeString(FramesConfig.ATT_HORIZONTAL, horizontalFrames.getValue());
                frames.writeString(FramesConfig.ATT_VERTICAL, verticalFrames.getValue());

                PropertiesFrames.updateSize(configurer, root, frames);

                configurer.save();
                PropertiesFrames.createAttributeFrames(properties, configurer);
                part.setInput(properties, configurer);
            }
        }
    }
}
