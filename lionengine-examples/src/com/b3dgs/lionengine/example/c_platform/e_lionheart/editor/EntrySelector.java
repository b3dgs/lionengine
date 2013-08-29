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

public class EntrySelector
        extends JPanel
{
    private static final long serialVersionUID = 1L;
    private static final int ICON_SIZE = 33;
    private static final int COL = 5;
    static final Color SELECT = new Color(128, 192, 128, 128);
    final Editor editor;
    final JTabbedPane tabbedPane;
    TypeWorld themeSelected;

    private final EnumMap<TypeEntityCategory, List<Image>> icons;

    public EntrySelector(Editor editor)
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
                    themeSelected = (TypeWorld) item;
                    loadEntrys((TypeWorld) item);
                }
            }
        });
        themeSelected = worlds[0];
        loadEntrys(themeSelected);
        add(tabbedPane, BorderLayout.CENTER);
    }

    void loadEntrys(TypeWorld theme)
    {
        tabbedPane.removeAll();
        editor.selection.theme = theme;
        editor.world.factory.setWorld(theme);
        editor.world.factory.loadAll(TypeEntity.values());
        int max = 0;
        for (final TypeEntityCategory category : TypeEntityCategory.values())
        {
            final List<Image> ico = icons.get(category);
            ico.clear();
            int length = 0;
            final List<String> names = new ArrayList<>(1);
            try
            {
                for (final TypeEntity entity : TypeEntity.values())
                {
                    if (entity.getCategory() == category)
                    {
                        final Media media = Media.get(AppLionheart.ENTITIES_DIR, category.getFolder(),
                                theme.toString(), entity.asPathName() + "_ico.png");
                        ico.add(Drawable.loadImage(media));
                        names.add(entity.toString());
                        length++;
                    }
                }
            }
            catch (final Exception ex)
            {
            }

            final JPanel panel = new Entrys(ico, names);
            panel.setLayout(new GridLayout(0, EntrySelector.COL));
            panel.setPreferredSize(new Dimension(EntrySelector.COL * EntrySelector.ICON_SIZE + 14,
                    EntrySelector.ICON_SIZE * ((length - 1) / EntrySelector.COL) + EntrySelector.ICON_SIZE + 1));
            panel.setMaximumSize(new Dimension(EntrySelector.COL * EntrySelector.ICON_SIZE + 14,
                    EntrySelector.ICON_SIZE * ((length - 1) / EntrySelector.COL) + EntrySelector.ICON_SIZE + 1));
            panel.setMinimumSize(new Dimension(EntrySelector.COL * EntrySelector.ICON_SIZE + 14,
                    EntrySelector.ICON_SIZE * ((length - 1) / EntrySelector.COL) + EntrySelector.ICON_SIZE + 1));
            tabbedPane.add(category.toString(), new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
            if (max < length)
            {
                max = length;
            }
        }
        repaint();
    }

    public TypeWorld getTheme()
    {
        return themeSelected;
    }

    private class Entrys
            extends JPanel
            implements MouseListener, MouseMotionListener
    {
        private static final long serialVersionUID = 1L;
        private final List<Image> icons;
        private final List<String> names;
        private int mx;
        private int my;
        private boolean click;

        Entrys(List<Image> ico, List<String> names)
        {
            addMouseListener(this);
            addMouseMotionListener(this);
            icons = ico;
            this.names = names;
            click = false;
        }

        @Override
        public void paintComponent(Graphics g)
        {
            final int width = getWidth();
            final int height = getHeight();
            g.clearRect(0, 0, width, height);

            int x = 0, y = 0;
            for (int i = 0; i < icons.size(); i++)
            {
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x * EntrySelector.ICON_SIZE, y * EntrySelector.ICON_SIZE, EntrySelector.ICON_SIZE,
                        EntrySelector.ICON_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(x * EntrySelector.ICON_SIZE, y * EntrySelector.ICON_SIZE, EntrySelector.ICON_SIZE,
                        EntrySelector.ICON_SIZE);
                g.drawImage(icons.get(i).getSurface(), x * EntrySelector.ICON_SIZE + 1,
                        y * EntrySelector.ICON_SIZE + 1, null);
                x++;
                if (x >= EntrySelector.COL)
                {
                    x = 0;
                    y++;
                }
            }

            x = mx / EntrySelector.ICON_SIZE * EntrySelector.ICON_SIZE;
            y = my / EntrySelector.ICON_SIZE * EntrySelector.ICON_SIZE;
            final int size = icons.size();
            final int num = mx / EntrySelector.ICON_SIZE + 1 + my / EntrySelector.ICON_SIZE * EntrySelector.COL;
            final TypeEntityCategory[] categories = TypeEntityCategory.values();
            if (num <= size && x < EntrySelector.COL * EntrySelector.ICON_SIZE && mx > 0)
            {
                g.setColor(EntrySelector.SELECT);
                g.fillRect(x + 1, y + 1, EntrySelector.ICON_SIZE - 1, EntrySelector.ICON_SIZE - 1);
                if (click)
                {
                    click = false;
                    editor.selection.category = categories[tabbedPane.getSelectedIndex()];
                    editor.selection.id = TypeEntity.values()[num - 1];
                    editor.setSelectionState(ToolBar.PLACE);
                    editor.repaint();
                }
            }
        }

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

        private void update(MouseEvent e)
        {
            mx = e.getX();
            my = e.getY();
            repaint();
        }
    }
}
