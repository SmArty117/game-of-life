package uk.ac.cam.ds780.oop.gol;

/**
 * Created by serem on 15-Nov-16.
 * The implementation of World using a 2D array. This does not have the size
 * limitations that PackedWorld has.
 */
public class ArrayWorld extends World implements Cloneable {

    private boolean[][] mWorld;
    private boolean[] mDeadRow;

    /**
     * @param formatSeed is the formatted String, as described in Packed class
     * @throws PatternFormatException if the formatted String is wrong
     */
    public ArrayWorld(String formatSeed) throws PatternFormatException
    {
        super(formatSeed);
        mWorld = new boolean[getHeight()][getWidth()];
        mDeadRow = new boolean[getWidth()];
        getPattern().initialise(this);

        //look for dead rows and make them point to mDeadRow
        for (int row = 0; row < this.getHeight(); row++) {
            boolean dead = true;
            for(boolean c : this.mWorld[row])
                if(c) dead = false;
            if(dead)
                this.mWorld[row] = mDeadRow;
        }
    }

    /**
     * @param pattern is a Pattern object. It is used to initialise the World.
     */
    public ArrayWorld(Pattern pattern)
    {
        super(pattern);
        mWorld = new boolean[getHeight()][getWidth()];
        mDeadRow = new boolean[getWidth()];
        pattern.initialise(this);

        //look for dead rows and make them point to mDeadRow
        for (int row = 0; row < this.getHeight(); row++) {
            boolean dead = true;
            for(boolean c : this.mWorld[row])
                if(c) dead = false;
            if(dead)
                this.mWorld[row] = mDeadRow;
        }
    }

    public ArrayWorld(ArrayWorld toCopy)
    {
        super(toCopy);
        this.mDeadRow = toCopy.mDeadRow;

        this.mWorld = new boolean[getHeight()][getWidth()];
        for (int row = 0; row < toCopy.getHeight(); row++) {
            if(toCopy.mWorld[row] != mDeadRow)
                System.arraycopy(toCopy.mWorld[row], 0, this.mWorld[row], 0,this.getWidth());
            else this.mWorld[row] = mDeadRow;
        }
    }

    //region Overrides
    @Override
    public boolean getCell(int col, int row)
    {
        if(row < 0 || row >= getHeight()) return false;
        if(col < 0 || col >= getWidth()) return false;

        return mWorld[row][col];
    }

    @Override
    public void setCell(int col, int row, boolean value)
    {
        if(row < 0 || row >= getHeight()) return;
        if(col < 0 || col >= getWidth()) return;

        mWorld[row][col] = value;
    }

    @Override
    void nextGenerationImpl()
    {
        boolean[][] nextGeneration = new boolean[mWorld.length][];

        for (int y = 0; y < mWorld.length; ++y)
        {
            nextGeneration[y] = new boolean[mWorld[y].length];

            for (int x = 0; x < mWorld[y].length; ++x)
            {
                boolean nextCell = computeCell(x, y);
                nextGeneration[y][x] = nextCell;
            }
        }

        mWorld = nextGeneration;
    }

    @Override
    public ArrayWorld clone() throws CloneNotSupportedException
    {
        ArrayWorld clone = (ArrayWorld) super.clone();

        clone.mWorld = new boolean[getHeight()][getWidth()];
        for (int row = 0; row < this.getHeight(); row++) {
            if(this.mWorld[row] != mDeadRow)
                System.arraycopy(this.mWorld[row], 0, clone.mWorld[row], 0,this.getWidth());
            else clone.mWorld[row] = mDeadRow;
        }

        return clone;
    }
    //endregion
}
