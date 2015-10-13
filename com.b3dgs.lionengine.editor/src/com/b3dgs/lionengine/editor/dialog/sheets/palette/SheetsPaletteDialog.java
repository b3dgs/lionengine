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
package com.b3dgs.lionengine.editor.dialog.sheets.palette;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.core.swt.ToolsSwt;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.Focusable;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilSwt;
import com.b3dgs.lionengine.editor.utility.UtilText;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Sheets palette dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class SheetsPaletteDialog implements MouseListener, Focusable
{
    /** Dialog instance. */
    private static volatile SheetsPaletteDialog instance;
    /** Last location. */
    private static volatile Point lastLocation;

    /**
     * Open the dialog.
     * 
     * @param parent The parent shell.
     */
    public static synchronized void open(Shell parent)
    {
        if (instance == null)
        {
            instance = new SheetsPaletteDialog(parent);
            instance.open();
        }
    }

    /** Map reference. */
    private final MapTile map = WorldModel.INSTANCE.getMap();
    /** Shell dialog. */
    private final Shell shell;
    /** GC minimap. */
    private final GC gc;
    /** Current sheet id. */
    private volatile int sheetId;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    private SheetsPaletteDialog(Shell parent)
    {
        shell = new Shell(parent, SWT.DIALOG_TRIM);
        shell.setText(Messages.Title);
        shell.setLayout(UtilSwt.borderless());
        shell.addDisposeListener(event ->
        {
            lastLocation = shell.getLocation();
            instance = null;
        });

        int width = 0;
        int height = 0;
        for (final Integer id : map.getSheets())
        {
            final SpriteTiled sheet = map.getSheet(id);
            width = Math.max(width, sheet.getWidth());
            height = Math.max(height, sheet.getHeight());
        }

        final Composite composite = new Composite(shell, SWT.DOUBLE_BUFFERED);
        composite.setLayoutData(new GridData(width, height));
        composite.addMouseListener(this);
        gc = new GC(composite);

        createBottom();
    }

    /**
     * Open minimap dialog.
     */
    private void open()
    {
        UtilSwt.open(shell);
        if (lastLocation != null)
        {
            shell.setLocation(lastLocation);
        }
        render();
    }

    /**
     * Create the bottom part.
     */
    private void createBottom()
    {
        final Composite area = new Composite(shell, SWT.NONE);
        area.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        area.setLayout(new GridLayout(3, false));

        final Button previous = UtilButton.create(area, "<", null);
        final Text sheetIdText = UtilText.create("sheet", area);
        sheetIdText.setText(String.valueOf(sheetId));
        final Button next = UtilButton.create(area, ">", null);

        UtilButton.setAction(previous, () ->
        {
            sheetId--;
            sheetId = Math.max(sheetId, 0);
            sheetIdText.setText(String.valueOf(sheetId));
            render();
        });
        UtilButton.setAction(next, () ->
        {
            sheetId++;
            sheetId = Math.min(sheetId, map.getSheets().size() - 1);
            sheetIdText.setText(String.valueOf(sheetId));
            render();
        });
    }

    /**
     * Render the active sheet.
     */
    private void render()
    {
        if (!gc.isDisposed())
        {
            final SpriteTiled sheet = map.getSheet(Integer.valueOf(sheetId));
            gc.drawImage(ToolsSwt.getBuffer(sheet.getSurface()), 0, 0);
        }
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseDown(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseUp(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseDoubleClick(MouseEvent event)
    {
        // Nothing to do
    }

    /*
     * Focusable
     */

    @Override
    public void focus()
    {
        shell.forceFocus();
    }
}
