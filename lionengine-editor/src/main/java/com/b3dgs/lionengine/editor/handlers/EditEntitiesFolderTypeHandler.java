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
package com.b3dgs.lionengine.editor.handlers;

import java.io.File;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectsModel;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.file.XmlNodeNotFoundException;
import com.b3dgs.lionengine.file.XmlParser;

/**
 * Edit the type entity folder properties.
 * 
 * @author Pierre-Alexandre
 */
public class EditEntitiesFolderTypeHandler
{
    /** The default type name. */
    private static final String DEFAULT_NAME = "category";

    /**
     * Get the root node from the type file.
     * 
     * @param xmlParser The XML parser.
     * @param media The type media.
     * @return The type root node.
     */
    private static XmlNode getRoot(XmlParser xmlParser, Media media)
    {
        final XmlNode root;
        if (media.getFile().isFile())
        {
            root = xmlParser.load(media);
        }
        else
        {
            root = com.b3dgs.lionengine.file.File.createXmlNode("lionengine:type");
            final XmlNode typeName = com.b3dgs.lionengine.file.File.createXmlNode("name");
            typeName.setText(EditEntitiesFolderTypeHandler.DEFAULT_NAME);
            root.add(typeName);
        }
        return root;
    }

    /**
     * Enter the new type name.
     * 
     * @param parent The shell parent.
     * @param xmlParser The XML parser.
     * @param media The type media.
     * @param root The root node.
     */
    private static void enterName(Shell parent, XmlParser xmlParser, Media media, XmlNode root)
    {
        try
        {
            final XmlNode typeName = root.getChild("name");
            final InputDialog inputDialog = new InputDialog(parent, "Entities folder naming",
                    "Enter the entities folder name", typeName.getText(), null);
            inputDialog.open();
            final String value = inputDialog.getValue();
            if (value != null)
            {
                typeName.setText(value);
                xmlParser.save(root, media);
            }
        }
        catch (final XmlNodeNotFoundException exception)
        {
            throw new LionEngineException(exception);
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
        final File type = new File(selection.getFile(), "type.xml");
        final Media media = project.getResourceMedia(type.getPath());
        final XmlParser xmlParser = com.b3dgs.lionengine.file.File.createXmlParser();

        final XmlNode root = EditEntitiesFolderTypeHandler.getRoot(xmlParser, media);
        EditEntitiesFolderTypeHandler.enterName(parent, xmlParser, media, root);
    }
}
