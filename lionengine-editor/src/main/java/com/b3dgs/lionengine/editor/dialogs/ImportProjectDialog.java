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
package com.b3dgs.lionengine.editor.dialogs;

import java.io.File;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectGenerator;

/**
 * Represents the new project dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImportProjectDialog
        extends AbstractProjectDialog
{
    /** Icon. */
    private static final Image ICON = Tools.getIcon("dialog", "import-project.png");

    /**
     * Constructor.
     * 
     * @param parent The shell parent.
     */
    public ImportProjectDialog(Shell parent)
    {
        super(parent, Messages.ImportProjectDialogTitle, Messages.ImportProjectDialog_HeaderTitle,
                Messages.ImportProjectDialog_HeaderDesc, ImportProjectDialog.ICON);
        createDialog();
        projectNameText.setEditable(false);
        projectLocationText.setEditable(false);
        projectClassesText.setEditable(false);
        projectResourcesText.setEditable(false);

        onLocationSelected("C:\\Users\\Pierre-Alexandre\\git\\lionheart-remake");
    }

    /*
     * AbstractProjectDialog
     */

    @Override
    protected void onLocationSelected(String path)
    {
        if (path != null)
        {
            projectLocationText.setText(path);
            projectNameText.setText(new File(path).getName());
            try
            {
                final Project project = Project.create(new File(path));
                projectClassesText.setText(project.getClasses());
                projectResourcesText.setText(project.getResources());
                tipsLabel.setVisible(false);
                finish.setEnabled(true);
            }
            catch (final LionEngineException exception)
            {
                setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ImportProjectDialog_InvalidImport);
                tipsLabel.setVisible(true);
                finish.setEnabled(false);
            }
        }
    }

    @Override
    protected void onFinish()
    {
        final String name = projectNameText.getText();
        final File location = new File(projectLocationText.getText());
        final String classes = projectClassesText.getText();
        final String resources = projectResourcesText.getText();
        final ProjectGenerator createProject = new ProjectGenerator(name, location, classes, resources);
        createProject.createProperties(location);
        project = Project.create(location);
    }
}
