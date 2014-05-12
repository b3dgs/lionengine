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
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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

    /** Palette combo. */
    Combo paletteCombo;
    /** Race combo. */
    Combo raceCombo;
    /** Category combo. */
    Combo categoryCombo;
    /** Theme combo. */
    Combo themeCombo;
    /** Bottom composite. */
    Composite bottom;
    /** Entities composite. */
    Composite entitiesComposite;

    /**
     * Create the composite.
     * 
     * @param parent The parent reference.
     */
    @PostConstruct
    public void createComposite(Composite parent)
    {
        parent.setLayout(new GridLayout(1, false));

        createTop(parent);

        final Label separatorHeader = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createMiddle(parent);

        final Label separatorMiddle = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorMiddle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createBottom(parent);
    }

    /**
     * Create the top part, almost dedicated to palette selection.
     * 
     * @param parent The composite parent.
     */
    private void createTop(Composite parent)
    {
        final Composite top = new Composite(parent, SWT.NONE);
        top.setLayout(new GridLayout(2, false));

        final Label paletteLabel = new Label(top, SWT.NONE);
        paletteLabel.setText("Palette");

        paletteCombo = new Combo(top, SWT.NONE);
    }

    /**
     * Create the race selection.
     * 
     * @param middle The composite parent.
     */
    private void createRaceSelection(Composite middle)
    {
        final Composite raceComposite = new Composite(middle, SWT.NONE);
        raceComposite.setLayout(new GridLayout(2, false));
        final Label raceLabel = new Label(raceComposite, SWT.NONE);
        raceLabel.setText("Race");
        raceCombo = new Combo(raceComposite, SWT.NONE);
        raceCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final String selection = raceCombo.getItem(raceCombo.getSelectionIndex());
                final Object data = raceCombo.getData(selection);
                if (data instanceof File)
                {
                    FactoryEntityPart.load((File) data, categoryCombo);
                }
            }
        });
    }

    /**
     * Create the category selection.
     * 
     * @param middle The composite parent.
     */
    private void createCategorySelection(Composite middle)
    {
        final Composite categoryComposite = new Composite(middle, SWT.NONE);
        categoryComposite.setLayout(new GridLayout(2, false));
        final Label categoryLabel = new Label(categoryComposite, SWT.NONE);
        categoryLabel.setText("Category");
        categoryCombo = new Combo(categoryComposite, SWT.NONE);
        categoryCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final String selection = categoryCombo.getItem(categoryCombo.getSelectionIndex());
                final Object data = categoryCombo.getData(selection);
                if (data instanceof File)
                {
                    FactoryEntityPart.load((File) data, themeCombo);
                }
            }
        });
    }

    /**
     * Create the theme selection.
     * 
     * @param middle The composite parent.
     */
    private void createThemeSelection(Composite middle)
    {
        final Composite themeComposite = new Composite(middle, SWT.NONE);
        themeComposite.setLayout(new GridLayout(2, false));
        final Label themeLabel = new Label(themeComposite, SWT.NONE);
        themeLabel.setText("Theme");
        themeCombo = new Combo(themeComposite, SWT.NONE);
        themeCombo.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final String selection = themeCombo.getItem(themeCombo.getSelectionIndex());
                final Object data = themeCombo.getData(selection);
                if (data instanceof File)
                {
                    if (entitiesComposite != null)
                    {
                        entitiesComposite.dispose();
                    }
                    entitiesComposite = new Composite(bottom, SWT.NONE);
                    entitiesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                    entitiesComposite.setLayout(new GridLayout(4, true));

                    final FactoryObjectGame<?, ?> factoryEntity = WorldViewModel.INSTANCE.getFactoryEntity();
                    loadEntities(factoryEntity, (File) data);

                    if (!bottom.isDisposed())
                    {
                        bottom.pack();
                    }
                }
            }
        });
    }

    /**
     * Create the middle part, dedicated to entity group selection.
     * 
     * @param parent The composite parent.
     */
    private void createMiddle(Composite parent)
    {
        final Composite middle = new Composite(parent, SWT.NONE);
        middle.setLayout(new GridLayout(1, false));

        createRaceSelection(middle);
        createCategorySelection(middle);
        createThemeSelection(middle);
    }

    /**
     * Create the bottom part, dedicated to the entity list.
     * 
     * @param parent The composite parent.
     */
    private void createBottom(Composite parent)
    {
        bottom = new Composite(parent, SWT.NONE);
        bottom.setLayout(new GridLayout(1, false));
    }

    /**
     * Set the factory entity used.
     * 
     * @param factoryEntity The factory entity reference.
     */
    public void setFactoryEntity(FactoryObjectGame<?, ?> factoryEntity)
    {
        final File entitiesPath = new File(Project.getActive().getResourcesPath(), factoryEntity.getFolder());
        FactoryEntityPart.load(entitiesPath, raceCombo);

        if (!bottom.isDisposed())
        {
            bottom.update();
        }
    }

    /**
     * Load elements from root folder.
     * 
     * @param path The folder path.
     * @param combo The combo reference.
     */
    static void load(File path, Combo combo)
    {
        final File[] folders = path.listFiles();
        if (folders != null)
        {
            final List<File> elements = new ArrayList<>(1);
            for (final File folder : folders)
            {
                if (folder.isDirectory())
                {
                    elements.add(folder);
                }
            }
            final File[] items = elements.toArray(new File[elements.size()]);
            final String[] names = new String[items.length];
            for (int i = 0; i < items.length; i++)
            {
                final String name = UtilConversion.toTitleCaseWord(items[i].getName());
                combo.setData(name, items[i]);
                names[i] = name;
            }
            combo.setItems(names);
        }
    }

    /**
     * Load all entities from their folder (filter them by extension, <code>*.xml</code> expected).
     * 
     * @param factoryEntity The factory entity reference.
     * @param themePath The theme folder path.
     */
    void loadEntities(FactoryObjectGame<?, ?> factoryEntity, File themePath)
    {
        final File[] entityFiles = themePath.listFiles();
        if (entityFiles != null)
        {
            for (final File entityFile : entityFiles)
            {
                if (entityFile.isFile() && UtilFile.isType(entityFile, FactoryObjectGame.FILE_DATA_EXTENSION))
                {
                    loadEntity(factoryEntity, entityFile);
                }
            }
        }
    }

    /**
     * Load an entity from its file data, and add it to the tab.
     * 
     * @param factoryEntity The factory entity reference.
     * @param file The entity data file.
     */
    private void loadEntity(FactoryObjectGame<?, ?> factoryEntity, File file)
    {
        final Project project = Project.getActive();
        final File classesPath = project.getClassesPath();

        final Label entityLabel = new Label(entitiesComposite, SWT.NONE);
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
