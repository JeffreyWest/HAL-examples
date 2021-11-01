package Examples.DiscreteTuringPattern;
import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GifMaker;
import HAL.Gui.GridWindow;
import HAL.Rand;
import HAL.Util;

import static HAL.Util.CircleHood;

public class DiscreteTuringPattern extends AgentGrid2D<TuringCell> {

    public static final int DOMAIN_SIZE = 200;
    public GridWindow vis;


    public static final int Sar = 3; // activation neighborhood radius
    public static final int Sir = 6; // inactivation neighborhood radius

    // inhibition & activation neighborhoods:
    public int[] activation_neighborhood = CircleHood(false,Sar);
    public int[] inhibition_neighborhood = CircleHood(false,Sir);

    public static final double w1 = 1.0;
    public static final double w2 = -0.3;
    double initial_seeding_fraction = 0.1;

    public static void main(String[]args){

        DiscreteTuringPattern grid=new DiscreteTuringPattern();
        grid.Draw();

        GifMaker gif = new GifMaker("Examples/DiscreteTuringPattern/"+
                Integer.toString(Sar) + "_" +
                Integer.toString(Sir) + "_" +
                Integer.toString((int)(-w2*100)) + "_"
                +"gif.gif",100,true);
        gif.AddFrame(grid.vis);

        int totalTime = 25;

        for (int tick = 2; tick <= totalTime; tick++) {
            grid.vis.TickPause(0); // pause for n milliseconds
            grid.StepCells();
            grid.Draw();
            gif.AddFrame(grid.vis);
        }
        gif.Close();
        grid.vis.Close();
    }

    // constructor for the grid
    public DiscreteTuringPattern() {
        super(DOMAIN_SIZE, DOMAIN_SIZE, TuringCell.class,false,false);

        Rand rn = new Rand();

        for (int i = 0; i < (DOMAIN_SIZE*DOMAIN_SIZE); i++) {
            if (rn.Double() < initial_seeding_fraction) {
                this.NewAgentSQ(i);
            }
        }

        //used for visualization
        int scale_factor = (xDim > 300) ? 1 : 3;
        vis=new GridWindow(xDim,yDim,scale_factor);
    }


    // step through all cells in a given row, determine if they're dead/alive
    public void StepCells(){
        for (int i = 0; i < length; i++) {
            DetermineType(i);
        }
    }

    public void Draw() {
        for (int i = 0; i < vis.length; i++) {
            TuringCell c = this.GetAgent(i);
            this.vis.SetPix(i, (c!=null) ? Util.BLACK : Util.WHITE);
        }
    }

    public void DetermineType(int i) {

        int S1 = MapOccupiedHood(activation_neighborhood, i);
        int S2 = MapOccupiedHood(inhibition_neighborhood, i);

        double v1 = (S1 * w1);
        double v2 = (S2 * w2);

        if ( (v1+v2)  > 0) {
            // only create if empty
            if ( GetAgent(i)==null ) {
                NewAgentSQ(i);
            }
        } else if ( (v1+v2)  < 0) {
            // only destroy if present
            if ( GetAgent(i)!=null ) {
                GetAgent(i).Dispose();
            }
        } else {
            // same as previous time step
        }
    }
}

class TuringCell extends AgentSQ2Dunstackable<DiscreteTuringPattern>{


}