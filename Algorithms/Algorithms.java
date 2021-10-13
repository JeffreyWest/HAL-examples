package Examples.Algorithms;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.Grid1Dobject;
import HAL.Gui.GifMaker;
import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;
import HAL.Rand;
import HAL.Util;

public class Algorithms extends Grid1Dobject<AlgorithmRow> {

    static final int BUBBLE = 0, ODDEVEN=1;

    static final int DIMENSION = 100; // was 300
    static final int SCALE_FACTOR = 5;
    static final int PAUSE = 0; // set a "pause" between timesteps (milliseconds)
    static final boolean SAVE_GIF = true;
    static final int GIF_DRAW_MODIFIER = 1;

    // used for visualization ( do not change )
    public UIGrid vis;
    public Rand rn;
    public GifMaker gifMaker = (SAVE_GIF) ? new GifMaker("Examples/Algorithms/algo.gif",100*GIF_DRAW_MODIFIER,true) : null;//used for visualization;


    /*
        main
    */

    public static void main(String[]args){


        Rand rn = new Rand(1);

        UIWindow win = new UIWindow("Algorithms",true,null,true);


        Algorithms algorithms = new Algorithms(rn);
        win.AddCol(0, algorithms.vis);
        win.RunGui();

        //BUBBLE/ODDEVEN
        algorithms.StepAlgorithm(BUBBLE);

        if (algorithms.SAVE_GIF) { algorithms.gifMaker.Close(); }
        win.Close();
        return;
    }


    /*
        Algorithms CONSTRUCTOR
    */

    public Algorithms(Rand rn) {
        super(DIMENSION,false);

        // each row is a new Algorithm row
        vis = new UIGrid(DIMENSION, DIMENSION, SCALE_FACTOR);

        for (int i = 0; i < DIMENSION; i++) {
            this.Set(i,new AlgorithmRow(rn,DIMENSION,vis));
        }

        Draw();
    }


    /*
        StepAlgorithm()
            - loop through the algorithm, draw after all rows complete one iteration
    */

    public void StepAlgorithm(int algorithm){

        switch (algorithm) {
            case BUBBLE:
                BubbleSort();
                break;
            case ODDEVEN:
                OddEven();
                break;
            default:
                // code block
                BubbleSort();
                break;
        }




    }

    // draw the outbreak world (without chart included)
    public void Draw() {

        for (int i = 0; i < DIMENSION; i++) {
            Get(i).Draw(vis,i);
        }

        if (SAVE_GIF) {
            gifMaker.AddFrame(vis);
        }
        this.vis.TickPause(PAUSE);
    }






    // BUBBLE SORT
    public void BubbleSort() {
        for (int r = 0; r < DIMENSION; r++) {
            for (int j = 0; j < DIMENSION; j++) {
                AlgorithmRow algorithmRow = Get(j);
                algorithmRow.BubbleSort(r);
            }
            Draw();
        }
    }

    public void OddEven() {


        boolean allSorted = false;


        while (!allSorted) {
            allSorted = true;

            // loop every other column
            for (int i = 0; i <= this.xDim - 2; i = i + 2) {

                // loop every row which needs to be sorted:
                for (int j = 0; j < DIMENSION; j++) {
                    AlgorithmRow algorithmRow = Get(j);
                    boolean thisSorted = algorithmRow.OddEven(i);

                    allSorted = (!thisSorted) ? false : allSorted;
                }
            }
            Draw();
        }



    }


}