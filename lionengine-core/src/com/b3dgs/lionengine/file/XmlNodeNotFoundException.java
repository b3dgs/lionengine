package com.b3dgs.lionengine.file;

/**
 * Thrown when an xml node is not found.
 */
public class XmlNodeNotFoundException
        extends Exception
{
    /** Serial uid. */
    private static final long serialVersionUID = 553335854614950463L;

    /**
     * Constructor.
     * 
     * @param message The message
     */
    public XmlNodeNotFoundException(String message)
    {
        super(message);
    }
}
