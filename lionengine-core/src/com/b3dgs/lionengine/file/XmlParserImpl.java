package com.b3dgs.lionengine.file;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Xml parser implementation.
 */
class XmlParserImpl
        implements XmlParser
{
    /**
     * Create a xml parser.
     */
    XmlParserImpl()
    {
        // Nothing to do
    }

    /*
     * XmlParser
     */

    @Override
    public XmlNode load(Media media)
    {
        final Element root;
        final String file = media.getPath();
        try
        {
            final SAXBuilder builder = new SAXBuilder();
            final Document gamesave = builder.build(Media.getStream(media, "XmlNode", false));
            root = gamesave.getRootElement();
        }
        catch (final JDOMException exception)
        {
            throw new LionEngineException(exception, "The XmlParser was unable to parse the following file \"", file,
                    "\"");
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, "An error occured while reading the following file: \"", file,
                    "\"");
        }
        return new XmlNodeImpl(root);
    }

    @Override
    public void save(XmlNode root, Media media)
    {
        final String file = media.getPath();
        final XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

        try (OutputStream outputStream = new FileOutputStream(file);)
        {
            outputter.output(new Document(((XmlNodeImpl) root).getElement()), outputStream);
        }
        catch (final FileNotFoundException exception)
        {
            throw new LionEngineException(exception, "The following file was not found: \"", file, "\"");
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, "An error occured while reading the following file: \"", file,
                    "\"");
        }
    }
}
