import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BeatBox {
    JPanel mainPanel;
    ArrayList<JCheckBox> checkboxList; // to store the user checked boxes
    Sequencer sequencer;
    Sequence sequence;
    Track track;
    JFrame theFrame;

    String[] instrumentNames = {"Brass Dru,","Closed Hit-Hat","Open Hit-Hat","Acoustic Snare" +
            "Crash Cymbal","Hand Clap","High Tom","Hi Bongo","Maracas","Whistle","Low Conga","Cowbell",
            "Vibraslap","Low-mid Tom","High Agogo","Open Hi Conga"}; //names of instruments
    int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63}; // represent drum keys

    public static void main(String[] args) {
        new BeatBox().buildGui();
   }
    public void buildGui() {
        theFrame = new JFrame("Cyber BeatBox");
        theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));//a margin between edges of panel

        checkboxList = new ArrayList<JCheckBox>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        JButton start = new JButton("Start");
        start.addActionListener(new MyStartListener());
        buttonBox.add(start);

        JButton stop = new JButton("Stop");
        stop.addActionListener(new MyStopListener());
        buttonBox.add(stop);

        JButton upTempo = new JButton("Tempo Up");
        upTempo.addActionListener(new MyUpTempoListener());
        buttonBox.add(upTempo);

        JButton downTempo = new JButton("Tempo Down");
        downTempo.addActionListener(new MyDownTempoListener());
        buttonBox.add(downTempo);

        Box nameBox = new Box(BoxLayout.Y_AXIS);
        for (int i = 0; i< 15; i++){
            nameBox.add(new Label(instrumentNames[i]));
        }
        background.add(BorderLayout.EAST,buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        theFrame.getContentPane().add(background);

        GridLayout grid = new GridLayout(16,16);
        grid.setVgap(1);
        grid.setHgap(2);
        mainPanel = new JPanel(grid);
        background.add(BorderLayout.CENTER,mainPanel);

        for (int i = 0; i < 256; i++){ // making check box and setting them to false
            JCheckBox c = new JCheckBox();
            c.setSelected(false);
            checkboxList.add(c);
            mainPanel.add(c);
        }//end of loop

        setUpMidi();
        theFrame.setBounds(50,50,300,300);
        theFrame.pack();
        theFrame.setVisible(true);
    }// close buildGui method

    public void setUpMidi () {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequence = new Sequence(Sequence.PPQ,4);
            track = sequence.createTrack();
            sequencer.setTempoInBPM(120);


        } catch (Exception e) {}
    }
    public void buildTrackAndStart() {
        int[] trackList = null;    ///holding the values for one instrument
        sequence.deleteTrack(track); //getting rid of the old tracks
        track = sequence.createTrack();// make new track

        for (int i = 0; i < 16; i++){ //do this for each of the 16  ROWS(Bas,Congo..etc)
            trackList = new int[16];

            int key = instruments[i]; //set the 'key' that represents which instrument this is

            for (int j = 0; j< 16; j++){

                JCheckBox jc = checkboxList.get(j + 16*i);
                if (jc.isSelected()){ // if beat is selected put it in the key value array,
                    trackList[j] = key;
                }else { // if not, dont play. set it to zero
                    trackList[j] = 0;
                }///close inner loop

                makeTracks(trackList);
                track.add(makeEvent(176,1,127,0,16));//make events and add them to the track
            }//close Outer loop

            track.add(makeEvent(192,9,1,0,15));// to make sure there IS an event at beat 16

            //Play the Beat
            try {
                sequencer.setSequence(sequence);
                sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
                sequencer.start();
                sequencer.setTempoInBPM(120);
            }catch (Exception e) {e.printStackTrace();}

        }

    } // close buildTrackAndStart method
    public class MyStartListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            buildTrackAndStart();
        }
    } //close inner method

    public class MyStopListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            sequencer.stop();
        }
    }//close inner class

    public class MyUpTempoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor * 1.03));

        }
    }// Close inner class

    public class MyDownTempoListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor((float) (tempoFactor* .97));
        }
    }// close inner class

    public void makeTracks (int[] list){
        for (int i = 0; i < 16; i++) {
            int key = list[i];

            if (key != 0) {
                track.add(makeEvent(144,9,key,100,i));
                track.add(makeEvent(128,9,key,100,i + 1));

            }
        }
    }

    public MidiEvent makeEvent (int comd, int chan, int one, int two, int tick) {
        MidiEvent event = null;
        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(comd, chan, one,two);
            event = new MidiEvent(a,tick);
        } catch (Exception E) {E.printStackTrace();}
        return event;
    }




    }

