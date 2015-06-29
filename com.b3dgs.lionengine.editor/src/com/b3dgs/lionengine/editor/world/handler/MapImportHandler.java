/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.editor.world.handler;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.Activator;
import com.b3dgs.lionengine.editor.UtilEclipse;
import com.b3dgs.lionengine.editor.dialog.AbstractDialog;
import com.b3dgs.lionengine.editor.world.WorldViewModel;
import com.b3dgs.lionengine.editor.world.WorldViewPart;
import com.b3dgs.lionengine.editor.world.dialog.MapImportDialog;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileFeature;

/**
 * Import map handler.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MapImportHandler
{
    /** Extension ID. */
    public static final String EXTENSION_ID = Activator.PLUGIN_ID + ".mapFeatures";
    /** Extension point attribute map feature class. */
    private static final String EXTENSION_CLASS = "class";
    /** Extension point attribute map feature config dialog. */
    private static final String EXTENSION_DIALOG = "dialog";

    /**
     * Execute the handler.
     * 
     * @param shell The shell reference.
     */
    @Execute
    public void execute(Shell shell)
    {
        final MapImportDialog importMapDialog = new MapImportDialog(shell);
        importMapDialog.open();

        final WorldViewPart part = UtilEclipse.getPart(WorldViewPart.ID, WorldViewPart.class);
        part.update();

        checkMapFeaturesExtensionPoint(shell);
    }

    /**
     * Check the map features extension point.
     * 
     * @param parent The parent shell.
     */
    private void checkMapFeaturesExtensionPoint(Shell parent)
    {
        final MapTile map = WorldViewModel.INSTANCE.getMap();
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                EXTENSION_ID);
        for (final IConfigurationElement element : elements)
        {
            try
            {
                final String featureClass = element.getAttribute(EXTENSION_CLASS);
                final Class<? extends MapTileFeature> implementation = Activator.getMainBundle()
                        .loadClass(featureClass).asSubclass(MapTileFeature.class);
                map.createFeature(implementation);

                final String featureConfig = element.getAttribute(EXTENSION_DIALOG);
                if (featureConfig != null)
                {
                    final AbstractDialog dialog = UtilEclipse.createClass(featureConfig, AbstractDialog.class, parent);
                    dialog.open();
                }
            }
            catch (final ReflectiveOperationException exception)
            {
                Verbose.exception(getClass(), "checkMapFeaturesExtensionPoint", exception);
            }
        }
    }
}
