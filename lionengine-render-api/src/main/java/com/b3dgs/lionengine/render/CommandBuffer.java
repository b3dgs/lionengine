/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.render;

import java.util.Arrays;

/**
 * Render commands of one frame, in preallocated parallel primitive arrays (ADR-0026, ADR-0031, ADR-0033,
 * REQ-SPRITE-008, REQ-SPRITE-010).
 *
 * <p>
 * Each frame, the render code clears the buffer, then adds one entry per draw with
 * {@link #add(int, int, int, int, int)} and completes the entry just added with the chained setters, for example
 * {@code commands.add(texture, 0, 0, 16, 16).position(x, y).layer(2)}. Each setter changes one field of the last entry
 * added and nothing else: the setters can be called in any order, a second call to the same setter replaces the first
 * value, and no setter applies to the entries added later. Unset fields keep their defaults: position
 * {@code (0, 0)}, scale {@code 1}, rotation {@code 0}, pivot {@code (0, 0)}, no tint ({@link #NO_TINT}), alpha
 * {@code 1}, layer {@code 0}. Positions are logical-frame pixels relative to the viewport, and give where the pivot is
 * drawn; the pivot is in pixels from the top-left of the source rectangle; rotations are degrees, counter-clockwise on
 * screen. The render backend then presents the entries by ascending layer, then submission order, without modifying
 * the buffer.
 * </p>
 *
 * <p>
 * A viewport set with {@link #setViewport(Viewport)} clips and offsets the entries added after it, until the next
 * viewport or {@link #resetViewport()}; the default viewport is the whole logical frame. When full, the buffer doubles
 * its capacity at the next {@link #add(int, int, int, int, int)}; once it fits the busiest frame, it allocates nothing
 * (ADR-0019). Thread: the runtime thread writes it in the render phase and the backend reads it in
 * {@code present}; not thread-safe.
 * </p>
 *
 * @since 11.0.0
 */
public final class CommandBuffer
{
    /** Tint of an entry without tint: opaque white, which leaves the colors unchanged. */
    public static final int NO_TINT = 0xFFFFFFFF;

    /** Capacity growth factor. */
    private static final int GROWTH = 2;
    /** Viewport index of the whole logical frame. */
    private static final int FRAME = -1;
    /** Number of values of a viewport. */
    private static final int VIEWPORT_FIELDS = 4;
    /** Offset of the left edge in a viewport. */
    private static final int FIELD_X = 0;
    /** Offset of the top edge in a viewport. */
    private static final int FIELD_Y = 1;
    /** Offset of the width in a viewport. */
    private static final int FIELD_WIDTH = 2;
    /** Offset of the height in a viewport. */
    private static final int FIELD_HEIGHT = 3;
    /** Error message of a setter without entry. */
    private static final String ERROR_NO_ENTRY = "No entry added since the last clear";

    /**
     * Checks that a size is positive.
     *
     * @param value The size.
     * @param name The name of the size, for the message.
     * @throws IllegalArgumentException If the size is not positive.
     */
    private static void checkPositive(int value, String name)
    {
        if (value <= 0)
        {
            throw new IllegalArgumentException(name + " not positive: " + value);
        }
    }

    /** Logical frame width in pixels. */
    private final int logicalWidth;
    /** Logical frame height in pixels. */
    private final int logicalHeight;
    /** Texture handles. */
    private int[] textures;
    /** Source rectangle left edges. */
    private int[] sourceX;
    /** Source rectangle top edges. */
    private int[] sourceY;
    /** Source rectangle widths. */
    private int[] sourceWidth;
    /** Source rectangle heights. */
    private int[] sourceHeight;
    /** Horizontal positions. */
    private float[] positionX;
    /** Vertical positions. */
    private float[] positionY;
    /** Horizontal scales. */
    private float[] scaleX;
    /** Vertical scales. */
    private float[] scaleY;
    /** Rotations in degrees. */
    private float[] rotations;
    /** Horizontal pivots. */
    private float[] pivotX;
    /** Vertical pivots. */
    private float[] pivotY;
    /** Tints. */
    private int[] tints;
    /** Alphas. */
    private float[] alphas;
    /** Layers. */
    private int[] layers;
    /** Viewport index of each entry, {@link #FRAME} for the whole frame. */
    private int[] entryViewports;
    /** Entries in presentation order. */
    private int[] order;
    /** Merge buffer of the sort. */
    private int[] scratch;
    /** Number of entries. */
    private int size;
    /** Viewport rectangles, four values each: x, y, width, height. */
    private int[] viewports = new int[GROWTH * VIEWPORT_FIELDS];
    /** Number of viewports. */
    private int viewportCount;
    /** Viewport of the next entries. */
    private int viewport = FRAME;
    /** {@code true} when {@link #order} is up to date. */
    private boolean sorted;

    /**
     * Creates an empty buffer.
     *
     * @param logicalWidth The logical frame width in pixels, the width of the default viewport (positive).
     * @param logicalHeight The logical frame height in pixels, the height of the default viewport (positive).
     * @param capacity The initial number of entries (positive).
     * @throws IllegalArgumentException If an argument is not positive.
     */
    public CommandBuffer(int logicalWidth, int logicalHeight, int capacity)
    {
        checkPositive(logicalWidth, "Logical width");
        checkPositive(logicalHeight, "Logical height");
        checkPositive(capacity, "Capacity");
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        allocate(capacity);
    }

    /**
     * Removes every entry and viewport; the next entries use the whole logical frame. Call it at the start of each
     * frame. The capacity is kept.
     */
    public void clear()
    {
        size = 0;
        viewportCount = 0;
        viewport = FRAME;
        sorted = false;
    }

    /**
     * Sets the viewport of the entries added from now on.
     *
     * @param viewport The viewport, in logical-frame pixels (never {@code null}).
     * @throws IllegalArgumentException If {@code viewport} is {@code null}.
     */
    public void setViewport(Viewport viewport)
    {
        if (viewport == null)
        {
            throw new IllegalArgumentException("Viewport must not be null");
        }
        if ((viewportCount + 1) * VIEWPORT_FIELDS > viewports.length)
        {
            viewports = Arrays.copyOf(viewports, viewports.length * GROWTH);
        }
        final int base = viewportCount * VIEWPORT_FIELDS;
        viewports[base + FIELD_X] = viewport.x();
        viewports[base + FIELD_Y] = viewport.y();
        viewports[base + FIELD_WIDTH] = viewport.width();
        viewports[base + FIELD_HEIGHT] = viewport.height();
        this.viewport = viewportCount;
        viewportCount++;
    }

    /**
     * Sets the whole logical frame as the viewport of the entries added from now on.
     */
    public void resetViewport()
    {
        viewport = FRAME;
    }

    /**
     * Adds an entry with the default values, drawing a rectangle of a texture.
     *
     * @param texture The texture handle of the backend's {@link TextureStore}.
     * @param x The left edge of the source rectangle in texture pixels.
     * @param y The top edge of the source rectangle in texture pixels.
     * @param width The width of the source rectangle in pixels (positive).
     * @param height The height of the source rectangle in pixels (positive).
     * @return This buffer, to set the other fields of the entry.
     * @throws IllegalArgumentException If {@code width} or {@code height} is not positive.
     */
    public CommandBuffer add(int texture, int x, int y, int width, int height)
    {
        checkPositive(width, "Source width");
        checkPositive(height, "Source height");
        if (size == textures.length)
        {
            allocate(size * GROWTH);
        }
        final int entry = size;
        textures[entry] = texture;
        sourceX[entry] = x;
        sourceY[entry] = y;
        sourceWidth[entry] = width;
        sourceHeight[entry] = height;
        positionX[entry] = 0.0F;
        positionY[entry] = 0.0F;
        scaleX[entry] = 1.0F;
        scaleY[entry] = 1.0F;
        rotations[entry] = 0.0F;
        pivotX[entry] = 0.0F;
        pivotY[entry] = 0.0F;
        tints[entry] = NO_TINT;
        alphas[entry] = 1.0F;
        layers[entry] = 0;
        entryViewports[entry] = viewport;
        size++;
        sorted = false;
        return this;
    }

    /**
     * Sets where the pivot of the last entry is drawn.
     *
     * @param x The horizontal position in logical-frame pixels, relative to the viewport.
     * @param y The vertical position in logical-frame pixels, relative to the viewport.
     * @return This buffer.
     * @throws IllegalStateException If no entry was added since the last clear.
     */
    public CommandBuffer position(float x, float y)
    {
        final int entry = last();
        positionX[entry] = x;
        positionY[entry] = y;
        return this;
    }

    /**
     * Sets the scale of the last entry, around its pivot.
     *
     * @param x The horizontal scale ({@code 1} for none, negative to mirror).
     * @param y The vertical scale ({@code 1} for none, negative to flip).
     * @return This buffer.
     * @throws IllegalStateException If no entry was added since the last clear.
     */
    public CommandBuffer scale(float x, float y)
    {
        final int entry = last();
        scaleX[entry] = x;
        scaleY[entry] = y;
        return this;
    }

    /**
     * Sets the rotation of the last entry, around its pivot.
     *
     * @param degrees The rotation in degrees, counter-clockwise on screen.
     * @return This buffer.
     * @throws IllegalStateException If no entry was added since the last clear.
     */
    public CommandBuffer rotation(float degrees)
    {
        rotations[last()] = degrees;
        return this;
    }

    /**
     * Sets the pivot of the last entry: the point of the source rectangle drawn at the position, scaled and rotated
     * around.
     *
     * @param x The horizontal pivot in pixels from the left edge of the source rectangle.
     * @param y The vertical pivot in pixels from the top edge of the source rectangle.
     * @return This buffer.
     * @throws IllegalStateException If no entry was added since the last clear.
     */
    public CommandBuffer pivot(float x, float y)
    {
        final int entry = last();
        pivotX[entry] = x;
        pivotY[entry] = y;
        return this;
    }

    /**
     * Sets the tint of the last entry, multiplied with the texture colors.
     *
     * @param argb The tint, packed {@code 0xAARRGGBB}; {@link #NO_TINT} for none.
     * @return This buffer.
     * @throws IllegalStateException If no entry was added since the last clear.
     */
    public CommandBuffer tint(int argb)
    {
        tints[last()] = argb;
        return this;
    }

    /**
     * Sets the opacity of the last entry.
     *
     * @param alpha The opacity in {@code [0, 1]}, {@code 1} for opaque.
     * @return This buffer.
     * @throws IllegalArgumentException If {@code alpha} is outside {@code [0, 1]} or not a number.
     * @throws IllegalStateException If no entry was added since the last clear.
     */
    public CommandBuffer alpha(float alpha)
    {
        if (Float.isNaN(alpha) || alpha < 0.0F || alpha > 1.0F)
        {
            throw new IllegalArgumentException("Alpha outside [0, 1]: " + alpha);
        }
        alphas[last()] = alpha;
        return this;
    }

    /**
     * Sets the layer of the last entry: entries are presented by ascending layer, then submission order.
     *
     * @param layer The layer.
     * @return This buffer.
     * @throws IllegalStateException If no entry was added since the last clear.
     */
    public CommandBuffer layer(int layer)
    {
        layers[last()] = layer;
        sorted = false;
        return this;
    }

    /**
     * Returns the number of entries.
     *
     * @return The number of entries since the last clear.
     */
    public int size()
    {
        return size;
    }

    /**
     * Returns the number of entries the buffer holds without growing.
     *
     * @return The capacity.
     */
    public int getCapacity()
    {
        return textures.length;
    }

    /**
     * Returns the entry presented at a rank: entries by ascending layer, then submission order. The order is computed
     * once after each change, without allocation.
     *
     * @param rank The rank in presentation order, in {@code [0, size())}.
     * @return The entry index, for the getters.
     * @throws IndexOutOfBoundsException If {@code rank} is outside {@code [0, size())}.
     */
    public int getEntry(int rank)
    {
        checkEntry(rank);
        if (!sorted)
        {
            sort();
        }
        return order[rank];
    }

    /**
     * Returns the texture handle of an entry.
     *
     * @param entry The entry index, in submission order.
     * @return The texture handle.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getTexture(int entry)
    {
        checkEntry(entry);
        return textures[entry];
    }

    /**
     * Returns the left edge of the source rectangle of an entry.
     *
     * @param entry The entry index.
     * @return The left edge in texture pixels.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getSourceX(int entry)
    {
        checkEntry(entry);
        return sourceX[entry];
    }

    /**
     * Returns the top edge of the source rectangle of an entry.
     *
     * @param entry The entry index.
     * @return The top edge in texture pixels.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getSourceY(int entry)
    {
        checkEntry(entry);
        return sourceY[entry];
    }

    /**
     * Returns the width of the source rectangle of an entry.
     *
     * @param entry The entry index.
     * @return The width in pixels.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getSourceWidth(int entry)
    {
        checkEntry(entry);
        return sourceWidth[entry];
    }

    /**
     * Returns the height of the source rectangle of an entry.
     *
     * @param entry The entry index.
     * @return The height in pixels.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getSourceHeight(int entry)
    {
        checkEntry(entry);
        return sourceHeight[entry];
    }

    /**
     * Returns the horizontal position of an entry.
     *
     * @param entry The entry index.
     * @return The position of the pivot in logical-frame pixels, relative to the viewport.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public float getX(int entry)
    {
        checkEntry(entry);
        return positionX[entry];
    }

    /**
     * Returns the vertical position of an entry.
     *
     * @param entry The entry index.
     * @return The position of the pivot in logical-frame pixels, relative to the viewport.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public float getY(int entry)
    {
        checkEntry(entry);
        return positionY[entry];
    }

    /**
     * Returns the horizontal scale of an entry.
     *
     * @param entry The entry index.
     * @return The horizontal scale.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public float getScaleX(int entry)
    {
        checkEntry(entry);
        return scaleX[entry];
    }

    /**
     * Returns the vertical scale of an entry.
     *
     * @param entry The entry index.
     * @return The vertical scale.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public float getScaleY(int entry)
    {
        checkEntry(entry);
        return scaleY[entry];
    }

    /**
     * Returns the rotation of an entry.
     *
     * @param entry The entry index.
     * @return The rotation in degrees, counter-clockwise on screen.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public float getRotation(int entry)
    {
        checkEntry(entry);
        return rotations[entry];
    }

    /**
     * Returns the horizontal pivot of an entry.
     *
     * @param entry The entry index.
     * @return The pivot in pixels from the left edge of the source rectangle.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public float getPivotX(int entry)
    {
        checkEntry(entry);
        return pivotX[entry];
    }

    /**
     * Returns the vertical pivot of an entry.
     *
     * @param entry The entry index.
     * @return The pivot in pixels from the top edge of the source rectangle.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public float getPivotY(int entry)
    {
        checkEntry(entry);
        return pivotY[entry];
    }

    /**
     * Returns the tint of an entry.
     *
     * @param entry The entry index.
     * @return The tint, packed {@code 0xAARRGGBB}, {@link #NO_TINT} for none.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getTint(int entry)
    {
        checkEntry(entry);
        return tints[entry];
    }

    /**
     * Returns the opacity of an entry.
     *
     * @param entry The entry index.
     * @return The opacity in {@code [0, 1]}.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public float getAlpha(int entry)
    {
        checkEntry(entry);
        return alphas[entry];
    }

    /**
     * Returns the layer of an entry.
     *
     * @param entry The entry index.
     * @return The layer.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getLayer(int entry)
    {
        checkEntry(entry);
        return layers[entry];
    }

    /**
     * Returns the left edge of the viewport of an entry.
     *
     * @param entry The entry index.
     * @return The left edge in logical-frame pixels, {@code 0} for the whole frame.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getViewportX(int entry)
    {
        return viewportValue(entry, FIELD_X, 0);
    }

    /**
     * Returns the top edge of the viewport of an entry.
     *
     * @param entry The entry index.
     * @return The top edge in logical-frame pixels, {@code 0} for the whole frame.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getViewportY(int entry)
    {
        return viewportValue(entry, FIELD_Y, 0);
    }

    /**
     * Returns the width of the viewport of an entry.
     *
     * @param entry The entry index.
     * @return The width in pixels, the logical frame width for the whole frame.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getViewportWidth(int entry)
    {
        return viewportValue(entry, FIELD_WIDTH, logicalWidth);
    }

    /**
     * Returns the height of the viewport of an entry.
     *
     * @param entry The entry index.
     * @return The height in pixels, the logical frame height for the whole frame.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    public int getViewportHeight(int entry)
    {
        return viewportValue(entry, FIELD_HEIGHT, logicalHeight);
    }

    /**
     * Returns one value of the viewport of an entry.
     *
     * @param entry The entry index.
     * @param field The offset of the value in a viewport.
     * @param frame The value for the whole frame.
     * @return The value.
     * @throws IndexOutOfBoundsException If {@code entry} is outside {@code [0, size())}.
     */
    private int viewportValue(int entry, int field, int frame)
    {
        checkEntry(entry);
        final int index = entryViewports[entry];
        if (index == FRAME)
        {
            return frame;
        }
        return viewports[index * VIEWPORT_FIELDS + field];
    }

    /**
     * Returns the last entry.
     *
     * @return The index of the last entry.
     * @throws IllegalStateException If no entry was added since the last clear.
     */
    private int last()
    {
        if (size == 0)
        {
            throw new IllegalStateException(ERROR_NO_ENTRY);
        }
        return size - 1;
    }

    /**
     * Checks an entry index or a rank.
     *
     * @param index The index.
     * @throws IndexOutOfBoundsException If {@code index} is outside {@code [0, size())}.
     */
    private void checkEntry(int index)
    {
        if (index < 0 || index >= size)
        {
            throw new IndexOutOfBoundsException("Entry " + index + " outside [0, " + size + ')');
        }
    }

    /**
     * Sorts the entries by ascending layer, keeping the submission order within a layer: a bottom-up merge sort into
     * the preallocated scratch array, skipped when the layers are already in order.
     */
    private void sort()
    {
        boolean ordered = true;
        for (int i = 0; i < size; i++)
        {
            order[i] = i;
            if (i > 0 && layers[i] < layers[i - 1])
            {
                ordered = false;
            }
        }
        if (!ordered)
        {
            int[] source = order;
            int[] target = scratch;
            for (int width = 1; width < size; width *= GROWTH)
            {
                for (int start = 0; start < size; start += GROWTH * width)
                {
                    merge(source, target, start, Math.min(start + width, size), Math.min(start + GROWTH * width, size));
                }
                final int[] swap = source;
                source = target;
                target = swap;
            }
            if (source != order)
            {
                System.arraycopy(source, 0, order, 0, size);
            }
        }
        sorted = true;
    }

    /**
     * Merges two consecutive sorted runs, the left run first on equal layers.
     *
     * @param source The runs.
     * @param target The merged run.
     * @param start The first index of the left run.
     * @param middle The first index of the right run.
     * @param end The index after the right run.
     */
    private void merge(int[] source, int[] target, int start, int middle, int end)
    {
        int left = start;
        int right = middle;
        for (int i = start; i < end; i++)
        {
            if (left < middle && (right >= end || layers[source[left]] <= layers[source[right]]))
            {
                target[i] = source[left];
                left++;
            }
            else
            {
                target[i] = source[right];
                right++;
            }
        }
    }

    /**
     * Allocates the arrays for a capacity, keeping the entries.
     *
     * @param capacity The new capacity.
     */
    private void allocate(int capacity)
    {
        textures = grow(textures, capacity);
        sourceX = grow(sourceX, capacity);
        sourceY = grow(sourceY, capacity);
        sourceWidth = grow(sourceWidth, capacity);
        sourceHeight = grow(sourceHeight, capacity);
        positionX = grow(positionX, capacity);
        positionY = grow(positionY, capacity);
        scaleX = grow(scaleX, capacity);
        scaleY = grow(scaleY, capacity);
        rotations = grow(rotations, capacity);
        pivotX = grow(pivotX, capacity);
        pivotY = grow(pivotY, capacity);
        tints = grow(tints, capacity);
        alphas = grow(alphas, capacity);
        layers = grow(layers, capacity);
        entryViewports = grow(entryViewports, capacity);
        order = new int[capacity];
        scratch = new int[capacity];
        sorted = false;
    }

    /**
     * Returns an array of a capacity with the values of another.
     *
     * @param array The current array, {@code null} if none.
     * @param capacity The capacity.
     * @return The new array.
     */
    private static int[] grow(int[] array, int capacity)
    {
        if (array == null)
        {
            return new int[capacity];
        }
        return Arrays.copyOf(array, capacity);
    }

    /**
     * Returns an array of a capacity with the values of another.
     *
     * @param array The current array, {@code null} if none.
     * @param capacity The capacity.
     * @return The new array.
     */
    private static float[] grow(float[] array, int capacity)
    {
        if (array == null)
        {
            return new float[capacity];
        }
        return Arrays.copyOf(array, capacity);
    }
}
