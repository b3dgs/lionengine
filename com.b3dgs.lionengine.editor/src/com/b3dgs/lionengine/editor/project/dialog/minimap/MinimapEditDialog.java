/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.project.dialog.minimap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.Engine;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.dialog.widget.BrowseWidget;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.UtilSwt;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MinimapConfig;
import com.b3dgs.lionengine.stream.Xml;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Edit minimap dialog.
 */
public class MinimapEditDialog extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "edit.png");

    /** Minimap config. */
    private BrowseWidget minimap;

    /**
     * Create the dialog.
     * 
     * @param parent The parent reference.
     */
    public MinimapEditDialog(Shell parent)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON, SWT.SHELL_TRIM);

        createDialog();
        dialog.setMinimumSize(640, 448);
        finish.setEnabled(false);
    }

    /**
     * Set the save folder destination.
     * 
     * @param destination The destination folder.
     */
    public void setLocation(String destination)
    {
        minimap.setLocation(destination);
    }

    /**
     * Create the sheet area.
     * 
     * @param parent The parent composite.
     */
    private void createSheetArea(Composite parent)
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        int maxWidth = 0;
        int maxHeight = 0;
        for (final Integer sheet : map.getSheets())
        {
            final SpriteTiled sprite = map.getSheet(sheet);
            maxWidth = Math.max(maxWidth, sprite.getWidth());
            maxHeight = Math.max(maxHeight, sprite.getHeight());
        }

        final Label sheetLabel = new Label(parent, SWT.BORDER);
        sheetLabel.setLayoutData(new GridData(maxWidth, maxHeight));
        sheetLabel.setImage(map.getSheet(Integer.valueOf(0)).getSurface().getSurface());
    }

    /**
     * Create the color area chooser.
     * 
     * @param parent The parent composite.
     */
    private void createColorArea(Composite parent)
    {
        final Composite colorArea = new Composite(parent, SWT.NONE);
        colorArea.setLayout(new GridLayout(1, false));

        final Button colorPicker = new Button(colorArea, SWT.NONE);
        colorPicker.setText("Color");

        final Label color = new Label(colorArea, SWT.BORDER);
        color.setLayoutData(new GridData(32, 24));
        colorPicker.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                final ColorDialog dialog = new ColorDialog(colorPicker.getShell());
                final RGB rgb = dialog.open();
                final Color old = color.getBackground();
                if (old != null)
                {
                    old.dispose();
                }
                color.setBackground(new Color(color.getDisplay(), rgb));
            }
        });
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        content.setLayout(new GridLayout(1, false));
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final String[] filter = UtilDialog.getXmlFilter();
        final String label = Messages.MinimapConfig;
        minimap = new BrowseWidget(content, label, label + " (" + filter[0] + ")", filter, true);

        final Label separator1 = new Label(content, SWT.SEPARATOR | SWT.HORIZONTAL);
        separator1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Composite area = new Composite(content, SWT.NONE);
        area.setLayout(new GridLayout(3, false));
        area.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true));

        createSheetArea(area);

        final Label separator2 = new Label(area, SWT.SEPARATOR | SWT.VERTICAL);
        separator2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createColorArea(area);

        UtilSwt.setEnabled(area, false);
        minimap.addListener(media ->
        {
            if (media.exists())
            {
                UtilSwt.setEnabled(area, true);
                finish.setEnabled(true);
            }
        });
    }

    @Override
    protected void onFinish()
    {
        final XmlNode root = Xml.create(MinimapConfig.NODE_MINIMAP);
        root.writeString(Configurer.HEADER, Engine.WEBSITE);

        Xml.save(root, minimap.getMedia());
    }
}
