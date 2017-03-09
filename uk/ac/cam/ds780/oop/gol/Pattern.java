package uk.ac.cam.ds780.oop.gol;

/**
 * Created by serem on 15-Nov-16.
 * The class that supports the seed pattern for World. It is used to parse
 * the input string, save the pattern's details and initialise the World with
 * the SEED contained in the formatted string.
 */
public class Pattern implements Comparable<Pattern> {


    private final String mName;
    private final String mAuthor;
    private final int mWidth;
    private final int mHeight;
    private final int mStartCol;
    private final int mStartRow;
    private final String mCells;

    public String getName()
    {
        return mName;
    }

    public String getAuthor()
    {
        return mAuthor;
    }

    public int getWidth()
    {
        return mWidth;
    }

    public int getHeight()
    {
        return mHeight;
    }

    public int getStartCol()
    {
        return mStartCol;
    }

    public int getStartRow()
    {
        return mStartRow;
    }

    public String getCells()
    {
        return mCells;
    }

    /**
     * @param format a string of format: NAME:AUTHOR:WIDTH:HEIGHT:STARTCOL:STARTROW:SEED
     *               Where: NAME is the name of the pattern
     *                      AUTHOR is the name of the author
     *                      WIDTH and HEIGHT give the size of the pattern
     *                      STARTCOL and STARTROW give the column and row from which to start inserting the SEED
     *                      SEED contains some values to which the world is initialised, in rows separated by spaces
     *               Based on this formatted string the constructor builds an instance of Pattern
     *
     *               Example: "Glider:Richard K. Guy:20:20:1:1:001 101 011"
     * @throws PatternFormatException if the string does not match the required format
     */
    public Pattern(String format) throws PatternFormatException
    {

        String[] fields = format.split(":");

        if(fields.length != 7)
            throw new PatternFormatException("Incorrect number of fields in pattern (found " + fields.length + ")");

        mName = fields[0];
        mAuthor = fields[1];

        try
        {
            mWidth = Integer.parseInt(fields[2]);
            if(mWidth < 0)
                throw new PatternFormatException("Found parsing width field: negative numbers not allowed!");

        }
        catch(NumberFormatException numExc)
        {
            throw new PatternFormatException("Found parsing width field:\n" + numExc.getMessage());
        }

        try
        {
            mHeight = Integer.parseInt(fields[3]);
            if(mHeight < 0)
                throw new PatternFormatException("Found parsing height field: negative numbers not allowed!");

        }
        catch(NumberFormatException numExc)
        {
            throw new PatternFormatException("Found parsing height field:\n" + numExc.getMessage());
        }

        try
        {
            mStartCol = Integer.parseInt(fields[4]);
            if(mStartCol < 0)
                throw new PatternFormatException("Found parsing start column field: negative numbers not allowed!");

        }
        catch(NumberFormatException numExc)
        {
            throw new PatternFormatException("Found parsing start column field:\n" + numExc.getMessage());
        }

        try
        {
            mStartRow = Integer.parseInt(fields[5]);
            if(mStartRow < 0)
                throw new PatternFormatException("Found parsing start row field: negative numbers not allowed!");

        }
        catch(NumberFormatException numExc)
        {
            throw new PatternFormatException("Found parsing start row field:\n" + numExc.getMessage());
        }

        mCells = fields[6];

        //check if pattern does not overflow the world
        if(mCells.split(" ").length + getStartRow() - 1 >= getHeight())
            throw new PatternFormatException("Seed contains too many rows!");

        for(String s : mCells.split(" "))
        {
            if(s.length() +  getStartCol() - 1 >= getWidth())
                throw new PatternFormatException("Seed contains too many columns!");
            for(char c : s.toCharArray())
                if(c != '1' && c != '0')
                    throw new PatternFormatException("Seed contains forbidden characters. (" + c +") Only use 1 and 0" +
                            " in Seed.");
        }
    }


    /**
     * This function initialises the World argument with the seed given
     * @param world is the world being initialised
     */
    public void initialise(World world)
    {
        String[] seed = mCells.split(" ");


        for(int i = 0; i < seed.length; i++)
        {
            char[] rowArray = seed[i].toCharArray();

            for(int j = 0; j < rowArray.length; j++)
                if(rowArray[j] == '1')
                    world.setCell(mStartCol + j, mStartRow + i, true);
        }
    }

    @Override
    public int compareTo(Pattern p) {
        return mName.compareTo(p.mName);
    }

    @Override
    public String toString() {
        return this.getName() + " (" + this.getAuthor() + ")";
    }


    /*public static void testMain(String[] args)
    {
        //String test = "Glider:Richard Guy:20:20:1:1:010 001 111";
        try
        {
            Pattern pat = new Pattern(args[0]);

            System.out.println("Name: " + pat.getName());
            System.out.println("Author: " + pat.getAuthor());
            System.out.println("Width: " + pat.getWidth());
            System.out.println("Height: " + pat.getHeight());
            System.out.println("StartCol: " + pat.getStartCol());
            System.out.println("StartRow: " + pat.getStartRow());
            System.out.println("Pattern: " + pat.getCells());
        }
        catch(PatternFormatException exc)
        {
            System.out.println(exc.getMessage());
        }
    }*/
}
