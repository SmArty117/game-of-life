package uk.ac.cam.ds780.oop.gol;

/**
 * Created by serem on 22-Nov-16.
 * This is an Exception that is thrown when one attempts to find a Pattern
 * in PatternStore and the Pattern is not found using the given criteria.
 */
public class PatternNotFound extends Exception {
    public PatternNotFound(String msg)
    {
        super("PatternNotFound. " + msg);
    }

    public PatternNotFound(String criteria, String key)
    {
        super("PatternNotFound. Looked up by " + criteria + ": " + key);
    }
}
