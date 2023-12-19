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
package com.b3dgs.lionengine.editor.map;

import java.util.Collection;
import java.util.Map;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.map.constaint.ConstraintsExtractHandler;
import com.b3dgs.lionengine.editor.map.constaint.Messages;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.game.feature.tile.TileGroup;
import com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;
import com.b3dgs.lionengine.game.feature.tile.map.transition.Transition;
import com.b3dgs.lionengine.game.feature.tile.map.transition.TransitionType;
import com.b3dgs.lionengine.game.feature.tile.map.transition.TransitionsConfig;

/**
 * Test the constraints extraction dialog.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class ConstraintsExtractTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Fill extract dialog with test values.
     * 
     * @param bot The bot used.
     */
    private static void fillDialog(SWTBot bot)
    {
        bot.waitUntil(Conditions.shellIsActive(Messages.Title));
        final SWTBotShell shellMain = bot.activeShell();

        bot.buttonWithTooltip(com.b3dgs.lionengine.editor.widget.levelrip.Messages.AddLevelRip).click();

        bot.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.dialog.Messages.ResourceDialog_Title));
        final SWTBotShell shellResources = bot.activeShell();

        final SWTBot levelRips = bot.activeShell().bot();
        final Project project = ProjectModel.INSTANCE.getProject();
        levelRips.tree(0).getTreeItem(project.getName()).getNode(project.getResources()).getNode("level.png").select();
        levelRips.button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();

        bot.waitUntil(Conditions.shellCloses(shellResources));

        bot.button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();

        bot.waitUntil(Conditions.shellCloses(shellMain));
    }

    /**
     * Check produced files.
     */
    static void checkResult()
    {
        final Media media = UtilEditorTests.getMedia(TransitionsConfig.FILENAME);
        final Map<Transition, Collection<Integer>> constraints = TransitionsConfig.imports(media);

        final String group = MapTileGroupModel.NO_GROUP_NAME;
        final Collection<Integer> expected = constraints.get(new Transition(TransitionType.CENTER, group, group));
        final Collection<TileGroup> groups = TileGroupsConfig.imports(UtilEditorTests.getMedia(TileGroupsConfig.FILENAME));

        final Collection<Integer> all = groups.iterator().next().getTiles();
        Assert.assertTrue(expected + " " + all, all.containsAll(expected));
    }

    /**
     * Test the constraints extraction.
     */
    @Test
    public void testConstraintsExtractDialog()
    {
        Assert.assertNotNull(ImportProjectTest.createProject(BOT, getClass()));
        UtilEditorTests.copy(".map", Medias.create("sheets.xml"));
        UtilEditorTests.copy(".map", Medias.create("groups.xml"));
        UtilEditorTests.copy(".map", Medias.create("0.png"));

        BOT.menu(UtilNl.get(ConstraintsExtractHandler.ID), true).click();
        fillDialog(BOT);

        checkResult();
    }
}
