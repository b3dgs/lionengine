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
package com.b3dgs.lionengine.editor.dialog.minimap;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.swt.ToolsSwt;
import com.b3dgs.lionengine.editor.Focusable;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
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
import com.b3dgs.lionengine.game.object.Factory;

/**
 * Minimap dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class MinimapDialog implements MouseListener, MouseMoveListener, MouseWheelListener,
                                 WorldMouseMoveListener, WorldMouseScrollListener, WorldKeyboardListener, Focusable
{
    /** Active reference. */
    private static volatile MinimapDialog instance;
    /** Last location. */
    private static volatile Point lastLocation;
    /** Minimap shell. */
    private static volatile Shell minimapShell;

    /**
     * Create the minimap dialog.
     * 
     * @param parent The parent reference.
     */
    public static synchronized void create(Shell parent)
    {
        if (instance == null)
        {
            instance = new MinimapDialog(parent);
            instance.open();
            if (lastLocation != null)
            {
                instance.shell.setLocation(lastLocation);
            }
        }
    }

    /** Map reference. */
    private final MapTile map = WorldModel.INSTANCE.getMap();
    /** Camera reference. */
    private final Camera camera = WorldModel.INSTANCE.getCamera();
    /** Minimap reference. */
    private final Minimap minimap = WorldModel.INSTANCE.getMinimap();
    /** World reference. */
    private final WorldPart part = WorldModel.INSTANCE.getServices().get(WorldPart.class);
    /** Shell dialog. */
    private final Shell shell;
    /** Composite reference. */
    private final Composite composite;
    /** Minimap configuration. */
    private final Text config;
    /** Green color. */
    private final Color green;
    /** GC minimap. */
    private GC gc;
    /** Mouse click. */
    private volatile boolean click;
    /** Minimap active. */
    private volatile boolean active;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    private MinimapDialog(Shell parent)
    {
        shell = new Shell(parent, SWT.DIALOG_TRIM);
        shell.setText(Messages.Title);
        shell.setLayout(UtilSwt.borderless());

        minimap.load();

        composite = new Composite(shell, SWT.DOUBLE_BUFFERED);
        composite.setLayoutData(new GridData(minimap.getWidth(), minimap.getHeight()));
        gc = new GC(composite);
        green = shell.getDisplay().getSystemColor(SWT.COLOR_GREEN);

        config = createConfigLocation(shell);
        createButtons(parent, shell);
    }

    /**
     * Open minimap dialog.
     */
    private void open()
    {
        composite.addMouseListener(this);
        composite.addMouseMoveListener(this);
        composite.addMouseTrackListener(UtilSwt.createFocusListener(this));

        final WorldUpdater updater = part.getUpdater();
        updater.addMouseMoveListener(this);
        updater.addMouseScrollListener(this);
        updater.addKeyboardListener(this);

        shell.addDisposeListener(event ->
        {
            updater.removeListeners(MinimapDialog.this);
            lastLocation = shell.getLocation();
            instance = null;
        });
        shell.addMouseWheelListener(this);

        UtilSwt.open(shell);
    }

    /**
     * Create the buttons area.
     * 
     * @param parentShell The shell parent.
     * @param parent The parent reference.
     */
    private void createButtons(Shell parentShell, Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        content.setLayout(new GridLayout(3, false));

        final Button browse = UtilButton.createBrowse(content);
        browse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        UtilButton.setAction(browse, () -> selectConfig());

        final Button automatic = UtilButton.create(content, Messages.Generate, AbstractDialog.ICON_OK);
        automatic.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        UtilButton.setAction(automatic, () -> actionAutomatic(config.getText()));

        final Button finish = UtilButton.create(content,
                                                com.b3dgs.lionengine.editor.dialog.Messages.Finish,
                                                AbstractDialog.ICON_OK);
        finish.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        UtilButton.setAction(finish, () -> openMinimapShell(parentShell));
    }

    /**
     * Open the minimap shell and dispose the current shell.
     * 
     * @param parent The parent shell
     */
    private void openMinimapShell(Shell parent)
    {
        final WorldUpdater updater = part.getUpdater();
        updater.addMouseMoveListener(this);
        updater.addMouseScrollListener(this);
        updater.addKeyboardListener(this);

        gc.dispose();
        shell.dispose();
        if (minimapShell != null)
        {
            minimapShell.dispose();
        }
        minimapShell = new Shell(parent, SWT.BORDER | SWT.DOUBLE_BUFFERED);
        minimapShell.setSize(minimap.getWidth(), minimap.getHeight());
        minimapShell.setLayout(UtilSwt.borderless());
        minimapShell.setBackgroundImage(ToolsSwt.getBuffer(minimap.getSurface()));
        minimapShell.addMouseListener(this);
        minimapShell.addMouseMoveListener(this);
        minimapShell.addMouseWheelListener(this);
        minimapShell.addMouseTrackListener(UtilSwt.createFocusListener(() -> minimapShell.forceFocus()));
        minimapShell.addDisposeListener(event -> updater.removeListeners(MinimapDialog.this));
        minimapShell.setLocation(part.getRenderer().getLocation());
        minimapShell.open();
        gc = new GC(minimapShell);
        render();
    }

    /**
     * Create the configuration location area.
     * 
     * @param parent The parent composite.
     * @return The created text.
     */
    private Text createConfigLocation(final Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        content.setLayout(new GridLayout(2, false));

        final Label locationLabel = new Label(content, SWT.NONE);
        locationLabel.setText(com.b3dgs.lionengine.editor.project.dialog.Messages.AbstractProjectDialog_Location);
        locationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        final Text text = new Text(content, SWT.BORDER);
        text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        return text;
    }

    /**
     * Load minimap configuration.
     * 
     * @param media The configuration media.
     */
    private void loadConfig(Media media)
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
    private void render()
    {
        if (!gc.isDisposed())
        {
            gc.drawImage(ToolsSwt.getBuffer(minimap.getSurface()), 0, 0);
            gc.setForeground(green);

            final int width = camera.getWidth() / map.getTileWidth();
            final int height = camera.getHeight() / map.getTileHeight();
            final int x = (int) camera.getX() / map.getTileWidth();
            final int y = minimap.getHeight() - (int) camera.getY() / map.getTileHeight() - height - 1;
            gc.drawRectangle(x, y, width, height);

            active = true;
        }
    }

    /**
     * Select the configuration file.
     */
    private void selectConfig()
    {
        final File file = UtilDialog.selectResourceFile(shell, true, new String[]
        {
            Messages.FileDesc
        }, new String[]
        {
            "*.xml"
        });
        if (file != null)
        {
            final String normalized = UtilFile.normalizeExtension(file.getName(), Factory.FILE_DATA_EXTENSION);
            final Media media = Medias.get(new File(file.getParentFile(), normalized));
            config.setText(media.getPath());
            if (!file.exists())
            {
                minimap.automaticColor(media);
            }
            else
            {
                loadConfig(media);
            }
        }
    }

    /**
     * Called when clicked on automatic button.
     * 
     * @param text The selected minimap configuration text.
     */
    private void actionAutomatic(String text)
    {
        if (text.isEmpty())
        {
            minimap.automaticColor(null);
            render();
        }
        else
        {
            final Media media = Medias.create(UtilFile.normalizeExtension(text, Factory.FILE_DATA_EXTENSION));
            minimap.automaticColor(media);
            render();
        }
    }

    /**
     * Move the map from minimap location.
     * 
     * @param mx The mouse X.
     * @param my The mouse Y.
     */
    private void moveMap(int mx, int my)
    {
        if (click && active)
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
        if (active)
        {
            part.getUpdater().mouseScrolled(event);
            part.getRenderer().mouseScrolled(event);
        }
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
        if (active)
        {
            render();
        }
    }

    /*
     * WorldKeyboardListener
     */

    @Override
    public void onKeyPushed(Integer key)
    {
        if (active)
        {
            render();
        }
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
