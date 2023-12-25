/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.editor.dialog;

import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.utility.control.UtilSwt;

/**
 * Abstract editor.
 */
public abstract class EditorAbstract implements MDirtyable
{
    /** Shell reference. */
    private final Shell shell;
    /** Title. */
    private final String title;
    /** Dirty flag. */
    private boolean dirty;
    /** Last dirty flag. */
    private boolean dirtyOld;

    /**
     * Editor constructor base.
     * 
     * @param parent The parent reference.
     * @param title The editor title.
     * @param icon The editor icon.
     */
    protected EditorAbstract(Composite parent, String title, Image icon)
    {
        this.title = title;
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
    public void create()
    {
        createContent(shell);
        createBottom(shell);
        shell.layout(true, true);
    }

    /**
     * Open the dialog and wait for close.
     */
    public void openAndWait()
    {
        UtilSwt.open(shell);
        shell.setData(this);
        final Display display = shell.getDisplay();
        while (!shell.isDisposed())
        {
            if (!display.readAndDispatch())
            {
                display.sleep();
            }
        }
    }

    /**
     * Set the editor minimum size.
     * 
     * @param width The horizontal size.
     * @param height The vertical size.
     */
    public final void setMinimumSize(int width, int height)
    {
        shell.setMinimumSize(width, height);
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

        final Button exit = UtilButton.create(bottom, Messages.Exit, null);
        exit.setImage(DialogAbstract.ICON_EXIT);
        UtilButton.setAction(exit, () ->
        {
            onExit();
            shell.dispose();
        });
    }

    @Override
    public void setDirty(boolean value)
    {
        dirty = value;
        if (dirtyOld != dirty)
        {
            dirtyOld = dirty;
            if (dirty)
            {
                shell.setText(Constant.STAR + title);
                shell.update();
            }
            else
            {
                shell.update();
                shell.setText(title);
            }
        }
    }

    @Override
    public boolean isDirty()
    {
        return dirty;
    }
}
