package Examples.MultiGrid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.*;
import HAL.Rand;
import HAL.Util;


class TCell extends AgentSQ2Dunstackable<TCells> {

    boolean activated = false;

    public void Move() {
        if (G.rn.Double() < 1) {
            int n = G.MapEmptyHood(G.hood, Isq());
            if (n > 0) {
                G.NewAgentSQ(G.hood[G.rn.Int(n)]);
                this.Dispose();
            }
        }
    }

    public void CheckActive() {
        if (G.maingrid.GetAgent(this.Isq()) != null ) {
            this.activated = true;
        }
    }
}

public class TCells extends AgentGrid2D<TCell> {

    public static final int GRID_SIZE = 100;
    Main maingrid;

    Rand rn = new Rand();
    int[] hood = Util.MooreHood(false);
    public void Step() {
        for (TCell tc: this) {
            tc.CheckActive();
        }
        for (TCell tc: this) {
            tc.Move();
        }
    }

    // constructor
    public TCells(Main maingrid) {
        super(GRID_SIZE,GRID_SIZE,TCell.class);

        for(int y = 0; y < 800; y++) {
            int i = rn.Int(100*100);
            if (GetAgent(i) == null) {
                NewAgentSQ(i);
            }
        }
        this.maingrid = maingrid;
    }
}