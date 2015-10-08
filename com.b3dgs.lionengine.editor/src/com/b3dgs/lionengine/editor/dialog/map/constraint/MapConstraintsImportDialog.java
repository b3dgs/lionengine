/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.dialog.map.constraint;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.utility.UtilButton;
import com.b3dgs.lionengine.editor.utility.UtilDialog;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.map.ConstraintsExtractor;
import com.b3dgs.lionengine.game.map.MapTile;

/**
 * Represents the import map tile constraints dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MapConstraintsImportDialog extends AbstractDialog
{
    /** Icon. */
    public static final Image ICON = UtilIcon.get("dialog", "import.png");
    /** Xml filter. */
    private static final String XML = "*.xml";

    /** Constraints location. */
    Text constraintsLocationText;
    /** Constraints config file. */
    Media constraintsConfig;

    /**
     * Create an import map tile constraints dialog.
     * 
     * @param parent The shell parent.
     */
    public MapConstraintsImportDialog(Shell parent)
    {
        super(parent,
              Messages.Title,
              Messages.HeaderTitle,
              Messages.HeaderDesc,
              ICON);
        createDialog();
        dialog.setMinimumSize(512, 160);
        finish.setEnabled(false);
        finish.forceFocus();
    }

    /**
     * Update the tips label.
     */
    void updateTipsLabel()
    {
        tipsLabel.setVisible(false);
    }

    /**
     * Browse the constraints location.
     */
    void browseConstraintsLocation()
    {
        final File file = UtilDialog.selectResourceFile(dialog, true, new String[]
        {
            Messages.ConstraintsConfigFileFilter
        }, new String[]
        {
            XML
        });
        if (file != null)
        {
            onConstraintsConfigLocationSelected(file);
        }
    }

    /**
     * Called when the groups config location has been selected.
     * 
     * @param path The selected groups config location path.
     */
    void onConstraintsConfigLocationSelected(File path)
    {
        final Project project = Project.getActive();
        try
        {
            constraintsConfig = project.getResourceMedia(new File(path.getAbsolutePath()));
            constraintsLocationText.setText(constraintsConfig.getPath());
            finish.setEnabled(true);
        }
        catch (final LionEngineException exception)
        {
            setTipsMessage(AbstractDialog.ICON_ERROR, Messages.ErrorConstraints);
        }
        updateTipsLabel();
    }

    /**
     * Create the constraints location area.
     * 
     * @param content The content composite.
     */
    private void createConstraintsLocationArea(Composite content)
    {
        final Composite groupArea = new Composite(content, SWT.NONE);
        groupArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        groupArea.setLayout(new GridLayout(3, false));

        final Label locationLabel = new Label(groupArea, SWT.NONE);
        locationLabel.setText(Messages.ConstraintsLocation);

        constraintsLocationText = new Text(groupArea, SWT.BORDER);
        constraintsLocationText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        constraintsLocationText.setEditable(false);

        final Button browse = UtilButton.create(groupArea,
                                                com.b3dgs.lionengine.editor.dialog.Messages.Browse,
                                                null);
        browse.setImage(AbstractDialog.ICON_BROWSE);
        UtilButton.setAction(browse, () -> browseConstraintsLocation());
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        createConstraintsLocationArea(content);
    }

    @Override
    protected void onFinish()
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        final ConstraintsExtractor extractor = new ConstraintsExtractor(map);

        extractor.check();
        extractor.export(constraintsConfig);
    }
}
