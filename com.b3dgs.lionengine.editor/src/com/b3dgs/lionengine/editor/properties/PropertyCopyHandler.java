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
package com.b3dgs.lionengine.editor.properties;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Copy selected property handler.
 */
public final class PropertyCopyHandler
{
    /**
     * Create handler.
     */
    public PropertyCopyHandler()
    {
        super();
    }

    /**
     * Execute the handler.
     */
    @Execute
    public void execute()
    {
        PropertiesModel.INSTANCE.setCopyData(null);
        PropertiesModel.INSTANCE.setCopyText(null);
        for (final TreeItem item : PropertiesModel.INSTANCE.getTree().getSelection())
        {
            PropertiesModel.INSTANCE.setCopyData(item.getData());
            PropertiesModel.INSTANCE.setCopyText(item.getText(PropertiesPart.COLUMN_VALUE));
        }
    }
}
