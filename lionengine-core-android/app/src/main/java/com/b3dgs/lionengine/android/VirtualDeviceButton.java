/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionengine.android;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.geom.Area;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Text;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.io.DevicePointer;
import com.b3dgs.lionengine.io.DevicePush;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Represents a virtual button aimed to receive a click from a {@link DevicePointer}.
 */
public class VirtualDeviceButton implements DevicePush, Updatable, Renderable
{
    /** No key code value. */
    private static final Integer NO_KEY_CODE = Integer.valueOf(-1);
    /** Pointers. */
    private static final Integer[] POINTERS = new Integer[]
    {
        Integer.valueOf(0),
        Integer.valueOf(1),
        Integer.valueOf(2)
    };

    /** Pointer reference. */
    protected final Mouse pointer;
    /** Push listener. */
    private final ListenableModel<InputDeviceListener> listeners = new ListenableModel<>();
    /** Text printer. */
    private final Text text = Graphics.createText(14);
    /** Buttons defined. */
    private final List<Button> buttons = new ArrayList<>();
    /** List of keys. */
    private final Collection<Integer> keys = new HashSet<>();
    /** Pressed states. */
    private final Collection<Integer> pressed = new HashSet<>();

    private final Integer index = Integer.valueOf(0);

    /** Last key code. */
    private Integer lastCode = NO_KEY_CODE;
    /** Visibility. */
    private boolean visible = false;
    private boolean enabled = false;
    /** Scale. */
    private double scale;

    /**
     * Create device.
     *
     * @param pointer The pointer reference.
     */
    public VirtualDeviceButton(Mouse pointer)
    {
        super();

        this.pointer = pointer;
        text.setColor(ColorRgba.GRAY_LIGHT);
    }

    /**
     * Set screen scale.
     * @param scale The screen scale.
     */
    public void setScale(double scale)
    {
        this.scale = scale;
    }

    /**
     * Add a button.
     *
     * @param area The area representation.
     * @param code The associated code.
     * @param label The associated label.
     */
    public void addButton(Area area, Integer code, String label)
    {
        final Button button = new Button(area, code, label);
        buttons.add(button);
    }

    /**
     * Add a button.
     *
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The width.
     * @param height The height.
     * @param code The associated code.
     * @param image The associated image.
     */
    public void addButton(int x, int y, int width, int height, Integer code, Media image)
    {
        final Button button = new Button(new Rectangle(x, y, width, height), code, image);
        buttons.add(button);
    }

    /**
     * Check if visible.
     * @return The visibility.
     */
    protected boolean isVisible()
    {
        return visible;
    }

    @Override
    public InputDevice getCurrent(int id)
    {
        return this;
    }

    @Override
    public void addListener(InputDeviceListener listener)
    {
        listeners.addListener(listener);
    }

    @Override
    public void removeListener(InputDeviceListener listener)
    {
        listeners.removeListener(listener);
    }

    @Override
    public boolean isPushed(Integer key)
    {
        return keys.contains(key);
    }

    @Override
    public boolean isPushedOnce(Integer key)
    {
        if (keys.contains(key) && !pressed.contains(key))
        {
            pressed.add(key);
            return true;
        }
        return false;
    }

    @Override
    public Integer getPushed()
    {
        return lastCode;
    }

    @Override
    public boolean isPushed()
    {
        return !NO_KEY_CODE.equals(lastCode);
    }

    /**
     * Called on button pressed.
     *
     * @param button The pressed button.
     */
    private void onPressed(Button button)
    {
        lastCode = button.code;

        if (!keys.contains(lastCode))
        {
            keys.add(lastCode);
        }

        final int n = listeners.size();
        for (int i = 0; i < n; i++)
        {
            listeners.get(i).onDeviceChanged(index, button.code, (char) button.code.intValue(), true);
        }
    }

    /**
     * Called on button released.
     *
     * @param button The released button.
     */
    private void onReleased(Button button)
    {
        lastCode = NO_KEY_CODE;

        keys.remove(button.code);
        pressed.remove(button.code);

        final int n = listeners.size();
        for (int i = 0; i < n; i++)
        {
            listeners.get(i).onDeviceChanged(index, button.code, (char) button.code.intValue(), false);
        }
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        if (pointer.isPushed())
        {
            enabled = true;
        }
        if (visible && enabled)
        {
            for (int i = 0; i < buttons.size(); i++)
            {
                final Button button = buttons.get(i);
                boolean found = false;
                for (int index = 1; index < 3; index++)
                {
                    final Integer p = POINTERS[index];
                    if (pointer.isPushed(p) && button.area.contains(pointer.getX(p), pointer.getY(p)))
                    {
                        if (!button.pressed)
                        {
                            onPressed(button);
                            button.pressed = true;
                        }
                        found = true;
                        break;
                    }
                }
                if (!found && button.pressed)
                {
                    onReleased(button);
                    button.pressed = false;
                }
            }
        }
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        if (visible && enabled)
        {
            g.setColor(ColorRgba.GRAY);

            for (int i = 0; i < buttons.size(); i++)
            {
                final Button button = buttons.get(i);

                if (button.image == null)
                {
                    g.drawRect((int) button.area.getX(),
                            (int) button.area.getY(),
                            button.area.getWidth(),
                            button.area.getHeight(),
                            false);
                }
                else
                {
                    g.drawImage(button.image.getSurface(), (int) (button.image.getX() * scale), (int) (button.image.getY() * scale));
                }
                if (button.label != null)
                {
                    text.draw(g,
                            (int) button.area.getX() + 3 + (button.area.getWidth() - text.getStringWidth(g, button.label)) / 2,
                            (int) button.area.getY() + 1 + (button.area.getHeight() - text.getSize()) / 2,
                            Align.CENTER,
                            button.label);
                }
            }
        }
    }

    @Override
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Set enabled flag.
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public String getName()
    {
        return VirtualDeviceButton.class.getSimpleName();
    }

    /**
     * Button representation.
     */
    private static class Button
    {
        /** Area reference. */
        private final Area area;
        /** Code reference. */
        private final Integer code;
        /** Label reference. */
        private final String label;
        /** Label reference. */
        private final Sprite image;
        /** Pressed flag. */
        private boolean pressed;

        /**
         * Create button.
         *
         * @param area The area representation.
         * @param code The associated code.
         * @param label The associated label.
         */
        Button(Area area, Integer code, String label)
        {
            super();

            this.area = area;
            this.code = code;
            this.label = label;
            image = null;
        }

        /**
         * Create button.
         *
         * @param area The area representation.
         * @param code The associated code.
         * @param image The associated image.
         */
        Button(Area area, Integer code, Media image)
        {
            super();

            this.area = area;
            this.code = code;
            this.image = Drawable.loadSprite(image);
            this.image.load();
            this.image.prepare();
            this.image.setLocation(area.getX(), area.getY());
            label = null;
        }
    }
}
