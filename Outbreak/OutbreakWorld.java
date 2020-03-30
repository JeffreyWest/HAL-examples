package Outbreak;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GifMaker;
import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;
import HAL.Rand;
import HAL.Util;

//cells grow and mutate
class Person extends AgentSQ2Dunstackable<OutbreakWorld>{

    public final static int SUSCEPTIBLE = 0;
    public final static int ASYMPTOMATIC_INFECTED = 3;
    public final static int SYMPTOMATIC_INFECTED = 4;
    public final static int RECOVERED = 2;
    public final static int DEAD = 1;

    // cell attributes: type; future_type; fitness
    int type;
    int future_type;
    boolean quarantined;

    // count-down timers until next state:
    int symptomatic_counter; // count until go from A -> S
    int recovered_counter; // count until go from S -> R


    public void Init(int type) {
        this.type = type;
        this.future_type = type;
        this.symptomatic_counter = 0;
        this.recovered_counter = 0;
        this.quarantined = false;
    }

    void IncrementCounters() {
        if (this.type == ASYMPTOMATIC_INFECTED) {
            symptomatic_counter++;
            if (symptomatic_counter >= G.TIME_UNTIL_SYMPTOMATIC) {
                this.future_type = SYMPTOMATIC_INFECTED;
            }
        }
        if (this.type == SYMPTOMATIC_INFECTED) {
            recovered_counter++;
            if (recovered_counter >= G.TIME_UNTIL_RECOVERED) {
                this.future_type = RECOVERED;
            }
        }
    }

    // assumes you are infected, checks neighborhood to see if you infect someone else
    void InfectOthers() {
        double infection_rate = G.TRANSMISSION;
        int nNeighbors = this.MapOccupiedHood(G.hood);

        for (int i = 0; i < G.ENCOUNTERS_PER_DAY; i++) {
            Person thisNeighbor = G.GetAgent(G.hood[G.rn.Int(nNeighbors)]); // random neighbor in hood
            if (thisNeighbor.type == SUSCEPTIBLE) {
                if ((G.rn.Double() < infection_rate)) {
                    thisNeighbor.future_type = Person.ASYMPTOMATIC_INFECTED;
                }
            }
        }
    }
}

public class OutbreakWorld extends AgentGrid2D<Person> {

    // infection parameters
    public double TRANSMISSION = Math.pow(0.45,3);
    public int ENCOUNTERS_PER_DAY = 4;
    public int TRAVEL_RADIUS = 15;

    public double QUARANTINE_RATE_SYMPTOMATIC = 0.9;
    public double QUARANTINE_RATE_ASYMPTOMATIC = 0.0;
    public double FATALITY_RATE = 0.01;

    public double HOSPITAL_CAPACITY = 0.05;
    public double FATALITY_MULTIPLIER_DUE_TO_EXCEEDING_CAPACITY = 2.0;

    public int TIME_UNTIL_SYMPTOMATIC = 7;
    public int TIME_UNTIL_RECOVERED = 8;

    static final int TOTAL_TIME = 100;
    static final int DIMENSION = 300;
    static final int SCALE_FACTOR = 1;
    static final int PAUSE = 10; // set a "pause" between timesteps (milliseconds)
    static final boolean SAVE_GIF = true;
    static final int GIF_DRAW_MODIFIER = 1;
    
    // chart of sizes
    public int CHART_TIME_SCALE; // do not change
    public boolean ABOVE_CAPACITY = false;


    public UIGrid vis;
    public Rand rn = new Rand();

    public int[] hood = Util.RectangleHood(false,TRAVEL_RADIUS,TRAVEL_RADIUS);

    public UIWindow win = new UIWindow("Outbreak",true,null,true);


    public GifMaker gifMaker = (SAVE_GIF) ? new GifMaker("Outbreak/outbreak_world.gif",100*GIF_DRAW_MODIFIER,true) : null;//used for visualization;

    /*
        OutbreakWorld CONSTRUCTOR
    */

    public OutbreakWorld(boolean include_chart) {
        super(DIMENSION, DIMENSION, Person.class,false,false);

        SetupVisualization(include_chart);

        // set the full domain to "SUSCEPTIBLE" type
        for (int i = 0; i < length; i++) { NewAgentSQ(i).Init(Person.SUSCEPTIBLE); }

        // set the center to be single person asymptomatic
        GetAgent((this.xDim-1)/2,(this.xDim-1)/2).Init(Person.ASYMPTOMATIC_INFECTED);
    }

    public void SetupVisualization(boolean include_chart) {
        CHART_TIME_SCALE = (DIMENSION*3) > TOTAL_TIME ? Math.round((DIMENSION*3)/TOTAL_TIME) : -Math.round(TOTAL_TIME/(DIMENSION*3));

        if (CHART_TIME_SCALE > 0) {
            vis = new UIGrid(DIMENSION+Math.round(TOTAL_TIME*CHART_TIME_SCALE),DIMENSION,SCALE_FACTOR);
        } else {
            vis = new UIGrid(DIMENSION+Math.round(TOTAL_TIME/Math.abs(CHART_TIME_SCALE)),DIMENSION,SCALE_FACTOR);
        }

        win.AddCol(0,vis);
        win.RunGui();
    }

    /*
        StepCells()
            - loop through each cell at every time step
            -
    */

    public void StepCells(int time){

        // check how many infected:
        double infected_percent = 0.0;
        for (Person c : this) {
            if ((c.type == Person.ASYMPTOMATIC_INFECTED) || (c.type == Person.SYMPTOMATIC_INFECTED)) {
                infected_percent += 1.0;
            }
        }
        infected_percent /= (double)(DIMENSION*DIMENSION);


        // multiple fatality rate, if above hospital capacity
        double effective_fatality_rate = FATALITY_RATE;
        ABOVE_CAPACITY = false;
        if (infected_percent > HOSPITAL_CAPACITY) {
            effective_fatality_rate = FATALITY_RATE*FATALITY_MULTIPLIER_DUE_TO_EXCEEDING_CAPACITY;
            ABOVE_CAPACITY = true;
        }

        for (Person c : this) {
            if (c.type == Person.SUSCEPTIBLE) {
                // nothing yet
            }

            if ((c.type == Person.ASYMPTOMATIC_INFECTED)) {
                c.InfectOthers();
                if (rn.Double() < QUARANTINE_RATE_ASYMPTOMATIC) {
                    c.quarantined = true;
                }
                if (rn.Double() < effective_fatality_rate) {
                    c.future_type = Person.DEAD;
                }
            }

            if (c.type == Person.SYMPTOMATIC_INFECTED) {
                c.InfectOthers();
                if (rn.Double() < QUARANTINE_RATE_SYMPTOMATIC) {
                    c.quarantined = true;
                }
                if (rn.Double() < effective_fatality_rate) {
                    c.future_type = Person.DEAD;
                }
            }

            if (c.type == Person.RECOVERED) {
                c.quarantined = false;
            }

            c.IncrementCounters();
        }



        for (Person c : this) { c.type = c.future_type; }
    }

    /*
        OutbreakWorld() model
            -
    */

    public static void main(String[]args){

        OutbreakWorld grid=new OutbreakWorld(true);

        for (int tick = 0; tick < TOTAL_TIME; tick++) {

            grid.Draw(tick);


            if ((SAVE_GIF) && (tick % GIF_DRAW_MODIFIER == 0)) {
                grid.gifMaker.AddFrame(grid.vis);
            }

            System.out.println("Time:  " + tick);
            grid.StepCells(tick+1);
        }

        if (grid.SAVE_GIF) { grid.gifMaker.Close(); }
    }

    public void Draw(int time) {
        int[] counts = new int[] {0,0,0,0,0};

        for (int i = 0; i < xDim*yDim; i++) {
            Person c = this.GetAgent(i);
            if (c!=null) {
                counts[c.type]++;
                this.vis.SetPix(i,(c.quarantined) ? Util.BLUE :  ReturnColor(c.type));
            }
        }

        int pixels[] = new int[counts.length];
        for (int type = 0; type < counts.length; type++) {
            pixels[type] = (int) Math.round(DIMENSION * (double) counts[type] / (double) (DIMENSION * DIMENSION));
        }

        // smooth out rounding errors
        int check_sum = 0;
        for (int i = 0; i < pixels.length; i++) { check_sum+= pixels[i]; }

        if (pixels[Person.SUSCEPTIBLE] > 0) {
            pixels[Person.SUSCEPTIBLE] += DIMENSION - check_sum;
        } else {
            pixels[Person.RECOVERED] += DIMENSION - check_sum;
        }

        // colors over time:
        if (time > 0) {
            int bottom_ticker = 0;
            for (int type = 0; type < counts.length; type++) {
                int percent = pixels[type];
                for (int percent_point = 0; percent_point < percent; percent_point++) {
                    if ((time < TOTAL_TIME) && (bottom_ticker + percent_point < DIMENSION)) {
                        int test = DIMENSION - 1 - (bottom_ticker + percent_point);

                        if (CHART_TIME_SCALE > 0) {
                            for (int i = 0; i < CHART_TIME_SCALE; i++) {
                                this.vis.SetPix(time * CHART_TIME_SCALE + i + xDim, test, ReturnColor(type));
                            }
                        } else {
                            this.vis.SetPix(time / Math.abs(CHART_TIME_SCALE) + xDim, test, ReturnColor(type));
                        }
                    }
                }
                bottom_ticker += percent;
            }
        }

        // indicate if above hospital capacity
        if (ABOVE_CAPACITY) {
            // assumes square:
            for (int i = 0; i < xDim; i++) {
                vis.SetPix(0,i,Util.GREEN);
                vis.SetPix(xDim-1,i,Util.GREEN);
                vis.SetPix(i,0,Util.GREEN);
                vis.SetPix(i,xDim-1,Util.GREEN);
            }
        }


        this.vis.TickPause(PAUSE);
    }


    int ReturnColor(int type) {
        if ((type == Person.SUSCEPTIBLE)) {
            return Util.RGB256(100,100,100);
        } else if ((type == Person.ASYMPTOMATIC_INFECTED)) {
            return Util.RGB256(255, 221, 226);
        } else if ((type == Person.SYMPTOMATIC_INFECTED)) {
            return Util.RGB256(255, 68, 68);
        } else if ((type == Person.RECOVERED)) {
            return Util.RGB256(153, 153, 153);
        } else if ((type == Person.DEAD)) {
            return Util.BLACK;
        }
        return Util.BLACK;
    }
}