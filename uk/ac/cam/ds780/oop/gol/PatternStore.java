package uk.ac.cam.ds780.oop.gol;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by serem on 22-Nov-16.
 * This is a class that loads and stores the patterns for running GameOfLife
 */
public class PatternStore {

    private List<Pattern> mPatterns = new LinkedList<>();
    private Map<String,List<Pattern>> mMapAuths = new HashMap<>();
    private Map<String,Pattern> mMapName = new HashMap<>();

    public PatternStore(String source) throws IOException {
        if (source.startsWith("http://")) {
            loadFromURL(source);
        }
        else {
            loadFromDisk(source);
        }
    }

    public PatternStore(Reader source) throws IOException {
        load(source);
    }

    private void load(Reader r) throws IOException {
        BufferedReader bfr = new BufferedReader(r);
        String line = bfr.readLine();
        int i = 1;

        while(line != null)
        {
            //System.out.println(line);
            try{
                Pattern tempPattern = new Pattern(line);

                //add pattern to list of patterns
                mPatterns.add(tempPattern);

                //add pattern to authors map
                if(mMapAuths.containsKey(tempPattern.getAuthor()))
                    mMapAuths.get(tempPattern.getAuthor()).add(tempPattern);
                else
                {
                    List<Pattern> tempList = new LinkedList<>();
                    tempList.add(tempPattern);
                    mMapAuths.put(tempPattern.getAuthor(), tempList);
                }

                //add pattern to names map
                mMapName.put(tempPattern.getName(), tempPattern);

            }
            catch (PatternFormatException pfe)
            {
                System.out.println("In line " + i + ": " + line);
                System.out.println(pfe.getMessage());
            }

            line = bfr.readLine();
            i++;
        }
    }


    private void loadFromURL(String url) throws IOException {
        URL urlObj = new URL(url);
        URLConnection connection = urlObj.openConnection();
        Reader urlReader = new InputStreamReader(connection.getInputStream());
        load(urlReader);
    }

    private void loadFromDisk(String filename) throws IOException {
        Reader fileReader = new FileReader(filename);
        load(fileReader);
    }

    public List<Pattern> getPatternsNameSorted() {
        List<Pattern> copy = new LinkedList<>(mPatterns);
        Collections.sort(copy);
        return copy;
    }

    public Pattern[] getPatternsArray() {
        Pattern[] patterns = new Pattern[mPatterns.size()];
        int k = -1;
        for(Pattern p : mPatterns)
            patterns[++k] = p;
        Arrays.sort(patterns);
        return patterns;
    }

    public List<Pattern> getPatternsAuthorSorted() {
        List<Pattern> copy = new LinkedList<>(mPatterns);
        Collections.sort(copy, new Comparator<Pattern>() {
            @Override
            public int compare(Pattern o1, Pattern o2) {
                if(o1.getAuthor().compareTo(o2.getAuthor()) == 0)
                    return o1.compareTo(o2);
                else return o1.getAuthor().compareTo(o2.getAuthor());
            }
        });
        return copy;
    }

    public List<Pattern> getPatternsByAuthor(String author) throws PatternNotFound {
        if(mMapAuths.containsKey(author))
        {
            List<Pattern> patterns = new LinkedList<>(mMapAuths.get(author));
            Collections.sort(patterns);
            return patterns;
        }
        else throw new PatternNotFound("author", author);
    }

    public Pattern getPatternByName(String name) throws PatternNotFound {
        if(mMapName.containsKey(name))
            return mMapName.get(name); //this is OK because Pattern is immutable
        else throw new PatternNotFound("name", name);
    }

    public List<String> getPatternAuthors() {
        List<String> authors = new LinkedList<>();
        for(Pattern tempPattern : mPatterns)
            if(!authors.contains(tempPattern.getAuthor()))
                authors.add(tempPattern.getAuthor());
        Collections.sort(authors);
        return authors;
    }

    public List<String> getPatternNames() {
        List<String> names = new LinkedList<>();
        for(Pattern tempPattern : mPatterns)
            names.add(tempPattern.getName());
        Collections.sort(names);
        return names;
    }

    /*public static void main(String args[]) throws java.io.IOException {
        PatternStore p = new PatternStore(args[0]);

        List<String> authors = p.getPatternAuthors();
        for(String s : authors)
            System.out.println(s);

        System.out.println();
        List<Pattern> patterns = p.getPatternsAuthorSorted();
        for(Pattern tempPattern : patterns)
            System.out.println(tempPattern.getName());
        //System.exit(0);
    }*/

}
