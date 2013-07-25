package com.b3dgs.lionengine.game.rts.skill;

import com.b3dgs.lionengine.game.FactoryGame;

/**
 * Abstract skill factory. It performs a list of available skills from a file considering an input enumeration. Data are
 * stored with an enumeration as key.
 * 
 * @param <T> Skill enum type used.
 * @param <S> Setup entity type used.
 * @param <K> Skill type used.
 */
public abstract class FactorySkillRts<T extends Enum<T>, S extends SetupSkillRts, K extends SkillRts<T>>
        extends FactoryGame<T, S>
{
    /**
     * Create a new skill factory.
     * 
     * @param clazz The enum class.
     */
    public FactorySkillRts(Class<T> clazz)
    {
        super(clazz);
    }

    /**
     * Create a skill from its id.
     * 
     * @param id The skill id.
     * @return The skill instance.
     */
    public abstract K createSkill(T id);
}
