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
package com.b3dgs.lionengine.editor.factory;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionengine.game.SetupGame;

/**
 * Represents the factory entity view, where the entities list is displayed.
 * 
 * @author Pierre-Alexandre
 */
public class FactoryEntityPart
{
    /** ID. */
    public static final String ID = Activator.PLUGIN_ID + ".part.factory-entity";

    /**
     * Load the entity icon if has.
     * 
     * @param entityLabel The entity label reference.
     * @param file The entity data file.
     * @param setup The entity setup reference.
     * @throws LionEngineException If an error occurred when loading the icon.
     */
    private static void loadEntityIcon(Label entityLabel, File file, SetupGame setup) throws LionEngineException
    {
        try
        {
            final String icon = setup.configurable.getDataString("icon");
            final File iconPath = new File(file.getParentFile(), icon);
            if (iconPath.isFile())
            {
                final ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconPath.toURI().toURL());
                entityLabel.setImage(descriptor.createImage());
            }
        }
        catch (final MalformedURLException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /** Composite. */
    private Composite composite;
    /** Entity tabs. */
    private TabFolder entityTab;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(Composite parent)
    {
        composite = parent;
    }

    /**
     * Update the view.
     */
    public void update()
    {
        if (entityTab != null)
        {
            entityTab.dispose();
        }
        entityTab = new TabFolder(composite, SWT.NONE);

        final FactoryObjectGame<?, ?> factoryEntity = WorldViewModel.INSTANCE.getFactoryEntity();
        final File entitiesPath = new File(Project.getActive().getResourcesPath(), factoryEntity.getFolder());

        loadFolder(factoryEntity, entitiesPath);

        if (!composite.isDisposed())
        {
            composite.pack();
        }

        // Hack to avoid a layout problem with RowLayout (fill only one row...)
        final Point point = composite.getShell().getSize();
        composite.getShell().setSize(point.x + 10, point.y);
        composite.getShell().layout(true);
        composite.getShell().setSize(point.x, point.y);
        composite.getShell().layout(true);
    }

    /**
     * Force focus.
     */
    @Focus
    public void focus()
    {
        composite.forceFocus();
    }

    /**
     * Load an entities folder. If this folder contains sub folder, they will be loaded in sub tabs.
     * 
     * @param factoryEntity The factory entity reference.
     * @param entitiesPath The entities folder path.
     */
    private void loadFolder(FactoryObjectGame<?, ?> factoryEntity, File entitiesPath)
    {
        final File[] elements = entitiesPath.listFiles();
        if (elements != null)
        {
            for (final File element : elements)
            {
                final TabItem category = new TabItem(entityTab, SWT.NONE);
                category.setText(UtilConversion.toTitleCaseWord(element.getName()));

                final Composite composite = new Composite(entityTab, SWT.NONE);
                composite.setLayout(new RowLayout(SWT.HORIZONTAL));

                loadEntities(factoryEntity, element, composite);
                category.setControl(composite);
            }
        }
    }

    /**
     * Load all entities from their folder (filter them by extension, <code>*.xml</code> expected).
     * 
     * @param factoryEntity The factory entity reference.
     * @param folder The entity folder.
     * @param composite The composite category reference.
     */
    private void loadEntities(FactoryObjectGame<?, ?> factoryEntity, File folder, Composite composite)
    {
        final File[] elements = folder.listFiles();
        if (elements != null)
        {
            for (final File element : elements)
            {
                if (element.isFile() && UtilFile.isType(element, FactoryObjectGame.FILE_DATA_EXTENSION))
                {
                    loadEntity(factoryEntity, element, composite);
                }
                else if (element.isDirectory())
                {
                    loadFolder(factoryEntity, element);
                }
            }
        }
    }

    /**
     * Load an entity from its file data, and add it to the tab.
     * 
     * @param factoryEntity The factory entity reference.
     * @param file The entity data file.
     * @param composite The composite category reference.
     */
    private void loadEntity(FactoryObjectGame<?, ?> factoryEntity, File file, Composite composite)
    {
        final Project project = Project.getActive();
        final File classesPath = project.getClassesPath();

        final Label entityLabel = new Label(composite, SWT.NONE);
        final String name = UtilFile.removeExtension(file.getName());
        entityLabel.setText(name);

        final List<File> classNames = UtilFile.getFilesByName(classesPath, entityLabel.getText() + ".class");

        // TODO handle the case when there is multiple class with the same name
        if (classNames.size() == 1)
        {
            final String path = classNames.get(0).getPath();
            final Media classPath = project.getClassMedia(path);
            final Class<?> type = project.getClass(ObjectGame.class, classPath);
            final SetupGame setup = factoryEntity.getSetup(type, ObjectGame.class);

            FactoryEntityPart.loadEntityIcon(entityLabel, file, setup);
            entityLabel.setData(type);
        }
    }
}
