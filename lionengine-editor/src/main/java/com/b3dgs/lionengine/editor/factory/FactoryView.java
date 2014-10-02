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
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.palette.PaletteView;
import com.b3dgs.lionengine.editor.project.EntitiesFolderTester;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.configurer.ConfigSurface;

/**
 * Represents the factory view, where the objects list is displayed.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryView
        implements PaletteView
{
    /** View ID. */
    public static final String ID = "factory";

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
        final GridLayout layout = new GridLayout(2, true);
        layout.marginHeight = 0;
        typeComposite.setLayout(layout);

        final Label typeLabel = new Label(typeComposite, SWT.NONE);
        typeLabel.setText(typeName);

        final Combo typeCombo = new Combo(typeComposite, SWT.READ_ONLY);
        return typeCombo;
    }

    /**
     * Load the object icon if has.
     * 
     * @param objectLabel The object label reference.
     * @param file The object data file.
     * @param setup The object setup reference.
     * @throws LionEngineException If an error occurred when loading the icon.
     */
    private static void loadObjectIcon(Label objectLabel, File file, SetupGame setup) throws LionEngineException
    {
        try
        {
            final ConfigSurface configSurface = ConfigSurface.create(setup.getConfigurer());
            final String icon = configSurface.getIcon();
            final File iconPath = new File(file.getParentFile(), icon);
            if (iconPath.isFile())
            {
                final ImageDescriptor descriptor = ImageDescriptor.createFromURL(iconPath.toURI().toURL());
                objectLabel.setImage(descriptor.createImage());
            }
        }
        catch (final MalformedURLException exception)
        {
            throw new LionEngineException(exception);
        }
    }

    /** The combo hierarchy. */
    final Map<String, Composite> hierarchy = new HashMap<>();
    /** The factory reference. */
    private FactoryObjectGame<?> factory;
    /** Middle composite. */
    Composite middle;
    /** Bottom composite. */
    Composite bottom;
    /** Objects composite. */
    Composite objectsComposite;
    /** Last selected object. */
    Label lastObject;

    /**
     * Set the factory object used.
     * 
     * @param factory The factory reference.
     */
    public void setFactory(FactoryObjectGame<?> factory)
    {
        this.factory = factory;
    }

    /**
     * Load elements from root folder.
     * 
     * @param factory The factory reference.
     * @param path The folder path.
     * @param parent The composite parent.
     * @return The created child composite.
     * @throws FileNotFoundException If not a type folder.
     */
    Composite load(final FactoryObjectGame<?> factory, File path, final Composite parent) throws FileNotFoundException
    {
        final File[] folders = path.listFiles();
        if (folders != null)
        {
            final String typeName = Tools.getObjectsFolderTypeName(path);
            final Composite composite = new Composite(parent, SWT.NONE);
            final GridLayout layout = new GridLayout(1, false);
            layout.marginHeight = 0;
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
            final Combo typeCombo = FactoryView.createCombo(typeName, composite);
            FactoryView.fillCombo(typeCombo, folders);

            middle.getShell().layout(true, true);

            typeCombo.addSelectionListener(new SelectionAdapter()
            {
                @Override
                public void widgetSelected(SelectionEvent selectionEvent)
                {
                    WorldViewModel.INSTANCE.setSelectedObject(null);
                    lastObject = null;
                    loadSub(typeCombo, typeName, composite);
                }
            });
            return composite;
        }
        return null;
    }

    /**
     * Load sub tree from current type.
     * 
     * @param typeCombo The type combo.
     * @param typeName The current type name.
     * @param composite The composite reference.
     */
    void loadSub(Combo typeCombo, String typeName, Composite composite)
    {
        final Object data = typeCombo.getData(typeCombo.getItem(typeCombo.getSelectionIndex()));
        if (data instanceof File)
        {
            if (objectsComposite != null)
            {
                objectsComposite.dispose();
            }
            final File typeFolder = (File) data;
            try
            {
                if (hierarchy.containsKey(typeName))
                {
                    hierarchy.get(typeName).dispose();
                    hierarchy.remove(typeName);
                }
                final Composite child = load(factory, typeFolder, composite);
                if (child != null)
                {
                    hierarchy.put(typeName, child);
                }
            }
            catch (final FileNotFoundException exception)
            {
                createObjects(typeFolder.listFiles());
            }
        }
    }

    /**
     * Create the objects list from their file.
     * 
     * @param objectsFile The objects file.
     */
    void createObjects(File[] objectsFile)
    {
        objectsComposite = new Composite(bottom, SWT.BORDER);
        objectsComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        objectsComposite.setLayout(new RowLayout());

        final FactoryObjectGame<?> factory = WorldViewModel.INSTANCE.getFactory();
        loadObjects(factory, objectsFile);
    }

    /**
     * Load all objects from their folder (filter them by extension, <code>*.xml</code> expected).
     * 
     * @param factory The factory reference.
     * @param objectFiles The objects path.
     */
    void loadObjects(FactoryObjectGame<?> factory, File[] objectFiles)
    {
        if (objectFiles != null)
        {
            for (final File objectFile : objectFiles)
            {
                if (objectFile.isFile() && EntitiesFolderTester.isEntityFile(objectFile))
                {
                    loadObject(factory, objectFile);
                }
            }
        }
        if (!bottom.isDisposed())
        {
            bottom.getShell().layout(true, true);
        }
    }

    /**
     * Load an object from its file data, and add it to the tab.
     * 
     * @param factory The factory reference.
     * @param file The object data file.
     */
    private void loadObject(FactoryObjectGame<?> factory, File file)
    {
        final Label objectLabel = new Label(objectsComposite, SWT.NONE);
        objectLabel.setLayoutData(new RowData(34, 34));
        objectLabel.setBackground(objectLabel.getDisplay().getSystemColor(SWT.COLOR_GRAY));
        final String name = UtilFile.removeExtension(file.getName());
        objectLabel.setText(name);

        objectLabel.addMouseTrackListener(new MouseTrackListener()
        {
            @Override
            public void mouseHover(MouseEvent e)
            {
                // Nothing to do
            }

            @Override
            public void mouseExit(MouseEvent e)
            {
                if (lastObject != objectLabel)
                {
                    objectLabel.setBackground(objectLabel.getDisplay().getSystemColor(SWT.COLOR_GRAY));
                }
            }

            @Override
            public void mouseEnter(MouseEvent e)
            {
                objectLabel.setBackground(objectLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
            }
        });
        objectLabel.addMouseListener(new MouseListener()
        {
            @Override
            public void mouseUp(MouseEvent mouseEvent)
            {
                if (lastObject != null)
                {
                    lastObject.setBackground(lastObject.getDisplay().getSystemColor(SWT.COLOR_GRAY));
                }
                if (lastObject == objectLabel)
                {
                    WorldViewModel.INSTANCE.setSelectedObject(null);
                    lastObject = null;
                }
                else
                {
                    final Object data = objectLabel.getData();
                    if (data instanceof Media)
                    {
                        WorldViewModel.INSTANCE.setSelectedObject((Media) data);
                        objectLabel.setBackground(objectLabel.getDisplay().getSystemColor(SWT.COLOR_BLACK));
                        lastObject = objectLabel;
                    }
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

        final Media media = UtilityMedia.get(file);
        final SetupGame setup = factory.getSetup(media);

        FactoryView.loadObjectIcon(objectLabel, file, setup);
        objectLabel.setToolTipText(name);
        objectLabel.setData(media);
    }

    /**
     * Create the middle part, dedicated to group group selection.
     * 
     * @param parent The composite parent.
     */
    private void createMiddle(Composite parent)
    {
        middle = new Composite(parent, SWT.NONE);
        middle.setLayout(new GridLayout(1, false));
    }

    /**
     * Create the bottom part, dedicated to the object list.
     * 
     * @param parent The composite parent.
     */
    private void createBottom(Composite parent)
    {
        bottom = new Composite(parent, SWT.NONE);
        bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        bottom.setLayout(new GridLayout(1, false));
    }

    /*
     * PaletteView
     */

    @Override
    public void create(Composite parent)
    {
        parent.setLayout(new GridLayout(1, false));

        createMiddle(parent);

        final Label separatorMiddle = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        separatorMiddle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        createBottom(parent);

        final File objectsPath = new File(Project.getActive().getResourcesPath(), factory.getFolder());
        try
        {
            load(factory, objectsPath, middle);
        }
        catch (final FileNotFoundException exception)
        {
            createObjects(objectsPath.listFiles());
        }
    }

    @Override
    public String getId()
    {
        return FactoryView.ID;
    }
}
