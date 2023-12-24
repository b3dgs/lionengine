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
package com.b3dgs.lionengine.editor.frame.properties;

import java.io.File;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.AttributesReader;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.properties.PropertiesPart;
import com.b3dgs.lionengine.editor.properties.PropertiesProviderObject;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.SurfaceConfig;
import com.b3dgs.lionengine.graphic.drawable.ImageHeader;
import com.b3dgs.lionengine.graphic.drawable.ImageInfo;

/**
 * Element properties part.
 */
public class PropertiesFrames implements PropertiesProviderObject
{
    /** Icon frames. */
    private static final Image ICON_FRAMES = UtilIcon.get("properties", "frames.png");

    /**
     * Create the frames attribute.
     * 
     * @param properties The properties tree reference.
     * @param configurer The configurer reference.
     */
    public static void createAttributeFrames(Tree properties, Configurer configurer)
    {
        final TreeItem iconItem = new TreeItem(properties, SWT.NONE);
        iconItem.setText(Messages.Frames);
        iconItem.setData(FramesConfig.NODE_FRAMES);
        iconItem.setImage(PropertiesFrames.ICON_FRAMES);

        final FramesConfig configFrames = FramesConfig.imports(configurer);

        final TreeItem framesHorizontal = new TreeItem(iconItem, SWT.NONE);
        PropertiesPart.createLine(framesHorizontal, Messages.Horizontal, String.valueOf(configFrames.getHorizontal()));
        framesHorizontal.setData(FramesConfig.ATT_HORIZONTAL);

        final TreeItem framesVertical = new TreeItem(iconItem, SWT.NONE);
        PropertiesPart.createLine(framesVertical, Messages.Vertical, String.valueOf(configFrames.getVertical()));
        framesVertical.setData(FramesConfig.ATT_VERTICAL);
    }

    /**
     * Update the frames.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    private static boolean updateFrames(TreeItem item, Configurer configurer)
    {
        final InputDialog frames = new InputDialog(item.getParent().getShell(),
                                                   Messages.Title,
                                                   Messages.Message,
                                                   item.getText(1),
                                                   new InputValidator(InputValidator.INTEGER_POSITIVE_STRICT_MATCH,
                                                                      Messages.Error));
        if (frames.open() == Window.OK)
        {
            final Xml root = configurer.getRoot();
            final Xml framesNode = root.getChildXml(FramesConfig.NODE_FRAMES);
            framesNode.writeString((String) item.getData(), frames.getValue());
            item.setText(PropertiesPart.COLUMN_VALUE, frames.getValue());
            updateSize(configurer, root, framesNode);

            return true;
        }
        return false;
    }

    /**
     * Update the size property depending of the image and frames.
     * 
     * @param configurer The configurer reference.
     * @param root The root node.
     * @param framesNode The frames node.
     */
    public static void updateSize(Configurer configurer, Xml root, AttributesReader framesNode)
    {
        final Xml size;
        final File file = new File(configurer.getPath(),
                                   root.getChild(SurfaceConfig.NODE_SURFACE).getString(SurfaceConfig.ATT_IMAGE));
        final ImageHeader info = ImageInfo.get(ProjectModel.INSTANCE.getProject().getResourceMedia(file));
        if (!root.hasNode(SizeConfig.NODE_SIZE))
        {
            size = root.createChild(SizeConfig.NODE_SIZE);
            size.writeInteger(SizeConfig.ATT_WIDTH, info.getWidth());
            size.writeInteger(SizeConfig.ATT_HEIGHT, info.getHeight());
        }
        else
        {
            size = root.getChildXml(SizeConfig.NODE_SIZE);
        }
        size.writeInteger(SizeConfig.ATT_WIDTH, info.getWidth() / framesNode.getInteger(FramesConfig.ATT_HORIZONTAL));
        size.writeInteger(SizeConfig.ATT_HEIGHT, info.getHeight() / framesNode.getInteger(FramesConfig.ATT_VERTICAL));
    }

    /**
     * Create properties.
     */
    public PropertiesFrames()
    {
        super();
    }

    /*
     * PropertiesProvider
     */

    @Override
    public void setInput(Tree properties, Configurer configurer)
    {
        final Xml root = configurer.getRoot();
        if (root.hasNode(FramesConfig.NODE_FRAMES))
        {
            createAttributeFrames(properties, configurer);
        }
    }

    @Override
    public boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        boolean updated = false;
        if (FramesConfig.ATT_HORIZONTAL.equals(data) || FramesConfig.ATT_VERTICAL.equals(data))
        {
            updated = updateFrames(item, configurer);
        }
        return updated;
    }
}
