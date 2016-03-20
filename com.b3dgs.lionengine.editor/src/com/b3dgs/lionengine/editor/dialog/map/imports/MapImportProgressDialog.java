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
package com.b3dgs.lionengine.editor.dialog.map.imports;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.editor.dialog.AbstractProgressDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.map.LevelRipConverter;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Map import progress dialog.
 */
public class MapImportProgressDialog extends AbstractProgressDialog
                                     implements LevelRipConverter.ProgressListener, LevelRipConverter.Canceler
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "import.png");

    /** Media level. */
    private final Media levelRip;
    /** Minimap. */
    private Image minimap;
    /** Width. */
    private int width;
    /** Height. */
    private int height;
    /** Discover GC. */
    private GC gc;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     * @param levelRip The level rip used.
     */
    public MapImportProgressDialog(Shell parent, Media levelRip)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.Progress, ICON);
        this.levelRip = levelRip;
        createDialog();
        finish.dispose();
        cancel.getParent().setLayout(new GridLayout(1, false));
        cancel.getParent().layout();
        dialog.setMinimumSize(128, 128);
    }

    /**
     * Draw the map progress.
     * 
     * @param percent The progress percent.
     * @param progressTileX The horizontal progress.
     * @param progressTileY The vertical progress.
     */
    private void drawMap(int percent, int progressTileX, int progressTileY)
    {
        gc.drawImage(minimap, 0, 0);
        if (percent < 100)
        {
            if (progressTileY < 100)
            {
                gc.fillRectangle(0, progressTileY + 1, width, height - progressTileY);
            }
            gc.fillRectangle(progressTileX, progressTileY, width - progressTileX, 1);
        }
    }

    @Override
    protected void createProgressContent(Composite content)
    {
        try (final InputStream stream = levelRip.getInputStream())
        {
            final ImageData data = new ImageData(stream);
            final MapTile map = WorldModel.INSTANCE.getMap();

            width = data.width / map.getTileWidth();
            height = data.height / map.getTileHeight();

            final ImageData minimapData = data.scaledTo(width, height);
            minimap = new Image(content.getDisplay(), minimapData);

            final Label label = new Label(dialog, SWT.NONE);
            label.setLayoutData(new GridData(minimapData.width, minimapData.height));

            gc = new GC(label);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    @Override
    protected void onTerminated()
    {
        gc.dispose();
        minimap.dispose();
    }

    @Override
    public void notifyProgress(int percent, int progressTileX, int progressTileY)
    {
        if (!isDisposed())
        {
            setProgress(percent);
            drawMap(percent, progressTileX, progressTileY);
            dialog.getDisplay().readAndDispatch();
        }
    }

    @Override
    public boolean isCanceled()
    {
        return super.isCanceled();
    }
}
