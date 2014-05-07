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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.UtilityFile;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.project.Project;

/**
 * Represents the import map dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ImportMapDialog
        extends Dialog
{
    /** Icon. */
    private static final Image ICON = Activator.getIcon("dialog", "import-project.png");

    /** Dialog shell. */
    final Shell dialog;
    /** Level rip location. */
    Text levelRipLocationText;
    /** Patterns location. */
    Text patternsLocationText;
    /** Finish button. */
    Button finish;
    /** Level rip file. */
    String levelRip;
    /** Patterns directory. */
    String patternsDirectory;

    /**
     * Constructor.
     * 
     * @param parent The shell parent.
     */
    public ImportMapDialog(Shell parent)
    {
        super(parent);
        dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setMinimumSize(500, 300);
        final GridLayout dialogLayout = new GridLayout(1, false);
        dialogLayout.marginHeight = 0;
        dialogLayout.marginWidth = 0;
        dialogLayout.verticalSpacing = 0;
        dialog.setLayout(dialogLayout);
        dialog.setText(Messages.ImportMapDialog_Title);
        dialog.setImage(Activator.getIcon("product.png")); //$NON-NLS-1$
        createDialog();
    }

    /**
     * Open the dialog.
     * 
     * @return <code>true</code> if opened, <code>false</code> if canceled.
     */
    public boolean open()
    {
        dialog.pack(true);
        Activator.center(dialog);
        dialog.open();

        final Display display = dialog.getDisplay();
        while (!dialog.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
        return levelRip != null && patternsDirectory != null;
    }

    /**
     * Get the level rip location.
     * 
     * @return The level rip location.
     */
    public String getLevelRipLocation()
    {
        return levelRip;
    }

    /**
     * Get the patterns location.
     * 
     * @return The patterns location.
     */
    public String getPatternsLocation()
    {
        return patternsDirectory;
    }

    /**
     * Create the dialog.
     */
    private void createDialog()
    {
        final Composite header = new Composite(dialog, SWT.NONE);
        header.setLayout(new GridLayout(2, false));
        header.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        ImportMapDialog.createHeader(header);
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
        bottom.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        bottom.setLayout(new GridLayout(2, false));
        createBottom(bottom);
    }

    /**
     * Create the dialog header.
     * 
     * @param header The header reference.
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
        title.setText(Messages.ImportMapDialog_HeaderTitle);

        final Label text = new Label(titleArea, SWT.NONE);
        text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WHITE));
        text.setText(Messages.ImportMapDialog_HeaderDesc);

        final Label iconLabel = new Label(header, SWT.NONE);
        iconLabel.setImage(ImportMapDialog.ICON);
        iconLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
        iconLabel.setBackground(iconLabel.getDisplay().getSystemColor(SWT.COLOR_WHITE));
    }

    /**
     * Create the content part of the dialog.
     * 
     * @param content The content composite.
     */
    private void createContent(Composite content)
    {
        createLevelRipLocationArea(content);
        createPatternsLocationArea(content);
    }

    /**
     * Create the level rip location area.
     * 
     * @param content The content composite.
     */
    private void createLevelRipLocationArea(Composite content)
    {
        final Composite levelRipArea = new Composite(content, SWT.NONE);
        levelRipArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        levelRipArea.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(levelRipArea, SWT.NONE);
        locationLabel.setText(Messages.ImportMapDialog_LevelRipLocation);

        levelRipLocationText = new Text(levelRipArea, SWT.BORDER);
        levelRipLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        levelRipLocationText.setEditable(false);

        final Button browse = new Button(levelRipArea, SWT.PUSH);
        final GridData browseData = new GridData();
        browseData.widthHint = 64;
        browse.setLayoutData(browseData);
        browse.setText(Messages.AbstractProjectDialog_Browse);
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final FileDialog fileDialog = new FileDialog(dialog, SWT.APPLICATION_MODAL);
                final Project project = Project.getActive();
                fileDialog.setFilterPath(UtilityFile.getPath(project.getPath().getPath(), project.getResources()));
                fileDialog.setFilterNames(new String[]
                {
                    Messages.ImportMapDialog_FileFilter
                });
                fileDialog.setFilterExtensions(new String[]
                {
                    "*.bmp;*.png"
                });
                final String path = fileDialog.open();
                if (path != null)
                {
                    levelRipLocationText.setText(path);
                    levelRip = levelRipLocationText.getText();
                    finish.setEnabled(levelRip != null && patternsDirectory != null);
                }
            }
        });
    }

    /**
     * Create the patterns location area.
     * 
     * @param content The content composite.
     */
    private void createPatternsLocationArea(Composite content)
    {
        final Composite levelRipArea = new Composite(content, SWT.NONE);
        levelRipArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        levelRipArea.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(levelRipArea, SWT.NONE);
        locationLabel.setText(Messages.ImportMapDialog_PatternsLocation);

        patternsLocationText = new Text(levelRipArea, SWT.BORDER);
        patternsLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        patternsLocationText.setEditable(false);

        final Button browse = new Button(levelRipArea, SWT.PUSH);
        final GridData browseData = new GridData();
        browseData.widthHint = 64;
        browse.setLayoutData(browseData);
        browse.setText(Messages.AbstractProjectDialog_Browse);
        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final DirectoryDialog directoryDialog = new DirectoryDialog(dialog, SWT.APPLICATION_MODAL);
                final Project project = Project.getActive();
                directoryDialog.setFilterPath(UtilityFile.getPath(project.getPath().getPath(), project.getResources()));
                final String path = directoryDialog.open();
                if (path != null)
                {
                    patternsLocationText.setText(path);
                    patternsDirectory = patternsLocationText.getText();
                    final boolean isValid = levelRip != null && patternsDirectory != null
                            && new File(patternsDirectory, "patterns.xml").isFile();
                    finish.setEnabled(isValid);
                }
            }
        });
    }

    /**
     * Create the bottom part of the dialog.
     * 
     * @param bottom The bottom composite.
     */
    private void createBottom(Composite bottom)
    {
        final Composite buttonArea = new Composite(bottom, SWT.NONE);
        buttonArea.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        buttonArea.setLayout(new GridLayout(2, false));

        finish = new Button(buttonArea, SWT.PUSH);
        final GridData finishData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        finishData.widthHint = AbstractProjectDialog.BOTTOM_BUTTON_WIDTH;
        finish.setLayoutData(finishData);
        finish.setText(Messages.AbstractProjectDialog_Finish);
        finish.setEnabled(false);
        finish.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                dialog.dispose();
            }
        });

        final Button cancel = new Button(buttonArea, SWT.PUSH);
        final GridData cancelData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        cancelData.widthHint = AbstractProjectDialog.BOTTOM_BUTTON_WIDTH;
        cancel.setLayoutData(cancelData);
        cancel.setText(Messages.AbstractProjectDialog_Cancel);
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
