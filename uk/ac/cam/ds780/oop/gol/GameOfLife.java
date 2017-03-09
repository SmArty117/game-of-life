package uk.ac.cam.ds780.oop.gol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by serem on 15-Nov-16.
 * The main class that instantiates a world and plays it.
 */
public class GameOfLife {

    private World mWorld;
    private PatternStore mStore;
    private ArrayList<World> mCachedWorlds;

    /*public GameOfLife(World w){
        mWorld = w;
    }*/

    public GameOfLife(PatternStore store) {
        this.mStore = store;
        this.mWorld = null;
    }

    private World copyWorld(boolean useCloning) {
        if(!useCloning) {
            if(mWorld instanceof ArrayWorld)
                return new ArrayWorld((ArrayWorld) mWorld);
            else if(mWorld instanceof PackedWorld)
                return new PackedWorld((PackedWorld) mWorld);
        }

        else
            try{
            return mWorld.clone();
            }
            catch(CloneNotSupportedException cnse) {
                System.out.println(cnse.getMessage());
            }
        return null;
    }

    public void print() {
        System.out.println("- " + mWorld.getGenerationCount());
        for (int row = 0; row < mWorld.getHeight(); row++) {
            for (int col = 0; col < mWorld.getWidth(); col++) {
                System.out.print(mWorld.getCell(col, row) ? "#" : "_");
            }
            System.out.println();
        }
    }



    /**
     * Takes the command line input and plays the game:
     *
     * f to go one gen forward
     * b to go one gen back
     * q to quit
     * l to list possible patterns
     * p X to play pattern X (X is an int)
     *
     * note: when moving through generations have to copy mWorld because
     * the references to each generation are stored in mCachedWorlds
     *
     * @throws IOException when the buffered reader does
     */
    public void play() throws IOException {

        String response = "";
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Please select a pattern to play (l to list):");
        response = in.readLine();

        while (!response.equals("q")) {

            if (response.equals("f")) {
                if (mWorld == null) System.out.println("Please select a pattern to play (l to list):");
                else {
                    if(mCachedWorlds.indexOf(mWorld) == mCachedWorlds.size()-1) {
                        mWorld = copyWorld(true);
                        mWorld.nextGeneration();
                        mCachedWorlds.add(mWorld);
                    }
                    else{
                        mWorld = mCachedWorlds.get(mCachedWorlds.indexOf(mWorld)+1);
                    }
                    print();
                }
            }
            else if (response.equals("b")) {
                if (mWorld == null) System.out.println("Please select a pattern to play (l to list):");
                else if (mWorld.getGenerationCount() > 0)
                    mWorld = mCachedWorlds.get(mCachedWorlds.indexOf(mWorld)-1);
                print();
            }
            else if (response.equals("l")) {
                List<Pattern> names = mStore.getPatternsNameSorted();
                int i = 0;
                for (Pattern p : names) {
                    System.out.println(i + " " + p.getName() + "  (" + p.getAuthor() + ")");
                    i++;
                }
            }
            else if (response.startsWith("p")) {
                List<Pattern> names = mStore.getPatternsNameSorted();

                try {
                    int worldIndex = Integer.parseInt(response.split(" ")[1]);
                    Pattern p = names.get(worldIndex);
                    if(p.getWidth() * p.getHeight() <= 64)
                        mWorld = new PackedWorld(p);
                    else mWorld = new ArrayWorld(p);
                    mCachedWorlds = new ArrayList<>();
                    mCachedWorlds.add(mWorld);
                    print();
                }
                catch (NumberFormatException | ArrayIndexOutOfBoundsException exc)
                {
                    System.out.println("Usage: \"p X\" to select pattern with number X. X must be an integer.");
                }
            }
            else {
                System.out.println("Invalid input.");
                System.out.println("Usage: l to list. p to choose world. f to play world.");
            }
            response = in.readLine();
        }
    }

    /*
     * http://www.cl.cam.ac.uk/teaching/current/OOProg/ticks/life.txt
     * large patterns repository
     */
    public static void mainmain(String args[]) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: java GameOfLife <path/url to store>");
            return;
        }

        try {
            PatternStore ps = new PatternStore(args[0]);
            GameOfLife gol = new GameOfLife(ps);
            gol.play();
        } catch (IOException ioe) {
            System.out.println("Failed to load pattern store.");
        }
    }

    //test
    public static void main(String[] args) throws IOException {
        try {
            PatternStore ps = new PatternStore("http://www.cl.cam.ac.uk/teaching/current/OOProg/ticks/life.txt");
            GameOfLife gol = new GameOfLife(ps);
            gol.play();
        } catch (IOException ioe) {
            System.out.println("Failed to load pattern store.");
            System.out.println(ioe.getMessage());
        }
    }
}
