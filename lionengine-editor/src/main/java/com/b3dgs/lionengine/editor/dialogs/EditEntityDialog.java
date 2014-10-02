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

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.editor.InputValidator;
import com.b3dgs.lionengine.editor.Tools;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.UtilSwt;
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
    private static final Image ICON = UtilEclipse.getIcon("dialog", "edit-entity.png");

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
        configurable = new Configurable();
        configurable.load(entity);
        createDialog();
        finish.setEnabled(true);
    }

    /**
     * Update the element node.
     * 
     * @param parent The parent shell.
     * @param element The element node name.
     */
    void updateElement(Shell parent, String element)
    {
        final String file = Tools.selectFile(parent, entity.getFile().getParentFile().getPath(), true);
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
            final String iconName = configurable.getString(Configurable.SURFACE_ICON, Configurable.SURFACE);
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
     * Generate frames node from input values.
     * 
     * @param shell The shell parent.
     * @return <code>true</code> if generated, <code>false</code> if cancelled.
     */
    boolean generateFrames(Shell shell)
    {
        final InputDialog horizontalFrames = new InputDialog(shell, "Frames", "Number of horizontal frames", "1",
                new InputValidator("[1-9][0-9]*", "Invalid frames number !"));
        if (horizontalFrames.open() == Window.OK)
        {
            final InputDialog verticalFrames = new InputDialog(shell, "Frames", "Number of vertical frames", "1",
                    new InputValidator("[1-9][0-9]*", "Invalid frames number !"));
            if (verticalFrames.open() == Window.OK)
            {
                final XmlNode frames = Stream.createXmlNode(Configurable.FRAMES);
                frames.writeString(Configurable.FRAMES_HORIZONTAL, horizontalFrames.getValue());
                frames.writeString(Configurable.FRAMES_VERTICAL, verticalFrames.getValue());
                configurable.getRoot().add(frames);
                return true;
            }
        }
        return false;
    }

    /**
     * Set the entity icon and update the size if necessary.
     * 
     * @param icon The entity icon.
     */
    void setEntityIcon(Image icon)
    {
        if (icon != null)
        {
            final ImageData data = icon.getImageData();
            entityIcon.setLayoutData(new GridData(data.width, data.height));
            entityIcon.setImage(icon);
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
        setEntityIcon(getEntityIcon(parent, entity));

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

        createAssignButton(actions, Messages.EditEntityDialog_AssignSurface, Configurable.SURFACE_IMAGE);
        createAssignButton(actions, Messages.EditEntityDialog_AssignIcon, Configurable.SURFACE_ICON);

        final Button editAnimations = UtilSwt.createButton(actions, AnimationEditor.DIALOG_TITLE,
                AnimationEditor.DIALOG_ICON);
        editAnimations.setImage(AnimationEditor.DIALOG_ICON);
        editAnimations.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                if (configurable.hasSurface())
                {
                    boolean show = true;
                    if (!configurable.hasFrames())
                    {
                        show = generateFrames(dialog);
                    }
                    if (show)
                    {
                        final AnimationEditor animationEditor = new AnimationEditor(parent, configurable);
                        animationEditor.open();
                    }
                }
                else
                {
                    MessageDialog.openWarning(dialog, "Error", "Surface not found, assign it first !");
                }
            }
        });

        final Button editCollisions = UtilSwt.createButton(actions, EntityCollisionEditor.DIALOG_TITLE, null);
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
        final Button assign = UtilSwt.createButton(parent, name, null);
        assign.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent)
            {
                updateElement(dialog, element);
                if (Configurable.SURFACE_ICON.equals(element))
                {
                    final Image image = entityIcon.getImage();
                    if (image != null)
                    {
                        image.dispose();
                    }
                    setEntityIcon(getEntityIcon(parent, entity));
                    parent.layout(true, true);
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
