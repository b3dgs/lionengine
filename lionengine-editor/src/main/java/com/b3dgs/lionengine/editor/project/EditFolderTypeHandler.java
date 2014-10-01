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
package com.b3dgs.lionengine.editor.project;

import java.io.File;
import java.io.IOException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Edit the type folder properties.
 * 
 * @author Pierre-Alexandre
 */
public class EditFolderTypeHandler
{
    /** Type node. */
    private static final String NODE_TYPE = Configurable.PREFIX + "type";
    /** Name node. */
    private static final String NODE_NAME = Configurable.PREFIX + "name";
    /** The default type name. */
    private static final String DEFAULT_NAME = "category";

    /**
     * Get the root node from the type file.
     * 
     * @param media The type media.
     * @return The type root node.
     */
    private static XmlNode getRoot(Media media)
    {
        final XmlNode root;
        if (media.getFile().isFile())
        {
            root = Stream.loadXml(media);
        }
        else
        {
            root = Stream.createXmlNode(EditFolderTypeHandler.NODE_TYPE);
            final XmlNode typeName = Stream.createXmlNode(EditFolderTypeHandler.NODE_NAME);
            typeName.setText(EditFolderTypeHandler.DEFAULT_NAME);
            root.add(typeName);
        }
        return root;
    }

    /**
     * Enter the new type name.
     * 
     * @param parent The shell parent.
     * @param media The type media.
     * @param root The root node.
     */
    private static void enterName(Shell parent, Media media, XmlNode root)
    {
        final XmlNode typeName = root.getChild(EditFolderTypeHandler.NODE_NAME);
        final InputDialog inputDialog = new InputDialog(parent, Messages.EditEntitiesFolderType_Name_Title,
                Messages.EditEntitiesFolderType_Name_Text, typeName.getText(), new InputValidator(
                        InputValidator.NAME_MATCH, com.b3dgs.lionengine.editor.Messages.InputValidator_Error_Name));
        inputDialog.open();
        final String value = inputDialog.getValue();
        if (value != null)
        {
            typeName.setText(value);
            Stream.saveXml(root, media);
        }
    }

    /**
     * Execute the handler.
     * 
     * @param partService The part service reference.
     * @param parent The shell parent.
     */
    @Execute
    public void execute(EPartService partService, Shell parent)
    {
        final Media selection = ProjectsModel.INSTANCE.getSelection();
        final Project project = Project.getActive();
        final File folder = selection.getFile();
        try
        {
            final File type = Tools.getFolderTypeFile(folder);
            final Media media = project.getResourceMedia(type.getPath());
            final XmlNode root = EditFolderTypeHandler.getRoot(media);
            EditFolderTypeHandler.enterName(parent, media, root);
        }
        catch (final IOException exception)
        {
            Verbose.exception(EditFolderTypeHandler.class, "execute", exception);
        }
    }
}
