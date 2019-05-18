/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.ArrayList;
import gravsim.State;
import java.util.Iterator;
import java.util.HashMap;

/**
 *
 * @author Nathan
 */
public class SimulationResults {
    private class ObjectLocator{
        public ArrayList<Integer> timeIndex;
        public ArrayList<Integer> stateIndex;
        
        ObjectLocator(){
            //index of which state (in time) an object exists
            timeIndex = new ArrayList<Integer>();
            //index of where in the state list an object exists
            stateIndex = new ArrayList<Integer>();
        }
    }
    
    private ArrayList<ArrayList<State>> simStates;
    private ArrayList<Double> simTimes;
    private HashMap<String,ObjectLocator> objectLocations;
    
    SimulationResults(){
        simStates = new ArrayList<ArrayList<State>>();
        simTimes = new ArrayList<Double>();
        objectLocations = new HashMap<String,ObjectLocator>();
    }
    
    public void addRecord(ArrayList<State> newStates, double newTime){
        simStates.add(newStates);
        simTimes.add(newTime);
        
        
        //for each object state, record which time index and which state index for
        //  that time the unique object exits for later reference
        for(int objectIndex = 0; objectIndex < newStates.size(); objectIndex++){
            String thisName = newStates.get(objectIndex).getName();
            
            if(!objectLocations.containsKey(thisName)){
                objectLocations.put(thisName,new ObjectLocator());
            }
            objectLocations.get(thisName).timeIndex.add(simStates.size()-1);
            objectLocations.get(thisName).stateIndex.add(objectIndex);
        }
    }
    
}
