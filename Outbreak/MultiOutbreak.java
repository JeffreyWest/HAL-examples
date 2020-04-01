package Outbreak;
import HAL.Gui.UIWindow;
import HAL.Tools.MultiWellExperiment.MultiWellExperiment;
import HAL.Util;

import static HAL.Util.*;

public class MultiOutbreak {

    // used for "StepFn" argument of MultiWellExperiment
    //  - arguments: model, well index
    //  - update the model argument for one timestep.
    public static void StepModel(OutbreakWorld model,int iWell){
        model.StepCells();
    }

    // used for "ColorFn" argument of MultiWellExperiment
    //  - arguments: model, x, and y
    //  - used to set one pixel of the visualization.
    public static int DrawModel(OutbreakWorld model,int x,int y){
        Person c = model.GetAgent(x,y);
//        return (c.quarantined) ? Util.BLUE :  OutbreakWorld.ReturnColor(c.type);

//        return Util.RGB256((int)(model.QUARANTINE_RATE_ASYMPTOMATIC*255),0,0);

        return OutbreakWorld.ReturnColor(c.type);
    }
    public static void main(String[] args){


        int rows = 10;
        int columns = 10;

        OutbreakWorld[] models=new OutbreakWorld[rows*columns];

        // set up all models
        int i = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                models[i] = new OutbreakWorld();
                models[i].QUARANTINE_RATE_SYMPTOMATIC = ((double) row / 20.0);
                models[i].QUARANTINE_RATE_ASYMPTOMATIC = ((double) row / 20.0);
                i++;
            }
        }

        int scale_factor = 1;
        MultiWellExperiment<OutbreakWorld> expt=new MultiWellExperiment<OutbreakWorld>(rows,columns,models,models[0].DIMENSION,models[0].DIMENSION, 1, WHITE, MultiOutbreak::StepModel, MultiOutbreak::DrawModel);

        int TOTAL_TIME = 100;
        expt.RunGIF(TOTAL_TIME,"./Outbreak/test.gif",10,false);
//        expt.RunGIF(TOTAL_TIME,"./Outbreak/test.gif",1,false);

    }
}




