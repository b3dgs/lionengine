package com.b3dgs.lionengine.example.c_platform.e_lionheart.background;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Sequence;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.platform.background.BackgroundComponent;
import com.b3dgs.lionengine.game.platform.background.BackgroundElement;
import com.b3dgs.lionengine.game.platform.background.BackgroundPlatform;
import com.b3dgs.lionengine.utility.UtilityFile;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Water foreground implementation.
 */
public class Water
        extends BackgroundPlatform
{
    /** Screen width. */
    final int screenWidth;
    /** Screen height. */
    final int screenHeight;
    /** Standard height. */
    private int nominal;
    /** Water height. */
    private double height;
    /** Water depth. */
    private double depth;
    /** Water speed. */
    private double speed;

    /**
     * Create a new swamp background.
     * 
     * @param sequence The sequence reference.
     * @param theme The theme name.
     */
    public Water(Sequence sequence, String theme)
    {
        super(theme, 0, 0, sequence.wide);
        screenWidth = sequence.config.internal.getWidth();
        screenHeight = sequence.config.internal.getHeight();
        nominal = 210;
        height = 0.0;
        depth = 8.0;
        speed = 0.02;
        final String path = Media.getPath("foregrounds", theme);
        add(new Primary(path, this));
        add(new Secondary(path, this));
    }

    /**
     * Render the front part of the water.
     * 
     * @param g The graphic output.
     */
    public void renderBack(Graphic g)
    {
        renderComponent(0, g);
    }

    /**
     * Render the back part of the water.
     * 
     * @param g The graphic output.
     */
    public void renderFront(Graphic g)
    {
        renderComponent(1, g);
    }

    /**
     * Get the height.
     * 
     * @return The height.
     */
    public double getHeight()
    {
        return height;
    }

    /**
     * Set the height.
     * 
     * @param height The height to set.
     */
    public void setHeight(double height)
    {
        this.height = height;
    }

    /**
     * Get the depth.
     * 
     * @return The depth.
     */
    public double getDepth()
    {
        return depth;
    }

    /**
     * Get the nominal value.
     * 
     * @return The nominal value.
     */
    public int getNominal()
    {
        return nominal;
    }

    /**
     * Get the water speed.
     * 
     * @return The water speed.
     */
    public double getSpeed()
    {
        return speed;
    }

    /**
     * First front component, including water effect.
     */
    private class Primary
            implements BackgroundComponent
    {
        /** Water element. */
        private final BackgroundElement data;
        /** Water reference. */
        private final Water water;

        /**
         * Constructor.
         * 
         * @param path The primary surface path.
         * @param water The water reference.
         */
        Primary(String path, Water water)
        {
            final Sprite sprite = Drawable.loadSprite(Media.get(path, "calc.png"));
            sprite.load(false);
            data = new BackgroundElement(0, water.getNominal(), sprite);
            this.water = water;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            data.setOffsetY(y);
        }

        @Override
        public void render(Graphic g)
        {
            int w = 1;
            if (isWide())
            {
                w = 2;
            }

            // Render calc
            final Sprite sprite = (Sprite) data.getSprite();
            final int y = (int) (data.getOffsetY() + data.getMainY() + water.getHeight());
            if (y >= 0 && y < screenHeight)
            {
                for (int j = 0; j < w; j++)
                {
                    sprite.render(g, sprite.getWidth() * j, y);
                }
            }
        }
    }

    /**
     * Second front component, including water effect.
     */
    private class Secondary
            implements BackgroundComponent
    {
        /** Water element. */
        private final BackgroundElement data;
        /** Sprite. */
        private final SpriteAnimated anim;
        /** Animation data. */
        private final Animation animation;
        /** Water effect sprite. */
        private final Sprite effect;
        /** Water reference. */
        private final Water water;
        /** Height value. */
        private double height;
        /** Water x. */
        private double wx;
        /** Position y. */
        private int py;

        /**
         * Constructor.
         * 
         * @param path The secondary surface path.
         * @param water The water reference.
         */
        Secondary(String path, Water water)
        {
            final Sprite back = Drawable.loadSprite(Media.get(path, "back.png"));
            back.load(false);
            data = new BackgroundElement(0, water.getNominal(), back);

            if (UtilityFile.exists(Media.getPath(path, "effect.png")))
            {
                effect = Drawable.loadSprite(Media.get(path, "effect.png"));
                effect.load(false);
            }
            else
            {
                effect = null;
            }

            animation = Anim.createAnimation(1, 7, 0.25, false, true);
            anim = Drawable.loadSpriteAnimated(Media.get(path, "anim.png"), 7, 1);
            anim.load(false);
            anim.play(animation);
            this.water = water;
            height = 0.0;
            wx = 0.0;
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            anim.updateAnimation(extrp);

            data.setOffsetX(data.getOffsetX() + speed * extrp);
            data.setOffsetX(UtilityMath.wrapDouble(data.getOffsetX(), 0.0, anim.getFrameWidth()));
            data.setOffsetY(y);

            height += water.getSpeed() * extrp;
            height = UtilityMath.wrapDouble(height, 0.0, 360.0);
            water.setHeight(Math.sin(height) * water.getDepth());
            py = y;
            wx += 0.06 * extrp;
        }

        @Override
        public void render(Graphic g)
        {
            int w = 1;
            if (isWide())
            {
                w = 2;
            }

            // w number of renders used to fill screen
            final Sprite sprite = (Sprite) data.getSprite();
            int y = (int) (data.getOffsetY() + data.getMainY() + water.getHeight());

            if (y >= 0 && y <= screenHeight)
            {
                for (int j = 0; j < w; j++)
                {
                    sprite.render(g, sprite.getWidth() * j, y);
                }
            }

            // animation rendering
            w = 6;
            if (isWide())
            {
                w = 8;
            }
            final int x = (int) (-data.getOffsetX() + data.getMainX());
            y = (int) (data.getOffsetY() + data.getMainY() - 16.0 + water.getHeight());

            if (y >= 0 && y <= screenHeight)
            {
                for (int j = 0; j < w; j++)
                {
                    anim.render(g, x + anim.getFrameWidth() * j, y);
                }
            }

            if (effect != null)
            {
                final int oy = y - effect.getHeight() + 22;
                if (oy >= 0 && oy <= screenHeight)
                {
                    for (int j = 0; j < w; j++)
                    {
                        effect.render(g, effect.getWidth() * j, oy);
                    }
                }
            }

            waterEffect(g, 0.06, 4.0, 0.8, 3.0);
        }

        /**
         * Update water effect.
         * 
         * @param g The graphics output.
         * @param speed The effect speed.
         * @param frequency The effect frequency.
         * @param amplitude The effect amplitude.
         * @param offsetForce The offset force.
         */
        private void waterEffect(Graphic g, double speed, double frequency, double amplitude, double offsetForce)
        {
            final int oy = py + (int) water.getHeight();
            for (int y = screenHeight - 32 + oy; y < screenHeight; y++)
            {
                final double inside = Math.cos(UtilityMath.wrapDouble(y + wx * frequency, 0.0, 360.0)) * amplitude;
                final double outside = Math.cos(wx) * offsetForce;
                g.copyArea(0, y, screenWidth, 1, (int) (inside + outside), 0);
            }
        }
    }
}
