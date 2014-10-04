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
package com.b3dgs.lionengine.editor.properties;

import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;
import com.b3dgs.lionengine.game.configurer.Configurer;

/**
 * Element properties part.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.properties";

    /**
     * Create the class attribute.
     * 
     * @param parent The parent reference.
     * @param configurer The configurer reference.
     * @return The attribute instance.
     */
    private static Composite createAttributeClass(Composite parent, Configurer configurer)
    {
        final Composite attribute = new Composite(parent, SWT.NONE);
        attribute.setLayout(new GridLayout(2, false));
        attribute.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        final Label name = new Label(attribute, SWT.NONE);
        name.setText("Class");

        final Text clazz = new Text(attribute, SWT.SEARCH);
        clazz.setText(configurer.getClassName());

        return attribute;
    }

    /**
     * Create the surface attribute.
     * 
     * @param parent The parent reference.
     * @param configurer The configurer reference.
     * @return The attribute instance.
     */
    private static Composite createAttributeSurface(Composite parent, Configurer configurer)
    {
        final Composite attributes = new Composite(parent, SWT.NONE);
        attributes.setLayout(new GridLayout(1, false));

        final Composite attributeSurface = new Composite(attributes, SWT.NONE);
        attributeSurface.setLayout(new GridLayout(2, false));
        attributeSurface.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        final Label nameSurface = new Label(attributeSurface, SWT.NONE);
        nameSurface.setText("Surface");

        final Text textSurface = new Text(attributeSurface, SWT.SEARCH);
        final ConfigSurface surface = ConfigSurface.create(configurer);
        textSurface.setText(surface.getImage());

        final String icon = surface.getIcon();
        if (icon != null)
        {
            final Composite attributeIcon = new Composite(attributes, SWT.NONE);
            attributeIcon.setLayout(new GridLayout(2, false));
            attributeIcon.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

            final Label nameIcon = new Label(attributeIcon, SWT.NONE);
            nameIcon.setText("Icon");

            final Text textIcon = new Text(attributeIcon, SWT.SEARCH);
            textIcon.setText(icon);
        }

        return attributes;
    }

    /** The attributes composite. */
    private Composite composite;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(Composite parent)
    {
        composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(1, false));
    }

    /**
     * Set the properties input.
     * 
     * @param configurer The configurer reference.
     */
    public void setInput(Configurer configurer)
    {
        for (final Control control : composite.getChildren())
        {
            control.dispose();
        }
        if (configurer != null)
        {
            PropertiesPart.createAttributeClass(composite, configurer);
            if (configurer.getRoot().hasChild(ConfigSurface.SURFACE))
            {
                PropertiesPart.createAttributeSurface(composite, configurer);
            }
        }
        composite.layout(true, true);
    }
}
