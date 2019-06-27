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
import java.util.Set;

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
        simStates.add(this.cloneList(newStates));
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
    private static ArrayList<State> cloneList(ArrayList<State> originalList){
        ArrayList<State> newList = new ArrayList<>(originalList);
//        System.out.printf("ArrayList cloned\n");
        for(int i = 0; i < newList.size(); i++){
//            System.out.printf("copying %d\n",i);
            newList.set(i, originalList.get(i).copy());
        }
//        System.out.printf("done SimulationResults.cloneList()\n");
        return newList;
    }
    
    public Set<String> getUniqueObjects(){
        return objectLocations.keySet();
    }
    
    public ArrayList<gravsim.State> getState(){
        return simStates.get(simStates.size()-1);
    }
    public ArrayList<gravsim.State> getState(int index){
//        System.out.printf("\n%f\n",simTimes.get(index));
        return simStates.get(index);
    }
    public double getTime(int index){
        return simTimes.get(index);
    }
    public int simStatesSize(){
        return this.simStates.size();
    }
}
