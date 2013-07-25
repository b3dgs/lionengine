package com.b3dgs.lionengine.file;

import java.io.IOException;

import com.b3dgs.lionengine.Media;

/**
 * Handle files manipulation, reading and writing.
 */
public final class File
{
    /**
     * Private constructor.
     */
    private File()
    {
        throw new RuntimeException();
    }

    /**
     * Open a file as read only.
     * 
     * @param media The media file.
     * @return The created FileReader.
     * @throws IOException If open failed.
     */
    public static FileReading createFileReading(Media media) throws IOException
    {
        return new FileReadingImpl(media);
    }

    /**
     * Open a file as write only.
     * 
     * @param media The media file.
     * @return The created FileWriter.
     * @throws IOException If write failed.
     */
    public static FileWriting createFileWriting(Media media) throws IOException
    {
        return new FileWritingImpl(media);
    }

    /**
     * Create an xml parser, in order to load an xml node from a file.
     * 
     * @return The parser reference.
     */
    public static XmlParser createXmlParser()
    {
        return new XmlParserImpl();
    }

    /**
     * Create an xml node from a name.
     * 
     * @param name The node name.
     * @return The node reference.
     */
    public static XmlNode createXmlNode(String name)
    {
        return new XmlNodeImpl(name);
    }
}
