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
package com.b3dgs.lionengine.graphic;

import java.io.IOException;
import java.io.InputStream;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.util.UtilStream;

/**
 * Get quick information from an image without reading all data.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final ImageInfo info = ImageInfo.get(Medias.create(&quot;dot.png&quot;));
 * Assert.assertEquals(64, info.getWidth());
 * Assert.assertEquals(32, info.getHeight());
 * Assert.assertEquals(&quot;png&quot;, info.getFormat());
 * </pre>
 * 
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class ImageInfo
{
    /** Unsupported format. */
    private static final String ERROR_FORMAT = "Unsupported image format";
    /** Read error. */
    private static final String ERROR_READ = "Cannot read image information";
    /** Invalid Jpg. */
    private static final String ERROR_JPG = "Invalid JPG file !";
    /** Message skipped. */
    private static final String MESSAGE_SKIPPED = "Skipped ";
    /** Message bytes instead of. */
    private static final String MESSAGE_BYTES_INSTEAD_OF = " bytes instead of ";

    /**
     * Get the image info of the specified image media.
     * 
     * @param media The media.
     * @return The image info instance.
     * @throws LionEngineException If media is <code>null</code> or cannot be read.
     */
    public static ImageInfo get(Media media)
    {
        return new ImageInfo(media);
    }

    /**
     * Check if the media is a valid image.
     * 
     * @param media The media reference.
     * @return <code>true</code> if is supported image, <code>false</code> else.
     */
    public static boolean isImage(Media media)
    {
        try
        {
            get(media);
            return true;
        }
        catch (final LionEngineException exception)
        {
            return false;
        }
    }

    /**
     * Read integer in image data.
     * 
     * @param input The stream.
     * @param bytesNumber The number of bytes to read.
     * @param bigEndian The big endian flag.
     * @return The integer read.
     * @throws IOException if error on reading.
     */
    private static int readInt(InputStream input, int bytesNumber, boolean bigEndian) throws IOException
    {
        final int oneByte = 8;
        int ret = 0;
        int sv;
        if (bigEndian)
        {
            sv = (bytesNumber - 1) * oneByte;
        }
        else
        {
            sv = 0;
        }
        final int cnt;
        if (bigEndian)
        {
            cnt = -oneByte;
        }
        else
        {
            cnt = oneByte;
        }
        for (int i = 0; i < bytesNumber; i++)
        {
            ret |= input.read() << sv;
            sv += cnt;
        }
        return ret;
    }

    /**
     * Skipped message error.
     * 
     * @param skipped The skipped value.
     * @param instead The instead value.
     * @throws IOException If not skipped the right number of bytes.
     */
    private static void checkSkippedError(long skipped, int instead) throws IOException
    {
        if (skipped != instead)
        {
            throw new IOException(MESSAGE_SKIPPED + skipped + MESSAGE_BYTES_INSTEAD_OF + instead);
        }
    }

    /** Image width. */
    private int width;
    /** Image height. */
    private int height;
    /** Image format. */
    private ImageFormat format;

    /**
     * Private constructor.
     * 
     * @param media The image media path.
     * @throws LionEngineException If media is <code>null</code> or cannot be read.
     */
    private ImageInfo(Media media)
    {
        Check.notNull(media);
        final InputStream input = media.getInputStream();
        try
        {
            final int byte1 = input.read();
            final int byte2 = input.read();
            final int byte3 = input.read();

            readFormat(input, byte1, byte2, byte3);
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_READ);
        }
        finally
        {
            UtilStream.safeClose(input);
        }
    }

    /**
     * Get image width.
     * 
     * @return The image width.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Get image height.
     * 
     * @return The image height.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Get image format.
     * 
     * @return The image format.
     */
    public ImageFormat getFormat()
    {
        return format;
    }

    /**
     * Check the image format and read it.
     * 
     * @param input The input stream.
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @param byte3 The third byte.
     * @throws IOException If an error occurred or invalid format.
     */
    private void readFormat(InputStream input, int byte1, int byte2, int byte3) throws IOException
    {
        if (isGif(byte1, byte2, byte3))
        {
            readGif(input);
        }
        else if (isJpg(byte1, byte2))
        {
            readJpg(input, byte3);
        }
        else if (isPng(byte1, byte2, byte3))
        {
            readPng(input);
        }
        else if (isBmp(byte1, byte2))
        {
            readBmp(input);
        }
        else if (isTiff(input, byte1, byte2, byte3))
        {
            readTiff(input, byte1);
        }
        else
        {
            throw new IOException(ERROR_FORMAT);
        }
    }

    /**
     * Check if can read as GIF.
     * 
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @param byte3 The third byte.
     * @return <code>true</code> if is gif, <code>false</code> else.
     */
    private static boolean isGif(int byte1, int byte2, int byte3)
    {
        return 'G' == byte1 && 'I' == byte2 && 'F' == byte3;
    }

    /**
     * Check if can read as JPG.
     * 
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @return <code>true</code> if is jpg, <code>false</code> else.
     */
    private static boolean isJpg(int byte1, int byte2)
    {
        return 0xFF == byte1 && 0xD8 == byte2;
    }

    /**
     * Check if can read as PNG.
     * 
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @param byte3 The third byte.
     * @return <code>true</code> if read, <code>false</code> else.
     */
    private static boolean isPng(int byte1, int byte2, int byte3)
    {
        return 137 == byte1 && 80 == byte2 && 78 == byte3;
    }

    /**
     * Check if can read as BMP.
     * 
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @return <code>true</code> if is bmp, <code>false</code> else.
     */
    private static boolean isBmp(int byte1, int byte2)
    {
        return 66 == byte1 && 77 == byte2;
    }

    /**
     * Check if can read as TIFF.
     * 
     * @param input The input stream.
     * @param byte1 The first byte.
     * @param byte2 The second byte.
     * @param byte3 The third byte.
     * @return <code>true</code> if is tiff, <code>false</code> else.
     * @throws IOException If an error occurred.
     */
    private static boolean isTiff(InputStream input, int byte1, int byte2, int byte3) throws IOException
    {
        final int byte4 = input.read();
        final boolean tiff1 = 'M' == byte1 && 'M' == byte2 && 0 == byte3 && 42 == byte4;
        final boolean tiff2 = 'I' == byte1 && 'I' == byte2 && 42 == byte3 && 0 == byte4;
        return tiff1 || tiff2;
    }

    /**
     * Read GIF header.
     * 
     * @param input The input stream.
     * @throws IOException If an error occurred.
     */
    private void readGif(InputStream input) throws IOException
    {
        final int headBytes = 3;
        final long skipped = input.skip(headBytes);
        checkSkippedError(skipped, headBytes);
        width = readInt(input, 2, false);
        height = readInt(input, 2, false);
        format = ImageFormat.GIF;
    }

    /**
     * Read JPG header.
     * 
     * @param input The input stream.
     * @param byte3 The third byte.
     * @throws IOException If an error occurred.
     */
    private void readJpg(InputStream input, int byte3) throws IOException
    {
        boolean success = false;
        int current = byte3;
        while (255 == current)
        {
            final int marker = input.read();
            final int len = readInt(input, 2, true);
            if (192 == marker || 193 == marker || 194 == marker)
            {
                final long skipped = input.skip(1);
                checkSkippedError(skipped, 1);
                height = readInt(input, 2, true);
                width = readInt(input, 2, true);
                format = ImageFormat.JPG;
                success = true;
                break;
            }
            final long skipped = input.skip(len - 2L);
            checkSkippedError(skipped, len - 2);
            current = input.read();
        }
        if (!success)
        {
            throw new IOException(ERROR_JPG);
        }
    }

    /**
     * Read PNG header.
     * 
     * @param input The input stream.
     * @throws IOException If an error occurred.
     */
    private void readPng(InputStream input) throws IOException
    {
        final int toSkip = 15;
        long skipped = input.skip(toSkip);
        checkSkippedError(skipped, toSkip);
        width = readInt(input, 2, true);
        skipped = input.skip(2);
        checkSkippedError(skipped, 2);
        height = readInt(input, 2, true);
        format = ImageFormat.PNG;
    }

    /**
     * Read BMP header.
     * 
     * @param input The input stream.
     * @throws IOException If an error occurred.
     */
    private void readBmp(InputStream input) throws IOException
    {
        final int toSkip = 15;
        long skipped = input.skip(toSkip);
        checkSkippedError(skipped, toSkip);
        width = readInt(input, 2, false);
        skipped = input.skip(2);
        checkSkippedError(skipped, 2);
        height = readInt(input, 2, false);
        format = ImageFormat.BMP;
    }

    /**
     * Read TIFF header.
     * 
     * @param input The input stream.
     * @param byte1 The firsts byte.
     * @throws IOException If an error occurred.
     */
    private void readTiff(InputStream input, int byte1) throws IOException
    {
        final long toSkip = 8L;
        final boolean bigEndian = 'M' == byte1;
        final int ifd = readInt(input, 4, bigEndian);
        input.skip(ifd - toSkip);
        // checkSkippedError(skipped, ifd - toSkip); fail when reading from JAR, not needed
        final int entries = readInt(input, 2, bigEndian);

        int w = -1;
        int h = -1;
        for (int i = 1; i <= entries; i++)
        {
            final int tag = readInt(input, 2, bigEndian);
            final int fieldType = readInt(input, 2, bigEndian);
            readInt(input, 4, bigEndian);
            int valOffset;
            if (3 == fieldType || 8 == fieldType)
            {
                valOffset = readInt(input, 2, bigEndian);
                final long skipped = input.skip(2);
                checkSkippedError(skipped, 2);
            }
            else
            {
                valOffset = readInt(input, 4, bigEndian);
            }
            if (256 == tag)
            {
                w = valOffset;
            }
            else if (257 == tag)
            {
                h = valOffset;
            }
            if (-1 != w && -1 != h)
            {
                width = w;
                height = h;
                format = ImageFormat.TIFF;
                break;
            }
        }
    }
}
