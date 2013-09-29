package com.b3dgs.lionengine.example.e_shmup.b_shipweapon;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon.FactoryWeapon;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon.Weapon;
import com.b3dgs.lionengine.example.e_shmup.b_shipweapon.weapon.WeaponType;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.entity.EntityGame;
import com.b3dgs.lionengine.input.Mouse;
import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Ship implementation.
 */
final class Ship
        extends EntityGame
{
    /** Ship surface. */
    private final SpriteTiled sprite;
    /** Id. */
    private final int id;
    /** Height. */
    private final int screenHeight;
    /** Weapon front. */
    private final Weapon weaponFront;
    /** Weapon rear. */
    private final Weapon weaponRear;
    /** Move sprite offset. */
    private int offset;

    /**
     * Constructor.
     * 
     * @param screenHeight The screen height.
     * @param factoryWeapon The weapon factory.
     */
    public Ship(int screenHeight, FactoryWeapon factoryWeapon)
    {
        super();
        this.screenHeight = screenHeight;
        sprite = Drawable.loadSpriteTiled(Media.get("sprites", "ships.png"), 24, 28);
        sprite.load(false);
        id = 10;
        weaponFront = factoryWeapon.createLauncher(WeaponType.PULSE_CANNON);
        weaponFront.setOwner(this);
        weaponRear = factoryWeapon.createLauncher(WeaponType.MISSILE_LAUNCHER);
        weaponRear.setOwner(this);
        setSize(24, 28);
        setLocation(160, 200);
    }

    /**
     * Update the ship.
     * 
     * @param extrp The extrapolation value.
     * @param mouse The mouse reference.
     */
    public void update(double extrp, Mouse mouse)
    {
        final double destX = mouse.getOnWindowX() - getWidth() / 2;
        final double destY = screenHeight - mouse.getOnWindowY() + getHeight() / 2;
        final double x = UtilityMath.curveValue(getLocationX(), destX, 3.0);
        final double y = UtilityMath.curveValue(getLocationY(), destY, 3.0);
        setLocation(x, y);
        updateOffset();
        if (mouse.hasClicked(Mouse.LEFT))
        {
            weaponFront.launch();
            weaponRear.launch();
        }
    }

    /**
     * Render the ship.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraGame camera)
    {
        sprite.render(g, id + offset, camera.getViewpointX(getLocationIntX()), camera.getViewpointY(getLocationIntY()));
    }

    /**
     * Update the ship offset depending of its movement.
     */
    private void updateOffset()
    {
        final double diffX = getLocationX() - getLocationOldX();
        if (diffX >= -1 && diffX <= 1)
        {
            offset = 0;
        }
        else if (diffX < -3)
        {
            offset = -2;
        }
        else if (diffX >= -3 && diffX < 1)
        {
            offset = -1;
        }
        else if (diffX > 1 && diffX <= 3)
        {
            offset = 1;
        }
        else if (diffX > 3)
        {
            offset = 2;
        }
    }
}
