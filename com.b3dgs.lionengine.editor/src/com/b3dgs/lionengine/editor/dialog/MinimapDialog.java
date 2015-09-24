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
package com.b3dgs.lionengine.editor.dialog;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.swt.UtilityImage;
import com.b3dgs.lionengine.core.swt.UtilityMedia;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.utility.UtilSwt;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.WorldPart;
import com.b3dgs.lionengine.editor.world.updater.WorldUpdateListener;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Minimap;

/**
 * Minimap dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MinimapDialog implements MouseListener, MouseMoveListener, WorldUpdateListener
{
    /** Shell. */
    final Shell shell;
    /** Minimap reference. */
    final Minimap minimap = WorldModel.INSTANCE.getMinimap();
    /** Map reference. */
    private final MapTile map = WorldModel.INSTANCE.getMap();
    /** Camera reference. */
    private final Camera camera = WorldModel.INSTANCE.getCamera();
    /** World reference. */
    final WorldPart part;
    /** Minimap configuration. */
    private Text config;
    /** GC minimap. */
    private volatile GC gc;
    /** Mouse click. */
    private volatile boolean click;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    public MinimapDialog(Shell parent)
    {
        shell = new Shell(parent, SWT.DIALOG_TRIM);
        shell.setText(Messages.Minimap_Title);

        final GridLayout layout = new GridLayout(1, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        shell.setLayout(layout);

        part = UtilPart.getPart(WorldPart.ID, WorldPart.class);
    }

    /**
     * Open minimap window.
     */
    public void open()
    {
        minimap.load();

        final Label label = new Label(shell, SWT.NONE);
        label.setLayoutData(new GridData(minimap.getWidth(), minimap.getHeight()));
        gc = new GC(label);
        label.addMouseListener(this);
        label.addMouseMoveListener(this);

        createConfigLocation(shell);
        createButtons(shell);

        shell.pack();
        UtilSwt.center(shell);
        shell.setVisible(true);

        part.getUpdater().addListener(this);
        shell.addDisposeListener(new DisposeListener()
        {
            @Override
            public void widgetDisposed(DisposeEvent event)
            {
                part.getUpdater().removeListener(MinimapDialog.this);
            }
        });
    }

    /**
     * Load minimap configuration.
     * 
     * @param media The configuration media.
     */
    void loadConfig(Media media)
    {
        if (media.getFile().isFile())
        {
            minimap.loadPixelConfig(media);
            minimap.prepare();
            render();
        }
    }

    /**
     * Create the configuration location area.
     * 
     * @param parent The parent composite.
     */
    private void createConfigLocation(final Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        content.setLayout(new GridLayout(2, false));

        final Label locationLabel = new Label(content, SWT.NONE);
        locationLabel.setText(com.b3dgs.lionengine.editor.project.dialog.Messages.AbstractProjectDialog_Location);
        locationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        config = new Text(content, SWT.BORDER);
        config.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
    }

    /**
     * Create the buttons area.
     * 
     * @param parent The parent reference.
     */
    private void createButtons(Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        content.setLayout(new GridLayout(3, false));

        final Text configMedia = config;
        final Button browse = UtilButton.create(content, Messages.AbstractDialog_Browse, AbstractDialog.ICON_BROWSE);
        browse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Button automatic = UtilButton.create(content, Messages.Minimap_Generate, AbstractDialog.ICON_OK);
        automatic.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        automatic.setEnabled(false);
        automatic.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                final Media media = Medias.create(configMedia.getText());
                if (media.getFile().isFile())
                {
                    minimap.automaticColor(media);
                }
                loadConfig(media);
            }
        });

        browse.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final File file = UtilDialog.selectResourceFile(parent.getShell(), true, new String[]
                {
                    Messages.Minimap_FileDesc
                }, new String[]
                {
                    "*.xml"
                });
                if (file != null)
                {
                    final Media media = UtilityMedia.get(file);
                    configMedia.setText(media.getPath());
                    automatic.setEnabled(media.getFile().isFile());
                    loadConfig(media);
                }
            }
        });

        final Button finish = UtilButton.create(content, Messages.AbstractDialog_Finish, AbstractDialog.ICON_OK);
        finish.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        finish.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                shell.dispose();
            }
        });
    }

    /**
     * Render the minimap.
     */
    private void render()
    {
        shell.getDisplay().asyncExec(() ->
        {
            if (!gc.isDisposed())
            {
                gc.drawImage(UtilityImage.getBuffer(minimap.getSurface()), 0, 0);
                gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_GREEN));

                final int width = camera.getWidth() / map.getTileWidth();
                final int height = camera.getHeight() / map.getTileHeight();
                final int x = (int) camera.getX() / map.getTileWidth();
                final int y = minimap.getHeight() - (int) camera.getY() / map.getTileHeight() - height - 1;
                gc.drawRectangle(x, y, width, height);
            }
        });
    }

    /**
     * Move the map from minimap location.
     * 
     * @param mx The mouse X.
     * @param my The mouse Y.
     */
    private void moveMap(int mx, int my)
    {
        if (click)
        {
            camera.setLocation(mx * map.getTileWidth(), (minimap.getHeight() - my) * map.getTileHeight());
            shell.getDisplay().asyncExec(() ->
            {
                part.update();
            });
            render();
        }
    }

    /*
     * MouseMoveListener
     */

    @Override
    public void mouseMove(MouseEvent event)
    {
        moveMap(event.x, event.y);
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseDown(MouseEvent event)
    {
        click = true;
        moveMap(event.x, event.y);
    }

    @Override
    public void mouseUp(MouseEvent event)
    {
        click = false;
    }

    @Override
    public void mouseDoubleClick(MouseEvent event)
    {
        // Nothing to do
    }

    /*
     * WorldUpdateListener
     */

    @Override
    public void notifyWorldUpdated()
    {
        render();
    }
}
