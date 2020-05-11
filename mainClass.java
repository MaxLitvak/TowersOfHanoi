//**Press "a" or "b" to make it go. Pressing "a" will take a long time to load with anything over 5 rings (five takes about 10 seconds)

import sun.java2d.pipe.AAShapePipe;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.IOException;
import java.lang.invoke.LambdaConversionException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.awt.*;
import java.util.HashMap;

public class mainClass implements Runnable, KeyListener {
    final int WIDTH = 1400;
    final int HEIGHT = 800;

    JFrame frame;
    Canvas canvas;
    BufferStrategy bufferStrategy;

    //This arraylist stores the places that the recursion has already been
    private ArrayList<ArrayList<String>[]> alreadyBeenThere = new ArrayList<>();

    //This arraylist stores the number of moves to the places in the previous arraylist
    private ArrayList<Integer> numOfMovesToPastPlaces = new ArrayList<>();

    //number of rings to start of with on left rod
    private int numberOfRings = 5;
    private ROD rods[];
    private boolean move = false;
    private String finalPath;
    private int numMovesInBestPath = 100000;
    private String easyPath = "";


    public static void main(String[] args) {
        mainClass ex = new mainClass();
        new Thread(ex).start();
    }

    public mainClass() {

        frame = new JFrame("towersOfHanoi");

        JPanel panel = (JPanel) frame.getContentPane();
        panel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        panel.setLayout(null);

        canvas = new Canvas();
        canvas.setBounds(0, 0, WIDTH, HEIGHT);
        canvas.setIgnoreRepaint(true);

        panel.add(canvas);
        canvas.addKeyListener(this);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

        canvas.createBufferStrategy(2);
        bufferStrategy = canvas.getBufferStrategy();

        canvas.requestFocus();

        rods = new ROD[3];
        for (int x = 0; x < 3; x++) {
            rods[x] = new ROD(x);
        }

        //puts rings onto the left rod
        for (int x = 0; x < numberOfRings; x++) {
            ring hi = new ring(numberOfRings - x, 0);
            rods[0].rings.add(hi);
        }
    }

    public void run() {

        while (true) {
            render();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {

            }
        }
    }

    public void render() {
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        g.clearRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.BLACK);
        g.fillRect(190, 200, 20, 600);
        g.fillRect(690, 200, 20, 600);
        g.fillRect(1190, 200, 20, 600);

        //draws the rings
        g.setColor(Color.RED);
        if (move == true) {
            for (int x = 0; x < 3; x++) {
                for (int a = 0; a < rods[x].rings.size(); a++) {
                    g.fillRect(x * 500 + 200 - (rods[x].rings.get(a).size * 20), 760 - a * 38, rods[x].rings.get(a).size * 40, 33);
                }
            }
        } else {
            for (int a = 0; a < numberOfRings; a++) {
                g.fillRect(200 - ((numberOfRings - a) * 20), 760 - a * 38, (numberOfRings - a) * 40, 33);
            }
        }

        g.dispose();
        bufferStrategy.show();
    }

    //This method finds the best path without knowing the pattern of the problem
    private void recursion(ROD[] allRods, int destination, int origin, int numMoves, String path) {
        if (origin != -1) {
            path = path + Integer.toString(origin) + " " + Integer.toString(destination) + " ";
        }
        numMoves++;

        ROD temporaryRod[];
        temporaryRod = new ROD[3];
        temporaryRod[0] = allRods[0];
        temporaryRod[1] = allRods[1];
        temporaryRod[2] = allRods[2];

        if (temporaryRod[2].rings.size() == numberOfRings) {
            if (numMoves < numMovesInBestPath) {
                finalPath = path;
                numMovesInBestPath = numMoves;
            }
        } else {
            boolean stop = false;
            ArrayList<String>[] position = new ArrayList[3];
            for (int q = 0; q < 3; q++) {
                position[q] = new ArrayList<>();
                for (int i = 0; i < temporaryRod[q].rings.size(); i++) {
                    position[q].add(Integer.toString(temporaryRod[q].rings.get(i).size));
                }
            }

            for (int w=0; w<alreadyBeenThere.size(); w++){
                boolean SWITCH=true;
                for (int e=0; e<3; e++){
                    if (!(alreadyBeenThere.get(w)[e].equals(position[e]))){
                        SWITCH = false;
                    }
                }
                if (SWITCH == true){
                    if (numMoves >= numOfMovesToPastPlaces.get(w)){
                        stop = true;
                    }
                }
            }

            if (numMoves < numMovesInBestPath && stop == false || origin == -1) {
                for (int x = 0; x < 3; x++) {
                    if (temporaryRod[x].rings.size() > 0 && x != destination) {
                        if (temporaryRod[temporaryRod[x].otherRods[0]].rings.size() == 0) {
                            ArrayList<String>[] currentPosition = new ArrayList[3];
                            for (int q = 0; q < 3; q++) {
                                currentPosition[q] = new ArrayList<>();
                                for (int i = 0; i < temporaryRod[q].rings.size(); i++) {
                                    currentPosition[q].add(Integer.toString(temporaryRod[q].rings.get(i).size));
                                }
                            }
                            alreadyBeenThere.add(currentPosition);
                            numOfMovesToPastPlaces.add(numMoves);
                            temporaryRod[temporaryRod[x].otherRods[0]].rings.add(temporaryRod[x].rings.get(temporaryRod[x].rings.size() - 1));
                            temporaryRod[x].rings.remove(temporaryRod[x].rings.get(temporaryRod[x].rings.size() - 1));
                            recursion(temporaryRod, temporaryRod[x].otherRods[0], x, numMoves, path);
                            temporaryRod[x].rings.add(temporaryRod[temporaryRod[x].otherRods[0]].rings.get(temporaryRod[temporaryRod[x].otherRods[0]].rings.size() - 1));
                            temporaryRod[temporaryRod[x].otherRods[0]].rings.remove(temporaryRod[temporaryRod[x].otherRods[0]].rings.size() - 1);
                        } else {
                            if (temporaryRod[x].rings.get(temporaryRod[x].rings.size() - 1).size < temporaryRod[temporaryRod[x].otherRods[0]].rings.get(temporaryRod[temporaryRod[x].otherRods[0]].rings.size() - 1).size) {
                                ArrayList<String>[] currentPosition = new ArrayList[3];
                                for (int q = 0; q < 3; q++) {
                                    currentPosition[q] = new ArrayList<>();
                                    for (int i = 0; i < temporaryRod[q].rings.size(); i++) {
                                        currentPosition[q].add(Integer.toString(temporaryRod[q].rings.get(i).size));
                                    }
                                }
                                alreadyBeenThere.add(currentPosition);
                                numOfMovesToPastPlaces.add(numMoves);
                                temporaryRod[temporaryRod[x].otherRods[0]].rings.add(temporaryRod[x].rings.get(temporaryRod[x].rings.size() - 1));
                                temporaryRod[x].rings.remove(temporaryRod[x].rings.get(temporaryRod[x].rings.size() - 1));
                                recursion(temporaryRod, temporaryRod[x].otherRods[0], x, numMoves, path);
                                temporaryRod[x].rings.add(temporaryRod[temporaryRod[x].otherRods[0]].rings.get(temporaryRod[temporaryRod[x].otherRods[0]].rings.size() - 1));
                                temporaryRod[temporaryRod[x].otherRods[0]].rings.remove(temporaryRod[temporaryRod[x].otherRods[0]].rings.size() - 1);
                            }
                        }
                        if (temporaryRod[x].rings.size() > 0 && x != destination) {
                            if (temporaryRod[temporaryRod[x].otherRods[1]].rings.size() == 0) {
                                ArrayList<String>[] currentPosition = new ArrayList[3];
                                for (int q = 0; q < 3; q++) {
                                    currentPosition[q] = new ArrayList<>();
                                    for (int i = 0; i < temporaryRod[q].rings.size(); i++) {
                                        currentPosition[q].add(Integer.toString(temporaryRod[q].rings.get(i).size));
                                    }
                                }
                                alreadyBeenThere.add(currentPosition);
                                numOfMovesToPastPlaces.add(numMoves);
                                temporaryRod[temporaryRod[x].otherRods[1]].rings.add(temporaryRod[x].rings.get(temporaryRod[x].rings.size() - 1));
                                temporaryRod[x].rings.remove(temporaryRod[x].rings.get(temporaryRod[x].rings.size() - 1));
                                recursion(temporaryRod, temporaryRod[x].otherRods[1], x, numMoves, path);
                                temporaryRod[x].rings.add(temporaryRod[temporaryRod[x].otherRods[1]].rings.get(temporaryRod[temporaryRod[x].otherRods[1]].rings.size() - 1));
                                temporaryRod[temporaryRod[x].otherRods[1]].rings.remove(temporaryRod[temporaryRod[x].otherRods[1]].rings.size() - 1);
                            } else {
                                if (temporaryRod[x].rings.get(temporaryRod[x].rings.size() - 1).size < temporaryRod[temporaryRod[x].otherRods[1]].rings.get(temporaryRod[temporaryRod[x].otherRods[1]].rings.size() - 1).size) {
                                    ArrayList<String>[] currentPosition = new ArrayList[3];
                                    for (int q = 0; q < 3; q++) {
                                        currentPosition[q] = new ArrayList<>();
                                        for (int i = 0; i < temporaryRod[q].rings.size(); i++) {
                                            currentPosition[q].add(Integer.toString(temporaryRod[q].rings.get(i).size));
                                        }
                                    }
                                    alreadyBeenThere.add(currentPosition);
                                    numOfMovesToPastPlaces.add(numMoves);
                                    temporaryRod[temporaryRod[x].otherRods[1]].rings.add(temporaryRod[x].rings.get(temporaryRod[x].rings.size() - 1));
                                    temporaryRod[x].rings.remove(temporaryRod[x].rings.get(temporaryRod[x].rings.size() - 1));
                                    recursion(temporaryRod, temporaryRod[x].otherRods[1], x, numMoves, path);
                                    temporaryRod[x].rings.add(temporaryRod[temporaryRod[x].otherRods[1]].rings.get(temporaryRod[temporaryRod[x].otherRods[1]].rings.size() - 1));
                                    temporaryRod[temporaryRod[x].otherRods[1]].rings.remove(temporaryRod[temporaryRod[x].otherRods[1]].rings.size() - 1);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //This method finds the best path while knowing the pattern of the problem
    public void normalWay(int disk, int origin, int destination, int extra){
        if (disk == 0){
            easyPath = easyPath + Integer.toString(origin)+" "+Integer.toString(destination)+" ";
        } else{
            normalWay(disk-1, origin, extra, destination);
            easyPath = easyPath + Integer.toString(origin)+" "+Integer.toString(destination)+" ";
            normalWay(disk-1, extra, destination, origin);
        }
    }

    //This method moves the rings on its path
    public void moveOnPath(String FinalPath) {
        move = true;
        for (int x = 0; x < FinalPath.length(); x += 4) {
            rods[Integer.parseInt(FinalPath.substring(x + 2, x + 3))].rings.add(rods[Integer.parseInt(FinalPath.substring(x, x + 1))].rings.get(rods[Integer.parseInt(FinalPath.substring(x, x + 1))].rings.size() - 1));
            rods[Integer.parseInt(FinalPath.substring(x, x + 1))].rings.remove(rods[Integer.parseInt(FinalPath.substring(x, x + 1))].rings.size() - 1);
            try {
                //***change number millis to change the wait time inbetween the movement of rings
                Thread.sleep(600);
            } catch (InterruptedException e) {

            }
            render();
        }

    }


    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'a') {
            ROD hi[];
            hi = new ROD[3];
            hi[0] = rods[0];
            hi[1] = rods[1];
            hi[2] = rods[2];
            recursion(hi, -1, -1, 0, "");

            for (int x = 0; x < 3; x++) {
                rods[x].rings.clear();
            }
            for (int x = 0; x < numberOfRings; x++) {
                ring v = new ring(numberOfRings - x, 0);
                rods[0].rings.add(v);
            }
            int p = numMovesInBestPath-1;
            System.out.println("FINAL PATH - NUMBER OF MOVES: "+p);
            System.out.println(finalPath);
            for (int x = 0; x < finalPath.length(); x += 4) {
                System.out.println(finalPath.substring(x, x + 3));
            }
            moveOnPath(finalPath);
        }

        if (e.getKeyChar() == 'b'){
            normalWay(numberOfRings,0,1,2);
            System.out.println(easyPath);
            moveOnPath(easyPath);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
