package Examples.MultiGrid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.*;
import HAL.Rand;
import HAL.Util;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;



class MainAgent extends AgentSQ2Dunstackable<Main> {
    public void Move() {
        if (G.rn.Double() < 1) {
            int n = G.MapEmptyHood(G.hood, Isq());
            if (n > 0) {
                G.NewAgentSQ(G.hood[G.rn.Int(n)]);
                this.Dispose();
            }
        }
    }

    public void CheckDeath() {
        if (G.tcellsgrid.GetAgent(this.Isq()) != null ) {
            this.Dispose();
        }
    }
}

public class Main extends AgentGrid2D<MainAgent> {

    public static final int GRID_SIZE = 100;
    Rand rn = new Rand();

    TCells tcellsgrid;

    int[] hood = Util.MooreHood(false);


    public void Step() {
        for (MainAgent mainAgent: this) {
            mainAgent.CheckDeath();
        }
        for (MainAgent mainAgent: this) {
            mainAgent.Move();
        }
        tcellsgrid.Step();
    }

    // constructor
    public Main() {
        super(GRID_SIZE,GRID_SIZE,MainAgent.class);

        // 10 random agents
        for(int y = 0; y < 10; ++y) {
            int i = rn.Int(100*100);
            if (GetAgent(i) == null) {
                NewAgentSQ(i);
            }
        }
        tcellsgrid = new TCells(this);
    }

    public void Draw(UIGrid maingrid, UIGrid tcellsUIgrid) {
        for (int i = 0; i < maingrid.length; i++) {
            if (GetAgent(i) != null ){
                maingrid.SetPix(i, Util.BLACK);
            } else {
                maingrid.SetPix(i, Util.WHITE);
            }
        }

        for (int i = 0; i < tcellsUIgrid.length; i++) {
            if (tcellsgrid.GetAgent(i) != null ){

                if (tcellsgrid.GetAgent(i).activated) {
                    tcellsUIgrid.SetPix(i, Util.RED);
                } else {
                    tcellsUIgrid.SetPix(i, Util.YELLOW);
                }


            } else {
                tcellsUIgrid.SetPix(i, Util.WHITE);
            }
        }
    }

    public static void main(String[] args){
        // start window
        int scale_factor = 4;
        Main maingrid = new Main();

        UIWindow win = new UIWindow(true);
        UIGrid mainvis = new UIGrid(GRID_SIZE,GRID_SIZE,scale_factor);
        UIGrid tcellsvis = new UIGrid(GRID_SIZE,GRID_SIZE,scale_factor);
        win.AddCol(0,mainvis);
        win.AddCol(1,tcellsvis);

        win.RunGui();

        TickTimer tt = new TickTimer();

        for (int i = 0; i < 200; i++) {
            maingrid.Step();
            maingrid.Draw(mainvis,tcellsvis);
            System.out.println("Pop: " + maingrid.Pop());
            tt.TickPause(100);
        }
    }
}