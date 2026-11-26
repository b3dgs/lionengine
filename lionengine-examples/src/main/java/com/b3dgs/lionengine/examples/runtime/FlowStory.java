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
package com.b3dgs.lionengine.examples.runtime;

import com.b3dgs.lionengine.runtime.SceneFlow;

/**
 * The game context of {@link SceneFlowExample}: the data the scenes share through the flow, and the flow itself, so
 * that a scene can fire a trigger (REQ-SCENE-004).
 */
final class FlowStory
{
    /** Lifecycle calls of the scenes and applied transitions, in order. */
    private final StringBuilder journal = new StringBuilder();
    /** The flow, set once built. */
    private SceneFlow<FlowStory> flow;
    /** {@code true} once the player has selected a save slot. */
    private boolean slotSelected;
    /** Number of scenes created by the factories. */
    private int created;

    /**
     * Creates the context.
     */
    FlowStory()
    {
        super();
    }

    /**
     * Writes an entry in the journal.
     *
     * @param entry The entry.
     */
    void note(String entry)
    {
        if (!journal.isEmpty())
        {
            journal.append(", ");
        }
        journal.append(entry);
    }

    /**
     * Sets the flow.
     *
     * @param sceneFlow The flow.
     */
    void setFlow(SceneFlow<FlowStory> sceneFlow)
    {
        flow = sceneFlow;
    }

    /**
     * Fires a trigger of the flow.
     *
     * @param trigger The trigger name.
     */
    void trigger(String trigger)
    {
        flow.trigger(trigger);
    }

    /**
     * Selects a save slot.
     */
    void selectSlot()
    {
        slotSelected = true;
    }

    /**
     * Tells whether a save slot is selected.
     *
     * @return {@code true} once selected.
     */
    boolean isSlotSelected()
    {
        return slotSelected;
    }

    /**
     * Counts a scene created by a factory.
     */
    void countCreated()
    {
        created++;
    }

    /**
     * Returns the number of scenes created.
     *
     * @return The number of scenes created by the factories.
     */
    int getCreated()
    {
        return created;
    }

    /**
     * Returns the journal.
     *
     * @return The entries, in order.
     */
    String getJournal()
    {
        return journal.toString();
    }
}
