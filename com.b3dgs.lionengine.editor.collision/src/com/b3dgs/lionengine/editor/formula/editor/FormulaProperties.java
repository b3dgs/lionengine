/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.formula.editor;

import java.util.Collections;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.editor.ObjectListListener;
import com.b3dgs.lionengine.editor.ObjectProperties;
import com.b3dgs.lionengine.editor.map.group.editor.GroupList;
import com.b3dgs.lionengine.editor.utility.control.UtilCombo;
import com.b3dgs.lionengine.editor.utility.control.UtilText;
import com.b3dgs.lionengine.editor.validator.InputValidator;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.game.Axis;
import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.collision.tile.CollisionConstraint;
import com.b3dgs.lionengine.game.collision.tile.CollisionFormula;
import com.b3dgs.lionengine.game.collision.tile.CollisionFunction;
import com.b3dgs.lionengine.game.collision.tile.CollisionFunctionLinear;
import com.b3dgs.lionengine.game.collision.tile.CollisionFunctionType;
import com.b3dgs.lionengine.game.collision.tile.CollisionRange;
import com.b3dgs.lionengine.game.collision.tile.MapTileCollisionModel;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.tile.TileGroup;
import com.b3dgs.lionengine.graphic.ImageBuffer;

/**
 * Represents the formulas properties edition view.
 */
public class FormulaProperties extends ObjectProperties<CollisionFormula>
                               implements ObjectListListener<CollisionFormula>
{
    /**
     * Add the constraints list.
     * 
     * @param title The constraints type.
     * @param list The current list.
     * @param parent The parent composite.
     */
    private static void addConstraintsList(String title, GroupList list, Composite parent)
    {
        list.create(parent);
        ((Group) list.getTree().getParent()).setText(title);
    }

    /**
     * Add groups to constraint.
     * 
     * @param constraint The constraint reference.
     * @param orientation The orientation.
     * @param list The groups reference.
     */
    private static void addGroups(CollisionConstraint constraint, Orientation orientation, GroupList list)
    {
        for (final TileGroup group : list.getObjects())
        {
            constraint.add(orientation, group.getName());
        }
    }

    /**
     * Read constraints from specified list and orientation.
     * 
     * @param constraint The constraint container.
     * @param constraints The constraints tree.
     * @param orientation The orientation value.
     */
    private static void readConstraints(CollisionConstraint constraint, GroupList constraints, Orientation orientation)
    {
        constraints.clear();
        final Tree tree = constraints.getTree();
        for (final String group : constraint.getConstraints(orientation))
        {
            final TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText(group);
            item.setData(new TileGroup(group, Collections.emptyList()));
        }
    }

    /** Constraints top. */
    private final GroupList constraintsTop = new GroupList();
    /** Constraints bottom. */
    private final GroupList constraintsBottom = new GroupList();
    /** Constraints left. */
    private final GroupList constraintsLeft = new GroupList();
    /** Constraints right. */
    private final GroupList constraintsRight = new GroupList();
    /** GC preview. */
    private GC gc;
    /** Last collision function panel. */
    private Composite lastFunctionPanel;
    /** Function type. */
    private Combo type;
    /** Last type data. */
    private CollisionFunctionType lastType;
    /** Output type. */
    private Combo output;
    /** Minimum X. */
    private Text minX;
    /** Maximum X. */
    private Text maxX;
    /** Minimum Y. */
    private Text minY;
    /** Maximum Y. */
    private Text maxY;
    /** Function linear A. */
    private Text linearA;
    /** Function linear B. */
    private Text linearB;

    /**
     * Create formulas properties.
     */
    public FormulaProperties()
    {
        super();
    }

    /**
     * Update the formula preview rendering.
     */
    void updatePreview()
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        if (map.isCreated() && isFieldsFilled() && !linearB.getText().isEmpty())
        {
            final CollisionFormula formula = createObject("preview");
            final ImageBuffer buffer = MapTileCollisionModel.createFunctionDraw(formula,
                                                                                map.getTileWidth(),
                                                                                map.getTileHeight());
            gc.fillRectangle(0, 0, map.getTileWidth(), map.getTileHeight());
            gc.drawImage((Image) buffer.getSurface(), 0, 0);
        }
    }

    /**
     * Select the function type.
     * 
     * @param parent The composite parent.
     */
    void selectFunctionType(Composite parent)
    {
        final CollisionFunctionType current = (CollisionFunctionType) type.getData();
        if (lastType != current)
        {
            if (lastFunctionPanel != null)
            {
                lastFunctionPanel.dispose();
                lastFunctionPanel = null;
            }
            selectFunctionPanel(current, parent);
            parent.getShell().pack(true);
        }
        lastType = current;
    }

    /**
     * Select the function panel to display depending of the type.
     * 
     * @param type The function type.
     * @param parent The parent composite.
     */
    void selectFunctionPanel(CollisionFunctionType type, Composite parent)
    {
        switch (type)
        {
            case LINEAR:
                createCollisionFunctionLinear(parent);
                break;
            default:
                throw new LionEngineException(type);
        }
    }

    /**
     * Create the collision function linear area.
     * 
     * @param parent The parent composite.
     */
    private void createCollisionFunctionLinear(Composite parent)
    {
        final Composite linearArea = new Composite(parent, SWT.NONE);
        linearArea.setLayout(new GridLayout(2, false));

        linearA = UtilText.create(Messages.FunctionLinearA, linearArea);
        UtilText.addVerify(linearA, InputValidator.DOUBLE_MATCH);
        updatePreviewOnModify(linearA);

        linearB = UtilText.create(Messages.FunctionLinearB, linearArea);
        UtilText.addVerify(linearB, InputValidator.DOUBLE_MATCH);
        updatePreviewOnModify(linearB);

        lastFunctionPanel = linearArea;
    }

    /**
     * Create the preview.
     * 
     * @param parent The parent composite.
     */
    private void createPreview(Composite parent)
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        final Label preview = new Label(parent, SWT.BORDER);
        final GridData data = new GridData(map.getTileWidth(), map.getTileHeight());
        data.horizontalAlignment = SWT.CENTER;
        preview.setLayoutData(data);
        gc = new GC(preview);
    }

    /**
     * Create the template chooser.
     * 
     * @param parent The parent composite.
     */
    private void createTemplate(Composite parent)
    {
        final MapTile map = WorldModel.INSTANCE.getMap();
        final Combo template = UtilCombo.create(Messages.Template, parent, FormulaTemplate.values());
        UtilCombo.setAction(template, () ->
        {
            final CollisionFormula formula = ((FormulaTemplate) template.getData()).getFormula(map);
            notifyObjectSelected(formula);
        });
    }

    /**
     * Create the range properties.
     * 
     * @param parent The parent composite.
     */
    private void createTextRange(Composite parent)
    {
        final Composite outputArea = new Composite(parent, SWT.NONE);
        final GridLayout outputAreaLayout = new GridLayout(1, false);
        outputAreaLayout.marginHeight = 0;
        outputArea.setLayout(outputAreaLayout);
        output = UtilCombo.create(Messages.RangeOutput, outputArea, Axis.values());

        final Composite xArea = new Composite(parent, SWT.NONE);
        final GridLayout xAreaLayout = new GridLayout(2, false);
        xAreaLayout.marginHeight = 0;
        xArea.setLayout(xAreaLayout);
        minX = UtilText.create(Messages.RangeMinX, xArea);
        UtilText.addVerify(minX, InputValidator.INTEGER_MATCH);
        updatePreviewOnModify(minX);
        maxX = UtilText.create(Messages.RangeMaxX, xArea);
        UtilText.addVerify(maxX, InputValidator.INTEGER_MATCH);
        updatePreviewOnModify(maxX);

        final Composite yArea = new Composite(parent, SWT.NONE);
        final GridLayout yAreaLayout = new GridLayout(2, false);
        yAreaLayout.marginHeight = 0;
        yArea.setLayout(yAreaLayout);
        minY = UtilText.create(Messages.RangeMinY, yArea);
        UtilText.addVerify(minY, InputValidator.INTEGER_MATCH);
        updatePreviewOnModify(minY);
        maxY = UtilText.create(Messages.RangeMaxY, yArea);
        UtilText.addVerify(maxY, InputValidator.INTEGER_MATCH);
        updatePreviewOnModify(maxY);
    }

    /**
     * Create the function properties.
     * 
     * @param parent The parent composite.
     */
    private void createTextFunction(final Composite parent)
    {
        final Composite typeArea = new Composite(parent, SWT.NONE);
        final GridLayout typeAreaLayout = new GridLayout(1, false);
        typeAreaLayout.marginHeight = 0;
        typeArea.setLayout(typeAreaLayout);
        type = UtilCombo.create(Messages.FunctionType, typeArea, CollisionFunctionType.values());
        selectFunctionType(parent);
        UtilCombo.setAction(type, () -> selectFunctionType(parent));
    }

    /**
     * Create the constraint properties.
     * 
     * @param parent The parent composite.
     */
    private void createTextConstraint(Composite parent)
    {
        final Composite constraintArea = new Composite(parent, SWT.NONE);
        constraintArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        constraintArea.setLayout(new GridLayout(4, true));

        addConstraintsList(Orientation.NORTH.name(), constraintsTop, constraintArea);
        addConstraintsList(Orientation.SOUTH.name(), constraintsBottom, constraintArea);
        addConstraintsList(Orientation.WEST.name(), constraintsLeft, constraintArea);
        addConstraintsList(Orientation.EAST.name(), constraintsRight, constraintArea);
    }

    /**
     * Create the selected function.
     * 
     * @param type The function type.
     * @return The function instance.
     */
    private CollisionFunction createFunction(CollisionFunctionType type)
    {
        switch (type)
        {
            case LINEAR:
                final double a = Double.parseDouble(linearA.getText());
                final double b = Double.parseDouble(linearB.getText());
                return new CollisionFunctionLinear(a, b);
            default:
                throw new LionEngineException(type);
        }
    }

    /**
     * Update preview rendering when text has been modified.
     * 
     * @param text The text reference.
     */
    private void updatePreviewOnModify(Text text)
    {
        text.addModifyListener(event -> updatePreview());
    }

    /**
     * Load the function and fill fields.
     * 
     * @param function The function to load.
     */
    private void loadFunction(CollisionFunction function)
    {
        switch (function.getType())
        {
            case LINEAR:
                final CollisionFunctionLinear linear = (CollisionFunctionLinear) function;
                setValueDefault(linearA, Double.toString(linear.getA()));
                setValueDefault(linearB, Double.toString(linear.getB()));
                break;
            default:
                throw new LionEngineException(function.getType());
        }
    }

    /**
     * Check if all fields are filled.
     * 
     * @return <code>true</code> if filled, <code>false</code> else.
     */
    private boolean isFieldsFilled()
    {
        final boolean range = !minX.getText().isEmpty()
                              && !maxX.getText().isEmpty()
                              && !minY.getText().isEmpty()
                              && !maxY.getText().isEmpty();
        final boolean linear = !linearA.getText().isEmpty() && !linearB.getText().isEmpty();
        return !type.getText().isEmpty() && !output.getText().isEmpty() && range && linear;
    }

    /*
     * ObjectProperties
     */

    @Override
    protected void createTextFields(Composite parent)
    {
        final Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));

        final Group preview = new Group(composite, SWT.NONE);
        preview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        preview.setLayout(new GridLayout(1, false));
        preview.setText(Messages.Preview);
        createPreview(preview);
        createTemplate(preview);

        final Group range = new Group(composite, SWT.NONE);
        range.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        range.setLayout(new GridLayout(1, false));
        range.setText(Messages.Range);
        createTextRange(range);

        final Group function = new Group(composite, SWT.NONE);
        function.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        function.setLayout(new GridLayout(1, false));
        function.setText(Messages.Function);
        createTextFunction(function);

        final Group constraint = new Group(parent, SWT.NONE);
        constraint.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        constraint.setLayout(new GridLayout(1, false));
        constraint.setText(Messages.Constraint);
        createTextConstraint(constraint);

        notifyObjectDeleted(null);
    }

    @Override
    protected CollisionFormula createObject(String name)
    {
        final Axis output = (Axis) this.output.getData();
        final int minX = Integer.parseInt(this.minX.getText());
        final int maxX = Integer.parseInt(this.maxX.getText());
        final int minY = Integer.parseInt(this.minY.getText());
        final int maxY = Integer.parseInt(this.maxY.getText());
        final CollisionFunctionType type = (CollisionFunctionType) this.type.getData();

        final CollisionRange range = new CollisionRange(output, minX, maxX, minY, maxY);
        final CollisionFunction function = createFunction(type);
        final CollisionConstraint constraint = new CollisionConstraint();
        addGroups(constraint, Orientation.NORTH, constraintsTop);
        addGroups(constraint, Orientation.SOUTH, constraintsBottom);
        addGroups(constraint, Orientation.WEST, constraintsLeft);
        addGroups(constraint, Orientation.EAST, constraintsRight);

        return new CollisionFormula(name, range, function, constraint);
    }

    /**
     * Save constraints.
     */
    public void save()
    {
        constraintsTop.save();
        constraintsBottom.save();
        constraintsLeft.save();
        constraintsRight.save();
    }

    /*
     * ObjectListListener
     */

    @Override
    public void notifyObjectSelected(CollisionFormula formula)
    {
        final CollisionRange range = formula.getRange();
        output.setText(range.getOutput().name());
        output.setData(range.getOutput());
        setValueDefault(minX, Integer.toString(range.getMinX()));
        setValueDefault(maxX, Integer.toString(range.getMaxX()));
        setValueDefault(minY, Integer.toString(range.getMinY()));
        setValueDefault(maxY, Integer.toString(range.getMaxY()));

        final CollisionFunction function = formula.getFunction();
        UtilCombo.setDefaultValue(type, function.getType().name());
        type.setData(function.getType());
        loadFunction(function);

        final CollisionConstraint constraint = formula.getConstraint();
        readConstraints(constraint, constraintsTop, Orientation.NORTH);
        readConstraints(constraint, constraintsBottom, Orientation.SOUTH);
        readConstraints(constraint, constraintsLeft, Orientation.WEST);
        readConstraints(constraint, constraintsRight, Orientation.EAST);
    }

    @Override
    public void notifyObjectDeleted(CollisionFormula formula)
    {
        setValueDefault(output, Constant.EMPTY_STRING);
        setValueDefault(minX, Constant.EMPTY_STRING);
        setValueDefault(maxX, Constant.EMPTY_STRING);
        setValueDefault(minY, Constant.EMPTY_STRING);
        setValueDefault(maxY, Constant.EMPTY_STRING);
        setValueDefault(type, Constant.EMPTY_STRING);

        setValueDefault(linearA, Constant.EMPTY_STRING);
        setValueDefault(linearB, Constant.EMPTY_STRING);

        output.setData(null);
        type.setData(null);

        constraintsTop.clear();
        constraintsBottom.clear();
        constraintsLeft.clear();
        constraintsRight.clear();
    }
}
