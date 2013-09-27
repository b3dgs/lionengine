package com.b3dgs.lionengine.example.a_firstcode;

import java.awt.Font;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.utility.UtilityMessageBox;

/**
 * This is where the game loop is running. A sequence represents a thread handled by the Loader. To link a sequence with
 * another one, a simple call to {@link #end(Sequence)} is necessary. This will terminate the current sequence, and
 * start the linked one.
 */
final class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(320, 240, 60);

    /** Text drawer. */
    private final Text text;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        text = new Text(Font.SANS_SERIF, 12, Text.NORMAL);
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        text.setText("Hello");
        text.setLocation(width / 2, height / 2 - 8);
        text.setAlign(Align.CENTER);
    }

    @Override
    protected void update(double extrp)
    {
        // Update routine
    }

    @Override
    protected void render(Graphic g)
    {
        // Simple rendering
        text.render(g);
        // Direct rendering
        text.draw(g, width / 2, height / 2 + 8, Align.CENTER, "World");
    }

    /*
     * It is not necessary to override this method
     */
    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        UtilityMessageBox.information("Terminate !", "Closing app...");
    }
}
