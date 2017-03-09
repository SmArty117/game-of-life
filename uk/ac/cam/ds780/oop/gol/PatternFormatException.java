package uk.ac.cam.ds780.oop.gol;

/**
 * Created by serem on 15-Nov-16.
 * Provides exceptions for the GameOfLife project.
 */
public class PatternFormatException extends Exception
{
    public PatternFormatException(String msg)
    {
        super("Invalid pattern format: " + msg);
    }

}