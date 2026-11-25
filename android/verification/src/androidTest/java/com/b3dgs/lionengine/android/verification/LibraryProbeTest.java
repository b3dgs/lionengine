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
package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

/**
 * Java library APIs called from code compiled at level 21 and dexed for API 21 with core library desugaring
 * (ADR-0044, docs/android-compatibility.md).
 *
 * <p>
 * Each probe is a separate class, so that a missing class or method fails its own test only: on API 21 the call
 * throws a {@link LinkageError} ({@code NoSuchMethodError}, {@code NoClassDefFoundError}) when it runs. A probe of an
 * API that works asserts its result; a probe of an API that does not exist on API 21 asserts the failure (the tests
 * named {@code ...Fails} and {@code ...Missing}), so that the tables of docs/android-compatibility.md stay true when the
 * toolchain changes: a failure that disappears fails the test and the tables are updated. {@code VarHandle} has no probe: its
 * signature-polymorphic calls fail the D8 build below API 26.
 * </p>
 */
@RunWith(AndroidJUnit4.class)
public final class LibraryProbeTest
{
    /** Text of the probes. */
    private static final String TEXT = "a\nb";

    /**
     * Probe of the immutable collection factories (verified by the owner).
     */
    private static final class Factories
    {
        static Object run()
        {
            return List.of(1, 2).size() + Set.of(3).size() + Map.of("k", 4).get("k");
        }
    }

    /**
     * Probe of the immutable copies {@code List.copyOf}, {@code Set.copyOf}, {@code Map.copyOf}.
     */
    private static final class CopyOf
    {
        static Object run()
        {
            return List.copyOf(Arrays.asList(1, 2)).size() + Set.copyOf(Arrays.asList(3)).size()
                   + Map.copyOf(Map.of("k", 4)).get("k");
        }
    }

    /**
     * Probe of {@code Stream.toList()} and {@code Collectors} (streams come from desugar_jdk_libs below API 24).
     */
    private static final class Streams
    {
        static Object run()
        {
            return Stream.of(1, 2, 3).map(i -> i * 2).toList() + " "
                   + Stream.of("a", "b").collect(Collectors.joining(","));
        }
    }

    /**
     * Probe of {@code Collectors.teeing} (Java 12).
     */
    private static final class Teeing
    {
        static Object run()
        {
            return Stream.of(1, 2, 3).collect(Collectors.teeing(Collectors.counting(),
                                                                 Collectors.summingInt(i -> i),
                                                                 (count, sum) -> count + "/" + sum));
        }
    }

    /**
     * Probe of {@code Math.clamp} (Java 21, verified by the owner).
     */
    private static final class Clamp
    {
        static Object run()
        {
            return Math.clamp(5L, 0, 3) + Math.clamp(-1L, 0, 3);
        }
    }

    /**
     * Probe of {@code String.isBlank}, {@code strip} and {@code repeat} (Java 11).
     */
    private static final class StringMethods
    {
        static Object run()
        {
            return " \t".isBlank() + " " + " x ".strip() + "ab".repeat(2);
        }
    }

    /**
     * Probe of {@code String.formatted} (Java 15).
     */
    private static final class Formatted
    {
        static Object run()
        {
            return "%d-%s".formatted(1, "a");
        }
    }

    /**
     * Probe of {@code java.util.function}, {@code Optional} and {@code java.time} (desugar_jdk_libs below API 24 and 26).
     */
    private static final class Desugared
    {
        static Object run()
        {
            final java.util.function.IntUnaryOperator twice = i -> i * 2;
            return twice.applyAsInt(java.util.Optional.of(2).map(i -> i + 1).orElse(0)) + " "
                   + java.time.Duration.ofMillis(1500).getSeconds();
        }
    }

    /**
     * Probe of {@code java.util.zip} (PNG decoder and Tiled loader, docs/android-compatibility.md).
     */
    private static final class Zip
    {
        static Object run()
        {
            final byte[] data = "zip zip zip zip".getBytes(StandardCharsets.UTF_8);
            final Deflater deflater = new Deflater();
            deflater.setInput(data);
            deflater.finish();
            final byte[] packed = new byte[64];
            final int packedLength = deflater.deflate(packed);
            deflater.end();
            final Inflater inflater = new Inflater();
            inflater.setInput(packed, 0, packedLength);
            final byte[] unpacked = new byte[data.length];
            try
            {
                inflater.inflate(unpacked);
            }
            catch (final java.util.zip.DataFormatException exception)
            {
                throw new IllegalStateException(exception);
            }
            finally
            {
                inflater.end();
            }
            final CRC32 crc = new CRC32();
            crc.update(unpacked, 0, unpacked.length);
            return new String(unpacked, StandardCharsets.UTF_8) + " " + Long.toHexString(crc.getValue()) + " "
                   + gzip(data).length;
        }

        private static byte[] gzip(byte[] data)
        {
            try
            {
                final ByteArrayOutputStream packed = new ByteArrayOutputStream();
                try (GZIPOutputStream output = new GZIPOutputStream(packed))
                {
                    output.write(data);
                }
                final ByteArrayOutputStream unpacked = new ByteArrayOutputStream();
                try (InputStream input = new GZIPInputStream(new ByteArrayInputStream(packed.toByteArray())))
                {
                    int read = input.read();
                    while (read >= 0)
                    {
                        unpacked.write(read);
                        read = input.read();
                    }
                }
                return unpacked.toByteArray();
            }
            catch (final IOException exception)
            {
                throw new UncheckedIOException(exception);
            }
        }
    }

    /**
     * Probe of {@code ArrayDeque} as a {@code Deque} (API 9): the {@code addFirst} family of {@code Deque} is not the
     * sequenced-collection one.
     */
    private static final class DequeMethods
    {
        static Object run()
        {
            final Deque<Integer> deque = new ArrayDeque<>();
            deque.addFirst(1);
            deque.addLast(2);
            return deque.removeFirst() + deque.removeLast();
        }
    }

    /**
     * Probe of {@code java.util.Base64} (API 26).
     */
    private static final class Base64Probe
    {
        static Object run()
        {
            return java.util.Base64.getEncoder().encodeToString(new byte[] {1, 2, 3});
        }
    }

    /**
     * Probe of {@code SplittableRandom} (API 24).
     */
    private static final class SplittableRandomProbe
    {
        static Object run()
        {
            return Integer.valueOf(new java.util.SplittableRandom(1L).nextInt(1, 2));
        }
    }

    /**
     * Probe of {@code java.lang.ref.Cleaner} (API 33).
     */
    private static final class CleanerProbe
    {
        static Object run()
        {
            return java.lang.ref.Cleaner.create().register(new Object(), () ->
            {
                // Nothing to release.
            });
        }
    }

    /**
     * Probe of {@code java.nio.file} (API 26).
     */
    private static final class NioFiles
    {
        static Object run()
        {
            final File folder = InstrumentationRegistry.getInstrumentation().getTargetContext().getCacheDir();
            try
            {
                final java.nio.file.Path path = java.nio.file.Paths.get(folder.getPath(), "nio.txt");
                java.nio.file.Files.write(path, TEXT.getBytes(StandardCharsets.UTF_8));
                final String read = new String(java.nio.file.Files.readAllBytes(path), StandardCharsets.UTF_8);
                java.nio.file.Files.delete(path);
                return read;
            }
            catch (final IOException exception)
            {
                throw new UncheckedIOException(exception);
            }
        }
    }

    /**
     * Probe of {@code InputStream.readAllBytes} (Java 9, API 33).
     */
    private static final class ReadAllBytes
    {
        static Object run()
        {
            try
            {
                final InputStream input = new ByteArrayInputStream(new byte[3]);
                return Integer.valueOf(input.readAllBytes().length);
            }
            catch (final IOException exception)
            {
                throw new UncheckedIOException(exception);
            }
        }
    }

    /**
     * Probe of {@code CharSequence.isEmpty} called on a {@code StringBuilder} (Java 15, API 35), the call that lint
     * NewApi found in DisplayState.
     */
    private static final class CharSequenceIsEmpty
    {
        static Object run()
        {
            return Boolean.valueOf(new StringBuilder().isEmpty());
        }
    }

    /**
     * Probe of {@code String.lines} (Java 11; forbidden, ADR-0044).
     */
    private static final class Lines
    {
        static Object run()
        {
            return Long.valueOf(TEXT.lines().count());
        }
    }

    /**
     * Probe of {@code java.util.HexFormat} (Java 17; forbidden, ADR-0044).
     */
    private static final class Hex
    {
        static Object run()
        {
            return java.util.HexFormat.of().formatHex(new byte[] {1});
        }
    }

    /**
     * Probe of the sequenced-collection reads of {@code List} (Java 21; forbidden, ADR-0044).
     */
    private static final class SequencedRead
    {
        static Object run()
        {
            final List<Integer> list = new ArrayList<>(Arrays.asList(1, 2));
            return list.getFirst() + list.getLast() + list.reversed().get(0);
        }
    }

    /**
     * Probe of the sequenced-collection writes of {@code List} (Java 21; avoided as a precaution, ADR-0044).
     */
    private static final class SequencedListWrite
    {
        static Object run()
        {
            final List<Integer> list = new ArrayList<>();
            list.addFirst(1);
            list.addLast(2);
            return list.removeFirst() + list.removeLast();
        }
    }

    /**
     * Probe of the sequenced-collection writes of {@code LinkedHashSet} (Java 21; avoided as a precaution, ADR-0044).
     */
    private static final class SequencedSetWrite
    {
        static Object run()
        {
            final LinkedHashSet<Integer> set = new LinkedHashSet<>();
            set.addFirst(1);
            return set.removeFirst();
        }
    }

    /**
     * Creates the test.
     */
    public LibraryProbeTest()
    {
        super();
    }

    /**
     * {@code List.of}, {@code Set.of}, {@code Map.of}.
     */
    @Test
    public void testFactories()
    {
        assertEquals(Integer.valueOf(7), Factories.run());
    }

    /**
     * {@code List.copyOf}, {@code Set.copyOf}, {@code Map.copyOf}.
     */
    @Test
    public void testCopyOf()
    {
        assertEquals(Integer.valueOf(7), CopyOf.run());
    }

    /**
     * {@code Stream.toList()} and {@code Collectors.joining}.
     */
    @Test
    public void testStreams()
    {
        assertEquals("[2, 4, 6] a,b", Streams.run());
    }

    /**
     * {@code Collectors.teeing} is missing from desugar_jdk_libs 2.1.5: {@code NoSuchMethodError}.
     */
    @Test
    public void testTeeingMissing()
    {
        assertThrows(LinkageError.class, Teeing::run);
    }

    /**
     * {@code Math.clamp}.
     */
    @Test
    public void testClamp()
    {
        assertEquals(Integer.valueOf(3), Clamp.run());
    }

    /**
     * {@code String.isBlank}, {@code strip}, {@code repeat}.
     */
    @Test
    public void testStringMethods()
    {
        assertEquals("true xabab", StringMethods.run());
    }

    /**
     * {@code String.formatted} is missing on API 21: {@code NoSuchMethodError}.
     */
    @Test
    public void testFormattedMissing()
    {
        assertThrows(LinkageError.class, Formatted::run);
    }

    /**
     * {@code java.util.function}, {@code Optional}, {@code java.time} through core library desugaring.
     */
    @Test
    public void testDesugared()
    {
        assertEquals("6 1", Desugared.run());
    }

    /**
     * {@code Deflater}, {@code Inflater}, {@code CRC32}, {@code GZIPInputStream}.
     */
    @Test
    public void testZip()
    {
        assertEquals("zip zip zip zip 8d3b2554 15", Zip.run());
    }

    /**
     * {@code Deque.addFirst} family on {@code ArrayDeque}.
     */
    @Test
    public void testDeque()
    {
        assertEquals(Integer.valueOf(3), DequeMethods.run());
    }

    /**
     * {@code java.util.Base64}.
     */
    @Test
    public void testBase64()
    {
        assertEquals("AQID", Base64Probe.run());
    }

    /**
     * {@code SplittableRandom} is missing on API 21: {@code NoClassDefFoundError}.
     */
    @Test
    public void testSplittableRandomMissing()
    {
        assertThrows(LinkageError.class, SplittableRandomProbe::run);
    }

    /**
     * {@code Cleaner} is missing on API 21: {@code NoClassDefFoundError}.
     */
    @Test
    public void testCleanerMissing()
    {
        assertThrows(LinkageError.class, CleanerProbe::run);
    }

    /**
     * {@code java.nio.file} is missing on API 21 and not desugared: {@code NoClassDefFoundError}.
     */
    @Test
    public void testNioFilesMissing()
    {
        assertThrows(LinkageError.class, NioFiles::run);
    }

    /**
     * {@code InputStream.readAllBytes} is missing on API 21: {@code NoSuchMethodError}.
     */
    @Test
    public void testReadAllBytesMissing()
    {
        assertThrows(LinkageError.class, ReadAllBytes::run);
    }

    /**
     * {@code CharSequence.isEmpty} on a {@code StringBuilder} is missing on API 21: {@code NoSuchMethodError}.
     */
    @Test
    public void testCharSequenceIsEmptyMissing()
    {
        assertThrows(LinkageError.class, CharSequenceIsEmpty::run);
    }

    /**
     * {@code String.lines} fails on API 21 (forbidden list).
     */
    @Test
    public void testLinesFails()
    {
        assertThrows(LinkageError.class, Lines::run);
    }

    /**
     * {@code HexFormat} fails on API 21 (forbidden list).
     */
    @Test
    public void testHexFormatFails()
    {
        assertThrows(LinkageError.class, Hex::run);
    }

    /**
     * {@code getFirst}, {@code getLast}, {@code reversed} on a {@code List} fail on API 21 (forbidden list).
     */
    @Test
    public void testSequencedReadFails()
    {
        assertThrows(LinkageError.class, SequencedRead::run);
    }

    /**
     * {@code addFirst}, {@code addLast}, {@code removeFirst}, {@code removeLast} on a {@code List} are missing on API 21:
     * {@code NoSuchMethodError}.
     */
    @Test
    public void testSequencedListWriteMissing()
    {
        assertThrows(LinkageError.class, SequencedListWrite::run);
    }

    /**
     * {@code addFirst}, {@code removeFirst} on a {@code LinkedHashSet} are missing on API 21: {@code NoSuchMethodError}.
     */
    @Test
    public void testSequencedSetWriteMissing()
    {
        assertThrows(LinkageError.class, SequencedSetWrite::run);
    }
}
