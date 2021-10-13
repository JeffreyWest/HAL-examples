package Artwork;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.*;
import HAL.Interfaces.DoubleToColor;
import HAL.Rand;
import HAL.Util;

import static HAL.Util.*;
import java.util.ArrayList;



class Crystal extends AgentSQ2Dunstackable<Snowflake> {

    boolean isStatic;

    public Crystal Init(boolean thisStatic) {
        this.isStatic=thisStatic;
        return this;
    }

    public void Move() {
        // check all empty spaces, then move toward the square w/ highest concentration.
        int nDivOptions = G.MapEmptyHood(G.neighborhood,Xsq(),Ysq());

        if(nDivOptions!=0){
            int nextAgentID = G.neighborhood[G.rn.Int(nDivOptions)];
            G.NewAgentSQ(nextAgentID).Init(this.isStatic);
            this.Dispose();
        }

    }

    public boolean CheckTouching() {
        int nDivOptions = G.MapHood(G.neighborhood,Xsq(),Ysq());

        for (int i = 0; i < nDivOptions; i++) {
            Crystal c = G.GetAgent(G.neighborhood[i]);

            if (c!=null) {
                if (c.isStatic == true) {
                    this.isStatic = true;
                    return true;
                }
            }

        }

        return false;
    }

    void Step(){
        this.CheckTouching();

        if (!this.isStatic) {
            this.Move();
        }
    }
}

public class Snowflake extends AgentGrid2D<Crystal> {

    // neighborhoods
    int[]neighborhood=RectangleHood(true,2,2);
    Rand rn=new Rand(1);

    // constructor from parameters
    Snowflake(int sideLenX, int sideLenY, int crystals){
        super(sideLenX,sideLenY, Crystal.class,false,false);

        NewAgentSQ(sideLenX/2,sideLenY/2).Init(true);

        int c= 0;
        while (c < crystals) {

            int i = rn.Int(length);
            Crystal crystal_i = GetAgent(i);

            if (crystal_i == null) {
                NewAgentSQ(i).Init(false);
                c++;
            }
        }
    }
    void OriginalStep(){

        int i = 0;
        for (Crystal c:this) {
            if (i<length/4) {
                c.Step();
            }
            i++;
        }
        CleanShuffle(rn);
    }

    public static void main(String[] args) {

        int xDim = 250;
        int yDim = xDim;
        int ants = xDim*yDim / 10;
        int sf = 3;
        Snowflake model = new Snowflake(xDim,yDim, ants);
        UIGrid Vis = new UIGrid(model.xDim, model.yDim, sf);
        UIWindow win = CreateWindow(false, Vis);

        // new gif
//        String outputFilename = foldername + "everything.csv";
        String gifFilename = "snowflake.gif";
        GifMaker myGif = new GifMaker(gifFilename, 100,true);

        TickTimer tt = new TickTimer();
        int tic_pause = 10;

        int totalTime = 500;
        DrawCells(model, Vis);
        tt.TickPause(tic_pause);

        for (int i = 0; i <= totalTime; i++) {
            model.OriginalStep();
            DrawCells(model, Vis);
            myGif.AddFrame(Vis);

            tt.TickPause(tic_pause);
        }

        myGif.Close();
        win.Close();


        return;
    }

    public static void DrawCells(Snowflake model, UIGrid visCells) {
        for (int i = 0; i < visCells.length; i++) {
            Crystal c=model.GetAgent(i);
            if(c==null){
                visCells.SetPix(i, RGB(0,0,0));
            } else if (c.isStatic == true){
                visCells.SetPix(i, RGB(1,1,1));
            } else {
                visCells.SetPix(i, RGB(0.22,0.45,1));
            }
        }
    }

    public static UIWindow CreateWindow(boolean headless, UIGrid Vis) {
        UIWindow win = (headless) ? null : new UIWindow("Window", false, null, true);

        if (!headless) {
            win.AddCol(0, Vis);
            win.RunGui();
        }
        return win;
    }

}

