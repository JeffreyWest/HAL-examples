package Examples.Algorithms;

import Examples.Algorithms.Algorithms;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;

//cells grow and mutate
class Unit extends AgentSQ2Dunstackable<AlgorithmRow>{

//    public final static int SUSCEPTIBLE = 0;


    // cell attributes: type; future_type; fitness
    int number;
    int pixel;

    public void Init(int number) {
        this.number = number;
    }
}