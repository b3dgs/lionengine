#!/bin/bash
# Generates the OGG Vorbis test files of lionengine-audio-codec-ogg and their libvorbisfile decodings (ADR-0073).
# Deterministic: sine tones of the ffmpeg lavfi source, encoded with libvorbis at quality 0, bit-exact flags, no
# metadata, a fixed serial number per file. Verified with ffmpeg 6.1.1 and libvorbis 1.3.7.
# The references (*.reference.wav) are decoded by libvorbisfile itself (ov_read, 16-bit), through vorbisfile-decode.py.
# Usage: src/test/generate-ogg.sh (from lionengine-audio-codec-ogg), then update the provenance date if they change.
set -e
TOOLS="$(cd "$(dirname "$0")" && pwd)"
cd "$TOOLS/resources/com/b3dgs/lionengine/audio/codec/ogg"
F="-map_metadata -1 -fflags +bitexact -flags:a +bitexact"
enc() { ffmpeg -hide_banner -loglevel error -y -f lavfi -i "$1" -ac $2 -ar $3 -t $4 -c:a libvorbis -q:a 0 -serial_offset $5 $F "$6"; }
enc "sine=frequency=440:sample_rate=22050" 1 22050 0.5 101 mono-22050.ogg
enc "sine=frequency=880:sample_rate=48000" 1 48000 0.25 102 mono-48000.ogg
enc "sine=frequency=330:sample_rate=11025" 1 11025 0.5 103 mono-11025.ogg
enc "sine=frequency=523:sample_rate=22050" 1 22050 5 106 long-mono-22050.ogg
ffmpeg -hide_banner -loglevel error -y -f lavfi -i "sine=frequency=440:sample_rate=44100" -f lavfi -i "sine=frequency=660:sample_rate=44100" -filter_complex "[0][1]amerge=inputs=2" -t 0.25 -c:a libvorbis -q:a 0 -serial_offset 104 $F stereo-44100.ogg
ffmpeg -hide_banner -loglevel error -y -f lavfi -i "sine=frequency=440:sample_rate=22050" -f lavfi -i "sine=frequency=550:sample_rate=22050" -f lavfi -i "sine=frequency=660:sample_rate=22050" -filter_complex "[0][1][2]amerge=inputs=3" -t 0.1 -c:a libvorbis -q:a 0 -serial_offset 105 $F three-channels.ogg
for f in mono-22050 mono-48000 mono-11025 stereo-44100; do
  python3 -I "$TOOLS/vorbisfile-decode.py" $f.ogg $f.reference.wav
done
