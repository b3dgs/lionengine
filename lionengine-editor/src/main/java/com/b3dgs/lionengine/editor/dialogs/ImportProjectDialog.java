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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.Activator;
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
    private static final Image ICON = Activator.getIcon("dialog", "import-project.png");

    /**
     * Constructor.
     * 
     * @param parent The shell parent.
     */
    public ImportProjectDialog(Shell parent)
    {
        super(parent, Messages.ImportProjectDialogTitle);
        createDialog();
        projectNameText.setEditable(false);
        projectLocationText.setEditable(false);
        projectSourcesText.setEditable(false);
        projectResourcesText.setEditable(false);
    }

    /*
     * AbstractProjectDialog
     */

    @Override
    protected void createHeader(Composite header)
    {
        final Composite titleArea = new Composite(header, SWT.NONE);
        titleArea.setLayout(new GridLayout(1, false));
        titleArea.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
        titleArea.setBackground(titleArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        final Label title = new Label(titleArea, SWT.NONE);
        final FontData data = title.getFont().getFontData()[0];
        data.setHeight(10);
        data.setStyle(SWT.BOLD);
        title.setFont(new Font(title.getDisplay(), data));
        title.setBackground(title.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        title.setText(Messages.ImportProjectDialog_HeaderTitle);

        final Label text = new Label(titleArea, SWT.NONE);
        text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        text.setText(Messages.ImportProjectDialog_HeaderDesc);

        final Label iconLabel = new Label(header, SWT.NONE);
        iconLabel.setImage(ImportProjectDialog.ICON);
        iconLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
        iconLabel.setBackground(iconLabel.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    }

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
                projectSourcesText.setText(project.getSources());
                projectResourcesText.setText(project.getResources());
                tipsLabel.setVisible(false);
                finish.setEnabled(true);
            }
            catch (final LionEngineException exception)
            {
                setTipsMessage(NewProjectDialog.ICON_ERROR, Messages.ImportProjectDialog_InvalidImport);
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
        final String sources = projectSourcesText.getText();
        final String resources = projectResourcesText.getText();
        final ProjectGenerator createProject = new ProjectGenerator(name, location, sources, resources);
        createProject.createProperties(location);
        project = Project.create(location);
    }
}
