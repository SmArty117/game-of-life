package uk.ac.cam.ds780.oop.gol;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by serem on 03-Jan-17.
 * This class contains the GUI that runs Conway's Game of Life.
 */
public class GUILife extends JFrame implements ListSelectionListener {

    private World mWorld;
    private PatternStore mStore;
    private ArrayList<World> mCachedWorlds;
    private GamePanel mGamePanel;
    private JButton mPlayButton;
    private Timer mTimer;
    private boolean mPlaying;
    private int dt = 250;
    private JLabel mSpeedLabel;

    public GUILife(PatternStore ps) {
        super("Game of Life");
        mStore = ps;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1024,768);

        add(createPatternsPanel(),BorderLayout.WEST);
        add(createControlPanel(),BorderLayout.SOUTH);
        add(createGamePanel(),BorderLayout.CENTER);
    }

    //region GUI
    private void addBorder(JComponent component, String title) {
        Border etch = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
        Border tb = BorderFactory.createTitledBorder(etch,title);
        component.setBorder(tb);
    }

    private JPanel createGamePanel() {
        mGamePanel = new GamePanel();
        addBorder(mGamePanel,"Game Panel");
        return mGamePanel;
    }

    private JPanel createPatternsPanel() {
        JPanel patt = new JPanel();
        addBorder(patt,"Patterns");

        patt.setLayout(new BorderLayout());
        JList<Pattern> patList = new JList<>(mStore.getPatternsArray());
        patList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patList.addListSelectionListener(this);
        JScrollPane patScrollPane = new JScrollPane(patList);
        patt.add(patScrollPane);

        return patt;
    }

    private JPanel createControlPanel() {
        JPanel ctrl =  new JPanel();
        addBorder(ctrl,"Controls");

        JButton backButton = new JButton("< Back");
        JButton playButton = new JButton("Play");
        JButton fwdButton = new JButton("Forward >");
        JButton slowButton = new JButton("<< Slower");
        JButton autospeedButton = new JButton("Auto speed");
        JButton fastButton = new JButton("Faster >>");
        JButton resetButton = new JButton("Reset Game");
        JLabel speedLabel = new JLabel("Current speed: " + String.format("%.2f", 1000d/dt) + " fps");
        mSpeedLabel = speedLabel;

        playButton.setBackground(Color.GREEN);

        backButton.addActionListener(e -> {
                    if(mPlaying) runOrPause();
                    moveBack();
        });
        fwdButton.addActionListener(e -> {
            if(mPlaying) runOrPause();
            moveForward();
        });
        mPlayButton = playButton;
        playButton.addActionListener(e -> runOrPause());

        slowButton.addActionListener(e -> {
            if (mPlaying) {
                runOrPause();
                dt *= 2;
                updateSpeed();
                runOrPause();
            } else
                dt *= 2;
            updateSpeed();
        });

        fastButton.addActionListener(e -> {
            if (mPlaying) {
                runOrPause();
                dt /= 2;
                updateSpeed();
                runOrPause();
            } else
                dt /= 2;
            updateSpeed();
        });

        autospeedButton.addActionListener(e -> {
            if (mPlaying) {
                runOrPause();
                autoSpeed();
                updateSpeed();
                runOrPause();
            } else
                autoSpeed();
            updateSpeed();
        });

        resetButton.addActionListener(e -> {
            if(mPlaying) runOrPause();
            mWorld = mCachedWorlds.get(0);
            this.mGamePanel.display(mWorld);
        });

        ctrl.setLayout(new GridLayout(3, 3));
        ctrl.add(backButton);
        ctrl.add(playButton);
        ctrl.add(fwdButton);
        ctrl.add(slowButton);
        ctrl.add(autospeedButton);
        ctrl.add(fastButton);
        ctrl.add(speedLabel);
        ctrl.add(resetButton);

        return ctrl;
    }

    static private void createAlert(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        JOptionPane.showMessageDialog(null,
                exceptionText,
                "An Exception Occurred",
                JOptionPane.ERROR_MESSAGE);
    }
    //endregion

    //region backend
    private void moveBack() {
        if(mWorld != null) {
            if (mWorld.getGenerationCount() > 0)
                mWorld = mCachedWorlds.get(mCachedWorlds.indexOf(mWorld) - 1);
            mGamePanel.display(mWorld);
        }
    }

    private void moveForward() {
        if(mWorld != null) {
            if (mCachedWorlds.indexOf(mWorld) == mCachedWorlds.size() - 1) {
                mWorld = copyWorld(true);
                mWorld.nextGeneration();
                mCachedWorlds.add(mWorld);
            } else {
                mWorld = mCachedWorlds.get(mCachedWorlds.indexOf(mWorld) + 1);
            }
            mGamePanel.display(mWorld);
        }
    }

    private void autoSpeed() {
        dt = (int)Math.round(1000/Math.log((double)this.mWorld.getHeight()*this.mWorld.getWidth()));
    }

    private void updateSpeed() {
        this.mSpeedLabel.setText("Current speed: " + String.format("%.2f", 1000d/dt) + " fps");
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

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if(mPlaying) runOrPause();
        JList<Pattern> list = (JList<Pattern>) e.getSource();
        Pattern p = list.getSelectedValue();
        if(p.getWidth() * p.getHeight() <= 64)
            mWorld = new PackedWorld(p);
        else mWorld = new ArrayWorld(p);
        mCachedWorlds = new ArrayList<>();
        mCachedWorlds.add(mWorld);
        this.mGamePanel.display(mWorld);
        dt = 250; updateSpeed();
    }

    private void runOrPause() {
        if (mPlaying) {
            mTimer.cancel();
            mPlaying = false;
            mPlayButton.setText("Play");
            mPlayButton.setBackground(Color.GREEN);
        }
        else {
            mPlaying = true;
            mPlayButton.setText("Stop");
            mPlayButton.setBackground(Color.RED);
            mTimer = new Timer(true);
            mTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    moveForward();
                }
            }, 0, dt);
        }
    }
    //endregion

    public static void main(String[] args) {
        try{
            GUILife gui = new GUILife(new PatternStore("http://www.cl.cam.ac.uk/teaching/1617/OOProg/ticks/life.txt"));
            gui.setVisible(true);
        }
        catch(Exception ex) {
            createAlert(ex);
        }
    }
}
