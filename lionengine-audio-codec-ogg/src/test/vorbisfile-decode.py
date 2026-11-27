"""Decodes an OGG Vorbis file with libvorbisfile (ov_fopen, ov_read: 16-bit signed little-endian) into a WAV file.

Usage: python3 -I vorbisfile-decode.py <input.ogg> <output.wav>; needs libvorbisfile.so.3 (used by generate-ogg.sh, ADR-0073)."""
import ctypes
import sys
import wave

lib = ctypes.CDLL("libvorbisfile.so.3")
lib.ov_fopen.argtypes = [ctypes.c_char_p, ctypes.c_void_p]
lib.ov_read.argtypes = [ctypes.c_void_p, ctypes.c_char_p, ctypes.c_int, ctypes.c_int, ctypes.c_int, ctypes.c_int,
                        ctypes.POINTER(ctypes.c_int)]
lib.ov_read.restype = ctypes.c_long
lib.ov_info.argtypes = [ctypes.c_void_p, ctypes.c_int]
lib.ov_info.restype = ctypes.c_void_p


class Info(ctypes.Structure):
    _fields_ = [("version", ctypes.c_int), ("channels", ctypes.c_int), ("rate", ctypes.c_long)]


vf = ctypes.create_string_buffer(16384)
if lib.ov_fopen(sys.argv[1].encode(), vf) != 0:
    sys.exit("ov_fopen failed")
info = Info.from_address(lib.ov_info(vf, -1))
out = bytearray()
buffer = ctypes.create_string_buffer(4096)
section = ctypes.c_int()
while True:
    n = lib.ov_read(vf, buffer, 4096, 0, 2, 1, ctypes.byref(section))
    if n < 0:
        sys.exit("ov_read failed: %d" % n)
    if n == 0:
        break
    out += buffer.raw[:n]
with wave.open(sys.argv[2], "wb") as w:
    w.setnchannels(info.channels)
    w.setsampwidth(2)
    w.setframerate(info.rate)
    w.writeframes(bytes(out))
print(sys.argv[1], info.channels, info.rate, len(out) // (2 * info.channels))
