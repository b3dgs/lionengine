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
package com.b3dgs.lionengine.editor.dialogs;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.factory.FactoryEntityPart;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.file.XmlNodeNotFoundException;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.ObjectGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.purview.Configurable;

/**
 * Represents the entity edition dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EntityEditDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = Activator.getIcon("dialog", "edit-entity.png");

    /** Entity media. */
    final Media entity;
    /** Entity configurable. */
    final Configurable configurable;

    /**
     * Constructor.
     * 
     * @param parent The parent shell.
     * @param entity The entity media.
     */
    public EntityEditDialog(Shell parent, Media entity)
    {
        super(parent, Messages.EditEntityDialog_Title, Messages.EditEntityDialog_HeaderTitle,
                Messages.EditEntityDialog_HeaderDesc, EntityEditDialog.ICON);
        this.entity = entity;
        configurable = EntityEditDialog.getConfigurable(entity);
        createDialog();
        finish.setEnabled(true);
    }

    /**
     * Get the configurable from the entity descriptor.
     * 
     * @param entity The entity descriptor.
     * @return The entity configurable reference.
     */
    private static Configurable getConfigurable(Media entity)
    {
        final String entityName = entity.getFile().getName().replace("." + FactoryObjectGame.FILE_DATA_EXTENSION, "");
        final Class<?> entityClass = FactoryEntityPart.getEntityClass(entityName);
        final FactoryObjectGame<?, ?> factory = WorldViewModel.INSTANCE.getFactoryEntity();
        final SetupGame setup = factory.getSetup(entityClass, ObjectGame.class);
        return setup.configurable;
    }

    /**
     * Create the entity header.
     * 
     * @param parent The composite parent.
     * @param entity The entity media.
     */
    private void createEntityHeader(Composite parent, Media entity)
    {
        final Composite entityHeader = new Composite(parent, SWT.BORDER);
        entityHeader.setLayout(new GridLayout(3, false));
        entityHeader.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        final Label entityIcon = new Label(entityHeader, SWT.NONE);
        entityIcon.setImage(getEntityIcon(parent, entity));

        final Label entityName = new Label(entityHeader, SWT.NONE);
        entityName.setText(UtilConversion.toTitleCase(UtilFile.removeExtension(entity.getFile().getName())));

        final Group assignArea = new Group(entityHeader, SWT.SHADOW_IN);
        assignArea.setLayout(new GridLayout(2, true));
        assignArea.setText("Attributes");
        createEntityAssignSurface(assignArea);
        createEntityAssignIcon(assignArea);
    }

    /**
     * Create the assign surface area.
     * 
     * @param parent The composite parent.
     */
    private void createEntityAssignSurface(final Composite parent)
    {
        final Button assignSurface = new Button(parent, SWT.PUSH);
        assignSurface.setText("Assign surface");
        assignSurface.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final String file = selectFile(parent.getShell(), entity.getFile().getParentFile().getPath());
                if (file != null)
                {
                    final XmlNode root = configurable.getDataRoot();
                    XmlNode surfaceNode;
                    try
                    {
                        surfaceNode = root.getChild("lionengine:surface");
                    }
                    catch (final XmlNodeNotFoundException exception)
                    {
                        surfaceNode = com.b3dgs.lionengine.file.File.createXmlNode("lionengine:surface");
                        root.add(surfaceNode);
                    }
                    surfaceNode.writeString("image", file);
                }
            }
        });
    }

    /**
     * Select a file from a dialog and return its path relative to the starting path.
     * 
     * @param shell The shell parent.
     * @param path The starting path.
     * @return The selected file path.
     */
    String selectFile(Shell shell, String path)
    {
        final FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
        fileDialog.setFilterPath(path);
        final String file = fileDialog.open();
        if (file != null)
        {
            final Path reference = Paths.get(new File(path).toURI());
            final Path target = Paths.get(new File(file).toURI());
            return reference.relativize(target).toString();
        }
        return null;
    }

    /**
     * Create the assign icon area.
     * 
     * @param parent The composite parent.
     */
    private void createEntityAssignIcon(final Composite parent)
    {
        final Button assignIcon = new Button(parent, SWT.PUSH);
        assignIcon.setText("Assign icon");
        assignIcon.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final String file = selectFile(parent.getShell(), entity.getFile().getParentFile().getPath());
                if (file != null)
                {
                    final XmlNode root = configurable.getDataRoot();
                    XmlNode surfaceNode;
                    try
                    {
                        surfaceNode = root.getChild("lionengine:surface");
                    }
                    catch (final XmlNodeNotFoundException exception)
                    {
                        surfaceNode = com.b3dgs.lionengine.file.File.createXmlNode("lionengine:surface");
                        root.add(surfaceNode);
                    }
                    surfaceNode.writeString("icon", file);
                }
            }
        });
    }

    /**
     * Get the entity icon.
     * 
     * @param parent The composite parent.
     * @param entity The entity media.
     * @return The entity real icon, or an extract from its image if has, else <code>null</code>.
     */
    private Image getEntityIcon(Composite parent, Media entity)
    {
        try
        {
            final String iconName = configurable.getDataString("icon", "lionengine:surface");
            final File iconFile = new File(entity.getFile().getParent(), iconName);
            if (iconFile.isFile())
            {
                return new Image(parent.getDisplay(), iconFile.getPath());
            }
        }
        catch (final LionEngineException exception)
        {
            return null;
        }
        return null;
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        createEntityHeader(content, entity);
    }

    @Override
    protected void onFinish()
    {
        // Nothing to do
    }
}
