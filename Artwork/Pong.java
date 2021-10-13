//package Artwork;
//
//import HAL.GridsAndAgents.AgentGrid2D;
//import HAL.GridsAndAgents.AgentSQ2Dunstackable;
//import HAL.GridsAndAgents.PDEGrid2D;
//import HAL.Gui.*;
//import HAL.Interfaces.DoubleToColor;
//import HAL.Tools.FileIO;
//import HAL.Rand;
//import HAL.Util;
//
//import static HAL.Util.*;
//
//import java.io.File;
//import java.util.ArrayList;
//
//
//
//class Dot extends AgentSQ2Dunstackable<Pong> {
//
//    public void Move() {
//        // check all empty spaces, then move toward the square w/ highest concentration.
//        int nDivOptions = G.MapEmptyHood(G.neighborhood,Xsq(),Ysq());
//
//        if (G.FOLLOW_DIFFUSION) {
//            double max_val = -100.0;
//
//            for (int i = 0; i < nDivOptions; i++) {
//                double val = G.diffusr.Get(G.neighborhood[i]);
//                if (val > max_val) {
//                    G.direction_gradient.clear();
//                    G.direction_gradient.add(G.neighborhood[i]);
//                    max_val = val;
//                } else if (val == max_val)  {
//                    // don't clear, but add an equivalent one.
//                    G.direction_gradient.add(G.neighborhood[i]);
//                }
//            }
//
//            if (G.direction_gradient.size()>0) {
//                int index = G.direction_gradient.get(G.rn.Int(G.direction_gradient.size()));
//                G.NewAgentSQ(index);
//                this.Dispose();
//            }
//        } else {
//            if(nDivOptions!=0){
//                int nextAgentID = G.neighborhood[G.rn.Int(nDivOptions)];
//                G.NewAgentSQ(nextAgentID);
//                this.Dispose();
//            }
//        }
//
//
//    }
//
//    void Step(){
//        this.Move();
//        G.diffusr.Set(this.Isq(),G.diffusr_val);
//    }
//}
//
//public class Pong extends AgentGrid2D<Dot> {
//
//
//    public ArrayList<Integer> direction_gradient = new ArrayList<>();
//    public boolean FOLLOW_DIFFUSION = false;
//
//    public PDEGrid2D diffusr;
//
//    public double diffusr_val = 1.0;
//    public double D_DECAY = 0.01;
//    public double DIFFUSION_RATE = 0.1;
//
//    // neighborhoods
//    int[]neighborhood=VonNeumannHood(true);
//    Rand rn=new Rand(1);
//
//    // constructor from parameters
//    Pong(int sideLenX, int sideLenY, int ants){
//        super(sideLenX,sideLenY, Dot.class,true,true);
//
//        int a = 0;
//        while (a < ants) {
//
//            int i = rn.Int(length);
//            Dot ant_i = GetAgent(i);
//
//            if (ant_i == null) {
//                NewAgentSQ(i);
//                a++;
//            }
//        }
//
//        this.diffusr = new PDEGrid2D(sideLenX,sideLenY);
//        this.diffusr.Update();
//    }
//
//
//    void OriginalStep(){
//        for (Dot c:this) {
//            c.Step();
//        }
//        CleanShuffle(rn);
//    }
//
//
//
//    public static void main(String[] args) {
//
//        int xDim = 100;
//        int yDim = xDim;
//        int ants = 200;
//        int sf = 5;
//        AntsModel model = new AntsModel(xDim,yDim, ants);
//        UIGrid Vis = new UIGrid(model.xDim, model.yDim, sf);
//        UIWindow win = CreateWindow(false, Vis);
//
//        // new gif
////        String outputFilename = foldername + "everything.csv";
////        String gifFilename = foldername + "sim.gif";
////        GifMaker myGif = (sim < save_max) ? new GifMaker(gifFilename, 100,true) : null;
//
//        TickTimer tt = new TickTimer();
//        int tic_pause = 100;
//
//        int totalTime = 100;
//        DrawCells(model, Vis);
//        tt.TickPause(tic_pause);
//
//        for (int i = 0; i <= totalTime; i++) {
//
//            if (i>10) {
//                model.FOLLOW_DIFFUSION=true;
//            } else {
//                model.FOLLOW_DIFFUSION=false;
//            }
//
//            model.OriginalStep();
//            DrawCells(model, Vis);
//
//            tt.TickPause(tic_pause);
//        }
//
//        win.Close();
//
//
//        return;
//    }
//
//    public static void DrawCells(AntsModel model, UIGrid visCells) {
//        for (int i = 0; i < visCells.length; i++) {
//            Ant c=model.GetAgent(i);
//            if(c==null){
//                visCells.SetPix(i, RGB(1,1,1));
//            } else{
//                visCells.SetPix(i, RGB(0,0,0));
//            }
//        }
//    }
//
//
//    public static UIWindow CreateWindow(boolean headless, UIGrid Vis, UIGrid PDEVis) {
//        UIWindow win = (headless) ? null : new UIWindow("Window", false, null, true);
//
//        if (!headless) {
//            win.AddCol(0, Vis);
//            win.AddCol(1, PDEVis);
//            win.RunGui();
//        }
//        return win;
//    }
//
//}
//
