package Examples.ReadPNG;

import HAL.GridsAndAgents.*;
import HAL.Gui.OpenGL3DWindow;
import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;
import HAL.Rand;
import HAL.Util;


public class ReadPNG {

    public static void main(String[] args) {

        try {
            Grid2Dint g = Util.PNGtoGrid("Examples/ReadPNG/chalkboard.png");

            UIWindow win = new UIWindow("window", true);
            UIGrid grid = new UIGrid(g.xDim, g.yDim, 1, true);

            // add to window
            win.AddCol(0, grid);
            win.RunGui();

            Draw(grid,g);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public static void Draw(UIGrid grid, Grid2Dint g) {
        for (int i = 0; i < grid.length; i++) {
            grid.SetPix(i,g.Get(i));
        }
    }
}
