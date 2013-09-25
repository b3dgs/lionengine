package com.b3dgs.lionengine.tools;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.utility.UtilityFile;
import com.b3dgs.lionengine.utility.UtilityMath;
import com.b3dgs.lionengine.utility.UtilityMessageBox;
import com.b3dgs.lionengine.utility.TileExtractor;

/**
 * Tile converter and tile extractor tool.
 */
public class TileExtractorApp
{
    /**
     * Main function.
     * 
     * @param args arguments.
     */
    public static void main(String[] args)
    {
        final TileExtractorApp frame = new TileExtractorApp();
        frame.start();
    }

    /**
     * Get integer from dialog result.
     * 
     * @param string Input string.
     * @return Integer result.
     */
    private static int getInteger(String string)
    {
        try
        {
            final Pattern intsOnly = Pattern.compile("\\d*");
            final Matcher makeMatch = intsOnly.matcher(string);
            makeMatch.find();
            return UtilityMath.fixBetween(Integer.parseInt(makeMatch.group()), 0, Integer.MAX_VALUE);
        }
        catch (final PatternSyntaxException exception)
        {
            return 0;
        }
    }

    /** Main frame. */
    private final JFrame frame;

    /**
     * Constructor.
     */
    public TileExtractorApp()
    {
        frame = new JFrame("Tile Extractor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(256, 128);
        frame.setLayout(new GridLayout(2, 1));
        JButton button = new JButton("Extract tiles from a Levelrip");
        frame.add(button);
        button.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                extractTilesFromLevelRip();
            }
        });

        button = new JButton("Exit");
        frame.add(button);
        button.addActionListener(new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                terminate();
            }
        });
    }

    /**
     * Terminate the frame.
     */
    void terminate()
    {
        frame.dispose();
    }

    /**
     * Show frame.
     */
    public void start()
    {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Extract tiles from a level rip.
     */
    void extractTilesFromLevelRip()
    {
        frame.setVisible(false);
        final JDialog dialog = new JDialog();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Set level rip filename...");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setMultiSelectionEnabled(false);
        chooser.addChoosableFileFilter(new LevelRipFilter("bmp", "Map Image Rip"));
        chooser.addChoosableFileFilter(new LevelRipFilter("png", "Map Image Rip"));
        int approve = chooser.showOpenDialog(dialog);
        String filein = null;
        if (approve == JFileChooser.APPROVE_OPTION)
        {
            filein = chooser.getSelectedFile().getPath();
        }
        if (filein == null || !UtilityFile.exists(filein))
        {
            dialog.dispose();
            frame.setVisible(true);
            return;
        }

        chooser = new JFileChooser();
        chooser.setDialogTitle("Set tilesheet output filename...");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        approve = chooser.showSaveDialog(dialog);
        String fileout = null;
        if (approve == JFileChooser.APPROVE_OPTION)
        {
            fileout = chooser.getSelectedFile().getPath();
        }
        if (fileout == null)
        {
            dialog.dispose();
            frame.setVisible(true);
            return;
        }

        final int tileWidth = TileExtractorApp.getInteger(JOptionPane.showInputDialog(dialog, "Tile width: ",
                "Tile size", JOptionPane.OK_OPTION));
        final int tileHeight = TileExtractorApp.getInteger(JOptionPane.showInputDialog(dialog, "Tile height: ",
                "Tile size", JOptionPane.OK_OPTION));
        final int destWidth = TileExtractorApp.getInteger(JOptionPane.showInputDialog(dialog, "Tilesheet width: ",
                "Destination size", JOptionPane.OK_OPTION));
        final int destHeight = TileExtractorApp.getInteger(JOptionPane.showInputDialog(dialog, "Tilesheet height: ",
                "Destination size", JOptionPane.OK_OPTION));

        if (tileWidth != 0 && tileHeight != 0 && destWidth != 0 && destHeight != 0)
        {
            TileExtractor.start(Media.get(filein), Media.get(fileout), tileWidth, tileHeight, destWidth,
                    destHeight);
            UtilityMessageBox.information("Tile extractor", "Extraction finished !");
        }
        else
        {
            UtilityMessageBox.error("Tile extractor", "Some informations are wrong !");
        }
        dialog.dispose();
        frame.setVisible(true);
        frame.requestFocus();
    }
}
