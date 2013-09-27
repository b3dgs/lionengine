package com.b3dgs.lionengine.input;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Verbose;

/**
 * Mouse input implementation.
 */
class MouseImpl
        implements Mouse, MouseListener, MouseMotionListener, MouseWheelListener
{
    /** Clicks flags. */
    private final boolean[] clicks;
    /** Clicked flags. */
    private final boolean[] clicked;
    /** Robot instance reference. */
    private final Robot robot;
    /** Screen horizontal ratio. */
    private double xRatio;
    /** Screen vertical ratio. */
    private double yRatio;
    /** On screen monitor location x. */
    private int x;
    /** On screen monitor location y. */
    private int y;
    /** On local window location x. */
    private int wx;
    /** On local window location y. */
    private int wy;
    /** Move value x. */
    private int mx;
    /** Move value y. */
    private int my;
    /** Old location x. */
    private int oldX;
    /** Old location y. */
    private int oldY;
    /** Screen center x. */
    private int centerX;
    /** Screen center y. */
    private int centerY;
    /** Last click number. */
    private int lastClick;
    /** Moved flag. */
    private boolean moved;

    /**
     * Create a mouse input.
     */
    MouseImpl()
    {
        super();
        try
        {
            final int mouseButtons = Math.max(0, MouseInfo.getNumberOfButtons()) + 1;
            clicks = new boolean[mouseButtons];
            clicked = new boolean[mouseButtons];
        }
        catch (final HeadlessException exception)
        {
            throw new LionEngineException(exception);
        }
        x = 0;
        y = 0;
        wx = 0;
        wy = 0;
        mx = 0;
        my = 0;
        oldX = x;
        oldY = y;
        Robot r = null;
        try
        {
            r = new Robot();
        }
        catch (final AWTException exception)
        {
            Verbose.critical(MouseImpl.class, "constructor", "No mouse robot available !");
        }
        robot = r;
    }

    /**
     * Update coordinate from event.
     * 
     * @param event event consumed.
     */
    private void updateCoord(MouseEvent event)
    {
        oldX = x;
        oldY = y;
        x = event.getXOnScreen();
        y = event.getYOnScreen();
        wx = event.getX();
        wy = event.getY();
        mx = x - oldX;
        my = y - oldY;
    }

    /*
     * Mouse
     */

    @Override
    public void update()
    {
        mx = x - oldX;
        my = y - oldY;
        oldX = x;
        oldY = y;
    }

    @Override
    public void lock()
    {
        this.lock(centerX, centerY);
    }

    @Override
    public void lock(int x, int y)
    {
        if (robot != null)
        {
            robot.mouseMove(x, y);
            this.x = centerX;
            this.y = centerY;
            oldX = centerX;
            oldY = centerY;
        }
    }

    @Override
    public void doClick(int click)
    {
        if (robot != null)
        {
            int event;
            switch (click)
            {
                case LEFT:
                    event = InputEvent.BUTTON1_MASK;
                    break;
                case MIDDLE:
                    event = InputEvent.BUTTON2_MASK;
                    break;
                case RIGHT:
                    event = InputEvent.BUTTON3_MASK;
                    break;
                default:
                    event = -1;
                    break;
            }
            if (event > -1)
            {
                robot.mousePress(event);
                robot.mouseRelease(event);
            }
        }
    }

    @Override
    public void doClickAt(int click, int x, int y)
    {
        if (robot != null)
        {
            robot.mouseMove(x, y);
            doClick(click);
        }
    }

    @Override
    public void setConfig(Config config)
    {
        this.xRatio = config.getOutput().getWidth() / (double) config.getSource().getWidth();
        this.yRatio = config.getOutput().getHeight() / (double) config.getSource().getHeight();
    }

    @Override
    public void setCenter(int x, int y)
    {
        centerX = x;
        centerY = y;
    }

    @Override
    public int getMouseClick()
    {
        return lastClick;
    }

    @Override
    public int getOnScreenX()
    {
        return x;
    }

    @Override
    public int getOnScreenY()
    {
        return y;
    }

    @Override
    public int getOnWindowX()
    {
        return (int) (wx / xRatio);
    }

    @Override
    public int getOnWindowY()
    {
        return (int) (wy / yRatio);
    }

    @Override
    public int getMoveX()
    {
        return mx;
    }

    @Override
    public int getMoveY()
    {
        return my;
    }

    @Override
    public boolean hasClicked(int click)
    {
        return clicks[click];
    }

    @Override
    public boolean hasClickedOnce(int click)
    {
        if (clicks[click] && !clicked[click])
        {
            clicked[click] = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean moved()
    {
        if (moved)
        {
            moved = false;
            return true;
        }
        return false;
    }

    /*
     * MouseListener
     */

    @Override
    public void mousePressed(MouseEvent event)
    {
        lastClick = event.getButton();
        try
        {
            clicks[lastClick] = true;
        }
        catch (final ArrayIndexOutOfBoundsException exception)
        {
            Verbose.warning(MouseImpl.class, "mouseReleased", "Button out of range: ", String.valueOf(lastClick));
        }
    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        lastClick = 0;
        final int button = event.getButton();
        try
        {
            clicks[button] = false;
            clicked[button] = false;
        }
        catch (final ArrayIndexOutOfBoundsException exception)
        {
            Verbose.warning(MouseImpl.class, "mouseReleased", "Button out of range: ", String.valueOf(button));
        }
    }

    @Override
    public void mouseClicked(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseMoved(MouseEvent event)
    {
        moved = true;
        updateCoord(event);
    }

    @Override
    public void mouseDragged(MouseEvent event)
    {
        moved = true;
        updateCoord(event);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent event)
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
}
