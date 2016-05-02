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
package com.b3dgs.lionengine.editor.map.group.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.swt.Mouse;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.map.group.editor.GroupList;
import com.b3dgs.lionengine.editor.map.group.editor.GroupProperties;
import com.b3dgs.lionengine.editor.map.world.renderer.WorldSelectedTiles;
import com.b3dgs.lionengine.editor.map.world.updater.TileSelectionListener;
import com.b3dgs.lionengine.editor.map.world.updater.WorldInteractionTile;
import com.b3dgs.lionengine.editor.utility.Focusable;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.utility.control.UtilSwt;
import com.b3dgs.lionengine.editor.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget;
import com.b3dgs.lionengine.editor.widget.levelrip.LevelRipWidget.LevelRipsWidgetListener;
import com.b3dgs.lionengine.editor.world.PaletteModel;
import com.b3dgs.lionengine.editor.world.PaletteType;
import com.b3dgs.lionengine.editor.world.Selection;
import com.b3dgs.lionengine.editor.world.renderer.WorldRenderer;
import com.b3dgs.lionengine.editor.world.updater.WorldUpdater;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.editor.world.view.WorldView;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.map.LevelRipConverter;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.MapTileGroup;
import com.b3dgs.lionengine.game.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.map.transition.MapTileTransitionModel;
import com.b3dgs.lionengine.game.object.Factory;
import com.b3dgs.lionengine.game.object.Handler;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.game.tile.TileGroup;
import com.b3dgs.lionengine.game.tile.TileGroupType;
import com.b3dgs.lionengine.game.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.tile.TileRef;

/**
 * Edit map tile groups dialog.
 */
public class GroupsEditDialog extends AbstractDialog implements WorldView, Focusable, KeyListener,
                              TileSelectionListener, ObjectListListener<TileGroup>
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "import.png");

    /**
     * Load sheets from configuration file.
     * 
     * @param config The configuration file.
     * @param folder The folder root.
     * @return The loaded sheets.
     */
    private static Collection<SpriteTiled> loadSheets(TileSheetsConfig config, String folder)
    {
        final int tw = config.getTileWidth();
        final int th = config.getTileHeight();
        final Collection<SpriteTiled> sheets = new ArrayList<>();
        for (final String sheet : config.getSheets())
        {
            final Media media = Medias.create(folder, sheet);
            final SpriteTiled surface = Drawable.loadSpriteTiled(media, tw, th);
            surface.load();
            surface.prepare();
            sheets.add(surface);
        }
        return sheets;
    }

    /** Service reference. */
    private final Services services = new Services();
    /** Group properties. */
    private final GroupProperties properties = new GroupProperties();
    /** Groups list. */
    private final GroupList groupList = new GroupList(properties);
    /** Group types mapping. */
    final Map<String, TileGroupType> groupsTypes = new HashMap<>();
    /** Map reference. */
    private final MapTile map;
    /** Map group. */
    private final MapTileGroup mapGroup;
    /** Level rips area. */
    private Composite levelsArea;
    /** Level rips widget. */
    private LevelRipWidget levelRips;
    /** Next button. */
    private Button next;
    /** Sheets config. */
    private BrowseWidget sheets;
    /** World view. */
    private Composite view;
    /** Part service. */
    @Inject
    private EPartService partService;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    public GroupsEditDialog(Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON, SWT.SHELL_TRIM);

        services.add(new Camera());
        services.add(new Handler());
        services.add(new Factory(services));
        map = services.create(MapTileGame.class);
        mapGroup = map.createFeature(MapTileGroupModel.class);
        map.addFeature(new MapTileTransitionModel(services));
        services.add(new Selection());

        final PaletteModel palette = new PaletteModel();
        palette.setSelectedPalette(PaletteType.POINTER_TILE);
        services.add(palette);

        createDialog();
        dialog.setMinimumSize(640, 448);
        finish.setEnabled(false);
    }

    /**
     * Save groups defined.
     */
    public void save()
    {
        final Media groupsMedia = Medias.create(sheets.getMedia().getParentPath(), TileGroupsConfig.FILENAME);
        TileGroupsConfig.exports(groupsMedia, getGroups());
    }

    /**
     * Set the save folder destination.
     * 
     * @param destination The destination folder.
     */
    public void setLocation(String destination)
    {
        sheets.setLocation(destination);
    }

    /**
     * Show the world view.
     */
    public void showWorldView()
    {
        final Composite content = levelsArea.getParent();
        levelsArea.dispose();
        createWorldView(content);

        setFinishEnabled(true);
        content.layout(true, true);
    }

    /**
     * Load the levels rip.
     * 
     * @param sheets The sheets used.
     * @param levelRips The level rips.
     */
    public void load(Collection<SpriteTiled> sheets, Media... levelRips)
    {
        int offsetX = 0;
        int offsetY = 0;
        boolean first = true;
        final int size = (int) Math.ceil(Math.sqrt(levelRips.length));
        int count = 0;
        for (final Media levelRip : levelRips)
        {
            count++;
            final MapTile part;
            if (first)
            {
                part = map;
                first = false;
            }
            else
            {
                part = new MapTileGame();
            }
            part.loadSheets(sheets);
            LevelRipConverter.start(levelRip, part);
            map.append(part, offsetX, offsetY);

            offsetX += part.getInTileWidth();
            if (count >= size)
            {
                offsetY += part.getInTileHeight();
                offsetX = 0;
                count = 0;
            }
        }
    }

    /**
     * Get the defined map tile groups.
     * 
     * @return The defined map tile groups.
     */
    private Collection<TileGroup> getGroups()
    {
        final Map<String, Collection<TileRef>> groupsRef = new HashMap<>();
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile tile = map.getTile(tx, ty);
                if (tile != null)
                {
                    addToGroup(groupsRef, tile);
                }
            }
        }
        final Collection<TileGroup> groups = new HashSet<>();
        for (final Entry<String, Collection<TileRef>> entry : groupsRef.entrySet())
        {
            final String name = entry.getKey();
            groups.add(new TileGroup(name, groupsTypes.get(name), entry.getValue()));
        }
        return groups;
    }

    /**
     * Add tile to group list.
     * 
     * @param groups The group list reference.
     * @param tile The tile to add.
     */
    private void addToGroup(Map<String, Collection<TileRef>> groups, Tile tile)
    {
        final String group = mapGroup.getGroup(tile);
        if (group != null)
        {
            if (!groups.containsKey(group))
            {
                groups.put(group, new HashSet<>());
            }
            final Collection<TileRef> tiles = groups.get(group);
            tiles.add(new TileRef(tile));
        }
    }

    /**
     * Get the selected tile group name.
     * 
     * @return The selected tile group name, <code>null</code> if none.
     */
    private String getSelectedGroup()
    {
        final TileGroup group = groupList.getSelectedObject();
        if (group != null)
        {
            return group.getName();
        }
        return null;
    }

    /**
     * Create the levels chooser area.
     * 
     * @param parent The parent composite.
     * @return The created area.
     */
    private Composite createLevelsArea(Composite parent)
    {
        levelsArea = new Composite(parent, SWT.NONE);
        levelsArea.setLayout(new GridLayout(1, false));
        levelsArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        levelRips = new LevelRipWidget(levelsArea);
        levelRips.addListener(new LevelRipsWidgetListener()
        {
            @Override
            public void notifyLevelRipAdded(Media media)
            {
                checkNextEnabled();
            }

            @Override
            public void notifyLevelRipRemoved(Media media)
            {
                checkNextEnabled();
            }
        });

        sheets = new BrowseWidget(levelsArea,
                                  com.b3dgs.lionengine.editor.map.imports.Messages.SheetsLocation,
                                  com.b3dgs.lionengine.editor.map.imports.Messages.SheetsConfigFileFilter,
                                  true);
        sheets.addListener(media -> checkNextEnabled());
        return levelsArea;
    }

    /**
     * Check if button next can be enabled or not.
     */
    private void checkNextEnabled()
    {
        next.setEnabled(levelRips.getLevelRips().length > 0 && sheets.getMedia() != null);
    }

    /**
     * Create the group list area.
     * 
     * @param parent The parent composite.
     */
    private void createGroupsList(Composite parent)
    {
        final Composite areaList = new Composite(parent, SWT.NONE);
        areaList.setLayout(new GridLayout(1, false));
        areaList.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
        groupList.create(areaList);
        groupList.addListener(this);

        properties.create(areaList);
        groupList.addListener(properties);
    }

    /**
     * Create the world view area.
     * 
     * @param parent The composite parent.
     */
    private void createWorldView(Composite parent)
    {
        final Composite area = new Composite(parent, SWT.NONE);
        area.setLayout(new GridLayout(2, false));
        area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createGroupsList(area);

        final Composite areaView = new Composite(area, SWT.BORDER);
        areaView.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        services.add(this);
        final WorldUpdater updater = new WorldUpdater(partService, services);
        final WorldInteractionTile interactionTile = services.get(WorldInteractionTile.class);
        interactionTile.removeListener(interactionTile.getListener());
        interactionTile.addListener(this);
        services.add(updater);

        final WorldRenderer renderer = new WorldRenderer(partService, services);
        final WorldSelectedTiles selectedTiles = services.get(WorldSelectedTiles.class);
        interactionTile.removeListener(selectedTiles.getListener());

        view = WorldPart.createPart(areaView, updater, renderer);
        view.addMouseTrackListener(UtilSwt.createFocusListener(this));
        view.addKeyListener(this);

        final Media groupConfig = Medias.create(sheets.getMedia().getParentPath(), TileGroupsConfig.FILENAME);
        if (groupConfig.exists())
        {
            mapGroup.loadGroups(groupConfig);
            groupList.loadGroups(groupConfig);
        }
    }

    /**
     * Change tile group or remove, depending of mouse click.
     * 
     * @param click The mouse click.
     * @param tile The tile to update.
     * @param newGroup The new group to set.
     */
    private void changeGroup(int click, Tile tile, String newGroup)
    {
        if (click == Mouse.RIGHT)
        {
            mapGroup.changeGroup(tile, null);
        }
        else
        {
            mapGroup.changeGroup(tile, newGroup);
        }
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite levelsArea = createLevelsArea(content);
        next = UtilButton.create(levelsArea, com.b3dgs.lionengine.editor.dialog.Messages.Next, AbstractDialog.ICON_OK);
        next.setEnabled(false);
        UtilButton.setAction(next, () ->
        {
            final Media sheetsMedia = sheets.getMedia();
            final TileSheetsConfig config = TileSheetsConfig.imports(sheetsMedia);

            load(loadSheets(config, sheetsMedia.getParentPath()), levelRips.getLevelRips());
            showWorldView();
            update();
        });
    }

    @Override
    protected void onFinish()
    {
        groupList.save();

        for (final TreeItem item : groupList.getTree().getItems())
        {
            final TileGroup group = (TileGroup) item.getData();
            groupsTypes.put(group.getName(), group.getType());
        }
    }

    @Override
    @Focus
    public void focus()
    {
        view.forceFocus();
    }

    /*
     * WorldView
     */

    @Override
    public void update()
    {
        if (!view.isDisposed())
        {
            view.redraw();
        }
    }

    @Override
    public void setToolItemText(String item, String text)
    {
        // Nothing to do
    }

    @Override
    public <T> T getToolItem(String item, Class<T> clazz)
    {
        return null;
    }

    /*
     * KeyListener
     */

    @Override
    public void keyPressed(KeyEvent event)
    {
        update();
    }

    @Override
    public void keyReleased(KeyEvent event)
    {
        // Nothing to do
    }

    /*
     * TileSelectionListener
     */

    @Override
    public void notifyTileSelected(int click, Tile tile)
    {
        final String newGroup = getSelectedGroup();
        changeGroup(click, tile, newGroup);
        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                final Tile current = map.getTile(tx, ty);
                if (current != null
                    && current.getSheet().equals(tile.getSheet())
                    && current.getNumber() == tile.getNumber())
                {
                    changeGroup(click, current, newGroup);
                }
            }
        }
    }

    @Override
    public void notifyTileGroupSelected(String group)
    {
        // Nothing to do
    }

    /*
     * ObjectListListener
     */

    @Override
    public void notifyObjectSelected(TileGroup object)
    {
        services.get(WorldSelectedTiles.class).getListener().notifyTileGroupSelected(getSelectedGroup());
        update();
    }

    @Override
    public void notifyObjectDeleted(TileGroup object)
    {
        services.get(WorldSelectedTiles.class).getListener().notifyTileGroupSelected(null);
        update();
    }
}
