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
package com.b3dgs.lionengine.editor.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Sample part implementation.
 * 
 * @author Pierre-Alexandre
 */
public class SamplePart
{
    /** Dirty state. */
    @Inject
    MDirtyable dirty;

    /** Input text. */
    private Text txtInput;
    /** Table viewer. */
    private TableViewer tableViewer;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(Composite parent)
    {
        parent.setLayout(new GridLayout(1, false));

        txtInput = new Text(parent, SWT.BORDER);
        txtInput.setMessage("Enter text to mark part as dirty");
        txtInput.addModifyListener(new ModifyListener()
        {
            @Override
            public void modifyText(ModifyEvent e)
            {
                dirty.setDirty(true);
            }
        });
        txtInput.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        tableViewer = new TableViewer(parent);

        tableViewer.add("Sample item 1");
        tableViewer.add("Sample item 2");
        tableViewer.add("Sample item 3");
        tableViewer.add("Sample item 4");
        tableViewer.add("Sample item 5");
        tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
    }

    /**
     * Set the focus.
     */
    @Focus
    public void setFocus()
    {
        tableViewer.getTable().setFocus();
    }

    /**
     * Update the dirty save state.
     */
    @Persist
    public void save()
    {
        dirty.setDirty(false);
    }
}
