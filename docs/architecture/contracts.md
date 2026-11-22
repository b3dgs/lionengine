# Cross-cutting contracts

**Status: PROPOSED — conceptual contracts, no Java signature is fixed yet.**

Each public contract must specify: responsibility, invariants, lifecycle, calling thread, units, errors, data ownership, behavior when a capability is absent, and compatibility (including Android/DEX).

## Frontend and rendering

The frontend manages native resources and the platform lifecycle. Rendering receives presentation requests without exposing AWT/OpenGL/Android to the core.

To specify:
- creation, resizing, suspension and closing;
- rendering surface/context and associated thread;
- screen, world and viewport coordinates;
- GPU resources and their invalidation on context loss;
- synchronization between update and render.

## Unit conventions

The world uses `(x, y)` in meters. Durations are in seconds. Speeds are in `m/s`, accelerations in `m/s²`. Any API that converts to pixels must belong to the presentation boundary and state the conversion factor explicitly.

A time value must not be expressed sometimes in milliseconds, sometimes in seconds without explicit conversion. Prefer parameter names that include the unit when the Java type does not remove the ambiguity.

## Game loop

Conceptual contract:
- start, stop, suspend and resume;
- run the simulation against a time reference independent of machine speed;
- trigger rendering separately;
- be able to skip a presentation (frameskipping) without skipping or altering the required simulation calculations;
- extrapolate a state for presentation only;
- expose a lifecycle state;
- allow a deterministic, test-driven implementation.

**Mandatory invariant:** an extrapolated state never becomes the source state of the next simulation update. Extrapolation, including with a factor greater than `1`, must not introduce cumulative error in world calculations.

To define before implementation (OQ-020 to OQ-023): simulation step duration, monotonic clock, target render cadence, extrapolation formula and factor, maximum tolerated lag, catch-up strategy, pause, slow motion, exceptions and shutdown policy. The test loop must allow time to be advanced artificially and exact states to be verified.

## Resources

A resource must be identified independently of its physical location.

Envisaged origins:
- path relative to a resource space;
- filesystem;
- Android assets;
- temporary/memory storage;
- archive;
- stream supplied by the application.

To specify:
- resolution and priority between providers;
- distinction between logical URI and physical path;
- read/write opening;
- stream closing;
- concurrent access;
- caching, invalidation and reloading;
- errors: missing, inaccessible, invalid format, corruption.

Do not assume that a resource is a `File`: a resource in an archive or an Android asset may have no filesystem path.

## Audio

The audio API describes operations and their lifecycle, not the concrete audio engine.

To specify: play, pause, resume, stop, volume, looping, possible spatialization, streaming, latency, codecs and capabilities per backend. WAV/MP3/OGG are target formats, not a guarantee of universal decoding on every platform.

## Images, sprites and animations

Separate as much as necessary:
- image data and modifiable pixels;
- resource/texture consumable by rendering;
- sprite (region, transformation, display state);
- spritesheet (slicing);
- animation (sequence, duration, loop, state).

To specify: pixel format, alpha, mutation, copy/sharing, dimensions, origin, filtering, CPU/GPU synchronization and release.

## Tilemap

Distinguish:
- tileset: tile definitions and image regions;
- grid/map: tile placement;
- layers: order, visibility, properties;
- static content and dynamic modifications.

To specify: coordinates, tile size, missing tiles, layers, chunks, collisions and rendering strategy.

## Objects and traits

The simple model and the ECS model are candidate strategies, not necessarily a single common API (OQ-041, OQ-042).

An object must have an explicit lifecycle. A trait represents a composable capability/behavior and must define its preconditions, its state, its execution order and its interactions.

Envisaged examples: `Movable`, `Gravity`, `Collision`. Their names and semantics remain to be defined. Avoid circular dependencies between traits.

## Input

Normalize physical inputs into events and/or logical states. Distinguish device (keyboard/mouse/gamepad), game action and player.

To specify: mapping, key repeat, analog axes, dead zone, hot-plug, focus/loss of focus, multiple devices and event ordering.

## Multiplayer

Clearly distinguish:
- several local players;
- split-screen (several viewports/cameras);
- network (transport, session, replication/synchronization).

Split-screen must not be confused with networking. Protocol, authority, prediction, rollback and synchronization are outside the contract until decided (OQ-060).
