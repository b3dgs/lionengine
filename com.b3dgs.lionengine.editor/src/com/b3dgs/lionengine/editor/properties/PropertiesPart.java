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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.EMenuService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.animation.AnimationEditor;
import com.b3dgs.lionengine.editor.collision.EntityCollisionEditor;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.ConfigCollisions;
import com.b3dgs.lionengine.game.configurer.ConfigFrames;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Element properties part.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class PropertiesPart
        implements PropertiesListener
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.properties";
    /** Menu ID. */
    public static final String MENU_ID = PropertiesPart.ID + ".menu";
    /** Properties extension. */
    public static final String EXTENSION_PROPERTIES = "id";

    /** Class icon. */
    private static final Image ICON_CLASS = UtilEclipse.getIcon("properties", "class.png");
    /** Surface icon. */
    private static final Image ICON_SURFACE = UtilEclipse.getIcon("properties", "surface.png");
    /** Icon icon. */
    private static final Image ICON_ICON = UtilEclipse.getIcon("properties", "icon.png");
    /** Icon frames. */
    private static final Image ICON_FRAMES = UtilEclipse.getIcon("properties", "frames.png");
    /** Animations icon. */
    private static final Image ICON_ANIMATIONS = UtilEclipse.getIcon("properties", "animations.png");
    /** Collisions icon. */
    private static final Image ICON_COLLISIONS = UtilEclipse.getIcon("properties", "collisions.png");

    /** Properties tree. */
    Tree properties;
    /** Part services. */
    @Inject
    private EPartService partService;
    /** Extensions point. */
    private List<PropertiesListener> extensions;

    /**
     * Create the surface attribute.
     * 
     * @param configurer The configurer reference.
     */
    public void createAttributeSurface(Configurer configurer)
    {
        final TreeItem surfaceItem = new TreeItem(properties, SWT.NONE);
        surfaceItem.setText(Messages.Properties_Surface);
        surfaceItem.setData(ConfigSurface.SURFACE_IMAGE);
        surfaceItem.setImage(PropertiesPart.ICON_SURFACE);

        final ConfigSurface surface = ConfigSurface.create(configurer);
        final TreeItem surfaceName = new TreeItem(surfaceItem, SWT.NONE);
        surfaceName.setText(surface.getImage());
        surfaceName.setData(ConfigSurface.SURFACE_IMAGE);

        final String icon = surface.getIcon();
        if (icon != null)
        {
            createAttributeIcon(icon);
        }
    }

    /**
     * Create the surface attribute.
     * 
     * @param icon The icon path.
     */
    public void createAttributeIcon(String icon)
    {
        final TreeItem iconItem = new TreeItem(properties, SWT.NONE);
        iconItem.setText(Messages.Properties_SurfaceIcon);
        iconItem.setData(ConfigSurface.SURFACE_ICON);
        iconItem.setImage(PropertiesPart.ICON_ICON);

        final TreeItem iconName = new TreeItem(iconItem, SWT.NONE);
        iconName.setText(icon);
        iconName.setData(ConfigSurface.SURFACE_ICON);
    }

    /**
     * Create the frames attribute.
     * 
     * @param configurer The configurer reference.
     */
    public void createAttributeFrames(Configurer configurer)
    {
        final TreeItem iconItem = new TreeItem(properties, SWT.NONE);
        iconItem.setText(Messages.Properties_Frames);
        iconItem.setData(ConfigFrames.FRAMES);
        iconItem.setImage(PropertiesPart.ICON_FRAMES);

        final ConfigFrames configFrames = ConfigFrames.create(configurer);

        final TreeItem framesHorizontal = new TreeItem(iconItem, SWT.NONE);
        framesHorizontal.setText(String.valueOf(configFrames.getHorizontal()));
        framesHorizontal.setData(ConfigFrames.FRAMES_HORIZONTAL);

        final TreeItem framesVertical = new TreeItem(iconItem, SWT.NONE);
        framesVertical.setText(String.valueOf(configFrames.getVertical()));
        framesVertical.setData(ConfigFrames.FRAMES_VERTICAL);
    }

    /**
     * Create the animations attribute.
     */
    public void createAttributeAnimations()
    {
        final TreeItem animationsItem = new TreeItem(properties, SWT.NONE);
        animationsItem.setText(Messages.Properties_Animations);
        animationsItem.setData(ConfigAnimations.ANIMATION);
        animationsItem.setImage(PropertiesPart.ICON_ANIMATIONS);
    }

    /**
     * Create the collisions attribute.
     */
    public void createAttributeCollisions()
    {
        final TreeItem animationsItem = new TreeItem(properties, SWT.NONE);
        animationsItem.setText(Messages.Properties_Collisions);
        animationsItem.setData(ConfigCollisions.COLLISION);
        animationsItem.setImage(PropertiesPart.ICON_COLLISIONS);
    }

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     * @param menuService The menu service reference.
     */
    @PostConstruct
    public void createComposite(Composite parent, EMenuService menuService)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        properties = new Tree(composite, SWT.BORDER);
        properties.setLayout(new GridLayout(1, false));
        properties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        properties.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent e)
            {
                final TreeItem[] items = properties.getSelection();
                if (items.length > 0)
                {
                    final TreeItem item = items[0];
                    final Configurer configurer = (Configurer) properties.getData();
                    if (updateProperties(item, configurer))
                    {
                        configurer.save();
                    }
                }
            }
        });

        properties.addMenuDetectListener(new MenuDetectListener()
        {
            @Override
            public void menuDetected(MenuDetectEvent menuDetectEvent)
            {
                properties.getMenu().setVisible(false);
                properties.update();
            }
        });
        menuService.registerContextMenu(properties, PropertiesPart.MENU_ID);
        PropertiesModel.INSTANCE.setTree(properties);

        extensions = checkPropertiesExtensionPoint();
    }

    /**
     * Clear all sub items.
     * 
     * @param item The item root.
     */
    public void clear(TreeItem item)
    {
        item.setData(null);
        for (final TreeItem current : item.getItems())
        {
            clear(current);
        }
        item.dispose();
    }

    /**
     * Set the focus.
     */
    @Focus
    public void setFocus()
    {
        properties.setFocus();
    }

    /**
     * Update the properties.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    boolean updateProperties(TreeItem item, Configurer configurer)
    {
        final Object data = item.getData();
        boolean updated = false;
        if (Configurer.CLASS.equals(data))
        {
            updated = updateClass(item, configurer);
        }
        else if (ConfigSurface.SURFACE_IMAGE.equals(data))
        {
            updated = updateSurface(item, configurer);
        }
        else if (ConfigSurface.SURFACE_ICON.equals(data))
        {
            updated = updateIcon(item, configurer);
        }
        else if (ConfigFrames.FRAMES_HORIZONTAL.equals(data) || ConfigFrames.FRAMES_VERTICAL.equals(data))
        {
            updated = updateFrames(item, configurer);
        }
        else if (ConfigAnimations.ANIMATION.equals(data))
        {
            final AnimationEditor animationEditor = new AnimationEditor(properties, configurer);
            animationEditor.open();
        }
        else if (ConfigCollisions.COLLISION.equals(data))
        {
            final EntityCollisionEditor collisionsEditor = new EntityCollisionEditor(properties, configurer);
            collisionsEditor.open();
        }
        return updated;
    }

    /**
     * Create the attribute class.
     * 
     * @param configurer The configurer reference.
     */
    private void createAttributeClass(final Configurer configurer)
    {
        final TreeItem classItem = new TreeItem(properties, SWT.NONE);
        classItem.setText(Messages.Properties_Class);
        classItem.setData(Configurer.CLASS);
        classItem.setImage(PropertiesPart.ICON_CLASS);

        final TreeItem className = new TreeItem(classItem, SWT.NONE);
        className.setText(configurer.getClassName());
        className.setData(Configurer.CLASS);
    }

    /**
     * Update the class.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    private boolean updateClass(TreeItem item, Configurer configurer)
    {
        final File file = Tools.selectClassFile(properties.getShell());
        if (file != null)
        {
            final XmlNode root = configurer.getRoot();
            final XmlNode classeNode = root.getChild(Configurer.CLASS);
            final String clazz = Tools.getClass(file).getName();
            classeNode.setText(clazz);
            item.setText(clazz);
            return true;
        }
        return false;
    }

    /**
     * Update the surface.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    private boolean updateSurface(TreeItem item, Configurer configurer)
    {
        final String file = Tools.selectFile(properties.getShell(), configurer.getPath(), true);
        if (file != null)
        {
            final XmlNode root = configurer.getRoot();
            final XmlNode surfaceNode = root.getChild(ConfigSurface.SURFACE);
            surfaceNode.writeString(ConfigSurface.SURFACE_IMAGE, file);
            item.setText(file);
            return true;
        }
        return false;
    }

    /**
     * Update the icon.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    private boolean updateIcon(TreeItem item, Configurer configurer)
    {
        final String file = Tools.selectFile(properties.getShell(), configurer.getPath(), true);
        if (file != null)
        {
            final XmlNode root = configurer.getRoot();
            final XmlNode surfaceNode = root.getChild(ConfigSurface.SURFACE);
            surfaceNode.writeString(ConfigSurface.SURFACE_ICON, file);
            item.setText(file);
            return true;
        }
        return false;
    }

    /**
     * Update the frames.
     * 
     * @param item The item reference.
     * @param configurer The configurer reference.
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    private boolean updateFrames(TreeItem item, Configurer configurer)
    {
        final InputDialog frames = new InputDialog(properties.getShell(), "Frames", "Frames number", "1",
                new InputValidator("[1-9][0-9]*", "Invalid frames number !"));
        if (frames.open() == Window.OK)
        {
            final XmlNode root = configurer.getRoot();
            final XmlNode surfaceNode = root.getChild(ConfigSurface.SURFACE);
            surfaceNode.writeString((String) item.getData(), frames.getValue());
            item.setText(frames.getValue());
            return true;
        }
        return false;
    }

    /**
     * Check the properties extension point.
     * 
     * @return The properties instance from extension point or default one.
     */
    private List<PropertiesListener> checkPropertiesExtensionPoint()
    {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                PropertiesListener.EXTENSION_ID);
        final List<PropertiesListener> extensions = new ArrayList<>(1);
        if (elements.length > 0)
        {
            final String properties = elements[0].getAttribute(PropertiesPart.EXTENSION_PROPERTIES);
            if (properties != null)
            {
                extensions.add(UtilEclipse.getPart(partService, properties, PropertiesListener.class));
            }
        }
        return extensions;
    }

    /*
     * PropertiesListener
     */

    @Override
    public void setInput(Configurer configurer)
    {
        for (final TreeItem item : properties.getItems())
        {
            clear(item);
        }
        properties.setData(configurer);
        if (configurer != null)
        {
            createAttributeClass(configurer);
            final XmlNode root = configurer.getRoot();
            if (root.hasChild(ConfigSurface.SURFACE))
            {
                createAttributeSurface(configurer);
            }
            if (root.hasChild(ConfigFrames.FRAMES))
            {
                createAttributeFrames(configurer);
            }
            if (root.hasChild(ConfigAnimations.ANIMATION))
            {
                createAttributeAnimations();
            }
            if (root.hasChild(ConfigCollisions.COLLISION))
            {
                createAttributeCollisions();
            }
        }
        for (final PropertiesListener property : extensions)
        {
            property.setInput(configurer);
        }
    }
}
