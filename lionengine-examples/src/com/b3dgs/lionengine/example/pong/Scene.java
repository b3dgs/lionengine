package com.b3dgs.lionengine.example.pong;

import java.awt.Color;
import java.awt.Font;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * This is where the game loop is running.
 */
class Scene
        extends Sequence
{
    /** Number of lines in the middle. */
    private static final int LINES = 30;
    /** Text drawer. */
    private final Text text;
    /** Camera. */
    private final CameraGame camera;
    /** Rackets. */
    private final Set<Racket> rackets;
    /** Ball. */
    private final Ball ball;
    /** Handler. */
    private final Handler handler;

    /**
     * Create the scene and its vars.
     * 
     * @param loader The loader reference.
     */
    Scene(Loader loader)
    {
        super(loader);
        text = new Text(Font.SANS_SERIF, 16, Text.NORMAL);
        camera = new CameraGame();
        rackets = new HashSet<>(2);
        ball = new Ball(width, height);
        handler = new Handler(width, height, rackets, ball);
        setMouseVisible(false);
    }

    @Override
    protected void load()
    {
        camera.setView(0, 0, width, height);
        // Add a player on left
        rackets.add(new Racket(width, height, 10, height / 2, true));
        // Add a player on right
        rackets.add(new Racket(width, height, width - 10, height / 2, true));
        handler.setRacketSpeed(3.0);
        handler.engage();
    }

    @Override
    protected void update(double extrp)
    {
        handler.update(extrp, keyboard);
        // Terminate
        if (keyboard.isPressed(Keyboard.ESCAPE))
        {
            this.end();
        }
    }

    @Override
    protected void render(Graphic g)
    {
        // Clear screen
        clearScreen(g);

        // Draw middle line
        final int size = height / Scene.LINES;
        g.setColor(Color.GRAY);

        for (int i = 0; i < Scene.LINES; i++)
        {
            g.drawRect(width / 2 - 4, i * size + size / 4, 4, size / 2, true);
        }

        // Draw rackets
        for (final Racket racket : rackets)
        {
            racket.render(g, camera);
        }

        // Draw ball
        ball.render(g, camera);

        // Display scores
        text.setColor(Color.BLUE);
        text.draw(g, width / 4, 0, Align.CENTER, String.valueOf(handler.getScoreLeft()));
        text.draw(g, width / 2 + width / 4, 0, Align.CENTER, String.valueOf(handler.getScoreRight()));
    }
}
