/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.collision.map.assign;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.b3dgs.lionengine.editor.collision.CollisionVerifier;
import com.b3dgs.lionengine.editor.dialog.DialogAbstract;
import com.b3dgs.lionengine.editor.utility.UtilIcon;
import com.b3dgs.lionengine.editor.utility.control.UtilButton;
import com.b3dgs.lionengine.editor.utility.control.UtilText;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.view.WorldPart;

/**
 * Allows to configure the assigned collision from mouse on map.
 */
public class MapCollisionAssignDialog extends DialogAbstract
{
    /** Icon. */
    private static final Image ICON = UtilIcon.get("dialog", "assign-collision.png");
    /** Default offset value. */
    private static final String DEFAULT_OFFSET = "0";
    /** Dialog width. */
    private static final int DIALOG_WIDTH = 320;
    /** Dialog height. */
    private static final int DIALOG_HEIGHT = 160;

    /** Collision verifier. */
    private final CollisionVerifier verifier;

    /**
     * Create an import map dialog.
     * 
     * @param parent The shell parent.
     * @param verifier The verifier verifier.
     */
    public MapCollisionAssignDialog(Shell parent, CollisionVerifier verifier)
    {
        super(parent, Messages.Title, Messages.HeaderTitle, Messages.HeaderDesc, ICON);
        this.verifier = verifier;
        createDialog();
        dialog.setMinimumSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        finish.setEnabled(false);
        finish.forceFocus();
    }

    /**
     * Create the offset area.
     * 
     * @param content The content composite.
     */
    private void createOffsetArea(Composite content)
    {
        final Composite offsetArea = new Composite(content, SWT.NONE);
        offsetArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        final int items = 3;
        offsetArea.setLayout(new GridLayout(items, false));

        final Label offsetLabel = new Label(offsetArea, SWT.NONE);
        offsetLabel.setText(Messages.Offset);

        final Text offset = new Text(offsetArea, SWT.BORDER);
        offset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        UtilText.addVerify(offset, InputValidator.INTEGER_POSITIVE_MATCH);
        offset.setText(DEFAULT_OFFSET);

        final Button check = createCheckButton(offsetArea, offset);
        offset.addModifyListener(event ->
        {
            final boolean enabled = !offset.getText().isEmpty();
            check.setEnabled(enabled);
            setFinishEnabled(false);
        });
    }

    /**
     * Create the check collision button.
     * 
     * @param offsetArea The composite parent.
     * @param offset The offset text.
     * @return The created button.
     */
    private Button createCheckButton(Composite offsetArea, Text offset)
    {
        final Button check = UtilButton.createBrowse(offsetArea);
        check.setText(Messages.Check);

        UtilButton.setAction(check, () ->
        {
            verifier.verifyCollision(Integer.parseInt(offset.getText()));

            final WorldPart part = WorldModel.INSTANCE.getServices().get(WorldPart.class);
            if (!part.getUpdater().isCollisionsEnabled())
            {
                part.getUpdater().switchCollisionsEnabled();
            }
            part.update();
            setFinishEnabled(true);
        });

        return check;
    }

    @Override
    protected void createContent(Composite content)
    {
        createOffsetArea(content);
    }

    @Override
    protected void onFinish()
    {
        // Nothing to do
    }
}
