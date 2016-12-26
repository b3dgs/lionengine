/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map.sheet.extract;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.editor.dialog.AbstractProgressDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilSwt;
import com.b3dgs.lionengine.game.feature.tile.TilesExtractor;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Extract sheets progress dialog.
 */
public class SheetsExtractProgressDialog extends AbstractProgressDialog
                                         implements TilesExtractor.ProgressListener, TilesExtractor.Canceler
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "import.png");
    /** Default horizontal tiles number. */
    private static final int DEFAULT_HORIZONTAL_TILES = 16;

    /** Image width. */
    private final int horizontalTiles;
    /** Label reference. */
    private Label label;
    /** Current sheets area. */
    private GC gc;
    /** Old height. */
    private int oldHeight = Integer.MIN_VALUE;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     * @param horizontalTiles The horizontal tiles per line.
     */
    public SheetsExtractProgressDialog(Shell parent, int horizontalTiles)
    {
        super(parent, Messages.Title_Progress, Messages.HeaderTitle, Messages.Progress, ICON);
        if (horizontalTiles > 0)
        {
            this.horizontalTiles = horizontalTiles;
        }
        else
        {
            this.horizontalTiles = DEFAULT_HORIZONTAL_TILES;
        }

        createDialog();
        finish.dispose();
        cancel.getParent().setLayout(new GridLayout(1, false));
        cancel.getParent().layout();
        dialog.setMinimumSize(256, 256);
    }

    /**
     * Update the rendering size depending of the tiles.
     * 
     * @param tiles The extracted tiles collection.
     */
    private void updateSize(Collection<ImageBuffer> tiles)
    {
        final int height = (int) Math.ceil(tiles.size() / (double) horizontalTiles);
        if (height > oldHeight && !tiles.isEmpty())
        {
            if (gc != null)
            {
                gc.dispose();
            }
            final ImageBuffer tile = tiles.iterator().next();
            label.setLayoutData(new GridData(horizontalTiles * tile.getWidth(), (height + 1) * tile.getHeight()));
            label.getParent().pack(true);
            gc = new GC(label);
            oldHeight = height;
        }
    }

    /*
     * AbstractProgressDialog
     */

    @Override
    protected void createProgressContent(Composite content)
    {
        label = new Label(dialog, SWT.NONE);
    }

    @Override
    protected void onTerminated()
    {
        if (gc != null)
        {
            gc.dispose();
        }
    }

    /*
     * ProgressListener
     */

    @Override
    public void notifyProgress(int percent, Collection<ImageBuffer> tiles)
    {
        setProgress(percent);

        dialog.getDisplay().asyncExec(() ->
        {
            if (!isDisposed())
            {
                updateSize(tiles);
                int i = 0;
                for (final ImageBuffer tile : tiles)
                {
                    final int x = i % horizontalTiles;
                    final int y = (int) Math.floor(i / (double) horizontalTiles);

                    gc.drawImage((Image) tile.getSurface(), x * tile.getWidth(), y * tile.getHeight());
                    i++;
                }

                dialog.update();
                UtilSwt.center(dialog);
            }
        });
        dialog.getDisplay().readAndDispatch();
    }
}
