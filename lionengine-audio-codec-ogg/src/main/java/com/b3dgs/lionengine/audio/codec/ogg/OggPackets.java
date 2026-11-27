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
package com.b3dgs.lionengine.audio.codec.ogg;

import java.io.IOException;
import java.io.InputStream;

import com.b3dgs.lionengine.audio.AudioDecodingException;
import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;

/**
 * Packets of the logical streams of an OGG stream, one link after the other, read with the OGG layer of JOrbis
 * (ADR-0073).
 *
 * <p>
 * Reads the stream {@link #READ_BYTES} bytes at a time into the buffer of the page synchronizer, which keeps its size
 * once it holds the largest page, and splits the pages of the current link into packets. Strict: a page whose capture
 * pattern or checksum fails, a missing page, a page of another logical stream inside a link (multiplexed streams), a
 * page after the end of a link that does not start a new one, and a stream that ends inside a link or inside a page
 * are refused with an {@link AudioDecodingException}. Not thread-safe.
 * </p>
 */
final class OggPackets
{
    /** Bytes read from the stream at once. */
    static final int READ_BYTES = 4_096;
    /** Error message of truncated data. */
    static final String ERROR_TRUNCATED = "Truncated OGG Vorbis data: ";
    /** Error message of corrupted data. */
    static final String ERROR_CORRUPT = "Corrupted OGG Vorbis data: ";
    /** Error message of an unsupported stream. */
    static final String ERROR_UNSUPPORTED = "Unsupported OGG Vorbis stream: ";

    /** The stream read. */
    private final InputStream input;
    /** The page synchronizer, holding the bytes read. */
    private final SyncState sync = new SyncState();
    /** The current link, splitting its pages into packets. */
    private final StreamState stream = new StreamState();
    /** The page read last. */
    private final Page page = new Page();
    /** The packet taken last. */
    private final Packet packet = new Packet();
    /** The serial number of the current link. */
    private int serial;
    /** {@code true} once the page ending the current link is taken in. */
    private boolean linkEnded;

    /**
     * Creates the reader.
     *
     * @param input The stream, positioned at its first byte.
     */
    OggPackets(InputStream input)
    {
        super();

        this.input = input;
        sync.init();
    }

    /**
     * Starts the first link of the stream.
     *
     * @throws AudioDecodingException If the stream holds no OGG page, or its first page does not start a link.
     * @throws IOException If reading fails.
     */
    void open() throws IOException
    {
        if (!readPage())
        {
            throw new AudioDecodingException(ERROR_TRUNCATED + "no OGG page");
        }
        beginLink();
    }

    /**
     * Takes the next packet of the current link, reading its pages as needed.
     *
     * @return The packet, valid until the next call, or {@code null} once the link ended.
     * @throws AudioDecodingException If the data is truncated, corrupted or multiplexed.
     * @throws IOException If reading fails.
     */
    Packet next() throws IOException
    {
        int result = stream.packetout(packet);
        while (result == 0 && !linkEnded)
        {
            takePage();
            result = stream.packetout(packet);
        }
        if (result < 0)
        {
            throw new AudioDecodingException(ERROR_CORRUPT + "missing data between pages");
        }
        return result > 0 ? packet : null;
    }

    /**
     * Starts the next link of a chained stream, once the current link ended.
     *
     * @return {@code true} if a link starts, {@code false} if the stream ended after the last one.
     * @throws AudioDecodingException If data follows the last page without starting a new link.
     * @throws IOException If reading fails.
     */
    boolean nextLink() throws IOException
    {
        final boolean found = readPage();
        if (found)
        {
            beginLink();
        }
        else if (sync.getBufferOffset() > sync.getDataOffset())
        {
            throw new AudioDecodingException(ERROR_TRUNCATED + "last page incomplete");
        }
        return found;
    }

    /**
     * Starts a link from the page read last, which must be its first page.
     *
     * @throws AudioDecodingException If the page does not start a link.
     */
    private void beginLink()
    {
        if (page.bos() == 0)
        {
            throw new AudioDecodingException(ERROR_CORRUPT + "page outside a logical stream");
        }
        serial = page.serialno();
        stream.init(serial);
        stream.reset();
        takeIn();
    }

    /**
     * Reads the next page of the current link and takes it in.
     *
     * @throws AudioDecodingException If the stream ends before the end of the link, or the page is of another link.
     * @throws IOException If reading fails.
     */
    private void takePage() throws IOException
    {
        if (!readPage())
        {
            throw new AudioDecodingException(ERROR_TRUNCATED + "stream ended before its last page");
        }
        if (page.serialno() != serial)
        {
            throw new AudioDecodingException(ERROR_UNSUPPORTED + "multiplexed logical streams");
        }
        takeIn();
    }

    /**
     * Takes the page read last into the current link.
     *
     * @throws AudioDecodingException If the link refuses the page.
     */
    private void takeIn()
    {
        if (stream.pagein(page) != 0)
        {
            throw new AudioDecodingException(ERROR_CORRUPT + "page refused by its logical stream");
        }
        linkEnded = page.eos() != 0;
    }

    /**
     * Reads the next page from the stream.
     *
     * @return {@code true} if a page is read, {@code false} if the stream ended first.
     * @throws AudioDecodingException If bytes are skipped: no capture pattern, or a checksum failure.
     * @throws IOException If reading fails.
     */
    private boolean readPage() throws IOException
    {
        int result = sync.pageout(page);
        int read = 0;
        while (result == 0 && read >= 0)
        {
            final int position = sync.buffer(READ_BYTES);
            read = input.read(sync.data, position, READ_BYTES);
            if (read > 0)
            {
                sync.wrote(read);
                result = sync.pageout(page);
            }
        }
        if (result < 0)
        {
            throw new AudioDecodingException(ERROR_CORRUPT + "not an OGG page (capture pattern or checksum)");
        }
        return result > 0;
    }
}
