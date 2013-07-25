package com.b3dgs.lionengine.example.f_network;

import java.awt.Color;
import java.awt.Font;
import java.util.Iterator;
import java.util.LinkedList;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.network.ConnectionListener;
import com.b3dgs.lionengine.network.message.NetworkMessageChat;
import com.b3dgs.lionengine.network.purview.NetworkChat;

/**
 * Chat implementation.
 */
public class Chat
        extends NetworkChat
        implements ConnectionListener
{
    /** Background. */
    private static final Color BACKGROUND = new Color(128, 128, 128, 192);
    /** Background writing. */
    private static final Color BACKGROUND_WRITING = new Color(64, 64, 64, 192);
    /** Text. */
    private final Text text;
    /** Mario reference. */
    private final World<?> world;

    /**
     * Constructor.
     * 
     * @param world The world reference.
     */
    public Chat(World<?> world)
    {
        super(TypeMessage.MESSAGE_CHAT);
        text = new Text(Font.DIALOG, 9, Text.NORMAL);
        this.world = world;
    }

    @Override
    public void render(Graphic g)
    {
        g.setColor(Chat.BACKGROUND);
        g.drawRect(0, 180, 120, 60, true);
        g.setColor(Chat.BACKGROUND_WRITING);
        g.drawRect(0, 228, 120, 12, true);
        final Iterator<String> messages = new LinkedList<>(getMessages()).descendingIterator();
        int i = 1;
        while (messages.hasNext())
        {
            text.draw(g, 0, 230 - i * 12, Align.LEFT, messages.next());
            i++;
        }
        text.draw(g, 0, 230, Align.LEFT, ">:" + getWriting());
    }

    @Override
    protected boolean canAddChar(char c)
    {
        return true;
    }

    @Override
    protected String getMessage(NetworkMessageChat message)
    {
        return new StringBuilder(world.getClientName(message.getClientId())).append(" says: ")
                .append(message.getMessage()).toString();
    }

    @Override
    protected boolean canSendMessage(String message)
    {
        if (message.startsWith("/"))
        {
            world.applyCommand(message);
            return false;
        }
        return true;
    }

    /*
     * ClientListener
     */

    @Override
    public void notifyClientConnected(Byte id, String name)
    {
        addMessage(name + " connected");
    }

    @Override
    public void notifyClientDisconnected(Byte id, String name)
    {
        addMessage(name + " disconnected");
    }

    @Override
    public void notifyClientNameChanged(Byte id, String name)
    {
        addMessage(world.getClientName(id.byteValue()) + " renamed as: " + name);
    }

    @Override
    public void notifyConnectionEstablished(Byte id, String name)
    {
        addMessage("Connection established as: " + name);
    }

    @Override
    public void notifyMessageOfTheDay(String messageOfTheDay)
    {
        addMessage(messageOfTheDay);
    }

    @Override
    public void notifyConnectionTerminated(Byte id)
    {
        addMessage("Connection terminated");
    }
}
