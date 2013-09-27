package com.b3dgs.lionengine.example.c_platform.e_lionheart.landscape;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Scene;
import com.b3dgs.lionengine.game.platform.background.BackgroundComponent;
import com.b3dgs.lionengine.game.platform.background.BackgroundElement;
import com.b3dgs.lionengine.game.platform.background.BackgroundPlatform;
import com.b3dgs.lionengine.game.platform.background.Parallax;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Swamp full background implementation.
 */
final class Swamp
        extends BackgroundPlatform
{
    /** Moon rasters. */
    private static final int MOON_RASTERS = 20;

    /** The horizontal factor. */
    final double scaleH;
    /** The vertical factor. */
    final double scaleV;
    /** Number of parallax lines. */
    private final int parallaxsNumber = 96;
    /** Flickering flag. */
    private final boolean flickering;

    /**
     * Create a rastered element.
     * 
     * @param path The surface path.
     * @param name The element name.
     * @param x The location x.
     * @param y The location y.
     * @param rastersNumber The number of rasters to use.
     * @return The created element.
     */
    static ElementRastered createElementRastered(String path, String name, int x, int y, int rastersNumber)
    {
        final Sprite sprite = Drawable.loadSprite(Media.get(path, name));
        sprite.load(false);
        return new ElementRastered(x, y, sprite, rastersNumber);
    }

    /**
     * Constructor.
     * 
     * @param config The config reference.
     * @param scaleH The horizontal factor.
     * @param scaleV The horizontal factor.
     * @param theme The theme name.
     * @param flickering The flickering flag.
     */
    Swamp(Config config, double scaleH, double scaleV, String theme, boolean flickering)
    {
        super(theme, 0, 512);
        this.scaleH = scaleH;
        this.scaleV = scaleV;
        this.flickering = flickering;
        final String path = Media.getPath(AppLionheart.BACKGROUNDS_DIR, "Swamp", theme);
        final int width = config.getSource().getWidth();
        final int halfScreen = config.getSource().getWidth() / 4;
        add(new Backdrop(path, this.flickering, width));
        add(new Clouds(Media.get(path, "cloud.png"), width, 4));
        add(new Parallax(config, Media.get(path, "parallax.png"), parallaxsNumber, halfScreen, 124, 50, 100));

        totalHeight = 120;
        setOffsetY(config.getSource().getHeight() - Scene.SCENE_DISPLAY.getHeight() + 72);
    }

    /**
     * Backdrop represents the back background plus top background elements.
     */
    private final class Backdrop
            implements BackgroundComponent
    {
        /** Backdrop color A. */
        private final BackgroundElement backcolorA;
        /** Backdrop color B. */
        private final BackgroundElement backcolorB;
        /** Mountain element. */
        private final BackgroundElement mountain;
        /** Moon element. */
        private final ElementRastered moon;
        /** Mountain sprite. */
        private final Sprite mountainSprite;
        /** Screen width. */
        private final int screenWidth;
        /** Screen wide value. */
        private final int w;
        /** Flickering flag. */
        private final boolean flickering;
        /** Flickering counter. */
        private int flickerCount;
        /** Flickering type. */
        private boolean flickerType;

        /**
         * Constructor.
         * 
         * @param path The backdrop path.
         * @param flickering The flickering flag effect.
         * @param screenWidth The screen width.
         */
        Backdrop(String path, boolean flickering, int screenWidth)
        {
            this.flickering = flickering;
            this.screenWidth = screenWidth;

            if (flickering)
            {
                backcolorA = createElement(path, "backcolor_a.png", 0, 0, false);
                backcolorB = createElement(path, "backcolor_b.png", 0, 0, false);
                flickerCount = 0;
            }
            else
            {
                backcolorA = createElement(path, "backcolor.png", 0, 0, false);
                backcolorB = null;
            }

            mountain = createElement(path, "mountain.png", 0, 124, false);
            w = (int) Math.ceil(this.screenWidth / (double) ((Sprite) mountain.getSprite()).getWidthOriginal()) + 1;

            final int x = (int) (208 * scaleH);
            moon = Swamp.createElementRastered(path, "moon.png", x, getOffsetY(), Swamp.MOON_RASTERS);
            mountainSprite = (Sprite) mountain.getSprite();
        }

        @Override
        public void update(double extrp, int x, int y, double speed)
        {
            backcolorA.setOffsetY(y);
            moon.setOffsetY(-20 + getOffsetY());
            mountain.setOffsetX(UtilityMath.wrapDouble(mountain.getOffsetX() + speed * 0.24, 0.0,
                    mountainSprite.getWidth()));
            mountain.setOffsetY(y);

            if (flickering)
            {
                flickerCount = (flickerCount + 1) % 2;
                if (flickerCount == 0)
                {
                    flickerType = !flickerType;
                }
            }
        }

        @Override
        public void render(Graphic g)
        {
            // Render back background first
            final Sprite sprite;
            if (flickerType || !flickering)
            {
                sprite = (Sprite) backcolorA.getSprite();
            }
            else
            {
                sprite = (Sprite) backcolorB.getSprite();
            }
            for (int i = 0; i < Math.ceil(screenWidth / (double) sprite.getWidth()); i++)
            {
                sprite.render(g, backcolorA.getMainX() + i * sprite.getWidth(),
                        (int) (backcolorA.getOffsetY() + backcolorA.getMainY()));
            }
            // Render moon
            moon.getRaster((int) (mountain.getOffsetY() / 4 + Swamp.MOON_RASTERS)).render(g, moon.getMainX(),
                    (int) (moon.getOffsetY() + moon.getMainY()));

            // Render mountains
            final int oy = (int) (mountain.getOffsetY() + mountain.getMainY());
            final int ox = (int) (-mountain.getOffsetX() + mountain.getMainX());
            final int sx = mountainSprite.getWidth();
            for (int j = 0; j < w; j++)
            {
                mountainSprite.render(g, ox + sx * j, oy);
            }
        }
    }
}
