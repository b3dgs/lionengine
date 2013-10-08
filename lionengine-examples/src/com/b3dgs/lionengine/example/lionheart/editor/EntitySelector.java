/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.lionheart.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.example.lionheart.AppLionheart;
import com.b3dgs.lionengine.example.lionheart.Editor;
import com.b3dgs.lionengine.example.lionheart.WorldType;
import com.b3dgs.lionengine.example.lionheart.entity.EntityCategory;
import com.b3dgs.lionengine.example.lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.lionheart.landscape.LandscapeType;
import com.b3dgs.lionengine.swing.ActionCombo;
import com.b3dgs.lionengine.swing.ComboItem;
import com.b3dgs.lionengine.swing.UtilitySwing;

/**
 * Entity selection implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EntitySelector
        extends JPanel
{
    /** Uid. */
    private static final long serialVersionUID = 216176800083898793L;
    /** Icons size. */
    private static final int ICON_SIZE = 33;
    /** Number of columns. */
    private static final int COLUMNS = 5;
    /** Selection color. */
    static final ColorRgba SELECT = new ColorRgba(128, 192, 128, 128);
    /** Editor reference. */
    final Editor editor;
    /** Tabs. */
    final JTabbedPane tabbedPane;
    /** Landscape panel. */
    final JPanel landscape;
    /** Entity category icons list. */
    private final EnumMap<EntityCategory, List<Image>> icons;
    /** Entity selector panel. */
    private final JPanel panel;
    /** Landscape combo. */
    private JComboBox<ComboItem> landscapeCombo;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     */
    public EntitySelector(final Editor editor)
    {
        super();
        this.editor = editor;
        icons = new EnumMap<>(EntityCategory.class);
        tabbedPane = new JTabbedPane();
        for (final EntityCategory category : EntityCategory.values())
        {
            icons.put(category, new ArrayList<Image>(8));
        }
        setLayout(new BorderLayout());
        panel = new JPanel();
        add(panel, BorderLayout.SOUTH);
        add(tabbedPane, BorderLayout.CENTER);
        landscape = new JPanel();
        landscape.setLayout(new GridLayout(0, 1));
        add(landscape, BorderLayout.NORTH);
        setVisible(false);
    }

    /**
     * Load all entities.
     * 
     * @param world World type.
     */
    void loadEntities(WorldType world)
    {
        landscape.removeAll();
        landscapeCombo = UtilitySwing.addMenuCombo("Landscape", landscape,
                ComboItem.get(ComboItem.get(LandscapeType.getWorldLandscape(world))), new ActionCombo()
                {
                    @Override
                    public void action(Object item)
                    {
                        final LandscapeType landscape = (LandscapeType) ((ComboItem) item).getObject();
                        editor.world.level.setLandscape(landscape);
                    }
                });
        landscape.add(landscapeCombo);
        tabbedPane.removeAll();
        editor.world.factoryEntity.loadAll(EntityType.values());
        LandscapeType landscape = editor.world.level.getLandscape();
        if (landscape == null)
        {
            landscape = LandscapeType.values()[0];
        }
        landscapeCombo.setSelectedIndex(landscape.ordinal());
        int max = 0;
        for (final EntityCategory category : EntityCategory.values())
        {
            if (category == EntityCategory.PLAYER)
            {
                continue;
            }
            final List<Image> ico = icons.get(category);
            ico.clear();
            int length = 0;
            for (final EntityType entity : EntityType.values())
            {
                if (entity.getCategory() == category)
                {
                    final Media media = Media.get(AppLionheart.ENTITIES_DIR, category.getFolder(), world.toString(),
                            entity.asPathName() + "_ico.png");
                    ico.add(Drawable.loadImage(media));
                    length++;
                }
            }

            final JPanel panel = new Entity(ico);
            final int iconSize = EntitySelector.ICON_SIZE;
            final int cols = EntitySelector.COLUMNS;
            panel.setLayout(new GridLayout(0, cols));
            panel.setPreferredSize(new Dimension(cols * iconSize + 14, iconSize * ((length - 1) / cols) + iconSize + 1));
            panel.setMaximumSize(new Dimension(cols * iconSize + 14, iconSize * ((length - 1) / cols) + iconSize + 1));
            panel.setMinimumSize(new Dimension(cols * iconSize + 14, iconSize * ((length - 1) / cols) + iconSize + 1));
            tabbedPane.add(category.toString(), new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
            if (max < length)
            {
                max = length;
            }
        }
        setVisible(true);
        repaint();
    }

    /**
     * Entity with icon rendering.
     */
    private class Entity
            extends JPanel
            implements MouseListener, MouseMotionListener
    {
        /** Uid. */
        private static final long serialVersionUID = -3661526690034735481L;
        /** Icons list. */
        private final List<Image> icons;
        /** Mouse x. */
        private int mx;
        /** Mouse y. */
        private int my;
        /** Current mouse click. */
        private boolean click;

        /**
         * Constructor.
         * 
         * @param icons The icons list.
         */
        Entity(List<Image> icons)
        {
            this.icons = icons;
            click = false;
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        /**
         * Update the mouse.
         * 
         * @param event The mouse event.
         */
        private void update(MouseEvent event)
        {
            if (isEnabled())
            {
                mx = event.getX();
                my = event.getY();
                repaint();
            }
        }

        /**
         * Render the panel.
         * 
         * @param g The graphic output.
         */
        private void render(Graphic g)
        {
            final int width = getWidth();
            final int height = getHeight();
            g.clear(0, 0, width, height);

            final int iconSize = EntitySelector.ICON_SIZE;
            int x = 0, y = 0;
            for (int i = 0; i < icons.size(); i++)
            {
                g.setColor(ColorRgba.GRAY_LIGHT);
                g.drawRect(x * iconSize, y * iconSize, iconSize, iconSize, true);
                g.setColor(ColorRgba.BLACK);
                g.drawRect(x * iconSize, y * iconSize, iconSize, iconSize, false);
                g.drawImage(icons.get(i).getSurface(), x * iconSize + 1, y * iconSize + 1);
                x++;
                if (x >= EntitySelector.COLUMNS)
                {
                    x = 0;
                    y++;
                }
            }

            x = mx / iconSize * iconSize;
            y = my / iconSize * iconSize;
            final int size = icons.size();
            final int num = x / iconSize + 1 + y / iconSize * EntitySelector.COLUMNS;
            final EntityCategory[] categories = EntityCategory.values();
            if (num <= size && x < EntitySelector.COLUMNS * iconSize && mx > 0)
            {
                g.setColor(EntitySelector.SELECT);
                g.drawRect(x + 1, y + 1, iconSize - 1, iconSize - 1, true);
                if (click)
                {
                    click = false;
                    final int index = tabbedPane.getSelectedIndex();
                    int total = 0;
                    for (int i = 0; i < index; i++)
                    {
                        total += categories[i].getCount();
                    }
                    editor.setSelectedEntity(EntityType.values()[total + num - 1]);
                    editor.setSelectionState(SelectionType.PLACE);
                    editor.repaint();
                }
            }
        }

        /*
         * JPanel
         */

        @Override
        public void paintComponent(Graphics g2d)
        {
            if (isEnabled())
            {
                final Graphic g = UtilityImage.createGraphic();
                g.setGraphic(g2d);
                render(g);
            }
        }

        /*
         * MouseListener
         */

        @Override
        public void mouseClicked(MouseEvent event)
        {
            // Nothing to do
        }

        @Override
        public void mousePressed(MouseEvent event)
        {
            update(event);
            click = true;
        }

        @Override
        public void mouseReleased(MouseEvent event)
        {
            // Nothing to do
        }

        @Override
        public void mouseEntered(MouseEvent event)
        {
            // Nothing to do
        }

        @Override
        public void mouseExited(MouseEvent event)
        {
            // Nothing to do
        }

        @Override
        public void mouseDragged(MouseEvent event)
        {
            update(event);
        }

        @Override
        public void mouseMoved(MouseEvent event)
        {
            update(event);
        }
    }
}
