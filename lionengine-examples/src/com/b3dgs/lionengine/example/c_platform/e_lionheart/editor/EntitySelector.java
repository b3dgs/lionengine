package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Image;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Editor;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.TypeWorld;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityCategory;
import com.b3dgs.lionengine.swing.ActionCombo;
import com.b3dgs.lionengine.swing.ComboItem;
import com.b3dgs.lionengine.utility.UtilitySwing;

/**
 * Entity selection implementation.
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
    static final Color SELECT = new Color(128, 192, 128, 128);
    /** Editor reference. */
    final Editor editor;
    /** Tabs. */
    final JTabbedPane tabbedPane;
    /** Entity category icons list. */
    private final EnumMap<TypeEntityCategory, List<Image>> icons;
    /** Current selected world. */
    TypeWorld selectedWorld;

    /**
     * Constructor.
     * 
     * @param editor The editor reference.
     */
    public EntitySelector(Editor editor)
    {
        super();
        this.editor = editor;
        icons = new EnumMap<>(TypeEntityCategory.class);
        tabbedPane = new JTabbedPane();
        for (final TypeEntityCategory category : TypeEntityCategory.values())
        {
            icons.put(category, new ArrayList<Image>(8));
        }
        setLayout(new BorderLayout());
        final JPanel panel = new JPanel();
        add(panel, BorderLayout.NORTH);

        final TypeWorld[] worlds = TypeWorld.values();
        final ComboItem[] items = new ComboItem[worlds.length];
        for (int i = 0; i < worlds.length; i++)
        {
            items[i] = new ComboItem(worlds[i]);
        }
        UtilitySwing.addMenuCombo("Theme:", panel, items, new ActionCombo()
        {
            @Override
            public void action(Object item)
            {
                if (item != null)
                {
                    selectedWorld = (TypeWorld) item;
                    loadEntities((TypeWorld) item);
                }
            }
        });
        selectedWorld = worlds[0];
        loadEntities(selectedWorld);
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Load all entities.
     * 
     * @param world World type.
     */
    void loadEntities(TypeWorld world)
    {
        tabbedPane.removeAll();
        editor.selection.world = world;
        editor.world.factory.setWorld(world);
        editor.world.factory.loadAll(TypeEntity.values());
        int max = 0;
        for (final TypeEntityCategory category : TypeEntityCategory.values())
        {
            if (category == TypeEntityCategory.PLAYER)
            {
                continue;
            }
            final List<Image> ico = icons.get(category);
            ico.clear();
            int length = 0;
            for (final TypeEntity entity : TypeEntity.values())
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
        repaint();
    }

    /**
     * Get the current selected world.
     * 
     * @return The current world.
     */
    public TypeWorld getWorld()
    {
        return selectedWorld;
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
            addMouseListener(this);
            addMouseMotionListener(this);
            this.icons = icons;
            click = false;
        }

        /**
         * Update the mouse.
         * 
         * @param event The mouse event.
         */
        private void update(MouseEvent event)
        {
            mx = event.getX();
            my = event.getY();
            repaint();
        }

        /*
         * JPanel
         */

        @Override
        public void paintComponent(Graphics g)
        {
            final int width = getWidth();
            final int height = getHeight();
            g.clearRect(0, 0, width, height);

            final int iconSize = EntitySelector.ICON_SIZE;
            int x = 0, y = 0;
            for (int i = 0; i < icons.size(); i++)
            {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x * iconSize, y * iconSize, iconSize, iconSize);
                g.setColor(Color.BLACK);
                g.drawRect(x * iconSize, y * iconSize, iconSize, iconSize);
                g.drawImage(icons.get(i).getSurface(), x * iconSize + 1, y * iconSize + 1, null);
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
            final int num = mx / iconSize + 1 + my / iconSize * iconSize;
            final TypeEntityCategory[] categories = TypeEntityCategory.values();
            if (num <= size && x < EntitySelector.COLUMNS * iconSize && mx > 0)
            {
                g.setColor(EntitySelector.SELECT);
                g.fillRect(x + 1, y + 1, iconSize - 1, iconSize - 1);
                if (click)
                {
                    click = false;
                    final int index = tabbedPane.getSelectedIndex();
                    int total = 0;
                    for (int i = 0; i < index; i++)
                    {
                        total += categories[i].getCount();
                    }
                    editor.selection.type = TypeEntity.values()[total + num - 1];
                    editor.setSelectionState(TypeSelection.PLACE);
                    editor.repaint();
                }
            }
        }

        /*
         * MouseListener
         */

        @Override
        public void mouseClicked(MouseEvent e)
        {
            // Nothing to do
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
            update(e);
            click = true;
        }

        @Override
        public void mouseReleased(MouseEvent e)
        {
            // Nothing to do
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {
            // Nothing to do
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            // Nothing to do
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            update(e);
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
            update(e);
        }
    }
}
