/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.collision;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.map.CollisionFunction;
import com.b3dgs.lionengine.game.map.CollisionTile;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.TileGame;

/**
 * Represents the tile collision view, where the tile collision can be edited.
 * 
 * @author Pierre-Alexandre
 */
public class TileCollisionPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.tile-collision";

    /** Selected tile. */
    TileGame tile;
    /** Scroll composite. */
    private ScrolledComposite scrolledComposite;
    /** Formulas content. */
    private Composite content;
    /** Single height. */
    private int singleHeight;
    /** Tool bar. */
    private ToolBar toolbar;
    /** Formula list. */
    private List<TileCollisionComposite> formulas;
    /** Parent reference. */
    private Composite parent;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(final Composite parent)
    {
        this.parent = parent;
        formulas = new ArrayList<>(1);
        parent.setLayout(new GridLayout(1, false));

        createToolBar(parent);

        scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        scrolledComposite.setLayout(new GridLayout(1, false));
        scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        Tools.installMouseWheelScroll(scrolledComposite);

        content = new Composite(scrolledComposite, SWT.NONE);
        content.setLayout(new GridLayout(1, false));

        scrolledComposite.setContent(content);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setAlwaysShowScrollBars(true);
    }

    /**
     * Set the selected tile.
     * 
     * @param tile The selected tile.
     */
    public void setSelectedTile(TileGame tile)
    {
        this.tile = tile;
        for (final TileCollisionComposite formula : formulas)
        {
            formula.dispose();
        }
        formulas.clear();
        if (tile != null)
        {
            final CollisionTile collision = tile.getCollision();
            for (final CollisionFunction function : collision.getCollisionFunctions())
            {
                final TileCollisionComposite tileCollisionComposite = createFormula();
                tileCollisionComposite.setSelectedFunction(function);
            }
        }
    }

    /**
     * Remove an existing formula from the list.
     * 
     * @param formula The formula to remove.
     */
    public void removeFormula(TileCollisionComposite formula)
    {
        final MapTile<?> map = WorldViewModel.INSTANCE.getMap();
        map.removeCollisionFunction(formula.getCollisionFunction());

        formulas.remove(formula);
        formula.dispose();
        parent.layout(true, true);
    }

    /**
     * Set the enabled state.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public void setEnabled(boolean enabled)
    {
        toolbar.setEnabled(enabled);
        content.setEnabled(enabled);
    }

    /**
     * Create a blank formula.
     * 
     * @return The formula instance.
     */
    TileCollisionComposite createFormula()
    {
        final TileCollisionComposite tileCollisionComposite = new TileCollisionComposite(this, content);
        formulas.add(tileCollisionComposite);
        singleHeight = tileCollisionComposite.getMinHeight();
        scrolledComposite.setMinSize(content.computeSize(tileCollisionComposite.getMinWidth(),
                tileCollisionComposite.getMinHeight()));
        scrolledComposite.setMinHeight((singleHeight + 5) * formulas.size());
        parent.layout(true, true);

        return tileCollisionComposite;
    }

    /**
     * Create the tool bar.
     * 
     * @param parent The composite parent.
     */
    private void createToolBar(final Composite parent)
    {
        toolbar = new ToolBar(parent, SWT.HORIZONTAL);
        toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
        toolbar.setEnabled(false);

        final ToolItem add = new ToolItem(toolbar, SWT.PUSH);
        add.setText("Add formula");
        add.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final TileCollisionComposite tileCollisionComposite = createFormula();
                final MapTile<?> map = WorldViewModel.INSTANCE.getMap();
                map.assignCollisionFunction(tile.getCollision(), tileCollisionComposite.getCollisionFunction());
            }
        });
    }
}
