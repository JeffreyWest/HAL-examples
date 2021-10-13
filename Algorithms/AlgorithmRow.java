package Examples.Algorithms;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.Gui.GifMaker;
import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;
import HAL.Rand;
import HAL.Util;


public class AlgorithmRow extends AgentGrid2D<Unit> {
    public AlgorithmRow(Rand rn, int dimension, UIGrid vis) {
        super(dimension, 1, Unit.class,false,false);

        for (int i = 0; i < dimension; i++) {
            this.NewAgentSQ(i).Init(i);
        }

        // shuffle and draw
        this.ShuffleAgents(rn);

        int i = 0;
        for (Unit u : this) {
            u.number = i;
            i++;
        }
    }

    public void Draw(UIGrid vis, int row) {
        // draw each person's state, override if they're quarantined
        for (Unit u : this) {
            vis.SetPix(u.Isq(),row,ReturnColor(u.number));
        }
    }

    public int ReturnColor(int number) {
        double r = Util.Bound((double)number/this.xDim,0,this.xDim-1);
        return Util.HSBColor(0,r,1);
    }

    public void BubbleSort(int r) {
        for (int i = 0; i < (this.xDim - 1 - r); i++) {
            if(GetAgent(i).number > GetAgent(i+1).number) {
                int tmp = GetAgent(i).number;
                GetAgent(i).number = GetAgent(i+1).number;
                GetAgent(i+1).number = tmp;
            }
        }
    }


    public boolean OddEven(int i) {
        boolean isSorted = true;

        // j = 0 is an even-indexed element
        // j = 1 is an odd-indexed element
        // (check odd first)
        for (int j = 1; j >= 0; j--) {
            if (i+j<=this.xDim-2) {
                if (GetAgent(i+j).number > GetAgent(i+j + 1).number) {
                    int tmp = GetAgent(i+j).number;
                    GetAgent(i+j).number = GetAgent(i+j + 1).number;
                    GetAgent(i+j + 1).number = tmp;
                    isSorted = false;
                }
            }
        }

        return isSorted;
    }

}