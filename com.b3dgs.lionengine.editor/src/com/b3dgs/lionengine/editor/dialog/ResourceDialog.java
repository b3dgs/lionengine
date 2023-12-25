/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.editor.dialog;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.editor.project.ProjectModel;
import com.b3dgs.lionengine.editor.project.ProjectTreeCreator;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.UtilTree;
import com.b3dgs.lionengine.editor.utility.control.UtilText;

/**
 * Represents the resource dialog, allowing to select resources from project folder.
 */
public class ResourceDialog extends DialogAbstract
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "resource.png");
    /** Dialog width. */
    private static final int DIALOG_WIDTH = 512;
    /** Dialog height. */
    private static final int DIALOG_HEIGHT = 640;

    /**
     * Expand items.
     * 
     * @param tree The tree reference.
     */
    private static void expand(Tree tree)
    {
        for (final TreeItem item : tree.getItems())
        {
            if (!item.isDisposed())
            {
                item.setExpanded(true);
                for (final TreeItem child : item.getItems())
                {
                    child.setExpanded(true);
                }
            }
        }
    }

    /** Selection. */
    private final Collection<Media> selection = new HashSet<>();
    /** Selection flag. */
    private final int flag;
    /** Allowed file type. */
    private final Type type;
    /** Allowed extensions. */
    private final Collection<String> extensions;
    /** Open save flag. */
    private final boolean openSave;
    /** Filename. */
    private Text filename;

    /**
     * Create the dialog.
     * 
     * @param parent The parent shell.
     * @param multiple <code>true</code> for multiple selection, <code>false</code> for single.
     * @param openSave <code>true</code> for open mode, <code>false</code> for save mode.
     * @param type The file type filter.
     * @param extensions The allowed extensions, empty if all.
     */
    public ResourceDialog(Shell parent, boolean multiple, boolean openSave, Type type, String... extensions)
    {
        super(parent,
              Messages.ResourceDialog_Title,
              Messages.ResourceDialog_HeaderTitle,
              Messages.ResourceDialog_HeaderDesc,
              ICON);

        this.type = type;
        this.extensions = new HashSet<>(Arrays.asList(extensions));
        this.openSave = openSave;
        if (multiple)
        {
            flag = SWT.BORDER | SWT.CHECK | SWT.MULTI;
        }
        else
        {
            flag = SWT.BORDER | SWT.SINGLE;
        }

        createDialog();
        dialog.setMinimumSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        finish.setEnabled(true);
    }

    /**
     * Get the selected files.
     * 
     * @return The selected tiles.
     */
    public Collection<Media> getSelection()
    {
        return Collections.unmodifiableCollection(selection);
    }

    /**
     * Check if item must be removed due to filter.
     * 
     * @param item The item to check.
     */
    private void check(TreeItem item)
    {
        for (final TreeItem next : item.getItems())
        {
            check(next);
        }
        final Object data = item.getData();
        if (data != null)
        {
            final File file = ((Media) item.getData()).getFile();
            final boolean wrongType = Type.FOLDER == type && file.isFile();
            final boolean wrongExtension = Type.FILE.accept(file)
                                           && !extensions.isEmpty()
                                           && !extensions.contains(UtilFile.getExtension(file));
            if (wrongType || wrongExtension)
            {
                item.dispose();
            }
        }
    }

    /**
     * Update selection with selected element.
     * 
     * @param item The selected item.
     * @param selected The selected media.
     */
    private void updateSelection(TreeItem item, Media selected)
    {
        if (selected != null)
        {
            if (Type.FILE == type && selected.getFile().isDirectory())
            {
                for (final TreeItem child : item.getItems())
                {
                    child.setChecked(item.getChecked());
                }
            }
            if (selection.contains(selected))
            {
                selection.remove(selected);
            }
            else
            {
                selection.add(selected);
            }
        }
    }

    @Override
    protected void createContent(Composite content)
    {
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Tree tree = new Tree(content, flag);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        tree.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseDoubleClick(MouseEvent mouseEvent)
            {
                UtilTree.expandOnDoubleClick(tree);
            }
        });

        final ProjectTreeCreator creator = new ProjectTreeCreator(ProjectModel.INSTANCE.getProject());
        creator.create(tree);

        for (final TreeItem item : tree.getItems())
        {
            check(item);
        }

        tree.layout();
        tree.getDisplay().asyncExec(() -> expand(tree));
        tree.addSelectionListener(new SelectionAdapter()
        {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                final TreeItem item = (TreeItem) event.item;
                Media selected = (Media) item.getData();
                if (!openSave && selected != null)
                {
                    selection.clear();
                    selected = Medias.create(selected.getPath(), filename.getText());
                }
                updateSelection(item, selected);
            }
        });

        if (!openSave)
        {
            filename = UtilText.create(Messages.ResourceDialog_Filename, content);
            filename.setText(Constant.EMPTY_STRING);
        }
    }

    @Override
    protected void onCanceled()
    {
        selection.clear();
    }

    @Override
    protected void onFinish()
    {
        if (!openSave && Type.FOLDER == type)
        {
            final String extension = Constant.DOT
                                     + (extensions.isEmpty() ? Constant.EMPTY_STRING : extensions.iterator().next());
            final Media selected = Medias.create(selection.iterator().next().getPath(), filename.getText() + extension);
            selection.clear();
            selection.add(selected);
        }
    }

    /**
     * File type filtering.
     */
    public enum Type
    {
        /** File only. */
        FILE(true, false),
        /** Folder only. */
        FOLDER(false, true),
        /** Any type. */
        ANY(true, true);

        /** Accept file. */
        private final boolean file;
        /** Accept folder. */
        private final boolean folder;

        /**
         * Create type.
         * 
         * @param file Accept file.
         * @param folder Accept folder.
         */
        Type(boolean file, boolean folder)
        {
            this.file = file;
            this.folder = folder;
        }

        /**
         * Check if file is accepted.
         * 
         * @param element The element to check.
         * @return <code>true</code> if accepted.
         */
        public boolean accept(File element)
        {
            return element.isFile() && file || element.isDirectory() && folder;
        }
    }
}
