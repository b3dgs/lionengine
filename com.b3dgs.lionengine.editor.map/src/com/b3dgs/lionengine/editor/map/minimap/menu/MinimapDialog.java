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
package com.b3dgs.lionengine.editor.map.minimap.menu;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.editor.dialog.DialogAbstract;
import com.b3dgs.lionengine.editor.utility.Focusable;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.utility.control.UtilSwt;
import com.b3dgs.lionengine.editor.utility.dialog.UtilDialog;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.updater.WorldKeyboardListener;
import com.b3dgs.lionengine.editor.world.updater.WorldMouseMoveListener;
import com.b3dgs.lionengine.editor.world.updater.WorldMouseScrollListener;
import com.b3dgs.lionengine.editor.world.updater.WorldUpdater;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.Minimap;

/**
 * Minimap dialog.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
public final class MinimapDialog implements MouseListener, MouseMoveListener, MouseWheelListener,
                                 WorldMouseMoveListener, WorldMouseScrollListener, WorldKeyboardListener, Focusable
{
    /** Dialog instance. */
    private static MinimapDialog instance;
    /** Last location. */
    private static Point lastLocation;
    /** Minimap shell. */
    private static Shell minimapShell;

    /**
     * Open the dialog.
     * 
     * @param parent The parent shell.
     */
    public static void open(Shell parent)
    {
        if (instance == null)
        {
            instance = new MinimapDialog(parent);
            instance.open();
        }
    }

    /**
     * Set the current dialog instance.
     * 
     * @param instance The instance reference.
     */
    private static void setInstance(MinimapDialog instance)
    {
        MinimapDialog.instance = instance;
    }

    /**
     * Set the current minimap shell.
     * 
     * @param minimapShell The minimap shell.
     */
    private static void setMinimapShell(Shell minimapShell)
    {
        MinimapDialog.minimapShell = minimapShell;
    }

    /**
     * Set the last location.
     * 
     * @param location The last location.
     */
    private static void setLastLocation(Point location)
    {
        lastLocation = location;
    }

    /**
     * Get the last location.
     * 
     * @return The last location.
     */
    private static Point getLastLocation()
    {
        return lastLocation;
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
    private boolean click;
    /** Minimap active. */
    private boolean active;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    private MinimapDialog(Shell parent)
    {
        shell = new Shell(parent, SWT.DIALOG_TRIM);
        shell.setText(Messages.Title);
        shell.setToolTipText(Messages.Title);
        shell.setLayout(UtilSwt.borderless());

        minimap.load();

        composite = new Composite(shell, SWT.DOUBLE_BUFFERED);
        composite.setToolTipText(Messages.Title);
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
            updater.removeListeners(this);
            setLastLocation(shell.getLocation());
            setInstance(null);
        });
        shell.addMouseWheelListener(this);

        UtilSwt.open(shell);
        final Point location = getLastLocation();
        if (location != null)
        {
            shell.setLocation(location);
        }
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
        final int items = 3;
        content.setLayout(new GridLayout(items, false));

        final Button browse = UtilButton.createBrowse(content);
        browse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        UtilButton.setAction(browse, this::selectConfig);

        final Button automatic = UtilButton.create(content, Messages.Generate, DialogAbstract.ICON_OK);
        automatic.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        UtilButton.setAction(automatic, () -> actionAutomatic(config.getText()));

        final Button finish = UtilButton.create(content,
                                                com.b3dgs.lionengine.editor.dialog.Messages.Finish,
                                                DialogAbstract.ICON_OK);
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
        setMinimapShell(createMiniShell(parent));
        render();
    }

    /**
     * Create and open the mini shell.
     * 
     * @param parent The shell parent.
     * @return The mini shell.
     */
    private Shell createMiniShell(Shell parent)
    {
        final Shell miniShell = new Shell(parent, SWT.NONE);
        miniShell.setLayout(UtilSwt.borderless());
        miniShell.setText(Messages.Title);

        final Canvas canvas = new Canvas(miniShell, SWT.DOUBLE_BUFFERED);
        canvas.setToolTipText(Messages.Title);
        canvas.setLayoutData(new GridData(minimap.getWidth(), minimap.getHeight()));
        canvas.setBackgroundImage((Image) minimap.getSurface().getSurface());
        canvas.addMouseListener(this);
        canvas.addMouseMoveListener(this);
        canvas.addMouseWheelListener(this);
        canvas.addMouseTrackListener(UtilSwt.createFocusListener(canvas::forceFocus));
        canvas.addDisposeListener(event -> part.getUpdater().removeListeners(this));

        final WorldPart worldPart = part;
        miniShell.setLocation(worldPart.getLocation());
        parent.addControlListener(new ControlListener()
        {
            @Override
            public void controlResized(ControlEvent event)
            {
                parent.getDisplay().asyncExec(() -> miniShell.setLocation(worldPart.getLocation()));
            }

            @Override
            public void controlMoved(ControlEvent event)
            {
                miniShell.setLocation(worldPart.getLocation());
            }
        });
        miniShell.pack();
        miniShell.open();
        gc = new GC(canvas);
        return miniShell;
    }

    /**
     * Create the configuration location area.
     * 
     * @param parent The parent composite.
     * @return The created text.
     */
    private static Text createConfigLocation(final Composite parent)
    {
        final Composite content = new Composite(parent, SWT.NONE);
        content.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        content.setLayout(new GridLayout(2, false));

        final Label locationLabel = new Label(content, SWT.NONE);
        locationLabel.setText(com.b3dgs.lionengine.editor.dialog.project.Messages.AbstractProjectDialog_Location);
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
            gc.fillRectangle(0, 0, minimap.getWidth(), minimap.getHeight());
            gc.drawImage((Image) minimap.getSurface().getSurface(), 0, 0);
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
        UtilDialog.selectResourceFile(shell, true, UtilDialog.getXmlFilter()).ifPresent(media ->
        {
            config.setText(media.getPath());
            if (!media.exists())
            {
                minimap.automaticColor(media);
            }
            else
            {
                loadConfig(media);
            }
        });
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
            minimap.automaticColor();
        }
        else
        {
            final Media media = Medias.create(UtilFile.normalizeExtension(text, Factory.FILE_DATA_EXTENSION));
            minimap.automaticColor(media);
        }
        minimap.prepare();
        render();
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
            final double x = mx * (double) map.getTileWidth();
            final double y = (minimap.getHeight() - my) * (double) map.getTileHeight();
            camera.setLocation(x, y);
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
