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
package com.b3dgs.lionengine.editor.properties.tilecollision.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.utility.UtilCombo;
import com.b3dgs.lionengine.editor.utility.UtilText;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.collision.CollisionRange;

/**
 * Represents the collision range edition area.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionRangeComposite
{
    /** Default range value. */
    private static final String DEFAULT_RANGE_VALUE = "0";

    /** Range type. */
    private Combo comboRange;
    /** Range min x. */
    private Text textRangeMinX;
    /** Range max x. */
    private Text textRangeMaxX;
    /** Range min y. */
    private Text textRangeMinY;
    /** Range max y. */
    private Text textRangeMaxY;

    /**
     * Create composite.
     */
    public CollisionRangeComposite()
    {
        // Nothing to do
    }

    /**
     * Load the collision range from data.
     * 
     * @param range The collision range reference.
     */
    public void load(CollisionRange range)
    {
        comboRange.setText(range.getOutput().name());
        comboRange.setData(range.getOutput());

        textRangeMinX.setText(String.valueOf(range.getMinX()));
        textRangeMaxX.setText(String.valueOf(range.getMaxX()));
        textRangeMinY.setText(String.valueOf(range.getMinY()));
        textRangeMaxY.setText(String.valueOf(range.getMaxY()));
    }

    /**
     * Create the composite.
     * 
     * @param parent The parent composite.
     */
    public void create(Composite parent)
    {
        final Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        group.setText(Messages.Dialog_TileCollision_Range);

        comboRange = UtilCombo.create(Messages.Dialog_TileCollision_Axis, group, Axis.values());

        final Composite range = new Composite(group, SWT.BORDER);
        range.setLayout(new GridLayout(2, true));
        range.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        textRangeMinX = UtilText.create(Messages.Dialog_TileCollision_MinX, range);
        textRangeMinX.setText(DEFAULT_RANGE_VALUE);
        textRangeMaxX = UtilText.create(Messages.Dialog_TileCollision_MaxX, range);
        textRangeMaxX.setText(DEFAULT_RANGE_VALUE);
        textRangeMinY = UtilText.create(Messages.Dialog_TileCollision_MinY, range);
        textRangeMinY.setText(DEFAULT_RANGE_VALUE);
        textRangeMaxY = UtilText.create(Messages.Dialog_TileCollision_MaxY, range);
        textRangeMaxY.setText(DEFAULT_RANGE_VALUE);
    }

    /**
     * Get the configured collision range.
     * 
     * @return The configured collision range.
     */
    public CollisionRange get()
    {
        final CollisionRange range = new CollisionRange(Axis.valueOf(comboRange.getText()),
                                                        Integer.parseInt(textRangeMinX.getText()),
                                                        Integer.parseInt(textRangeMaxX.getText()),
                                                        Integer.parseInt(textRangeMinY.getText()),
                                                        Integer.parseInt(textRangeMaxY.getText()));
        return range;
    }
}
