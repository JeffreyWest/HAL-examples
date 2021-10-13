package Artwork;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.*;
import HAL.Interfaces.DoubleToColor;
import HAL.Tools.FileIO;
import HAL.Rand;
import HAL.Util;

import static HAL.Util.*;

import java.io.File;
import java.util.ArrayList;



class Ant extends AgentSQ2Dunstackable<AntsModel> {

    public void Move() {
        // check all empty spaces, then move toward the square w/ highest concentration.
        int nDivOptions = G.MapEmptyHood(G.neighborhood,Xsq(),Ysq());

        if (G.FOLLOW_DIFFUSION) {
            double max_val = -100.0;

            for (int i = 0; i < nDivOptions; i++) {
                double val = G.diffusr.Get(G.neighborhood[i]);
                if (val > max_val) {
                    G.direction_gradient.clear();
                    G.direction_gradient.add(G.neighborhood[i]);
                    max_val = val;
                } else if (val == max_val)  {
                    // don't clear, but add an equivalent one.
                    G.direction_gradient.add(G.neighborhood[i]);
                }
            }

            if (G.direction_gradient.size()>0) {
                int index = G.direction_gradient.get(G.rn.Int(G.direction_gradient.size()));
                G.NewAgentSQ(index);
                this.Dispose();
            }
        } else {
            if(nDivOptions!=0){
                int nextAgentID = G.neighborhood[G.rn.Int(nDivOptions)];
                G.NewAgentSQ(nextAgentID);
                this.Dispose();
            }
        }


    }

    void Step(){
        this.Move();
        G.diffusr.Set(this.Isq(),G.diffusr_val);
    }
}

public class AntsModel extends AgentGrid2D<Ant> {


    public ArrayList<Integer> direction_gradient = new ArrayList<>();
    public boolean FOLLOW_DIFFUSION = false;

    public PDEGrid2D diffusr;

    public double diffusr_val = 1.0;
    public double D_DECAY = 0.01;
    public double DIFFUSION_RATE = 0.1;

    // neighborhoods
    int[]neighborhood=VonNeumannHood(true);
    Rand rn=new Rand(1);

    // constructor from parameters
    AntsModel(int sideLenX, int sideLenY, int ants){
        super(sideLenX,sideLenY, Ant.class,true,true);

        int a = 0;
        while (a < ants) {

            int i = rn.Int(length);
            Ant ant_i = GetAgent(i);

            if (ant_i == null) {
                NewAgentSQ(i);
                a++;
            }
        }

        this.diffusr = new PDEGrid2D(sideLenX,sideLenY);
        this.diffusr.Update();
    }


    void OriginalStep(){
        for (Ant c:this) {
            c.Step();
        }
        CleanShuffle(rn);

        // decay:
        for (int i = 0; i < length; i++) {
            diffusr.Add(i,-D_DECAY*diffusr.Get(i));
        }


        // diffusion:
        diffusr.DiffusionADI(DIFFUSION_RATE,0); // D, boundary
        this.diffusr.Update();
    }



    public static void main(String[] args) {

        int xDim = 100;
        int yDim = xDim;
        int ants = 200;
        int sf = 5;
        AntsModel model = new AntsModel(xDim,yDim, ants);
        UIGrid Vis = new UIGrid(model.xDim, model.yDim, sf);
        UIGrid PDEVis = new UIGrid(model.xDim, model.yDim, sf);
        UIWindow win = CreateWindow(false, Vis, PDEVis);

        // new gif
//        String outputFilename = foldername + "everything.csv";
//        String gifFilename = foldername + "sim.gif";
//        GifMaker myGif = (sim < save_max) ? new GifMaker(gifFilename, 100,true) : null;

        TickTimer tt = new TickTimer();
        int tic_pause = 100;

        int totalTime = 100;
        DrawCells(model, Vis, PDEVis);
        tt.TickPause(tic_pause);

        for (int i = 0; i <= totalTime; i++) {

            if (i>10) {
                model.FOLLOW_DIFFUSION=true;
            } else {
                model.FOLLOW_DIFFUSION=false;
            }

            model.OriginalStep();
            DrawCells(model, Vis, PDEVis);

            tt.TickPause(tic_pause);
        }

        win.Close();


        return;
    }

    public static void DrawCells(AntsModel model, UIGrid visCells, UIGrid PDEvis) {
        for (int i = 0; i < visCells.length; i++) {
            Ant c=model.GetAgent(i);
            if(c==null){
                visCells.SetPix(i, RGB(1,1,1));
            } else{
                visCells.SetPix(i, RGB(0,0,0));
            }
        }

        // draw PDE grid.

        DoubleToColor colormap = (v) -> Util.HeatMapRGB(Util.Scale0to1(v, 0, 1));
        DrawDif(PDEvis, model.diffusr, colormap);

//        AddBand(ALL_GRIDS.get(INFg), colormap);
//        AddAverageMarker(ALL_GRIDS.get(INFg), tissue.AVERAGES[Tissue.INFG]);
//        AddDays(ALL_GRIDS.get(INFg));


    }

    public static void DrawDif(UIGrid vis, PDEGrid2D dif, DoubleToColor Color){
        for (int i = 0; i < dif.length; i++) {
            vis.SetPix(i,Color.GenColor(dif.Get(i)));
        }
    }


    /*

        GetAttributes()
            - used for phylogeny tracker, to output clone-specific attributes

     */

//    public static String GetAttributes(Clone c) {
//        return Integer.toString(c.kd) + "," + Integer.toString(c.kp) + "," + c.Hex();
//    }



    public static UIWindow CreateWindow(boolean headless, UIGrid Vis, UIGrid PDEVis) {
        UIWindow win = (headless) ? null : new UIWindow("Window", false, null, true);

        if (!headless) {
            win.AddCol(0, Vis);
            win.AddCol(1, PDEVis);
            win.RunGui();
        }
        return win;
    }

}

