package uk.ac.cam.ds780.oop.gol;

/**
 * Created by serem on 15-Nov-16.
 * This is the parent World class. It is inherited by PackedWorld and ArrayWorld.
 */
public abstract class World implements Cloneable {

    public World(String formatSeed) throws PatternFormatException
    {
        mPattern = new Pattern(formatSeed);
        mGeneration = 0;
    }

    public World(Pattern pattern)
    {
       this.mPattern = pattern;
       mGeneration = 0;
    }

    public World(World toCopy)
    {
        this.mGeneration = toCopy.mGeneration;
        this.mPattern = toCopy.mPattern; //okay because Pattern is immutable
    }

    private int mGeneration;

    public int getGenerationCount() {
        return mGeneration;
    }

    private Pattern mPattern;

    Pattern getPattern(){
        return mPattern;
    }

    public int getHeight() {
        return mPattern.getHeight();
    }

    public int getWidth() {
        return mPattern.getWidth();
    }

    //region Abstract Methods
    public abstract boolean getCell(int col, int row);
    public abstract void setCell(int col, int row, boolean value);
    abstract void nextGenerationImpl();
    //endregion

    //region Internal Functionality Methods
    private int countNeighbours(int col, int row)
    {
        int[] dcol = {0, 1, 1, 1, 0, -1, -1, -1};
        int[] drow = {-1, -1, 0, 1, 1, 1, 0, -1};

        int neighbours = 0;

        for(int i=0; i < 8; i++)
        {
            if(getCell(col+dcol[i], row+drow[i]))
                neighbours++;
        }

        return neighbours;
    }

    boolean computeCell(int col, int row)
    {
        boolean live = getCell(col, row);
        boolean next = false;
        int neighbours = countNeighbours(col, row);

        if(live)
        {
            if(neighbours == 2 || neighbours == 3)
                next = true;

            if(neighbours < 2 || neighbours > 3)
                next = false;
        }

        else
        if(neighbours == 3)
            next = true;

        return next;
    }

    /**
     * This method is used to call the implementation's own method for calculating
     * the next generation and then incrementing the generation counter.
     */
    public void nextGeneration()
    {
        nextGenerationImpl();
        mGeneration++;
    }
    //endregion

    @Override
    public World clone() throws CloneNotSupportedException
    {
         return (World) super.clone();
    }

}
