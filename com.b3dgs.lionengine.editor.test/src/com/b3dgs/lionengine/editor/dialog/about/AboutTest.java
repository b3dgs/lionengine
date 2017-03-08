/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.editor.dialog.about;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.editor.UtilNl;

/**
 * Test the about dialog.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class AboutTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Test the about dialog.
     */
    @Test
    public void testAboutDialog()
    {
        BOT.menu(UtilNl.get("menu.help.about"), true).click();

        BOT.waitUntil(Conditions.shellIsActive(Messages.Title));
        final SWTBotShell mainShell = BOT.activeShell();

        mainShell.bot().button(Messages.Ok).click();

        BOT.waitUntil(Conditions.shellCloses(mainShell));
    }
}
