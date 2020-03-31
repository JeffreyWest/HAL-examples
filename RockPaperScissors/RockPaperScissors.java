package RockPaperScissors;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GifMaker;
import HAL.Gui.GridWindow;
import HAL.Gui.UIGrid;
import HAL.Rand;
import HAL.Util;
//import UtilFunctions;

//cells grow and mutate
class RPSCell extends AgentSQ2Dunstackable<RockPaperScissors>{

    int type;
    int future_type;

    void Init(int t) {
        this.type = t;
    }

    void SetFutureType(){

        int nNeighbors = MapHood(G.hood);
        int nCompetitors = 0;

        this.future_type = this.type;
        int competitor_type = (this.type + 1) % G.NUM_TYPES; // this type is beaten by type + 1, mod 3.

        for (int i = 0; i < nNeighbors; i++) {
            RPSCell c = G.GetAgent(G.hood[i]);
            if (c.type == competitor_type ) {
                nCompetitors++;
            }
        }

        if (nCompetitors >= G.MIN_THRESHOLD_COMPETITORS) {
            if (G.rn.Double() < (1-G.ERROR_RATE)) {
                this.future_type = competitor_type;
            }
        }
    }
}

public class RockPaperScissors extends AgentGrid2D<RPSCell> {

    static final int DIMENSION = 100;
    static final int SCALE_FACTOR = 2;
    static final int PAUSE = 0; // set a "pause" between timesteps (milliseconds); 50 is recommended
    static final boolean SAVE_GIF = true;
    static final double ERROR_RATE = 0.2;


    // 2, 8 is cool
    // 3, 3 for spiral
    public int MIN_THRESHOLD_COMPETITORS = 3;
    public int NUM_TYPES = 3;
    public Rand rn = new Rand();

    int[] hood = Util.MooreHood(false);
    public GridWindow vis = new GridWindow(DIMENSION,DIMENSION,SCALE_FACTOR);//used for visualization;
    public GifMaker gifMaker = (SAVE_GIF) ? new GifMaker("RockPaperScissors/rps_spiral_test.gif",50,true) : null;//used for visualization;



    public RockPaperScissors(boolean spiral) {
        super(DIMENSION, DIMENSION, RPSCell.class,!spiral,!spiral);

        if (spiral) {
            for (int x = 0; x < xDim; x++) {
                for (int y = 0; y < yDim; y++) {
                    if (y > yDim / 2) {
                        NewAgentSQ(x, y).Init(0);
                    } else {
                        NewAgentSQ(x, y).Init(1);
                    }

                    if (x < xDim / 2) {
                        if (x < y) {
                            if (x < yDim - y) {
                                RPSCell c = GetAgent(x, y);
                                c.type = 2;
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = 0; x < xDim; x++) {
                for (int y = 0; y < yDim; y++) {
                    NewAgentSQ(x, y).Init(rn.Int(NUM_TYPES));
                }
            }
        }
    }

    public void StepCells(){
        for (RPSCell c : this) { c.SetFutureType(); }
        for (RPSCell c : this) { c.type = c.future_type; }
        Draw();
        if (SAVE_GIF) { gifMaker.AddFrame(vis); }
    }

    public static void main(String[]args){

        RockPaperScissors grid=new RockPaperScissors(true);

        grid.Draw();

        grid.StepCells();
        for (int tick = 0; tick < 100; tick++) {
            System.out.println(tick);
            grid.StepCells();
            grid.Draw();
        }

        if (grid.SAVE_GIF) { grid.gifMaker.Close(); }
        grid.vis.Close();
    }

    public void Draw() {
        for (int i = 0; i < vis.length; i++) {
            RPSCell c = this.GetAgent(i);
            if (c!=null) {
                this.vis.SetPix(i,Util.CategorialColor(c.type));
            }
        }
        this.vis.TickPause(PAUSE);
    }
}