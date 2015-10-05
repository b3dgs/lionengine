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
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
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
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.core.swt.ToolsSwt;
import com.b3dgs.lionengine.editor.Focusable;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilSwt;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.WorldPart;
import com.b3dgs.lionengine.editor.world.updater.WorldKeyboardListener;
import com.b3dgs.lionengine.editor.world.updater.WorldMouseMoveListener;
import com.b3dgs.lionengine.editor.world.updater.WorldMouseScrollListener;
import com.b3dgs.lionengine.editor.world.updater.WorldUpdater;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Minimap;

/**
 * Minimap dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MinimapDialog implements MouseListener, MouseMoveListener, MouseWheelListener, WorldMouseMoveListener,
                           WorldMouseScrollListener, WorldKeyboardListener, Focusable
{
    /** Shell. */
    final Shell shell;
    /** World reference. */
    final WorldPart part;
    /** Minimap reference. */
    final Minimap minimap = WorldModel.INSTANCE.getMinimap();
    /** Map reference. */
    private final MapTile map = WorldModel.INSTANCE.getMap();
    /** Camera reference. */
    private final Camera camera = WorldModel.INSTANCE.getCamera();
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
        shell.setLayout(UtilSwt.borderless());

        part = WorldModel.INSTANCE.getServices().get(WorldPart.class);
    }

    /**
     * Open minimap window.
     */
    public void open()
    {
        minimap.load();

        final Composite composite = new Composite(shell, SWT.DOUBLE_BUFFERED);
        composite.setLayoutData(new GridData(minimap.getWidth(), minimap.getHeight()));
        gc = new GC(composite);
        composite.addMouseListener(this);
        composite.addMouseMoveListener(this);

        createConfigLocation(shell);
        createButtons(shell);

        final WorldUpdater updater = part.getUpdater();
        updater.addMouseMoveListener(this);
        updater.addMouseScrollListener(this);
        updater.addKeyboardListener(this);
        shell.addDisposeListener(event -> updater.removeListeners(MinimapDialog.this));
        shell.addMouseWheelListener(this);
        composite.addMouseTrackListener(UtilSwt.createFocusListener(this));

        UtilSwt.open(shell);
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
     * Render the minimap.
     */
    void render()
    {
        if (!gc.isDisposed())
        {
            gc.drawImage(ToolsSwt.getBuffer(minimap.getSurface()), 0, 0);
            gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_GREEN));

            final int width = camera.getWidth() / map.getTileWidth();
            final int height = camera.getHeight() / map.getTileHeight();
            final int x = (int) camera.getX() / map.getTileWidth();
            final int y = minimap.getHeight() - (int) camera.getY() / map.getTileHeight() - height - 1;
            gc.drawRectangle(x, y, width, height);
        }
    }

    /**
     * Select the configuration file.
     * 
     * @param file The configuration file.
     */
    void selectConfig(File file)
    {
        final Media media = Medias.get(file);
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (final IOException exception)
            {
                Verbose.exception(getClass(), "widgetSelected", exception);
            }
            minimap.automaticColor(media);
        }
        config.setText(media.getPath());
        loadConfig(media);
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
        automatic.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                if (configMedia.getText().isEmpty())
                {
                    minimap.automaticColor(null);
                    render();
                }
                else
                {
                    final Media media = Medias.create(configMedia.getText());
                    if (media.getFile().isFile())
                    {
                        minimap.automaticColor(media);
                        render();
                    }
                }
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
                    selectConfig(file);
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
            part.update();
            render();
        }
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
     * MouseMoveListener
     */

    @Override
    public void mouseMove(MouseEvent event)
    {
        moveMap(event.x, event.y);
    }

    /*
     * MouseWheelListener
     */

    @Override
    public void mouseScrolled(MouseEvent event)
    {
        part.getUpdater().mouseScrolled(event);
        part.getRenderer().mouseScrolled(event);
    }

    /*
     * WorldMouseMoveListener
     */

    @Override
    public void onMouseMoved(int click, int oldMx, int oldMy, int mx, int my)
    {
        if (click > 0)
        {
            render();
        }
    }

    /*
     * WorldMouseScrollListener
     */

    @Override
    public void onMouseScroll(int value, int mx, int my)
    {
        render();
    }

    /*
     * WorldKeyboardListener
     */

    @Override
    public void onKeyPushed(Integer key)
    {
        render();
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
