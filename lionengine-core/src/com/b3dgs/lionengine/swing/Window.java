package com.b3dgs.lionengine.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Abstract window, mainly used by Options and Launcher.
 */
public abstract class Window
        extends JFrame
{
    /** Serial uid. */
    private static final long serialVersionUID = -1567341421280721456L;

    /** Window owner reference. */
    protected final JFrame parent;
    /** Main panel reference. */
    protected final JPanel mainPanel;

    /**
     * Create a new window.
     * 
     * @param title The window title.
     * @param width The window width.
     * @param height The window height.
     * @param parent The window owner.
     */
    public Window(String title, int width, int height, JFrame parent)
    {
        super(title);
        setResizable(false);
        setPreferredSize(new Dimension(width, height));
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                terminate();
            }
        });
        this.parent = parent;
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
    }

    /**
     * Start window.
     */
    public final void start()
    {
        if (parent != null)
        {
            parent.setEnabled(false);
        }
        pack();
        validate();
        setLocationRelativeTo(null);
        repaint();
        setEnabled(true);
        setVisible(true);
        requestFocus();
    }

    /**
     * Terminate window.
     */
    public final void terminate()
    {
        if (parent != null)
        {
            parent.setEnabled(true);
            parent.repaint();
            parent.requestFocus();
        }
        dispose();
    }

    /**
     * Set window icon.
     * 
     * @param filename The icon filename.
     */
    protected final void setIcon(String filename)
    {
        setIconImage(new ImageIcon(filename).getImage());
    }
}
