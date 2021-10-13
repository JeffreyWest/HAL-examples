package Artwork;

import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;
import HAL.Util;

import java.io.File;

public class Countdown {

    public static void main(String[] args) {




        System.out.println("test");

        UIWindow win = new UIWindow("Window", true);
        UIGrid Vis = new UIGrid(20,20,10);
        win.AddCol(0, Vis);
        win.RunGui();

        for (int i = 10; i >= 0; i--) {
//            Vis.SetString(Integer.toString(i),10,10, Util.RED,Util.BLACK);
            Vis.SetString(Integer.toString(i), false, Util.WHITE, Util.BLACK, 2);

            for (int j = 0; j < Vis.length; j++) {
                Vis.SetPix(i,Util.BLACK);
            }



            Vis.TickPause(1000);
        }






        return;
    }



}


