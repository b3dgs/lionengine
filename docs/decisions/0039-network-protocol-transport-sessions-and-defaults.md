# ADR-0039 — Network protocol, transport, sessions, players, chat and defaults

- Status: ACCEPTED
- Date: 2026-11-23
- Decision makers: project owner (need: "join from IP, client name, no need for security for now but it should be possible to add it later without breaking everything; list clients; internal chat; other classic standard features"); design decided by the agent under the owner's explicit delegation of 2026-11-23; reversible by a superseding ADR
- Related requirements: `REQ-NET-001` to `REQ-NET-017`, `REQ-MP-004`, `REQ-RT-001`, `REQ-RT-006`, `REQ-TECH-006`, `REQ-TECH-007`, `REQ-DEF-003`
- Open questions resolved: `OQ-165` (with [ADR-0038](0038-network-model-server-authority-snapshots-prediction.md))
- Supersedes / superseded by: none; details the `network` module of ADR-0038

## Context

ADR-0038 fixes the model (server authority, snapshots, prediction) and creates the `network` module. This ADR fixes what that module puts on the wire and offers to a game: transport, packet format, reliability, joining, players, chat, game messages, measurements, discovery, the security baseline and the default values, so that the batches need no further decision.

## Decision

### 1. Transport

- **UDP** datagrams. One non-blocking `java.nio.channels.DatagramChannel` per session, polled on the runtime thread by the session update (no thread, `REQ-RT-001`). Only calls available at Android API 21 are used (`DatagramChannel.open()`, `socket().bind(...)`, `configureBlocking(false)`, `receive`, `send`; not `DatagramChannel.bind`, API 24). TCP is not used: one socket for everything, no head-of-line blocking.
- A `Transport` contract (send a datagram to an address, poll received datagrams) with three implementations: `UdpTransport`; an in-process **loopback** (`LoopbackNetwork` creates linked transports with no socket, used by the host's local client and by tests); a **conditioned** wrapper that adds latency, jitter, loss, duplication and reordering from a seeded generator (tests and debugging of a real game).
- IPv4 and IPv6. Version 1 joins an IP literal and a port (no DNS lookup, which would block the runtime thread); host names are a later addition through the platform worker pool (ADR-0031). Default port **19880**, configurable in `[1024, 65535]`; a client binds an ephemeral port. Port forwarding for internet play is the host player's business.
- Datagram payload at most **1,200 bytes** by default (configurable in `[512, 1472]`), below common path MTUs; larger content is fragmented (section 3).
- A monotonic time source (`NetworkTime`, default `System.nanoTime`) is given to the session; tests use a controllable one. Network timing (resends, timeouts, round trip) uses it, never step counts or wall-clock dates.

### 2. Packet format

Big-endian. Header (21 bytes):

| Field | Size | Meaning |
|---|---|---|
| checksum | 4 | CRC32 of the protocol id (`0x4C494F4E`), the protocol version and the rest of the packet; the protocol id and version are not sent. A packet of another game, another version or corrupted is dropped silently and counted. |
| type | 1 | `INFO_REQUEST`, `INFO_RESPONSE`, `CONNECT_REQUEST`, `CONNECT_ACCEPT`, `CONNECT_REJECT`, `DATA`, `DISCONNECT` |
| session token | 8 | random 64-bit value given at acceptance; `0` before |
| sequence | 2 | packet sequence number, wrapping (`uint16`, compared with wrap-around) |
| ack, ack bits | 2 + 4 | last sequence received and a 32-bit field for the 32 before it |

The protocol version starts at 1. Malformed packets (reading past the end, a value out of its range, an unknown type) are dropped and counted; they never throw into game code.

### 3. Channels

A `DATA` packet carries, after the header, entries of two channels:

- **Unreliable-sequenced**: newest wins; an entry older than the last delivered one of the same stream is dropped. Used by input frames and snapshots (ADR-0038).
- **Reliable-ordered**: messages numbered by a `uint16` id, delivered once and in order. A sent message is repeated in later packets until a packet carrying it is acknowledged, at most once every 0.1 s; at most 256 messages in flight; the send queue holds at most 1,024 messages, beyond which the connection is closed with `OVERFLOW`.
- Messages up to **64 KiB** and snapshots up to the snapshot limit of `multiplayer` are **fragmented** to the datagram size and reassembled; a snapshot missing a fragment is simply superseded by a later one.
- When nothing was sent for 0.1 s, an empty `DATA` packet (keepalive) is sent so that acknowledgements, round trip and liveness keep flowing.

### 4. Joining

1. The client sends `CONNECT_REQUEST` with: protocol version, game id (registry form `namespace:name`, ADR-0032), game version (`int`), content hash (ADR-0038), player name, opaque authentication data (empty by default, at most 512 bytes) and a client nonce. The request is padded to the maximum datagram size, so that no answer is larger than the request (no amplification). It is resent every 0.5 s; the attempt fails with `TIMEOUT` after 10 s.
2. The server checks, in this order, and refuses with the first failing reason: `PROTOCOL_MISMATCH`, `GAME_MISMATCH` (id or version), `CONTENT_MISMATCH`, `NOT_JOINABLE` (the server closed joining, for example during a match that does not accept late joiners), `SERVER_FULL`, `NAME_INVALID`, `NAME_TAKEN`, `AUTH_FAILED` (refused by the `Authenticator` callback, default accept, which may add a text).
3. `CONNECT_ACCEPT` gives: the player id, the session token, the server tick, the step rate, the snapshot interval, the server name and the player list; then the normal flow starts (and, with `multiplayer`, a complete snapshot). A repeated request from an accepted or rejected address receives the same answer again.
4. At most one pending connection per source address; packets whose token or address does not match a connection are dropped. A client whose address changes (NAT rebinding) times out and joins again; reconnection to the same slot is a later candidate.

### 5. Liveness and leaving

- Timeout: no packet for **10 s** (configurable in `[1, 60]` s) closes the connection with `TIMEOUT`.
- A graceful leave sends `DISCONNECT` three times with a reason: `QUIT`, `KICKED` (with the text given by the server), `SERVER_SHUTDOWN`, `TIMEOUT`, `OVERFLOW`, `PROTOCOL_ERROR`.
- Client connection states: `DISCONNECTED`, `CONNECTING`, `CONNECTED`; every change is reported to a `ConnectionListener` with its reason on the runtime thread.

### 6. Players

- A player has an **id** (`int` from 1, never reused within a session; the server itself is `0`), a **name** (1 to 24 characters after trimming, no control characters, unique ignoring case with `Locale.ROOT`), a **host** flag (the host's local player), a measured **round trip** and **properties**.
- **Properties** are game-defined text key-value pairs (at most 16 per player, key `[a-z][a-z0-9_-]*` up to 32 characters, value up to 128 characters), for example team, color, character or a ready flag for a lobby. The server sets them, or a player requests a change that the server's `PropertyValidator` accepts (default: accept). Changes are sent reliably to everyone.
- The **player list** is the same on every machine, ordered by id. Joined, left (with reason) and changed events reach every peer through a `PlayerListener` on the runtime thread. The server sends every player's round trip to everyone once per second.
- Default maximum: **16** players including the host's player, configurable in `[1, 64]`. One player per connection in this version (several local players on one online client is a later candidate).
- Server administration: kick a player with a text, open or close joining, set the server name, stop the server (`SERVER_SHUTDOWN`). A player can request a name change, checked like a new name.

### 7. Chat

- A chat message has a sender id (`0` for a server message), the sender's name at sending time, a target (everyone or one player id), a text of 1 to 256 characters and the server tick of delivery.
- Clients send chat to the server reliably; the server passes it to a `ChatFilter` (default: unchanged; it may change the text or drop the message) and relays it to its targets. A client may send at most 5 messages in a burst, then 1 per second (token bucket); excess messages are dropped and the sender is told.
- Chat and player events are separate: the game formats "X joined" lines itself from player events.

### 8. Game messages

- A game registers message types by registry id (`namespace:name`) with a `MessageCodec<T>` (write and read through `NetWriter` and `NetReader`) and a delivery: `RELIABLE` (ordered) or `UNRELIABLE` (sequenced). The registration order and ids are part of the content hash.
- Directions: client to server; server to one player, to everyone, or to everyone but one. A `MessageListener<T>` receives the message and its sender id on the runtime thread, during the session update.
- Size: at most 64 KiB for a reliable message, one datagram for an unreliable one; larger is an `IllegalArgumentException` at sending.

### 9. Wire encoding

`NetWriter` and `NetReader` work over preallocated byte arrays: bytes, booleans, fixed-size `int` and `long`, variable-length integers (LEB128, zigzag for signed), `float` and `double` as their exact IEEE bits, UTF-8 strings with a length prefix and a maximum length checked on read. No reflection and no Java serialization (ADR-0020).

### 10. Measurements

Per connection, readable at any time without allocation (`ConnectionStats`): smoothed round trip in seconds (from acknowledged packets, exponential smoothing factor 0.1), packet loss over the last 64 sent packets, bytes per second sent and received (smoothed), counters of dropped packets by cause, of resent reliable messages and of connection attempts.

### 11. Server information and LAN discovery

- `INFO_REQUEST` (padded like a connect request) is answered by `INFO_RESPONSE`: server name, game id and version, protocol version, player count, maximum, joinable flag. A client can query a known address before joining ("ping a server").
- **LAN discovery** broadcasts `INFO_REQUEST` to `255.255.255.255` at a port (default the game port) and collects answers for a duration (default 1 s). On Android, a device that hosts must hold the Wi-Fi multicast lock to receive broadcasts; `platform-android` provides it as a capability (ADR-0020). Internet server lists and matchmaking are not provided.

### 12. Security baseline and extension points

No encryption and no authentication in this version: it targets LAN and friends. Already in place: server authority (ADR-0038), random session tokens from `java.security.SecureRandom`, address and token checks, anti-amplification padding, one pending connection per address, bounds checks on every read, chat rate limit. Security can be added later **without changing the session API**:

- the protocol version is in every checksum, so a secure protocol is a new version;
- the connect request carries opaque authentication data checked by the `Authenticator` callback (passwords, tokens of an external service);
- a `PacketProtection` stage sits between the encoded packet and the transport; version 1 uses the identity; a later version can encrypt and authenticate packets (for example AES-GCM through `javax.crypto`, available on Android) with keys agreed during the join.

Such a secure mode is decided by a new ADR.

### 13. Not provided

NAT traversal, relays, matchmaking, internet server lists, host migration, reconnection to the same slot, voice, encryption (until a new ADR), anti-cheat beyond server authority, bandwidth caps per client.

## Options considered

- Transport: UDP polled on the runtime thread (chosen) vs a receiving thread per socket (contradicts "the engine starts no thread") vs TCP for reliable data plus UDP (two connections, head-of-line blocking, NAT handled twice) vs a third-party library (a dependency and its Android record for something small to write).
- Integrity: salted CRC32 (chosen; `java.util.zip.CRC32`, API 1) vs no checksum (UDP's own checksum is weak and optional over IPv4) vs a cryptographic MAC now (security deferred by the owner).
- Join: one request with all checks (chosen) vs a challenge-response round trip (more robust against spoofing; can arrive with a secure protocol version).
- Player ids: never reused in a session (chosen; late packets and owned objects cannot be confused with a newcomer) vs lowest free id (small numbers, risk of confusion).

## Consequences

- Positive consequences: one small, testable protocol with clear limits; every value a batch needs is stated; security can be added by a protocol version without breaking games.
- Costs / constraints: own reliability code to write and test; IP literals only at first; LAN discovery on Android needs a platform capability; no protection against a hostile network until the secure version.
- Impact on modules and APIs: `network` (all of the above); `platform-android` (multicast lock capability, with the discovery batch).
- Impact on tests and documentation (including Android/DEX compatibility): unit tests of every rule and limit; a transport contract suite run on loopback and UDP; `android-compatibility.md` lists `DatagramChannel` at API 21, `CRC32`, `SecureRandom`, the `INTERNET` permission and the multicast lock.

## Validation

Need stated by the project owner on 2026-11-23; protocol and defaults decided by the agent under the owner's explicit delegation of the same day; reversible by a superseding ADR.
