package uk.ac.cam.ds780.oop.gol;


/**
 * Created by serem on 15-Nov-16.
 * The implementation of World using a packed long. Notice that this implementation
 * is limited to an 8 x 8 world, and hence an 8 x 8 pattern.
 */
public class PackedWorld extends World implements Cloneable {

    private long mWorld;

    public PackedWorld(String formatSeed) throws PatternFormatException
    {
        super(formatSeed);

        if(getPattern().getHeight() * getPattern().getWidth() > 64)
            throw new PatternFormatException("Pattern cannot be represented by a Packed (8 x 8) World: " + getPattern().getHeight() + " x " + getPattern().getWidth());

        mWorld = 0L;
        getPattern().initialise(this);
    }

    /**
     * @param pattern is a Pattern object. It is used to initialise the World.
     */
    public PackedWorld(Pattern pattern)
    {
        super(pattern);
        mWorld = 0L;
        pattern.initialise(this);
    }

    public PackedWorld(PackedWorld toCopy)
    {
        super(toCopy);
        this.mWorld = toCopy.mWorld;
    }

    //region Overrides
    @Override
    public boolean getCell(int col, int row)
    {
        if(row < 0 || row >= getHeight()) return false;
        if(col < 0 || col >= getWidth()) return false;

        int i = col + row*getWidth();
        return ((mWorld>>i) & 1) == 1;
    }

    @Override
    public void setCell(int col, int row, boolean value)
    {
        if(row < 0 || row >= getHeight()) return;
        if(col < 0 || col >= getWidth()) return;

        int i = col + row*getWidth();
        mWorld = setPackedLong(mWorld, i, value);
    }

    @Override
    void nextGenerationImpl()
    {
        long newWorld = 0;

        for(int y = 0; y < getHeight(); y++)
            for(int x = 0; x < getWidth(); x++)
            {
                boolean nextValue = computeCell(x, y);
                newWorld = setPackedLong(newWorld, x + getWidth()*y, nextValue);
            }

        mWorld = newWorld;
    }

    @Override
    public PackedWorld clone() throws CloneNotSupportedException
    {
        return (PackedWorld) super.clone();
    }
    //endregion

    /**
     * Modifies the value of one bit in the binary representation of a long (64 bit number)
     * @param packed - The packed long to modify
     * @param position - The bit to modify
     * @param value - The value to which to set the position bit
     * @return - The new modified packed long
     */
    private static long setPackedLong(long packed, long position, boolean value)
    {
        if(value) //set position bit to 1
        {
            packed = (packed | (1L<<position));
        }

        else //set position bit to 0
        {
            long singleBit = (packed & (1L<<position)); //get the position bit
            packed -= singleBit; //cancel it
        }

        return packed;
    }
}
