package com.b3dgs.lionengine.example.c_platform.e_lionheart.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.swing.JPanel;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Editor;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.Entity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.FactoryEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntity;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.Map;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.Coord;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.input.Mouse;

public class WorldPanel
        extends JPanel
        implements MouseListener, MouseMotionListener
{
    private static final long serialVersionUID = 1L;
    private static final Color COLOR_MOUSE_SELECTION = new Color(128, 128, 192, 192);
    private static final Color COLOR_ENTRY_SELECTION = new Color(128, 240, 128, 192);
    private static final Color COLOR_ENTRY_PATROL = new Color(240, 240, 128, 192);
    private static final Color COLOR_ENTRY_PATROL_AREA = new Color(128, 128, 128, 192);
    private final Editor editor;
    public final Map map;
    public final CameraPlatform camera;
    public final Handler entrys;
    public final FactoryEntity factory;
    private int mouseX;
    private int mouseY;
    private boolean selecting;
    private boolean selected;
    private boolean clicking;
    private int selectStartX;
    private int selectStartY;
    private int selectEndX;
    private int selectEndY;
    private int playerSelection;
    private final Coord playerStart;
    private final Coord playerEnd;
    private final TreeMap<Integer, CoordTile> checkpoints;

    public WorldPanel(final Editor editor)
    {
        super();
        this.editor = editor;
        map = new Map();
        camera = new CameraPlatform(640, 480);
        entrys = new Handler();
        factory = new FactoryEntity(camera, map, 60, null);
        playerStart = new Coord();
        playerEnd = new Coord();
        checkpoints = new TreeMap<>();
        setPreferredSize(new Dimension(640, 480));
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    @Override
    public void paintComponent(Graphics gd)
    {
        final Graphics2D g = (Graphics2D) gd;
        final int width = getWidth();
        final int height = getHeight();
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int hOff = editor.getHRealOffset();
        final int vOff = editor.getVRealOffset();
        final int areaX = WorldPanel.getRounded(width, tw);
        final int areaY = WorldPanel.getRounded(height, th);
        camera.setView(0, 0, areaX, areaY);

        // Background
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);

        // Map area
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, areaX, areaY);

        // Renders
        if (!map.getPatterns().isEmpty())
        {
            map.render(new Graphic(g), camera);
        }
        drawEntrys(g, hOff, vOff, height);
        g.setColor(Color.GREEN);
        g.fillRect((int) playerStart.getX() - hOff,
                (int) -playerStart.getY() + vOff + WorldPanel.getRounded(height, th) - Map.TILE_HEIGHT, Map.TILE_WIDTH,
                Map.TILE_HEIGHT);
        g.setColor(Color.RED);
        g.fillRect((int) playerEnd.getX() - hOff, (int) -playerEnd.getY() + vOff + WorldPanel.getRounded(height, th)
                - Map.TILE_HEIGHT, Map.TILE_WIDTH, Map.TILE_HEIGHT);

        for (final CoordTile p : checkpoints.values())
        {
            g.setColor(Color.YELLOW);
            g.fillRect(p.getX() - hOff, -p.getY() + vOff + WorldPanel.getRounded(height, th) - Map.TILE_HEIGHT,
                    Map.TILE_WIDTH, Map.TILE_HEIGHT);
        }

        drawCursor(g, tw, th, areaX, areaY);
        WorldPanel.drawGrid(g, tw, th, areaX, areaY, Color.GRAY);
        drawSelection(g);
    }

    private void drawEntrys(Graphics2D g, int hOff, int vOff, int height)
    {
        final int th = map.getTileHeight();
        for (final Entity entry : entrys.list())
        {
            final int sx = entry.getLocationIntX();
            final int sy = entry.getLocationIntY();

            // Patrol
            final int left = Map.TILE_WIDTH * entry.data.getPatrolLeft();
            final int right = Map.TILE_WIDTH * (entry.data.getPatrolLeft() + entry.data.getPatrolRight());
            g.setColor(WorldPanel.COLOR_ENTRY_PATROL_AREA);
            g.fillRect(sx - hOff - left, -sy + vOff + WorldPanel.getRounded(height, th) - entry.getHeight(),
                    entry.getWidth() + right, entry.getHeight());
            g.setColor(WorldPanel.COLOR_ENTRY_PATROL);
            if (entry.data.getMovement() == EntryData.HORI_MOV)
            {
                g.fillRect(sx - hOff - left + entry.getWidth() / 2, -sy + vOff + WorldPanel.getRounded(height, th),
                        right, Map.TILE_HEIGHT);
            }

            if (entry.data.isSelected() || entry.data.isOver())
            {
                g.setColor(WorldPanel.COLOR_ENTRY_SELECTION);
                g.fillRect(sx - hOff, -sy + vOff - entry.getHeight() + WorldPanel.getRounded(height, th),
                        entry.getWidth(), entry.getHeight());
            }
            entry.render(new Graphic(g), camera);
        }
    }

    private void drawCursor(Graphics2D g, int tw, int th, int areaX, int areaY)
    {
        if (!selecting)
        {
            if (mouseX >= 0 && mouseY >= 0 && mouseX < areaX && mouseY < areaY)
            {
                final int mx = WorldPanel.getRounded(mouseX, tw);
                final int my = WorldPanel.getRounded(mouseY, th);

                g.setColor(WorldPanel.COLOR_MOUSE_SELECTION);
                g.fill3DRect(mx, my, tw, th, true);
            }
        }
    }

    private static void drawGrid(Graphics2D g, int tw, int th, int areaX, int areaY, Color color)
    {
        g.setColor(color);
        for (int v = 0; v <= areaY; v += tw)
        {
            g.drawLine(0, v, areaX, v);
        }
        for (int h = 0; h <= areaX; h += th)
        {
            g.drawLine(h, 0, h, areaY);
        }
    }

    private void drawSelection(Graphics2D g)
    {
        if (selecting)
        {
            final int sx = selectStartX;
            final int sy = selectStartY;
            final int w = selectEndX - sx;
            final int h = selectEndY - sy;
            g.setColor(WorldPanel.COLOR_MOUSE_SELECTION);
            g.fillRect(sx, sy, w, h);
        }
    }

    private static int getRounded(int value, int round)
    {
        return value / round * round;
    }

    private void beginSelection(int mx, int my)
    {
        if (!selecting)
        {
            final int sx = WorldPanel.getRounded(mx, map.getTileWidth());
            final int sy = WorldPanel.getRounded(my, map.getTileHeight());
            selectStartX = sx;
            selectStartY = sy;
            selectEndX = sx;
            selectEndY = sy;
            selecting = true;
            selected = false;
        }
    }

    private void updateSelection(int mx, int my)
    {
        if (selecting)
        {
            selectEndX = WorldPanel.getRounded(mx, map.getTileWidth());
            selectEndY = WorldPanel.getRounded(my, map.getTileHeight());
            selecting = true;
            selected = true;
        }
    }

    private void endSelection(int mx, int my)
    {
        if (selecting)
        {
            int sx = selectStartX;
            int sy = selectStartY;
            int ex = WorldPanel.getRounded(mx, map.getTileWidth());
            int ey = WorldPanel.getRounded(my, map.getTileHeight());

            // Ensure selection is positive
            if (ex < sx)
            {
                final int tmp = sx;
                sx = ex;
                ex = tmp;
            }
            if (ey > sy)
            {
                final int tmp = sy;
                sy = ey;
                ey = tmp;
            }
            selectStartX = sx;
            selectStartY = sy;
            selectEndX = ex;
            selectEndY = ey;
            selecting = false;
        }
    }

    private void resetSelection()
    {
        selectStartX = -1;
        selectStartY = -1;
        selectEndX = -1;
        selectEndY = -1;
        selecting = false;
    }

    private Entity hitEntrys(int x, int y)
    {
        final int mx = WorldPanel.getRounded(x, map.getTileWidth());
        final int my = WorldPanel.getRounded(getHeight() - y, map.getTileHeight());
        for (final Entity entry : entrys.list())
        {
            if (hitEntry(entry, mx, my, mx + map.getTileWidth(), my + map.getTileHeight()))
            {
                return entry;
            }
        }
        return null;
    }

    private boolean hitEntry(Entity entry, int x1, int y1, int x2, int y2)
    {
        if (entry != null)
        {
            final int sx = WorldPanel.getRounded(entry.getLocationIntX(), map.getTileWidth()) - editor.getHRealOffset();
            final int sy = WorldPanel.getRounded(entry.getLocationIntY(), map.getTileHeight())
                    - editor.getVRealOffset();
            final Rectangle2D r1 = new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1);
            final Rectangle2D r2 = new Rectangle2D.Float(sx, sy, entry.getWidth(), entry.getHeight());
            if (r1.intersects(r2))
            {
                return true;
            }
        }
        return false;
    }

    private void selectEntrys()
    {
        for (final Entity entry : entrys.list())
        {
            entry.data.setSelection(false);
            final int offy = getHeight() - WorldPanel.getRounded(getHeight(), map.getTileHeight());
            final int sx = WorldPanel.getRounded(selectStartX, map.getTileWidth());
            final int sy = WorldPanel.getRounded(getHeight() - selectStartY - offy, map.getTileHeight());
            final int ex = WorldPanel.getRounded(selectEndX, map.getTileWidth());
            final int ey = WorldPanel.getRounded(getHeight() - selectEndY - offy, map.getTileHeight());
            if (hitEntry(entry, sx, sy, ex, ey))
            {
                entry.data.setSelection(true);
            }
        }
    }

    private void unSelectEntrys()
    {
        for (final Entity entry : entrys.list())
        {
            entry.data.setSelection(false);
        }
    }

    private List<Entity> getSelectedEntrys(boolean first)
    {
        final List<Entity> list = new ArrayList<>(0);
        for (final Entity entry : entrys.list())
        {
            if (entry.data.isSelected())
            {
                list.add(entry);
                if (first)
                {
                    return list;
                }
            }
        }
        return list;
    }

    public void setPlayerSelection(int sel)
    {
        playerSelection = sel;
    }

    public void saveEntrys(FileWriting file) throws IOException
    {
        file.writeShort((short) (playerStart.getX() / Map.TILE_WIDTH));
        file.writeShort((short) (playerStart.getY() / Map.TILE_HEIGHT));
        file.writeShort((short) (playerEnd.getX() / Map.TILE_WIDTH));
        file.writeShort((short) (playerEnd.getY() / Map.TILE_HEIGHT));
        file.writeShort((short) checkpoints.size());
        for (final CoordTile p : checkpoints.values())
        {
            file.writeShort((short) (p.getX() / Map.TILE_WIDTH));
            file.writeShort((short) (p.getY() / Map.TILE_HEIGHT));
        }
        file.writeShort((short) entrys.size());
        for (final Entity entry : entrys.list())
        {
            entry.save(file);
        }
    }

    public void loadEntrys(FileReading file) throws IOException
    {
        playerStart.set(file.readShort() * Map.TILE_WIDTH, file.readShort() * Map.TILE_HEIGHT);
        playerEnd.set(file.readShort() * Map.TILE_WIDTH, file.readShort() * Map.TILE_HEIGHT);
        final int size = file.readShort();
        for (int i = 0; i < size; i++)
        {
            addCheckpoint(file.readShort() * Map.TILE_WIDTH, file.readShort() * Map.TILE_HEIGHT);
        }
        final int n = file.readShort();
        final TypeEntity[] entities = TypeEntity.values();
        for (int i = 0; i < n; i++)
        {
            final byte id = file.readByte();
            final Entity entry = factory.createEntity(entities[id]);
            entry.load(file, true);
            entrys.add(entry);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        final int mx = e.getX();
        final int my = e.getY();
        clicking = true;
        final int tw = map.getTileWidth();
        final int th = map.getTileHeight();
        final int h = WorldPanel.getRounded(getHeight(), th) - map.getTileHeight();
        final int x = editor.getHRealOffset() + WorldPanel.getRounded(mx, tw);
        final int y = editor.getVRealOffset() - WorldPanel.getRounded(my, th) + h;

        switch (editor.getSelectionState())
        {
            case ToolBar.SELECT:
                if (e.getButton() == Mouse.LEFT)
                {
                    final Entity entry = hitEntrys(mx, my);
                    editor.toolBar.entryEditor.setEntry(entry);
                    if (entry != null)
                    {
                        selected = false;
                        if (!entry.data.isSelected())
                        {
                            unSelectEntrys();
                            entry.data.setSelection(true);
                        }
                    }
                    else
                    {
                        unSelectEntrys();
                        beginSelection(mx, my);
                    }
                }
                break;
            case ToolBar.PLACE:
                if (hitEntrys(mx, my) == null)
                {
                    unSelectEntrys();
                    final int id = editor.selection.id.ordinal();
                    final Entity entry = factory.createEntity(TypeEntity.values()[id]);
                    entry.teleport(WorldPanel.getRounded(x, tw), WorldPanel.getRounded(y, th));
                    entrys.add(entry);
                    entrys.update();
                }
                break;
            case ToolBar.DELETE:
                final Entity entry = hitEntrys(mx, my);
                if (entry != null)
                {
                    entrys.remove(entry);
                    entrys.update();
                }
                break;
            case ToolBar.PLAYER:
                switch (playerSelection)
                {
                    case ToolBar.PLAYER_PLACE_START:
                        playerStart.set(x, y);
                        break;
                    case ToolBar.PLAYER_PLACE_END:
                        playerEnd.set(x, y);
                        break;
                    case ToolBar.PLAYER_PLACE_ADD_CHK:
                        addCheckpoint(x, y);
                        break;
                    case ToolBar.PLAYER_PLACE_DEL_CHK:
                        removeCheckpoint(getCheckpointAt(x, y));
                        break;
                    default:
                        throw new LionEngineException("Unknown selection: " + playerSelection);
                }
                break;
        }
        update(mx, my);
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        final int mx = e.getX();
        final int my = e.getY();
        clicking = false;

        endSelection(mx, my);
        if (selected)
        {
            selectEntrys();
        }
        for (final Entity ent : getSelectedEntrys(false))
        {
            ent.teleport(WorldPanel.getRounded(ent.getLocationIntX(), map.getTileWidth()),
                    WorldPanel.getRounded(ent.getLocationIntY(), map.getTileHeight()));
        }
        resetSelection();
        update(mx, my);
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        final int mx = e.getX();
        final int my = e.getY();
        updateSelection(mx, my);

        for (final Entity entry : entrys.list())
        {
            if (entry.data.isSelected())
            {
                entry.moveLocation(1.0, mx - mouseX, mouseY - my);
            }
        }

        update(mx, my);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        final int mx = e.getX();
        final int my = e.getY();
        final int offy = getHeight() - WorldPanel.getRounded(getHeight(), map.getTileHeight());
        final int x = WorldPanel.getRounded(mx, map.getTileWidth());
        final int y = WorldPanel.getRounded(getHeight() - my - offy, map.getTileHeight());

        for (final Entity entry : entrys.list())
        {
            entry.data.setOver(false);
            if (hitEntry(entry, x, y, x + map.getTileWidth(), y + map.getTileHeight()))
            {
                entry.data.setOver(true);
            }
        }

        update(mx, my);
    }

    private void update(int mx, int my)
    {
        mouseX = mx;
        mouseY = my;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    public boolean isClicking()
    {
        return clicking;
    }

    public void addCheckpoint(int x, int y)
    {
        checkpoints.put(getHash(x, y), new CoordTile(x, y));
    }

    public CoordTile getCheckpointAt(int x, int y)
    {
        return checkpoints.get(getHash(x, y));
    }

    public int getHash(int x, int y)
    {
        return x / Map.TILE_WIDTH + map.getWidthInTile() * (y / Map.TILE_HEIGHT);
    }

    public void removeCheckpoint(CoordTile p)
    {
        if (p != null)
        {
            checkpoints.remove(getHash(p.getX(), p.getY()));
        }
    }
}
