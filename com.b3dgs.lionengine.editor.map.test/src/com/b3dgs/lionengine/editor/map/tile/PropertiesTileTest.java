/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.map.tile;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.editor.UtilEditorTests;
import com.b3dgs.lionengine.editor.map.UtilNl;
import com.b3dgs.lionengine.editor.map.imports.MapImportDialogTest;
import com.b3dgs.lionengine.editor.project.ImportProjectTest;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.world.view.WorldPart;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGroupModel;

/**
 * Test the tile properties.
 */
@RunWith(SWTBotJunit4ClassRunner.class)
public class PropertiesTileTest
{
    private static final SWTBot BOT = new SWTBot();

    /**
     * Set palette tile mode.
     * 
     * @param bot The bot used.
     */
    private static void setPaletteTile(SWTBot bot)
    {
        bot.toolbarToggleButton(0).click();
        bot.waitUntil(org.eclipse.swtbot.swt.finder.waits.Conditions.widgetIsEnabled(bot.toolbarToggleButton(0)));
    }

    /**
     * Get tile group value from properties.
     * 
     * @param bot The bot used.
     * @return The tile group value.
     */
    private static String getTileGroupValue(SWTBot bot)
    {
        return BOT.tree(1).getTreeItem(com.b3dgs.lionengine.editor.map.tile.Messages.TileGroup).row().get(1);
    }

    /**
     * Test the tile properties edition by changing tile group.
     */
    @Test
    public void testPropertiesTileEdit()
    {
        Assert.assertNotNull(ImportProjectTest.createProject(BOT, getClass()));
        UtilEditorTests.copy(".map", Medias.create("sheets.xml"));
        UtilEditorTests.copy(".map", Medias.create("groups.xml"));
        UtilEditorTests.copy(".map", Medias.create("0.png"));

        BOT.menu(UtilNl.get("menu.map.import"), true).click();
        MapImportDialogTest.fillDialog(BOT);

        final WorldPart world = UtilPart.getPart(WorldPart.ID, WorldPart.class);
        final AtomicReference<Rectangle> view = new AtomicReference<>();
        BOT.activeShell().display.syncExec(() -> view.set(world.getView()));

        setPaletteTile(BOT);
        BOT.canvas(1).click(view.get().x + 1, view.get().y + view.get().height - 1);
        BOT.waitUntil(Conditions.treeHasRows(BOT.tree(1), 3));

        Assert.assertEquals(MapTileGroupModel.NO_GROUP_NAME, getTileGroupValue(BOT));

        removeTileGroup(BOT);

        BOT.tree(1).getTreeItem(com.b3dgs.lionengine.editor.map.tile.Messages.TileGroup).select();
        Assert.assertEquals(Constant.EMPTY_STRING, getTileGroupValue(BOT));
    }

    /**
     * Choose no group from combo box.
     * 
     * @param bot The bot used.
     */
    private void removeTileGroup(SWTBot bot)
    {
        bot.tree(1).getTreeItem(com.b3dgs.lionengine.editor.map.tile.Messages.TileGroup).doubleClick();
        bot.waitUntil(Conditions.shellIsActive(com.b3dgs.lionengine.editor.map.tile.Messages.Title));

        final SWTBotShell shellProperties = bot.activeShell();
        shellProperties.bot().comboBox(0).setSelection(0);
        shellProperties.bot().button(com.b3dgs.lionengine.editor.dialog.Messages.Finish).click();
        bot.waitUntil(Conditions.shellCloses(shellProperties));
    }
}
