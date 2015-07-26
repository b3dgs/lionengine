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
package com.b3dgs.lionengine.editor.world;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionFunction;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;

/**
 * Represents the quick formula assignment area.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FormulaItem
{
    /** Element ID. */
    public static final String ID = "formulas";

    /** Loaded data. */
    final Map<String, CollisionFunction> values = new HashMap<>();
    /** The combo item. */
    Combo combo;
    /** Last loaded. */
    long lastModified;

    /**
     * Create item.
     */
    public FormulaItem()
    {
        // Nothing to do
    }

    /**
     * Get the current collision function selection.
     * 
     * @return The collision function selection.
     */
    public CollisionFunction getFunction()
    {
        return (CollisionFunction) combo.getData();
    }

    /**
     * Construct the item.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void create(Composite parent)
    {
        final GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 1;
        parent.setLayout(layout);

        final Label label = new Label(parent, SWT.NONE);
        label.setText(Messages.Toolbar_Formula);

        combo = new Combo(parent, SWT.SINGLE | SWT.READ_ONLY);
        combo.addMouseTrackListener(new MouseTrackListener()
        {
            @Override
            public void mouseEnter(MouseEvent event)
            {
                loadValues();
            }

            @Override
            public void mouseExit(MouseEvent event)
            {
                // Nothing to do
            }

            @Override
            public void mouseHover(MouseEvent event)
            {
                // Nothing to do
            }
        });
        combo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                combo.setData(values.get(combo.getText()));
            }
        });
    }

    /**
     * Load combo values.
     */
    void loadValues()
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        if (map.hasFeature(MapTileCollision.class))
        {
            final MapTileCollision mapCollision = map.getFeature(MapTileCollision.class);
            final long mofified = mapCollision.getFormulasConfig().getFile().lastModified();
            if (lastModified != mofified)
            {
                lastModified = mofified;
                values.clear();
                for (final CollisionFormula formula : mapCollision.getCollisionFormulas())
                {
                    values.put(formula.getName(), formula.getFunction());
                }
                combo.setItems(values.keySet().toArray(new String[values.size()]));
            }
        }
    }
}
