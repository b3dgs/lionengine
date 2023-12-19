/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.animation;

import java.io.File;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.animation.editor.Messages;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;

/**
 * Test the animation editor dialog.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class AnimationEditorTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Test the animation editor.
     */
    @Test
    public void testAnimationEditor()
    {
        final File projectFolder = ImportProjectTest.createProject(BOT, getClass());
        UtilEditorTests.copy(".animation", Medias.create("Mario.png"));
        UtilEditorTests.copy(".animation", Medias.create("Mario.xml"));

        UtilEditorTests.waitResourcesCopied(BOT, projectFolder, 3);

        BOT.tree(0).getTreeItem(projectFolder.getName()).getNode("resources").getNode("Mario.xml").click();
        BOT.tree(1).contextMenu(UtilNl.get("menu.animations-enable")).click();
        BOT.tree(1).getTreeItem(com.b3dgs.lionengine.editor.animation.properties.Messages.Animations).doubleClick();

        fillDialog(BOT);
    }

    /**
     * Fill animation dialog.
     * 
     * @param bot The bot used.
     */
    private void fillDialog(SWTBot bot)
    {
        bot.waitUntil(Conditions.shellIsActive(Messages.Title));
        final SWTBotShell shellEditor = BOT.activeShell();
        final SWTBot shellBot = shellEditor.bot();

        fillAnimation(shellBot);

        shellBot.tabItem(Messages.Animator).activate();
        shellBot.tree(0).getTreeItem(Animation.DEFAULT_NAME).click();

        useAnimator(shellBot);

        shellBot.button(com.b3dgs.lionengine.editor.dialog.Messages.Exit).click();
        bot.waitUntil(Conditions.shellCloses(shellEditor));
    }

    /**
     * Fill animation data by setting properties.
     * 
     * @param bot The bot used.
     */
    private void fillAnimation(SWTBot bot)
    {
        bot.textWithLabel(Messages.FirstFrame).setText("2");
        bot.textWithLabel(Messages.LastFrame).setText("6");
        bot.textWithLabel(Messages.AnimSpeed).setText("0.2");
        bot.checkBox(Messages.Reverse).select();
        bot.checkBox(Messages.Repeat).select();
    }

    /**
     * Use the animator by clicking on buttons.
     * 
     * @param bot The bot used.
     */
    private void useAnimator(SWTBot bot)
    {
        bot.button(2).click();
        bot.waitUntil(Conditions.widgetIsEnabled(bot.button(3)));
        bot.sleep(500);
        bot.button(3).click();
        bot.waitUntil(Conditions.widgetIsEnabled(bot.button(2)));
        bot.sleep(500);
        bot.button(2).click();
        bot.waitUntil(Conditions.widgetIsEnabled(bot.button(3)));
        bot.sleep(500);
        bot.button(4).click();
    }
}
