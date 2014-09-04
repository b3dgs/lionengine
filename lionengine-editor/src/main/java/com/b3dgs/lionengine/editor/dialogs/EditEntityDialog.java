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
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.animation.AnimationEditor;
import com.b3dgs.lionengine.editor.collision.EntityCollisionEditor;
import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Represents the entity edition dialog.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class EditEntityDialog
        extends AbstractDialog
{
    /** Icon. */
    private static final Image ICON = Tools.getIcon("dialog", "edit-entity.png");

    /** Entity media. */
    final Media entity;
    /** Entity configurable. */
    final Configurable configurable;
    /** Entity icon. */
    Label entityIcon;

    /**
     * Constructor.
     * 
     * @param parent The parent shell.
     * @param entity The entity media.
     */
    public EditEntityDialog(Shell parent, Media entity)
    {
        super(parent, Messages.EditEntityDialog_Title, Messages.EditEntityDialog_HeaderTitle,
                Messages.EditEntityDialog_HeaderDesc, EditEntityDialog.ICON);
        this.entity = entity;
        configurable = Tools.getConfigurable(entity);
        configurable.load(entity);
        createDialog();
        finish.setEnabled(true);
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
     * Update the element node.
     * 
     * @param parent The parent shell.
     * @param element The element node name.
     */
    void updateElement(Shell parent, String element)
    {
        final String file = selectFile(parent, entity.getFile().getParentFile().getPath());
        if (file != null)
        {
            final XmlNode root = configurable.getRoot();
            XmlNode surfaceNode;
            try
            {
                surfaceNode = root.getChild(Configurable.SURFACE);
            }
            catch (final LionEngineException exception)
            {
                surfaceNode = Stream.createXmlNode(Configurable.SURFACE);
                root.add(surfaceNode);
            }
            surfaceNode.writeString(element, file);
        }
    }

    /**
     * Get the entity icon.
     * 
     * @param parent The composite parent.
     * @param entity The entity media.
     * @return The entity real icon, or an extract from its image if has, else <code>null</code>.
     */
    Image getEntityIcon(Composite parent, Media entity)
    {
        try
        {
            final String iconName = configurable.getString("icon", Configurable.SURFACE);
            final File iconFile = new File(entity.getFile().getParent(), iconName);
            if (iconFile.isFile())
            {
                return new Image(parent.getDisplay(), iconFile.getPath());
            }
            return null;
        }
        catch (final LionEngineException exception)
        {
            return null;
        }
    }

    /**
     * Create the entity header.
     * 
     * @param parent The composite parent.
     */
    private void createEntityHeader(Composite parent)
    {
        final Composite entityHeader = new Composite(parent, SWT.BORDER);
        entityHeader.setLayout(new GridLayout(2, true));
        entityHeader.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));

        entityIcon = new Label(entityHeader, SWT.NONE);
        entityIcon.setImage(getEntityIcon(parent, entity));

        final Label entityName = new Label(entityHeader, SWT.NONE);
        entityName.setText(UtilConversion.toTitleCase(UtilFile.removeExtension(entity.getFile().getName())));
    }

    /**
     * Create the entity actions.
     * 
     * @param parent The composite parent.
     */
    private void createActions(final Composite parent)
    {
        final Composite actions = new Group(parent, SWT.NONE);
        actions.setLayout(new GridLayout(4, false));

        createAssignButton(actions, Messages.EditEntityDialog_AssignSurface, "image");
        createAssignButton(actions, Messages.EditEntityDialog_AssignIcon, "icon");

        final Button editAnimations = Tools.createButton(actions, AnimationEditor.DIALOG_TITLE, null);
        editAnimations.setImage(AnimationEditor.DIALOG_ICON);
        editAnimations.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final AnimationEditor animationEditor = new AnimationEditor(parent, configurable);
                animationEditor.open();
            }
        });

        final Button editCollisions = Tools.createButton(actions, EntityCollisionEditor.DIALOG_TITLE, null);
        editCollisions.setImage(EntityCollisionEditor.DIALOG_ICON);
        editCollisions.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                final EntityCollisionEditor entityCollisionEditor = new EntityCollisionEditor(parent, configurable);
                entityCollisionEditor.open();
            }
        });
    }

    /**
     * Create an assign button to an element.
     * 
     * @param parent The composite parent.
     * @param name The button name.
     * @param element The element node name.
     */
    private void createAssignButton(final Composite parent, final String name, final String element)
    {
        final Button assign = Tools.createButton(parent, name, null);
        assign.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                updateElement(parent.getShell(), element);
                if ("icon".equals(element))
                {
                    if (entityIcon.getImage() != null)
                    {
                        entityIcon.getImage().dispose();
                    }
                    entityIcon.setImage(getEntityIcon(parent, entity));
                }
            }
        });
    }

    /*
     * AbstractDialog
     */

    @Override
    protected void createContent(Composite content)
    {
        createEntityHeader(content);
        createActions(content);
    }

    @Override
    protected void onFinish()
    {
        Stream.saveXml(configurable.getRoot(), entity);
    }
}
