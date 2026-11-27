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

import com.b3dgs.lionengine.audio.AudioDecodingException;

/**
 * Checks of the three Vorbis header packets before JOrbis reads them, with the bounds of {@code libvorbis} and a
 * budget of the codebooks, so that a small corrupted or crafted file cannot make the decoder allocate an unbounded
 * amount of memory (ADR-0054, ADR-0073).
 *
 * <p>
 * JOrbis allocates the arrays a header declares before reading them, and reads them past the end of the packet: the
 * vendor and comment lengths of the comment header, the codebook sizes of the setup header, and, while decoding, the
 * residue range. These checks parse the headers with the Vorbis I specification and refuse, with an
 * {@link AudioDecodingException}:
 * </p>
 * <ul>
 * <li>a length or a count that the packet does not hold;</li>
 * <li>block sizes outside 64 to 8,192 samples;</li>
 * <li>a codebook whose entries and dimensions need more than 24 bits together, as {@code libvorbis} does;</li>
 * <li>codebooks above {@link #MAX_ENTRIES} entries or {@link #MAX_VALUES} values (entries × dimensions) in total, 16
 * times the largest setup of the {@code libvorbis} encoder (6,561 entries in a book, about 63,000 values in
 * total);</li>
 * <li>a floor or a residue naming a codebook that does not exist, a floor of more than 63 points, a residue whose
 * classifications do not fit its codebook (the check of {@code libvorbis}), or whose range goes past the residue
 * vector of a long block (half the block, times the channels for a residue of type 2, which interleaves them), which
 * JOrbis would not clamp.</li>
 * </ul>
 *
 * <p>
 * The mappings and modes, which allocate little, are left to the checks of JOrbis. A packet that does not start with
 * the signature of its header type is not checked here: JOrbis refuses it.
 * </p>
 */
final class VorbisHeaders
{
    /** Largest number of codebook entries in a setup header. */
    static final int MAX_ENTRIES = 1 << 20;
    /** Largest number of codebook values (entries × dimensions) in a setup header. */
    static final int MAX_VALUES = 1 << 20;
    /** Smallest block size. */
    static final int MIN_BLOCK = 64;
    /** Largest block size. */
    static final int MAX_BLOCK = 8_192;
    /** Largest number of points of a floor 1, the two implicit ones apart. */
    static final int MAX_FLOOR_POINTS = 63;

    /** Type of the identification header. */
    private static final int IDENTIFICATION = 1;
    /** Type of the comment header. */
    private static final int COMMENT = 3;
    /** Type of the setup header. */
    private static final int SETUP = 5;
    /** Signature of a header after its type. */
    private static final byte[] SIGNATURE =
    {
        'v', 'o', 'r', 'b', 'i', 's'
    };
    /** Largest bits of entries and dimensions together in a codebook. */
    private static final int MAX_BOOK_BITS = 24;
    /** Synchronization pattern of a codebook. */
    private static final long BOOK_SYNC = 0x56_4342L;
    /** Bits of a byte. */
    private static final int BITS_8 = 8;
    /** Bits of the fields of 3 bits. */
    private static final int BITS_3 = 3;
    /** Bits of the fields of 4 bits. */
    private static final int BITS_4 = 4;
    /** Bits of the fields of 5 bits. */
    private static final int BITS_5 = 5;
    /** Bits of the fields of 6 bits. */
    private static final int BITS_6 = 6;
    /** Bits of the fields of 16 bits. */
    private static final int BITS_16 = 16;
    /** Bits of the fields of 24 bits. */
    private static final int BITS_24 = 24;
    /** Bits of the fields of 32 bits. */
    private static final int BITS_32 = 32;
    /** Bits of the three bitrates of the identification header. */
    private static final int BITRATES = 3 * BITS_32;
    /** Bits of the amplitude fields of a floor 0. */
    private static final int FLOOR0_AMPLITUDE = BITS_6 + BITS_8;
    /** Bits of the range of a floor 1 multiplier. */
    private static final int MULTIPLIER = 2;
    /** Bits of the minimum and delta of a codebook lookup. */
    private static final int LOOKUP_RANGE = 2 * BITS_32;
    /** Largest residue type. */
    private static final int MAX_RESIDUE = 2;
    /** Largest codeword length. */
    private static final int MAX_LENGTH = 32;
    /** Name of a codebook in the messages. */
    private static final String CODEBOOK = "codebook ";

    /**
     * Checks that a packet starts with the signature of a header type.
     *
     * @param bits The packet, at its first bit; after the signature if it matches.
     * @param bytes The bytes of the packet.
     * @param type The header type.
     * @return {@code true} if it matches.
     */
    private static boolean isHeader(VorbisBits bits, int bytes, int type)
    {
        boolean header = bytes > SIGNATURE.length && bits.readByte() == type;
        for (int i = 0; header && i < SIGNATURE.length; i++)
        {
            header = bits.readByte() == SIGNATURE[i];
        }
        return header;
    }

    /**
     * Checks an identification header and returns its long block size.
     *
     * @param data The bytes holding the packet.
     * @param offset The index of its first byte.
     * @param bytes The bytes of the packet.
     * @return The long block size, {@code 0} if the packet is not an identification header.
     * @throws AudioDecodingException If its block sizes are invalid or unsupported.
     */
    static int checkIdentification(byte[] data, int offset, int bytes)
    {
        final VorbisBits bits = new VorbisBits(data, offset, bytes);
        if (!isHeader(bits, bytes, IDENTIFICATION))
        {
            return 0;
        }
        bits.skip(BITS_32 + BITS_8 + BITS_32 + BITRATES);
        final int shortBlock = 1 << bits.read(BITS_4);
        final int longBlock = 1 << bits.read(BITS_4);
        if (shortBlock < MIN_BLOCK || longBlock > MAX_BLOCK || shortBlock > longBlock)
        {
            throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + "block sizes " + shortBlock + " and "
                                             + longBlock);
        }
        return longBlock;
    }

    /**
     * Checks that the lengths of a comment header fit its packet.
     *
     * @param data The bytes holding the packet.
     * @param offset The index of its first byte.
     * @param bytes The bytes of the packet.
     * @throws AudioDecodingException If a length or the count of comments goes past the packet.
     */
    static void checkComment(byte[] data, int offset, int bytes)
    {
        final VorbisBits bits = new VorbisBits(data, offset, bytes);
        if (isHeader(bits, bytes, COMMENT))
        {
            bits.skipBytes(bits.read(BITS_32));
            final long comments = bits.read(BITS_32);
            for (long i = 0; i < comments; i++)
            {
                bits.skipBytes(bits.read(BITS_32));
            }
        }
    }

    /**
     * Checks the codebooks, floors and residues of a setup header.
     *
     * @param data The bytes holding the packet.
     * @param offset The index of its first byte.
     * @param bytes The bytes of the packet.
     * @param longBlock The long block size of the identification header.
     * @param channels The channels of the identification header.
     * @throws AudioDecodingException If the header is corrupted or above the bounds of the class.
     */
    static void checkSetup(byte[] data, int offset, int bytes, int longBlock, int channels)
    {
        final VorbisBits bits = new VorbisBits(data, offset, bytes);
        if (isHeader(bits, bytes, SETUP))
        {
            final int books = (int) bits.read(BITS_8) + 1;
            final int[] entries = new int[books];
            final int[] dimensions = new int[books];
            long totalEntries = 0L;
            long totalValues = 0L;
            for (int book = 0; book < books; book++)
            {
                checkCodebook(bits, entries, dimensions, book);
                totalEntries += entries[book];
                totalValues += (long) entries[book] * dimensions[book];
            }
            if (totalEntries > MAX_ENTRIES || totalValues > MAX_VALUES)
            {
                throw new AudioDecodingException(OggPackets.ERROR_UNSUPPORTED + "codebooks of " + totalEntries
                                                 + " entries and " + totalValues + " values, limit " + MAX_ENTRIES);
            }
            checkTime(bits);
            checkFloors(bits, books);
            checkResidues(bits, entries, dimensions, longBlock / 2, channels);
        }
    }

    /**
     * Checks a codebook and skips its content.
     *
     * @param bits The packet, at the codebook.
     * @param entries The entries of each codebook, receiving those of this one.
     * @param dimensions The dimensions of each codebook, receiving those of this one.
     * @param book The index of the codebook.
     * @throws AudioDecodingException If the codebook is corrupted or too large.
     */
    private static void checkCodebook(VorbisBits bits, int[] entries, int[] dimensions, int book)
    {
        if (bits.read(BITS_24) != BOOK_SYNC)
        {
            throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + CODEBOOK + book + " without its pattern");
        }
        final int dimension = (int) bits.read(BITS_16);
        final int count = (int) bits.read(BITS_24);
        if (dimension == 0 || count == 0 || ilog(dimension) + ilog(count) > MAX_BOOK_BITS)
        {
            throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + CODEBOOK + book + " of " + count
                                             + " entries of " + dimension + " dimensions");
        }
        entries[book] = count;
        dimensions[book] = dimension;
        if (bits.read(1) == 0L)
        {
            skipLengths(bits, count);
        }
        else
        {
            checkOrderedLengths(bits, count, book);
        }
        skipLookup(bits, count, dimension, book);
    }

    /**
     * Skips the codeword lengths of an unordered codebook.
     *
     * @param bits The packet, at the lengths.
     * @param count The entries of the codebook.
     */
    private static void skipLengths(VorbisBits bits, int count)
    {
        final boolean sparse = bits.read(1) != 0L;
        for (int entry = 0; entry < count; entry++)
        {
            if (!sparse || bits.read(1) != 0L)
            {
                bits.skip(BITS_5);
            }
        }
    }

    /**
     * Checks the codeword lengths of an ordered codebook.
     *
     * @param bits The packet, at the lengths.
     * @param count The entries of the codebook.
     * @param book The index of the codebook.
     * @throws AudioDecodingException If the lengths go past the entries or 32 bits.
     */
    private static void checkOrderedLengths(VorbisBits bits, int count, int book)
    {
        int length = (int) bits.read(BITS_5) + 1;
        long entry = 0L;
        while (entry < count)
        {
            entry += bits.read(ilog(count - (int) entry));
            if (entry > count || length > MAX_LENGTH)
            {
                throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + CODEBOOK + book + " lengths");
            }
            length++;
        }
    }

    /**
     * Skips the vector lookup of a codebook.
     *
     * @param bits The packet, at the lookup.
     * @param count The entries of the codebook.
     * @param dimension The dimensions of the codebook.
     * @param book The index of the codebook.
     * @throws AudioDecodingException If the lookup type is unknown, or the values go past the packet.
     */
    private static void skipLookup(VorbisBits bits, int count, int dimension, int book)
    {
        final long type = bits.read(BITS_4);
        if (type == 1L || type == 2L)
        {
            bits.skip(LOOKUP_RANGE);
            final long valueBits = bits.read(BITS_4) + 1L;
            bits.skip(1);
            final long values = type == 1L ? lookup1(count, dimension) : (long) count * dimension;
            bits.skip(values * valueBits);
        }
        else if (type != 0L)
        {
            throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + CODEBOOK + book + " lookup type " + type);
        }
    }

    /**
     * Skips the time domain transforms, all placeholders.
     *
     * @param bits The packet, at the transforms.
     * @throws AudioDecodingException If a transform is not zero.
     */
    private static void checkTime(VorbisBits bits)
    {
        final int count = (int) bits.read(BITS_6) + 1;
        for (int i = 0; i < count; i++)
        {
            if (bits.read(BITS_16) != 0L)
            {
                throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + "time domain transform");
            }
        }
    }

    /**
     * Checks the floors.
     *
     * @param bits The packet, at the floors.
     * @param books The number of codebooks.
     * @throws AudioDecodingException If a floor is corrupted or names a missing codebook.
     */
    private static void checkFloors(VorbisBits bits, int books)
    {
        final int count = (int) bits.read(BITS_6) + 1;
        for (int i = 0; i < count; i++)
        {
            final long type = bits.read(BITS_16);
            if (type == 0L)
            {
                checkFloor0(bits, books);
            }
            else if (type == 1L)
            {
                checkFloor1(bits, books);
            }
            else
            {
                throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + "floor type " + type);
            }
        }
    }

    /**
     * Checks a floor of type 0.
     *
     * @param bits The packet, after the floor type.
     * @param books The number of codebooks.
     * @throws AudioDecodingException If the floor is corrupted or names a missing codebook.
     */
    private static void checkFloor0(VorbisBits bits, int books)
    {
        final long order = bits.read(BITS_8);
        final long rate = bits.read(BITS_16);
        final long bark = bits.read(BITS_16);
        bits.skip(FLOOR0_AMPLITUDE);
        if (order == 0L || rate == 0L || bark == 0L)
        {
            throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + "floor 0 parameters");
        }
        final int count = (int) bits.read(BITS_4) + 1;
        for (int i = 0; i < count; i++)
        {
            checkBook(bits.read(BITS_8), books);
        }
    }

    /**
     * Checks a floor of type 1.
     *
     * @param bits The packet, after the floor type.
     * @param books The number of codebooks.
     * @throws AudioDecodingException If the floor is corrupted, names a missing codebook or has too many points.
     */
    private static void checkFloor1(VorbisBits bits, int books)
    {
        final int partitions = (int) bits.read(BITS_5);
        final int[] classes = new int[partitions];
        int maxClass = -1;
        for (int i = 0; i < partitions; i++)
        {
            classes[i] = (int) bits.read(BITS_4);
            maxClass = Math.max(maxClass, classes[i]);
        }
        final int[] classDimensions = new int[maxClass + 1];
        for (int i = 0; i <= maxClass; i++)
        {
            classDimensions[i] = (int) bits.read(BITS_3) + 1;
            final int subclasses = (int) bits.read(MULTIPLIER);
            if (subclasses != 0)
            {
                checkBook(bits.read(BITS_8), books);
            }
            for (int j = 0; j < 1 << subclasses; j++)
            {
                checkBook(bits.read(BITS_8) - 1L, books);
            }
        }
        bits.skip(MULTIPLIER);
        final int rangeBits = (int) bits.read(BITS_4);
        int points = 0;
        for (final int partitionClass : classes)
        {
            points += classDimensions[partitionClass];
        }
        if (points > MAX_FLOOR_POINTS)
        {
            throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + "floor 1 of " + points + " points");
        }
        bits.skip((long) points * rangeBits);
    }

    /**
     * Checks the residues.
     *
     * @param bits The packet, at the residues.
     * @param entries The entries of each codebook.
     * @param dimensions The dimensions of each codebook.
     * @param vector The size of the residue vector of a channel in a long block: half the block.
     * @param channels The channels.
     * @throws AudioDecodingException If a residue is corrupted, names a missing codebook or goes past its vector.
     */
    private static void checkResidues(VorbisBits bits, int[] entries, int[] dimensions, int vector, int channels)
    {
        final int count = (int) bits.read(BITS_6) + 1;
        for (int i = 0; i < count; i++)
        {
            final long type = bits.read(BITS_16);
            final long begin = bits.read(BITS_24);
            final long end = bits.read(BITS_24);
            bits.skip(BITS_24);
            final long limit = type == MAX_RESIDUE ? (long) vector * channels : vector;
            if (type > MAX_RESIDUE || begin > end || end > limit)
            {
                throw new AudioDecodingException(OggPackets.ERROR_UNSUPPORTED + "residue " + type + " from " + begin
                                                 + " to " + end + ", vector of " + limit);
            }
            final int classifications = (int) bits.read(BITS_6) + 1;
            final int book = checkBook(bits.read(BITS_8), entries.length);
            checkClassifications(classifications, entries[book], dimensions[book]);
            int stages = 0;
            for (int j = 0; j < classifications; j++)
            {
                int cascade = (int) bits.read(BITS_3);
                if (bits.read(1) != 0L)
                {
                    cascade |= (int) bits.read(BITS_5) << BITS_3;
                }
                stages += Integer.bitCount(cascade);
            }
            for (int j = 0; j < stages; j++)
            {
                checkBook(bits.read(BITS_8), entries.length);
            }
        }
    }

    /**
     * Checks that the classifications of a residue fit its codebook, as {@code libvorbis}: their count to the power of
     * the dimensions of the codebook is not above its entries.
     *
     * @param classifications The classifications of the residue.
     * @param entries The entries of its codebook.
     * @param dimension The dimensions of its codebook.
     * @throws AudioDecodingException If they do not fit.
     */
    private static void checkClassifications(int classifications, int entries, int dimension)
    {
        long values = 1L;
        for (int i = 0; i < dimension; i++)
        {
            values *= classifications;
            if (values > entries)
            {
                throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + "residue classifications");
            }
        }
    }

    /**
     * Checks that a codebook exists.
     *
     * @param book The index of the codebook; {@code -1} names no codebook and is accepted.
     * @param books The number of codebooks.
     * @return The index.
     * @throws AudioDecodingException If the codebook does not exist.
     */
    private static int checkBook(long book, int books)
    {
        if (book >= books)
        {
            throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + "missing codebook " + book);
        }
        return (int) book;
    }

    /**
     * Returns the number of bits of a value.
     *
     * @param value The value (not negative).
     * @return The position of its highest bit set, {@code 0} for {@code 0}.
     */
    private static int ilog(int value)
    {
        return Integer.SIZE - Integer.numberOfLeadingZeros(value);
    }

    /**
     * Returns the values of a lookup of type 1: the largest integer whose power of the dimensions is not above the
     * entries.
     *
     * @param entries The entries (positive).
     * @param dimension The dimensions (positive).
     * @return The values.
     */
    private static long lookup1(int entries, int dimension)
    {
        long values = (long) Math.floor(Math.pow(entries, 1.0 / dimension));
        while (power(values + 1L, dimension) <= entries)
        {
            values++;
        }
        while (values > 1L && power(values, dimension) > entries)
        {
            values--;
        }
        return values;
    }

    /**
     * Returns a value to a power, saturated above {@link Integer#MAX_VALUE}.
     *
     * @param value The value (positive).
     * @param exponent The exponent (positive).
     * @return The power, or a value above {@link Integer#MAX_VALUE} if it is larger.
     */
    private static long power(long value, int exponent)
    {
        long result = 1L;
        for (int i = 0; i < exponent && result <= Integer.MAX_VALUE; i++)
        {
            result *= value;
        }
        return result;
    }

    /**
     * Private constructor.
     */
    private VorbisHeaders()
    {
        throw new AssertionError();
    }
}
