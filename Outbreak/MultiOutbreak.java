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
        return (c.quarantined) ? Util.BLUE :  OutbreakWorld.ReturnColor(c.type);
    }
    public static void main(String[] args){


        int rows = 1;
        int columns = 10;

        OutbreakWorld[] models=new OutbreakWorld[rows*columns];


        // set up all models
        for (int i = 0; i < models.length; i++) {
            models[i] = new OutbreakWorld();
            models[i].QUARANTINE_RATE_SYMPTOMATIC = ((double)i/10.0);
            models[i].QUARANTINE_RATE_ASYMPTOMATIC = ((double)i/10.0);
            System.out.println(((double)i/10));
        }

        int scale_factor = 1;
        MultiWellExperiment<OutbreakWorld> expt=new MultiWellExperiment<OutbreakWorld>(columns,rows,models,models[0].DIMENSION,models[0].DIMENSION, scale_factor, WHITE, MultiOutbreak::StepModel, MultiOutbreak::DrawModel);
        expt.RunGIF(200,"./Outbreak/test.gif",2,false);
    }
}




