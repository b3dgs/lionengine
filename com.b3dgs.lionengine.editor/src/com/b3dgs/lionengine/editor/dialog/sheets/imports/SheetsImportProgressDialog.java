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
package com.b3dgs.lionengine.editor.dialog.sheets.imports;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.editor.dialog.AbstractProgressDialog;
import com.b3dgs.lionengine.game.tile.TileExtractor;

/**
 * Sheets import progress dialog.
 */
public class SheetsImportProgressDialog extends AbstractProgressDialog
                                        implements TileExtractor.ProgressListener, TileExtractor.Canceler
{
    /** Image width. */
    private final int width;
    /** Image height. */
    private final int height;
    /** Current sheets area. */
    private GC gc;
    /** Last image. */
    private ImageBuffer last;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     * @param width Image width.
     * @param height Image height.
     */
    public SheetsImportProgressDialog(Shell parent, int width, int height)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.Progress, SheetsImportDialog.ICON);
        this.width = width;
        this.height = height;
        createDialog();
        finish.dispose();
        cancel.getParent().setLayout(new GridLayout(1, false));
        cancel.getParent().layout();
        dialog.setMinimumSize(128, 128);
    }

    @Override
    protected void createProgressContent(Composite content)
    {
        final Label label = new Label(dialog, SWT.NONE);
        label.setLayoutData(new GridData(width, height));
        gc = new GC(label);
    }

    @Override
    protected void onTerminated()
    {
        // Nothing to do
    }

    @Override
    public void notifyProgress(int percent, ImageBuffer current)
    {
        if (!isDisposed())
        {
            setProgress(percent);
            if (last != current)
            {
                last = current;
                gc.fillRectangle(0, 0, current.getWidth(), current.getHeight());
            }
            gc.drawImage((Image) current.getSurface(), 0, 0);
            dialog.getDisplay().readAndDispatch();
        }
    }

    @Override
    public void notifyExtracted(ImageBuffer sheet)
    {
        // Nothing to do
    }
}
