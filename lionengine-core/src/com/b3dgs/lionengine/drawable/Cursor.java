package com.b3dgs.lionengine.drawable;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Display;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Used to represent a mouse cursor, desynchronized from the window mouse pointer or not. This way, it is possible to
 * set a specific sensibility.
 * 
 * @see Mouse
 */
public class Cursor
        implements Renderable
{
    /** Image reference. */
    private final Image[] surface;
    /** Cursor location x. */
    private double x;
    /** Cursor location y. */
    private double y;
    /** Horizontal sensibility. */
    private double sensibilityHorizontal;
    /** Vertical sensibility. */
    private double sensibilityVertical;
    /** Minimum location x. */
    private int minX;
    /** Minimum location y. */
    private int minY;
    /** Maximum location x. */
    private int maxX;
    /** Maximum location y. */
    private int maxY;
    /** Click number. */
    private int click;
    /** Lock flag. */
    private boolean lock;
    /** Surface id. */
    private int surfaceId;
    /** Rendering horizontal offset. */
    private int offsetX;
    /** Rendering vertical offset. */
    private int offsetY;

    /**
     * Create a cursor.
     * 
     * @param display The display reference.
     * @param medias The cursor media list.
     */
    public Cursor(Display display, Media... medias)
    {
        this(0, 0, display.getWidth(), display.getHeight(), medias);
    }

    /**
     * Create a cursor.
     * 
     * @param minX The minimal x.
     * @param minY The minimal y.
     * @param maxX The maximal x.
     * @param maxY The maximal y.
     * @param medias The cursor media list.
     */
    public Cursor(int minX, int minY, int maxX, int maxY, Media... medias)
    {
        Check.notNull(medias, "The cursor should have at least one image !");
        Check.argument(medias.length > 0, "The cursor should have at least one image !");

        x = 0.0;
        y = 0.0;
        sensibilityHorizontal = 1.0;
        sensibilityVertical = 1.0;
        surface = new Image[medias.length];

        int i = 0;
        for (final Media media : medias)
        {
            surface[i] = Drawable.loadImage(media);
            i++;
        }

        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxX = Math.max(maxX, minX);
        this.maxY = Math.max(maxY, minY);
        lock = false;
        surfaceId = 0;
        offsetX = 0;
        offsetY = 0;
    }

    /**
     * Update cursor position depending of mouse movement.
     * 
     * @param extrp The extrapolation value.
     * @param mouse The mouse reference (must not be <code>null</code>).
     * @param sync The sync mode (<code>true</code> = sync to window mouse; <code>false</code> = internal movement).
     */
    public void update(double extrp, Mouse mouse, boolean sync)
    {
        if (sync)
        {
            x = mouse.getOnWindowX();
            y = mouse.getOnWindowY();
        }
        else
        {
            x += mouse.getMoveX() * sensibilityHorizontal * extrp;
            y += mouse.getMoveY() * sensibilityVertical * extrp;
        }

        x = UtilityMath.fixBetween(x, minX, maxX);
        y = UtilityMath.fixBetween(y, minY, maxY);
        click = mouse.getMouseClick();

        if (lock && sync)
        {
            mouse.lock();
        }
    }

    /**
     * Render cursor on screen at this default location.
     * 
     * @param g The graphic output.
     */
    public void render(Graphic g)
    {
        surface[surfaceId].render(g, (int) x + offsetX, (int) y + offsetY);
    }

    /**
     * Render cursor number on screen at the specified location.
     * 
     * @param g The graphic output.
     * @param number The the image number to render (>= 0).
     * @param x The rendering offset x.
     * @param y The rendering offset y.
     */
    public void render(Graphic g, int number, int x, int y)
    {
        surface[number].render(g, (int) this.x + x, (int) this.y + y);
    }

    /**
     * Set the mouse lock.
     * 
     * @param lock Lock state.
     */
    public void setLockMouse(boolean lock)
    {
        this.lock = lock;
    }

    /**
     * Set cursor sensibility (move speed).
     * 
     * @param sh The horizontal sensibility (>= 0.0).
     * @param sv The vertical sensibility (>= 0.0).
     */
    public void setSensibility(double sh, double sv)
    {
        sensibilityHorizontal = Math.max(0.0, sh);
        sensibilityVertical = Math.max(0.0, sv);
    }

    /**
     * Set cursor location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void setLocation(int x, int y)
    {
        this.x = UtilityMath.fixBetween(x, minX, maxX);
        this.y = UtilityMath.fixBetween(y, minY, maxY);
    }

    /**
     * Set the surface id to render with {@link #render(Graphic)}.
     * 
     * @param surfaceId The surface id number (start at 0 which is default value).
     */
    public void setSurfaceId(int surfaceId)
    {
        this.surfaceId = Math.max(0, surfaceId);
    }

    /**
     * Get the current surface id used for rendering.
     * 
     * @return The current surface id.
     */
    public int getSurfaceId()
    {
        return surfaceId;
    }

    /**
     * Set the rendering offsets value (allows to apply an offset depending of the cursor surface).
     * 
     * @param ox The horizontal offset.
     * @param oy The vertical offset.
     */
    public void setRenderingOffset(int ox, int oy)
    {
        offsetX = ox;
        offsetY = oy;
    }

    /**
     * Allows cursor to move only inside the specified area. The cursor location will not exceed this area.
     * 
     * @param minX The minimal x.
     * @param minY The minimal y.
     * @param maxX The maximal x.
     * @param maxY The maximal y.
     */
    public void setArea(int minX, int minY, int maxX, int maxY)
    {
        this.minX = Math.min(minX, maxX);
        this.minY = Math.min(minY, maxY);
        this.maxX = Math.max(maxX, minX);
        this.maxY = Math.max(maxY, minY);
    }

    /**
     * Return mouse click number.
     * 
     * @return The mouse click number.
     */
    public int getClick()
    {
        return click;
    }

    /**
     * Get horizontal location.
     * 
     * @return The horizontal location.
     */
    public int getLocationX()
    {
        return (int) x;
    }

    /**
     * Get vertical location.
     * 
     * @return The vertical location.
     */
    public int getLocationY()
    {
        return (int) y;
    }

    /**
     * Get horizontal sensibility.
     * 
     * @return The horizontal sensibility.
     */
    public double getSensibilityHorizontal()
    {
        return sensibilityHorizontal;
    }

    /**
     * Get vertical sensibility.
     * 
     * @return The vertical sensibility.
     */
    public double getSensibilityVertical()
    {
        return sensibilityVertical;
    }

    /*
     * Renderable
     */

    /**
     * Render cursor on screen at specified location using the current surface id.
     * 
     * @param g The graphic output.
     * @param x The rendering offset x.
     * @param y The rendering offset y.
     */
    @Override
    public void render(Graphic g, int x, int y)
    {
        surface[0].render(g, (int) this.x + x, (int) this.y + y);
    }

    @Override
    public int getWidth()
    {
        return 1;
    }

    @Override
    public int getHeight()
    {
        return 1;
    }
}
