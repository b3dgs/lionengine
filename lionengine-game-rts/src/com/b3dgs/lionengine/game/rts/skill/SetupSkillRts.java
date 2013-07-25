package com.b3dgs.lionengine.game.rts.skill;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.SetupGame;

/**
 * Minimum skill requirement definition, used to create skills.
 */
public abstract class SetupSkillRts
        extends SetupGame
{
    /** Displayed name. */
    public final String name;
    /** Description. */
    public final String description;

    /**
     * Constructor. The minimum XML data needed.
     * 
     * <pre>
     * {@code
     * <skill name="Name" description="Description">
     * </skill>
     * }
     * </pre>
     * 
     * @param config The config media.
     */
    public SetupSkillRts(Media config)
    {
        super(config);
        name = configurable.getDataString("name");
        description = configurable.getDataString("description");
    }
}
