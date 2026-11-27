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
package com.b3dgs.lionengine.graphics;

/**
 * Binds {@link ImageDecoderContract} on {@link PngDecoder} (ADR-0045, REQ-SPRITE-009).
 */
final class PngDecoderContractTest extends ImageDecoderContract
{
    /** Pixels of the image: opaque red, half transparent green, transparent blue, opaque white; then the reverse. */
    private static final int[] PIXELS =
    {
        0xFFFF0000, 0x8000FF00, 0x000000FF, 0xFFFFFFFF, 0xFFFFFFFF, 0x000000FF, 0x8000FF00, 0xFFFF0000
    };
    /** Width of the image. */
    private static final int WIDTH = 4;
    /** Height of the image. */
    private static final int HEIGHT = 2;

    @Override
    protected ImageDecoder createDecoder()
    {
        return new PngDecoder();
    }

    @Override
    protected ImageDecoder createDecoder(int maxPixels)
    {
        return new PngDecoder(maxPixels);
    }

    @Override
    protected byte[] getImage()
    {
        return PngWriter.rgba(WIDTH, HEIGHT, PIXELS).write();
    }

    @Override
    protected PixelBuffer getExpected()
    {
        final PixelBuffer expected = new PixelBuffer(WIDTH, HEIGHT);
        expected.setPixels(PIXELS);
        return expected;
    }
}
