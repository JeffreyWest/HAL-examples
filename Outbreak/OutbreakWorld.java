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
    public final static int DEAD = 1;
    public final static int RECOVERED = 2;
    public final static int ASYMPTOMATIC_INFECTED = 3;
    public final static int SYMPTOMATIC_INFECTED = 4;

    // cell attributes: type; future_type; fitness
    int type;
    int future_type;
    boolean quarantined;

    // count-down timers until next state:
    int day_counter;


    public void Init(int type) {
        this.type = type;
        this.future_type = type;
        this.quarantined = false;
        this.day_counter = 0;
        G.counts[type]++;
    }

    void IncrementCounters() {
        day_counter++;

        if ((this.type == ASYMPTOMATIC_INFECTED) && (day_counter >= G.TIME_UNTIL_SYMPTOMATIC)) {
            this.future_type = SYMPTOMATIC_INFECTED;
            day_counter = 0;
        }
        if ((this.type == SYMPTOMATIC_INFECTED) && (day_counter >= G.TIME_UNTIL_RECOVERED)) {
            this.future_type = RECOVERED;
            day_counter = 0;
        }
    }

    // assumes you are infected, checks neighborhood to see if you infect someone else
    void InfectOthers() {
        double infection_rate = G.TRANSMISSION;
        int nNeighbors = this.MapOccupiedHood(G.hood);

        for (int i = 0; i < G.ENCOUNTERS_PER_DAY; i++) {
            Person thisNeighbor = G.GetAgent(G.hood[G.rn.Int(nNeighbors)]); // random neighbor in hood
            if ((thisNeighbor.type == SUSCEPTIBLE) && (!thisNeighbor.quarantined)) { // safe if in quarantine
                if ((G.rn.Double() < infection_rate)) {
                    thisNeighbor.future_type = Person.ASYMPTOMATIC_INFECTED;
                }
            }
        }
    }
}

public class OutbreakWorld extends AgentGrid2D<Person> {

    // infection parameters
    public double TRANSMISSION = 0.05;
    public int ENCOUNTERS_PER_DAY = 8;
    public int TRAVEL_RADIUS = 25;

    public double QUARANTINE_RATE_SYMPTOMATIC = 0.0;
    public double QUARANTINE_RATE_ASYMPTOMATIC = 0.0;
    public double FATALITY_RATE = 0.01;

    public double HOSPITAL_CAPACITY = 0.005;
    public double FATALITY_MULTIPLIER_DUE_TO_EXCEEDING_CAPACITY = 2.0;

    public int TIME_UNTIL_SYMPTOMATIC = 7;
    public int TIME_UNTIL_RECOVERED = 8;

    static final int TOTAL_TIME = 50;
    static final int DIMENSION = 100; // was 300
    static final int SCALE_FACTOR = 2;
    static final int PAUSE = 10; // set a "pause" between timesteps (milliseconds)
    static final boolean SAVE_GIF = true;
    static final int GIF_DRAW_MODIFIER = 1;

    // used for visualization ( do not change )
    public UIGrid vis;
    public Rand rn = new Rand();
    public int[] hood = Util.RectangleHood(false,TRAVEL_RADIUS,TRAVEL_RADIUS);
    public GifMaker gifMaker = (SAVE_GIF) ? new GifMaker("Outbreak/outbreak_world.gif",100*GIF_DRAW_MODIFIER,true) : null;//used for visualization;
    public int[] counts;
    public boolean ABOVE_CAPACITY = false;

    /*
        OutbreakWorld CONSTRUCTOR
    */

    public OutbreakWorld() {
        super(DIMENSION, DIMENSION, Person.class,false,false);

        // set up visualization
        vis = new UIGrid(DIMENSION, DIMENSION, SCALE_FACTOR);

        // setup initial counts vector
        counts = new int[] {0,0,0,0,0};

        // set the full domain to "SUSCEPTIBLE" type
        for (int i = 0; i < length; i++) { NewAgentSQ(i).Init(Person.SUSCEPTIBLE); }

        // set the center to be single person asymptomatic
        GetAgent((this.xDim-1)/2,(this.xDim-1)/2).Init(Person.ASYMPTOMATIC_INFECTED);

        GetAgent((this.xDim-1)/2+1,(this.xDim-1)/2).Init(Person.ASYMPTOMATIC_INFECTED);
        GetAgent((this.xDim-1)/2-1,(this.xDim-1)/2).Init(Person.ASYMPTOMATIC_INFECTED);
        GetAgent((this.xDim-1)/2,(this.xDim-1)/2+1).Init(Person.ASYMPTOMATIC_INFECTED);
        GetAgent((this.xDim-1)/2,(this.xDim-1)/2-1).Init(Person.ASYMPTOMATIC_INFECTED);

    }

    /*
        StepCells()
            - loop through each cell at every time step
    */

    public void StepCells(){

        Draw();

        // check how many infected:
        double infected_percent =  (counts[Person.ASYMPTOMATIC_INFECTED] + counts[Person.SYMPTOMATIC_INFECTED]) /  (double)(DIMENSION*DIMENSION);

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
                if (!c.quarantined) {
                    c.InfectOthers();
                }
                if (rn.Double() < QUARANTINE_RATE_ASYMPTOMATIC) {
                    c.quarantined = true;
                }
                if (rn.Double() < effective_fatality_rate) {
                    c.future_type = Person.DEAD;
                }
            }

            if (c.type == Person.SYMPTOMATIC_INFECTED) {
                if (!c.quarantined) {
                    c.InfectOthers();
                }
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

        // reset & count all types
        counts = new int[] {0,0,0,0,0};
        for (Person c : this) {
            c.type = c.future_type;

            // also update counts vector here:
            counts[c.type]++;
        }
    }

    public void Draw() {

        // draw each person's state, override if they're quarantined
        for (int i = 0; i < xDim*yDim; i++) {
            Person c = this.GetAgent(i);
            if (c!=null) {
                this.vis.SetPix(i,(c.quarantined) ? Util.BLUE :  ReturnColor(c.type));
            }
        }

        // Green boundary indicates if above hospital capacity
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

    public static int ReturnColor(int type) {
        if ((type == Person.SUSCEPTIBLE)) {
            return Util.RGB256(100,100,100);
        } else if ((type == Person.ASYMPTOMATIC_INFECTED)) {
            return Util.RGB256(255, 221, 226);
        } else if ((type == Person.SYMPTOMATIC_INFECTED)) {
            return Util.RGB256(255, 68, 68);
        } else if ((type == Person.RECOVERED)) {
            return Util.RGB256(50, 168, 82);
        } else if ((type == Person.DEAD)) {
            return Util.BLACK;
        }
        return Util.BLACK;
    }

    /*
        OutbreakWorld() model
            -
    */

    public static void main(String[]args){

        UIWindow win = new UIWindow("Outbreak",true,null,true);
        OutbreakWorld world = new OutbreakWorld();
        win.AddCol(0, world.vis);
        win.RunGui();

        for (int tick = 0; tick < TOTAL_TIME; tick++) {
            System.out.println("Time:  " + tick);
            world.StepCells();
        }

        if (world.SAVE_GIF) { world.gifMaker.Close(); }
    }


}