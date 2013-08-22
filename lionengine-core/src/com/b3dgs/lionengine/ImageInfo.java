package com.b3dgs.lionengine;

import java.io.IOException;
import java.io.InputStream;

/**
 * Get quick information from an image without reading all bytes.
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final ImageInfo info = ImageInfo.get(Media.get(&quot;dot.png&quot;));
 * Assert.assertEquals(64, info.getWidth());
 * Assert.assertEquals(32, info.getHeight());
 * Assert.assertEquals(&quot;png&quot;, info.getFormat());
 * </pre>
 */
public final class ImageInfo
{
    /**
     * Get the image info of the specified image media.
     * 
     * @param media The media.
     * @return The image info instance.
     */
    public static ImageInfo get(Media media)
    {
        return new ImageInfo(media);
    }

    /**
     * Read integer in image data.
     * 
     * @param inputStream The stream.
     * @param bytesNumber The number of bytes to read.
     * @param bigEndian The big endian flag.
     * @return The integer read.
     * @throws IOException if error on reading.
     */
    private static int readInt(InputStream inputStream, int bytesNumber, boolean bigEndian) throws IOException
    {
        int ret = 0;
        int sv = bigEndian ? (bytesNumber - 1) * 8 : 0;
        final int cnt = bigEndian ? -8 : 8;
        for (int i = 0; i < bytesNumber; i++)
        {
            ret |= inputStream.read() << sv;
            sv += cnt;
        }
        return ret;
    }

    /** Image width. */
    private int width;
    /** Image height. */
    private int height;
    /** Image format. */
    private String format;

    /**
     * Crate a new info reader.
     * 
     * @param media The image media path.
     */
    private ImageInfo(Media media)
    {
        Media.exist(media);
        try (InputStream inputStream = Media.getStream(media, "ImageInfo", false);)
        {
            final int byte1 = inputStream.read();
            final int byte2 = inputStream.read();
            final int byte3 = inputStream.read();

            final boolean gif = 'G' == byte1 && 'I' == byte2 && 'F' == byte3;
            final boolean jpg = 0xFF == byte1 && 0xD8 == byte2;
            final boolean png = 137 == byte1 && 80 == byte2 && 78 == byte3;
            final boolean bmp = 66 == byte1 && 77 == byte2;

            if (gif)
            {
                readGif(inputStream);
            }
            else if (jpg)
            {
                readJpg(inputStream, byte3);
            }
            else if (png)
            {
                readPng(inputStream);
            }
            else if (bmp)
            {
                readBmp(inputStream);
            }
            else
            {
                final int byte4 = inputStream.read();
                final boolean tiff = 'M' == byte1 && 'M' == byte2 && 0 == byte3 && 42 == byte4 || 'I' == byte1
                        && 'I' == byte2 && 42 == byte3 && 0 == byte4;

                if (tiff)
                {
                    readTiff(inputStream, byte1);
                }
                else
                {
                    throw new LionEngineException("Unsupported image format");
                }
            }
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, "Can not read image information");
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
    public String getFormat()
    {
        return format;
    }

    /**
     * Read GIF header.
     * 
     * @param inputStream The input stream.
     * @throws IOException If an error occurs.
     */
    private void readGif(InputStream inputStream) throws IOException
    {
        final long skipped = inputStream.skip(3);
        if (skipped != 3)
        {
            throw new IOException("Skipped " + skipped + " bytes instead of 3");
        }
        width = ImageInfo.readInt(inputStream, 2, false);
        height = ImageInfo.readInt(inputStream, 2, false);
        format = "gif";
    }

    /**
     * Read JPG header.
     * 
     * @param inputStream The input stream.
     * @param byte3 The third byte.
     * @throws IOException If an error occurs.
     */
    private void readJpg(InputStream inputStream, int byte3) throws IOException
    {
        int current = byte3;
        while (255 == current)
        {
            final int marker = inputStream.read();
            final int len = ImageInfo.readInt(inputStream, 2, true);
            if (192 == marker || 193 == marker || 194 == marker)
            {
                final long skipped = inputStream.skip(1);
                if (skipped != 1)
                {
                    throw new IOException("Skipped " + skipped + " bytes instead of 1");
                }
                height = ImageInfo.readInt(inputStream, 2, true);
                width = ImageInfo.readInt(inputStream, 2, true);
                format = "jpeg";
                break;
            }
            final long skipped = inputStream.skip(len - 2);
            if (skipped != len - 2)
            {
                throw new IOException("Skipped " + skipped + " bytes instead of " + (len - 2));
            }
            current = inputStream.read();
        }
    }

    /**
     * Read PNG header.
     * 
     * @param inputStream The input stream.
     * @throws IOException If an error occurs.
     */
    private void readPng(InputStream inputStream) throws IOException
    {
        final int toSkip = 15;
        long skipped = inputStream.skip(toSkip);
        if (skipped != toSkip)
        {
            throw new IOException("Skipped " + skipped + " bytes instead of 15");
        }
        width = ImageInfo.readInt(inputStream, 2, true);
        skipped = inputStream.skip(2);
        if (skipped != 2)
        {
            throw new IOException("Skipped " + skipped + " bytes instead of 2");
        }
        height = ImageInfo.readInt(inputStream, 2, true);
        format = "png";
    }

    /**
     * Read BMP header.
     * 
     * @param inputStream The input stream.
     * @throws IOException If an error occurs.
     */
    private void readBmp(InputStream inputStream) throws IOException
    {
        final int toSkip = 15;
        long skipped = inputStream.skip(toSkip);
        if (skipped != toSkip)
        {
            throw new IOException("Skipped " + skipped + " bytes instead of 15");
        }
        width = ImageInfo.readInt(inputStream, 2, false);
        skipped = inputStream.skip(2);
        if (skipped != 2)
        {
            throw new IOException("Skipped " + skipped + " bytes instead of 2");
        }
        height = ImageInfo.readInt(inputStream, 2, false);
        format = "bmp";
    }

    /**
     * Read TIFF header.
     * 
     * @param inputStream The input stream.
     * @param byte1 The firsts byte.
     * @throws IOException If an error occurs.
     */
    private void readTiff(InputStream inputStream, int byte1) throws IOException
    {
        final int toSkip = 8;
        final boolean bigEndian = 'M' == byte1;
        int w = -1, h = -1;
        final int ifd = ImageInfo.readInt(inputStream, 4, bigEndian);
        long skipped = inputStream.skip(ifd - toSkip);
        if (skipped != ifd - toSkip)
        {
            throw new IOException("Skipped " + skipped + " bytes instead of " + (ifd - toSkip));
        }
        final int entries = ImageInfo.readInt(inputStream, 2, bigEndian);

        for (int i = 1; i <= entries; i++)
        {
            final int tag = ImageInfo.readInt(inputStream, 2, bigEndian);
            final int fieldType = ImageInfo.readInt(inputStream, 2, bigEndian);
            ImageInfo.readInt(inputStream, 4, bigEndian);
            int valOffset;
            if (3 == fieldType || 8 == fieldType)
            {
                valOffset = ImageInfo.readInt(inputStream, 2, bigEndian);
                skipped = inputStream.skip(2);
                if (skipped != 2)
                {
                    throw new IOException("Skipped " + skipped + " bytes instead of 2");
                }
            }
            else
            {
                valOffset = ImageInfo.readInt(inputStream, 4, bigEndian);
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
                format = "tiff";
                break;
            }
        }
    }
}
