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
package com.b3dgs.lionengine.editor.properties.frames.handler;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.frames.Messages;
import com.b3dgs.lionengine.editor.properties.frames.PropertiesFrames;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Set frames handler.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
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
        // Nothing to do
    }

    /**
     * Execute the handler.
     */
    @Execute
    @SuppressWarnings("static-method")
    public void execute()
    {
        final PropertiesPart part = UtilPart.getPart(PropertiesPart.ID, PropertiesPart.class);
        final Tree properties = part.getTree();
        final Configurer configurer = (Configurer) properties.getData();
        final Shell shell = properties.getShell();

        final InputValidator validator = new InputValidator(InputValidator.INTEGER_POSITIVE_STRICT_MATCH,
                                                            Messages.Properties_Frames_Error);

        final InputDialog horizontalFrames = new InputDialog(shell,
                                                             Messages.Properties_Frames_Title,
                                                             Messages.Properties_Frames_NumberHorizontal,
                                                             DEFAULT_FRAMES,
                                                             validator);
        if (horizontalFrames.open() == Window.OK)
        {
            final InputDialog verticalFrames = new InputDialog(shell,
                                                               Messages.Properties_Frames_Title,
                                                               Messages.Properties_Frames_NumberVertical,
                                                               DEFAULT_FRAMES,
                                                               validator);
            if (verticalFrames.open() == Window.OK)
            {
                final XmlNode root = configurer.getRoot();
                final XmlNode frames = root.createChild(ConfigFrames.FRAMES);
                frames.writeString(ConfigFrames.FRAMES_HORIZONTAL, horizontalFrames.getValue());
                frames.writeString(ConfigFrames.FRAMES_VERTICAL, verticalFrames.getValue());

                PropertiesFrames.updateSize(configurer, root, frames);

                configurer.save();
                PropertiesFrames.createAttributeFrames(properties, configurer);
                part.setInput(properties, configurer);
            }
        }
    }
}
