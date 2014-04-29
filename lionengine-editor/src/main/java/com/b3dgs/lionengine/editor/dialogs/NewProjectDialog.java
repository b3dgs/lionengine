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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.UtilityFile;
import com.b3dgs.lionengine.editor.Activator;

/**
 * Represents the new project dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class NewProjectDialog
{
    /**
     * Create the header part of the dialog.
     * 
     * @param header The header composite.
     */
    private static void createHeader(Composite header)
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
        title.setText(Messages.NewProjectDialog_1);

        final Label text = new Label(titleArea, SWT.NONE);
        text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        text.setText(Messages.NewProjectDialog_2);

        final Label icon = new Label(header, SWT.NONE);
        icon.setImage(Activator.getIcon(UtilityFile.getPath("dialog", "new-project.png"))); //$NON-NLS-1$
        icon.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
        icon.setBackground(icon.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    }

    /** Dialog shell. */
    final Shell dialog;
    /** Project name text. */
    Text projectNameText;
    /** Project resources text. */
    Text projectResourcesText;

    /**
     * Constructor.
     * 
     * @param parent The parent reference.
     */
    public NewProjectDialog(Shell parent)
    {
        dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setMinimumSize(320, 300);
        final GridLayout dialogLayout = new GridLayout(1, false);
        dialogLayout.marginHeight = 0;
        dialogLayout.marginWidth = 0;
        dialogLayout.verticalSpacing = 0;
        dialog.setLayout(dialogLayout);
        dialog.setText(Messages.NewProjectDialog_0);
        createDialog();
    }

    /**
     * Open the dialog.
     */
    public void open()
    {
        dialog.pack(true);
        Activator.center(dialog);
        dialog.open();
    }

    /**
     * Create the dialog.
     */
    private void createDialog()
    {
        final Composite header = new Composite(dialog, SWT.NONE);
        header.setLayout(new GridLayout(2, false));
        header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        NewProjectDialog.createHeader(header);
        header.setBackground(header.getDisplay().getSystemColor(SWT.COLOR_WHITE));

        final Label separatorHeader = new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite content = new Composite(dialog, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
        content.setLayout(new GridLayout(1, false));
        createContent(content);

        final Label separatorContent = new Label(dialog, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorContent.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite bottom = new Composite(dialog, SWT.NONE);
        bottom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        bottom.setLayout(new GridLayout(1, false));
        createBottom(bottom);
    }

    /**
     * Create the content part of the dialog.
     * 
     * @param content The content composite.
     */
    private void createContent(Composite content)
    {
        createProjectNameArea(content);
        createProjectResourcesArea(content);
    }

    /**
     * Create the project name area.
     * 
     * @param content The content composite.
     */
    private void createProjectNameArea(Composite content)
    {
        final Composite nameArea = new Composite(content, SWT.NONE);
        nameArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        nameArea.setLayout(new GridLayout(2, false));

        final Label nameLabel = new Label(nameArea, SWT.NONE);
        nameLabel.setText(Messages.NewProjectDialog_3);

        projectNameText = new Text(nameArea, SWT.BORDER);
        projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        projectNameText.setTextLimit(32);
    }

    /**
     * Create the project resources area chooser.
     * 
     * @param content The content composite.
     */
    private void createProjectResourcesArea(Composite content)
    {
        final Composite resourcesArea = new Composite(content, SWT.NONE);
        resourcesArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        resourcesArea.setLayout(new GridLayout(3, false));

        final Label resourcesLabel = new Label(resourcesArea, SWT.NONE);
        resourcesLabel.setText(Messages.NewProjectDialog_6);

        projectResourcesText = new Text(resourcesArea, SWT.BORDER);
        projectResourcesText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        projectResourcesText.setTextLimit(64);
    }

    /**
     * Create the bottom part of the dialog.
     * 
     * @param bottom The bottom composite.
     */
    private void createBottom(Composite bottom)
    {
        final Composite buttonArea = new Composite(bottom, SWT.NONE);
        buttonArea.setLayout(new GridLayout(2, false));

        final Button finish = new Button(buttonArea, SWT.PUSH);
        final GridData finishData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        finishData.widthHint = 96;
        finish.setLayoutData(finishData);
        finish.setText(Messages.NewProjectDialog_5);
        finish.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                dialog.dispose();
            }
        });

        final Button cancel = new Button(buttonArea, SWT.PUSH);
        final GridData cancelData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        cancelData.widthHint = 96;
        cancel.setLayoutData(cancelData);
        cancel.setText(Messages.NewProjectDialog_4);
        cancel.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                dialog.dispose();
            }
        });
    }
}
