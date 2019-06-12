/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.adlmidi;

import com.b3dgs.lionengine.audio.Audio;

/**
 * Allows to play midi musics.
 */
public interface AdlMidi extends Audio
{
    /**
     * Set the internal bank to use.
     * 
     * <ul>
     * <li>Bank 0, AIL (Star Control 3, Albion, Empire 2, Sensible Soccer, Settlers 2, many others)
     * <li>Bank 1, Bisqwit (selection of 4op and 2op)</li>
     * <li>Bank 2, HMI (Descent, Asterix)</li>
     * <li>Bank 3, HMI (Descent:: Int)</li>
     * <li>Bank 4, HMI (Descent:: Ham)</li>
     * <li>Bank 5, HMI (Descent:: Rick)</li>
     * <li>Bank 6, HMI (Descent 2)</li>
     * <li>Bank 7, HMI (Normality)</li>
     * <li>Bank 8, HMI (Shattered Steel)</li>
     * <li>Bank 9, HMI (Theme Park)</li>
     * <li>Bank 10, HMI (3d Table Sports, Battle Arena Toshinden)</li>
     * <li>Bank 11, HMI (Aces of the Deep)</li>
     * <li>Bank 12, HMI (Earthsiege)</li>
     * <li>Bank 13, HMI (Anvil of Dawn)</li>
     * <li>Bank 14, DMX (Doom :: partially pseudo 4op)</li>
     * <li>Bank 15, DMX (Hexen, Heretic :: partially pseudo 4op)</li>
     * <li>Bank 16, DMX (MUS Play :: partially pseudo 4op)</li>
     * <li>Bank 17, AIL (Discworld, Grandest Fleet, Pocahontas, Slob Zone 3d, Ultima 4, Zorro)</li>
     * <li>Bank 18, AIL (Warcraft 2)</li>
     * <li>Bank 19, AIL (Syndicate)</li>
     * <li>Bank 20, AIL (Guilty, Orion Conspiracy, Terra Nova Strike Force Centauri :: 4op)</li>
     * <li>Bank 21, AIL (Magic Carpet 2)</li>
     * <li>Bank 22, AIL (Nemesis)</li>
     * <li>Bank 23, AIL (Jagged Alliance)</li>
     * <li>Bank 24, AIL (When Two Worlds War :: 4op, MISSING INSTRUMENTS)</li>
     * <li>Bank 25, AIL (Bards Tale Construction :: MISSING INSTRUMENTS)</li>
     * <li>Bank 26, AIL (Return to Zork)</li>
     * <li>Bank 27, AIL (Theme Hospital)</li>
     * <li>Bank 28, AIL (National Hockey League PA)</li>
     * <li>Bank 29, AIL (Inherit The Earth)</li>
     * <li>Bank 30, AIL (Inherit The Earth, file two)</li>
     * <li>Bank 31, AIL (Little Big Adventure :: 4op)</li>
     * <li>Bank 32, AIL (Wreckin Crew)</li>
     * <li>Bank 33, AIL (Death Gate)</li>
     * <li>Bank 34, AIL (FIFA International Soccer)</li>
     * <li>Bank 35, AIL (Starship Invasion)</li>
     * <li>Bank 36, AIL (Super Street Fighter 2 :: 4op)</li>
     * <li>Bank 37, AIL (Lords of the Realm :: MISSING INSTRUMENTS)</li>
     * <li>Bank 38, AIL (SimFarm, SimHealth :: 4op)</li>
     * <li>Bank 39, AIL (SimFarm, Settlers, Serf City)</li>
     * <li>Bank 40, AIL (Caesar 2 :: partially 4op, MISSING INSTRUMENTS)</li>
     * <li>Bank 41, AIL (Syndicate Wars)</li>
     * <li>Bank 42, AIL (Bubble Bobble Feat. Rainbow Islands, Z)</li>
     * <li>Bank 43, AIL (Warcraft)</li>
     * <li>Bank 44, AIL (Terra Nova Strike Force Centuri :: partially 4op)</li>
     * <li>Bank 45, AIL (System Shock :: partially 4op)</li>
     * <li>Bank 46, AIL (Advanced Civilization)</li>
     * <li>Bank 47, AIL (Battle Chess 4000 :: partially 4op, melodic only)</li>
     * <li>Bank 48, AIL (Ultimate Soccer Manager :: partially 4op)</li>
     * <li>Bank 49, AIL (Air Bucks, Blue And The Gray, America Invades, Terminator 2029)</li>
     * <li>Bank 50, AIL (Ultima Underworld 2)</li>
     * <li>Bank 51, AIL (Kasparov's Gambit)</li>
     * <li>Bank 52, AIL (High Seas Trader :: MISSING INSTRUMENTS)</li>
     * <li>Bank 53, AIL (Master of Magic, Master of Orion 2 :: 4op, std percussion)</li>
     * <li>Bank 54, AIL (Master of Magic, Master of Orion 2 :: 4op, orchestral percussion)</li>
     * <li>Bank 55, SB (Action Soccer)</li>
     * <li>Bank 56, SB (3d Cyberpuck :: melodic only)</li>
     * <li>Bank 57, SB (Simon the Sorcerer :: melodic only)</li>
     * <li>Bank 58, OP3 (The Fat Man 2op set)</li>
     * <li>Bank 59, OP3 (The Fat Man 4op set)</li>
     * <li>Bank 60, OP3 (JungleVision 2op set :: melodic only)</li>
     * <li>Bank 61, OP3 (Wallace 2op set, Nitemare 3D :: melodic only)</li>
     * <li>Bank 62, TMB (Duke Nukem 3D)</li>
     * <li>Bank 63, TMB (Shadow Warrior)</li>
     * <li>Bank 64, DMX (Raptor)</li>
     * <li>Bank 65, OP3 (Modded GMOPL by Wohlstand)</li>
     * <li>Bank 66, SB (Jammey O'Connel's bank)</li>
     * <li>Bank 67, TMB (Default bank of Build Engine)</li>
     * <li>Bank 68, WOPL (4op bank by James Alan Nguyen and Wohlstand)</li>
     * <li>Bank 69, TMB (Blood)</li>
     * <li>Bank 70, TMB (Lee)</li>
     * <li>Bank 71, TMB (Nam)</li>
     * <li>Bank 72, WOPL (DMXOPL3 bank by Sneakernets)</li>
     * <li>Bank 73, EA (Cartooners)</li>
     * </ul>
     * 
     * @param bank The bank id.
     */
    void setBank(int bank);

    /**
     * Get the total number of ticks.
     * 
     * @return The total number of ticks.
     */
    long getTicks();

    /**
     * Pause the audio (can be resumed).
     */
    void pause();

    /**
     * Resume the audio (if paused).
     */
    void resume();
}
