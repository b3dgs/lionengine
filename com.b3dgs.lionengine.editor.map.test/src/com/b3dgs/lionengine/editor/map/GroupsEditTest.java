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
package com.b3dgs.lionengine.editor.map;

import java.util.Collection;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.map.group.editor.Messages;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.feature.tile.TileGroup;
import com.b3dgs.lionengine.game.feature.tile.TileGroupType;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Test the groups edition dialog.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class GroupsEditTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Fill extract dialog with test values.
     * 
     * @param bot The bot used.
     */
    private static void fillDialogConfigure(SWTBot bot)
    {
        bot.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.map.group.menu.Messages.Title));

        bot.buttonWithTooltip(com.b3dgs.lionengine.editor.widget.levelrip.Messages.AddLevelRip).click();

        bot.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.dialog.Messages.ResourceDialog_Title));
        final SWTBotShell shellResources = bot.activeShell();

        final SWTBot levelRips = bot.activeShell().bot();
        final Project project = ProjectModel.INSTANCE.getProject();
        levelRips.tree(0).getTreeItem(project.getName()).getNode(project.getResources()).getNode("level.png").check();
        levelRips.button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();

        bot.waitUntil(Conditions.shellCloses(shellResources));

        bot.button(com.b3dgs.lionengine.editor.dialog.Messages.Next).click();
    }

    /**
     * Fill extract dialog with test values.
     * 
     * @param bot The bot used.
     */
    static void fillDialogAssign(SWTBot bot)
    {
        bot.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.map.group.menu.Messages.Title));
        final SWTBotShell shellMain = bot.shell(com.b3dgs.lionengine.editor.map.group.menu.Messages.Title);

        bot.comboBoxWithLabel(Messages.TileGroupType).setSelection(TileGroupType.TRANSITION.name());
        bot.button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();
        bot.waitUntil(Conditions.shellCloses(shellMain));
    }

    /**
     * Check produced files.
     */
    public static void checkResult()
    {
        final Media media = UtilEditorTests.getMedia(TileGroupsConfig.FILENAME);
        final Collection<TileGroup> tileGroups = TileGroupsConfig.imports(media);

        Assert.assertEquals(1, tileGroups.size());

        final TileGroup tileGroup = tileGroups.iterator().next();

        Assert.assertEquals(MapTileGroupModel.NO_GROUP_NAME, tileGroup.getName());
    }

    /**
     * Test the groups edition.
     */
    @Test
    public void testGroupsEditDialog()
    {
        Assert.assertNotNull(ImportProjectTest.createProject(BOT, getClass()));
        UtilEditorTests.copy(".map", Medias.create("sheets.xml"));
        UtilEditorTests.copy(".map", Medias.create("0.png"));

        BOT.menu(UtilNl.get("menu.map.edit-groups"), true).click();
        fillDialogConfigure(BOT);
        fillDialogAssign(BOT);

        checkResult();
    }
}
