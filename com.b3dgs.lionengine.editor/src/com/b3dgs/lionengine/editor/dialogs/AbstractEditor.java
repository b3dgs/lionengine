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
package com.b3dgs.lionengine.editor.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.UtilSwt;

/**
 * Abstract editor.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class AbstractEditor
{
    /** Shell reference. */
    final Shell shell;

    /**
     * Editor constructor base.
     * 
     * @param title The editor title.
     * @param icon The editor icon.
     * @param parent The parent reference.
     */
    public AbstractEditor(String title, Image icon, Composite parent)
    {
        shell = new Shell(parent.getDisplay(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
        shell.setLayout(new GridLayout(1, false));
        shell.setImage(icon);
        shell.setText(title);
    }

    /**
     * Create the editor content.
     * 
     * @param parent The parent composite.
     */
    protected abstract void createContent(Composite parent);

    /**
     * Open the dialog.
     */
    public void open()
    {
        createContent(shell);
        createBottom(shell);
        shell.pack(true);
        shell.layout(true, true);
        UtilSwt.center(shell);
        shell.setVisible(true);
    }

    /**
     * Called when editor is closed. Does nothing by default.
     */
    protected void onExit()
    {
        // Nothing to do
    }

    /**
     * Create the bottom part.
     * 
     * @param parent The parent reference.
     */
    private void createBottom(Composite parent)
    {
        final Composite bottom = new Composite(parent, SWT.NONE);
        bottom.setLayout(new GridLayout(1, false));
        bottom.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

        final Button exit = UtilSwt.createButton(bottom, "Exit", null);
        exit.setImage(AbstractDialog.ICON_EXIT);
        exit.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                onExit();
                shell.dispose();
            }
        });
    }
}
