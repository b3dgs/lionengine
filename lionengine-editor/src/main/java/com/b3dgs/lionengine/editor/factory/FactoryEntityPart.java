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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.Tools;
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
     * Fill the combo items with its folder list.
     * 
     * @param typeCombo The combo reference.
     * @param folders The handled folders.
     */
    private static void fillCombo(Combo typeCombo, File[] folders)
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
            typeCombo.setData(name, items[i]);
            names[i] = name;
        }
        typeCombo.setItems(names);
    }

    /**
     * Create a combo from the type name.
     * 
     * @param typeName The type name.
     * @param parent The parent composite.
     * @return The combo instance.
     */
    private static Combo createCombo(String typeName, Composite parent)
    {
        final Composite typeComposite = new Composite(parent, SWT.NONE);
        typeComposite.setLayout(new GridLayout(2, false));
        final Label typeLabel = new Label(typeComposite, SWT.NONE);
        typeLabel.setText(typeName);
        final Combo typeCombo = new Combo(typeComposite, SWT.READ_ONLY);
        return typeCombo;
    }

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
            final String icon = setup.getConfigurable().getSurface().getIcon();
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

    /** The combo hierarchy. */
    final Map<String, Composite> hierarchy = new HashMap<>();
    /** Palette combo. */
    Combo paletteCombo;
    /** Middle composite. */
    Composite middle;
    /** Bottom composite. */
    Composite bottom;
    /** Entities composite. */
    Composite entitiesComposite;
    /** Last selected entity. */
    Label lastEntity;

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
     * Set the factory entity used.
     * 
     * @param factoryEntity The factory entity reference.
     */
    public void setFactoryEntity(FactoryObjectGame<?, ?> factoryEntity)
    {
        final File entitiesPath = new File(Project.getActive().getResourcesPath(), factoryEntity.getFolder());
        load(factoryEntity, entitiesPath, middle);
    }

    /**
     * Load elements from root folder.
     * 
     * @param factoryEntity The factory entity reference.
     * @param path The folder path.
     * @param parent The composite parent.
     * @return The created child composite.
     */
    Composite load(final FactoryObjectGame<?, ?> factoryEntity, File path, final Composite parent)
    {
        final File[] folders = path.listFiles();
        if (folders != null)
        {
            final String typeName = Tools.getEntitiesFolderTypeName(path);
            final Composite composite = new Composite(parent, SWT.NONE);
            composite.setLayout(new GridLayout(1, false));
            final Combo typeCombo = FactoryEntityPart.createCombo(typeName, composite);
            FactoryEntityPart.fillCombo(typeCombo, folders);

            middle.getShell().layout(true, true);

            typeCombo.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent selectionEvent)
                {
                    WorldViewModel.INSTANCE.setSelectedEntity(null);
                    lastEntity = null;
                    final Object data = typeCombo.getData(typeCombo.getItem(typeCombo.getSelectionIndex()));
                    if (data instanceof File)
                    {
                        if (entitiesComposite != null)
                        {
                            entitiesComposite.dispose();
                        }
                        final File typeFolder = (File) data;
                        try
                        {
                            if (hierarchy.containsKey(typeName))
                            {
                                hierarchy.get(typeName).dispose();
                                hierarchy.remove(typeName);
                            }
                            final Composite child = load(factoryEntity, typeFolder, composite);
                            if (child != null)
                            {
                                hierarchy.put(typeName, child);
                            }
                        }
                        catch (final LionEngineException exception)
                        {
                            entitiesComposite = new Composite(bottom, SWT.BORDER);
                            entitiesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
                            entitiesComposite.setLayout(new RowLayout());

                            final FactoryObjectGame<?, ?> factoryEntity = WorldViewModel.INSTANCE.getFactoryEntity();
                            loadEntities(factoryEntity, typeFolder);
                        }
                    }
                }
            });
            return composite;
        }
        return null;
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
        if (!bottom.isDisposed())
        {
            bottom.getShell().layout(true, true);
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
        final Label entityLabel = new Label(entitiesComposite, SWT.NONE);
        entityLabel.setLayoutData(new RowData(34, 34));
        entityLabel.setBackground(entityLabel.getDisplay().getSystemColor(SWT.COLOR_GRAY));
        final String name = UtilFile.removeExtension(file.getName());
        entityLabel.setText(name);

        entityLabel.addMouseTrackListener(new MouseTrackListener()
        {
            @Override
            public void mouseHover(MouseEvent e)
            {
                // Nothing to do
            }

            @Override
            public void mouseExit(MouseEvent e)
            {
                if (lastEntity != entityLabel)
                {
                    entityLabel.setBackground(entityLabel.getDisplay().getSystemColor(SWT.COLOR_GRAY));
                }
            }

            @Override
            public void mouseEnter(MouseEvent e)
            {
                entityLabel.setBackground(entityLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
            }
        });
        entityLabel.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseUp(MouseEvent mouseEvent)
            {
                if (lastEntity != null)
                {
                    lastEntity.setBackground(lastEntity.getDisplay().getSystemColor(SWT.COLOR_GRAY));
                }
                if (lastEntity == entityLabel)
                {
                    WorldViewModel.INSTANCE.setSelectedEntity(null);
                    lastEntity = null;
                }
                else
                {
                    WorldViewModel.INSTANCE.setSelectedEntity(Tools.getEntityClass(entityLabel.getText()));
                    entityLabel.setBackground(entityLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
                    lastEntity = entityLabel;
                }
            }

            @Override
            public void mouseDown(MouseEvent mouseEvent)
            {
                // Nothing to do
            }

            @Override
            public void mouseDoubleClick(MouseEvent mouseEvent)
            {
                // Nothing to do
            }
        });

        final Class<? extends ObjectGame> type = Tools.getEntityClass(entityLabel.getText());
        final SetupGame setup = factoryEntity.getSetup(type, ObjectGame.class);

        FactoryEntityPart.loadEntityIcon(entityLabel, file, setup);
        entityLabel.setToolTipText(name);
        entityLabel.setData(type);
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

        paletteCombo = new Combo(top, SWT.READ_ONLY);
    }

    /**
     * Create the middle part, dedicated to entity group selection.
     * 
     * @param parent The composite parent.
     */
    private void createMiddle(Composite parent)
    {
        middle = new Composite(parent, SWT.NONE);
        middle.setLayout(new GridLayout(1, false));
    }

    /**
     * Create the bottom part, dedicated to the entity list.
     * 
     * @param parent The composite parent.
     */
    private void createBottom(Composite parent)
    {
        bottom = new Composite(parent, SWT.NONE);
        bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        bottom.setLayout(new GridLayout(1, false));
    }
}
