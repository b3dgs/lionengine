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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.utility.control.UtilSwt;

/**
 * Abstract progress dialog.
 */
public abstract class ProgressDialogAbstract extends DialogAbstract
{
    /** Progress bar. */
    private ProgressBar progress;

    /**
     * Dialog constructor base.
     * 
     * @param parent The parent reference.
     * @param title The dialog title.
     * @param headerTitle The header title.
     * @param headerDesc The header description.
     * @param headerIcon The header icon.
     */
    protected ProgressDialogAbstract(Shell parent,
                                     String title,
                                     String headerTitle,
                                     String headerDesc,
                                     Image headerIcon)
    {
        super(parent, title, headerTitle, headerDesc, headerIcon);
    }

    /**
     * Create the area under progress bar.
     * 
     * @param content The content reference.
     */
    protected abstract void createProgressContent(Composite content);

    /**
     * Called once dialog has terminated (finished or canceled).
     */
    protected abstract void onTerminated();

    /**
     * Terminate dialog manually.
     */
    public void finish()
    {
        onFinish();
        dialog.dispose();
    }

    /**
     * Set progress percent value.
     * 
     * @param percent The percent value.
     */
    public void setProgress(int percent)
    {
        if (!progress.isDisposed())
        {
            progress.setSelection(percent);
        }
    }

    /**
     * Check if progress is disposed.
     * 
     * @return <code>true</code> if disposed, <code>false</code> else.
     */
    public boolean isDisposed()
    {
        return progress.isDisposed();
    }

    @Override
    protected void createContent(Composite content)
    {
        progress = new ProgressBar(content, SWT.HORIZONTAL);
        progress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        progress.setMinimum(0);
        progress.setMaximum(100);

        final Label separatorHeader = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createProgressContent(content);
    }

    @Override
    public void open()
    {
        UtilSwt.open(dialog);
    }

    @Override
    protected void onFinish()
    {
        onTerminated();
    }

    @Override
    protected void onCanceled()
    {
        onTerminated();
    }
}
