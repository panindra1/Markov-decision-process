/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package markov.decision.process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JTable.PrintMode;

/**
 *
 * @author panindra
 */

enum Action{UP, LEFT, RIGHT, DOWN};


class State {
    int i;
    int j;
    float right;
    float left;
    float up;
    float down;
    double maxVal;    
    boolean iswall;
}

class UtilityValues {
    State state;
    Action action;    
};

public class MarkovDecisionProcess {
    static double mReward = -0.04;
    static int mTValue = 0;
    static Double[][] mInput = null;
    static double mAlpha = 0.0;
    static double mGamma = 0.99;
    static Integer mRPlus = 1;
    static double mThresholdAction = 10.0;
    static final float mDelta = 1f;
        
    static State[][] mTotalStates = null;
    static Map<State, ArrayList<Double>> mQMap = new HashMap<>();    
    static Map<State, ArrayList<Double>> mNsaMap = new HashMap<>();
   
    static State mPreviousState = null;
    static Action mPrevousAction = null;
    static double mPreviousReward = 0.0;
    
    static int inputLen = 0;
    static int inputWidth = 0;
      
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        String filename = "input.txt";
        File file = new File(filename);
        EnvironmentReader env = new EnvironmentReader(file);
        inputLen = env.getLength();
        inputWidth = env.getWidth();
        
        mInput = env.getEnvironment();    
        
        mTotalStates = new State[inputLen][inputWidth];
        
        
        for(int i = 0; i < inputLen; i++) {
            for(int j = 0; j< inputWidth; j++) {                            
                State s = new State();
                s.i = i;
                s.j = j;
                s.up = 0;
                s.down = 0;
                s.right = 0;
                s.left = 0;
                s.maxVal = 0;
                               
                if(mInput[i][j] == Integer.MIN_VALUE)
                    s.iswall = true;
                else
                    s.iswall = false;
                
                ArrayList<Double> listValues = new ArrayList<>();
                if( (i- 1) < 0 || (mInput[i - 1][j] == Double.MIN_VALUE))
                    listValues.add(-100.0);
                else
                    listValues.add(0.0);
                
                if(( j  - 1) < 0 || (mInput[i][j - 1] == Double.MIN_VALUE))
                    listValues.add(-100.0);
                else
                    listValues.add(0.0);
                
                if(( j  + 1) > inputLen - 1 ||  (mInput[i][j + 1] == Double.MIN_VALUE))
                    listValues.add(-100.0);
                else
                    listValues.add(0.0);
                
                if(( i  + 1) > inputWidth - 1 || (mInput[i + 1][j] == Double.MIN_VALUE))
                    listValues.add(-100.0);
                else
                    listValues.add(0.0);
                                
                mQMap.put(s, listValues);
                
                listValues = new ArrayList<>();
                listValues.add(0.0);
                listValues.add(0.0);
                listValues.add(0.0);
                listValues.add(0.0);
                
                mNsaMap.put(s, listValues);
                        
                mTotalStates[i][j] = s;
            }
        }
        int i = env.getStartY();
        int j = env.getStartX();
        
        //int i = 1;
        //int j = 0;
        
        //mPreviousState = mTotalStates[i][j- 1];
        //mPrevousAction = Action.RIGHT;
        
        
        mPreviousState = mTotalStates[i-1][j];
        mPrevousAction = Action.DOWN;
               
        Action action = computeQlearning(mTotalStates[i][j], mInput[i][j]);
        System.out.println("Current state = "  + i + ", " + j);
        System.out.println("Action = " + action);
        
        Map<State, ArrayList<Double>> previousStatesMap = new HashMap<State, ArrayList<Double>>();
        ArrayList<Double> valuesList;
        
        
        //deep copy contents of map
        for (Map.Entry<State, ArrayList<Double>> entry : mQMap.entrySet())
        {
        	valuesList = new ArrayList<Double>();
        	valuesList.addAll(entry.getValue());
            previousStatesMap.put(entry.getKey(), valuesList);
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        
        
        int num = 1000;
        for(int trial = 0; trial < 5000; trial++){
        while(num > 0) {
        	System.out.println("num = " + num);
        	
            if(action == Action.LEFT) {
                j = j - 1;
                
            }
            else if(action == Action.RIGHT) {
                j = j + 1;
                
            }
            else if(action == Action.UP) {
                i = i - 1;
            }
            else if(action == Action.DOWN) {
                i = i + 1;
            }
            
            if(action==null){
            	action = computeQlearningWithRepeatedState(mTotalStates[i][j], mInput[i][j]);
                System.out.print("Current state = "  + i + ", " + j + " ");
                System.out.println("Action = " + action);
                System.out.println("---------------------------------------");
            }
            
            else if(i >=0 && i <= inputLen && j >= 0 && j <= inputWidth ){           
                action = computeQlearning(mTotalStates[i][j], mInput[i][j]);
                System.out.print("Current state = "  + i + ", " + j + " ");
                System.out.println("Action = " + action);
                System.out.println("---------------------------------------");
            }
            
            num--;
            
        } //end while
      
            
        	boolean isConv = isConvergence(previousStatesMap, mQMap);
		    System.out.println("isConvergence = " + isConv);
		    
		    if(isConv == true){
		    	break;
		    }
		    
	        System.out.println("----------------------------------------------------");
	        //mQMap.clear();
	        //mNsaMap.clear();
	        
	        //generate new random start state and  previousAction
	        int minimum = 0, maximum = 6;
	        i = minimum + (int)(Math.random()*maximum); 
	        j = minimum + (int)(Math.random()*maximum);
	        
	        //check for walls 
	        while(mInput[i][j] == Double.MIN_VALUE){
	        	 i = minimum + (int)(Math.random()*maximum); 
	             j = minimum + (int)(Math.random()*maximum);
	        }
	        
	        //set the previous state and previous action
	        if(i-1 >= 0){
	        	mPreviousState = mTotalStates[i-1][j];
	        	mPrevousAction = Action.DOWN;
	        }
	        else if(j-1 >= 0){
	        	mPreviousState = mTotalStates[i][j-1];
	        	mPrevousAction = Action.RIGHT;
	        }
	        else if (j+1 < inputLen){
	        	mPreviousState = mTotalStates[i][j+1];
	        	mPrevousAction = Action.LEFT;
	        }
	        else {
	        	mPreviousState = mTotalStates[i+1][j];
	        	mPrevousAction = Action.UP;
	        }
	               
	        Action action1 = computeQlearning(mTotalStates[i][j], mInput[i][j]);
	        System.out.print("Current state = "  + i + ", " + j + " ");
	        System.out.println("Action = " + action1);
	        
	        }

    }
    
    static boolean isConvergence(Map<State, ArrayList<Double>> previousStatesMap, Map<State, ArrayList<Double>> mQMap2){
    	ArrayList<Double> listValuesForOldStates = new ArrayList<Double>();
    	ArrayList<Double> listValuesForNewStates = new ArrayList<Double>();
    	for (Map.Entry<State, ArrayList<Double>> entry : previousStatesMap.entrySet()){
    		listValuesForOldStates = entry.getValue();
    		listValuesForNewStates = mQMap2.get(entry.getKey());
    		for(int indx = 0; indx < listValuesForNewStates.size(); indx++ ){
    			if(Math.abs((listValuesForNewStates.get(indx) - listValuesForOldStates.get(indx))) > mDelta){
    				return false;
    			}
    		}
    	}
		return true;
    	
    }
    
    
    static Action computeQlearning(State currState, double currReward) {
        mTValue++;
        mAlpha  =  60.0/(59 + mTValue);
        Action retAction = null;
            
        if(currState != null) {
            if(mNsaMap.containsKey(mPreviousState)) {
                ArrayList<Double> listValues = mNsaMap.get(mPreviousState);
                if(mPrevousAction!= null) {
                    double val = listValues.get(mPrevousAction.ordinal());
                    val += 1;
                    listValues.set(mPrevousAction.ordinal(), val);                
                    mNsaMap.put(mPreviousState, listValues);
                }
            }                        
           
            if(mQMap.containsKey(currState)) {
                if(mPrevousAction!= null) {
                    ArrayList<Double> listValues = mQMap.get(mPreviousState);
            
                    double term2_1 = mQMap.get(mPreviousState).get(mPrevousAction.ordinal());
                    double term2_2 = mAlpha * mNsaMap.get(mPreviousState).get(mPrevousAction.ordinal()) * (mPreviousReward + (mGamma * giveMax(currState) - mQMap.get(mPreviousState).get(mPrevousAction.ordinal())));
                    
                    //System.out.println("(term2_1 + term2_2) = " + (term2_1 + term2_2));
                    listValues.set(mPrevousAction.ordinal(), (term2_1 + term2_2));
                    mPreviousState.maxVal = Collections.max(listValues);
                    
                    mQMap.put(mPreviousState, listValues);

                     
                   retAction = giveMaxAction(currState);
                   
                    if(retAction != null){
                    	 mPreviousState = currState;
                         mPreviousReward = currReward;
                         mPrevousAction = retAction;
                    }
                }
            }        
        }
        
        return retAction;
    }
 
    static Action computeQlearningWithRepeatedState(State currState, double currReward) {
        mTValue++;
        mAlpha  =  60.0/(59 + mTValue);
        Action retAction = null;
        
        State tempPrevState = currState;
        
        if(currState != null) {
            if(mNsaMap.containsKey(tempPrevState)) {
                ArrayList<Double> listValues = mNsaMap.get(tempPrevState);
                if(mPrevousAction!= null) {
                    double val = listValues.get(mPrevousAction.ordinal());
                    val += 1;
                    listValues.set(mPrevousAction.ordinal(), val);                
                    mNsaMap.put(tempPrevState, listValues);
                }
            }                        
           
            if(mQMap.containsKey(currState)) {
                if(mPrevousAction!= null) {
                    ArrayList<Double> listValues = mQMap.get(tempPrevState);
            
                    double term2_1 = mQMap.get(tempPrevState).get(mPrevousAction.ordinal());
                    double term2_2 = mAlpha * mNsaMap.get(tempPrevState).get(mPrevousAction.ordinal()) * (mPreviousReward + (mGamma * giveMax(currState) - mQMap.get(tempPrevState).get(mPrevousAction.ordinal())));
                    
                    //System.out.println("(term2_1 + term2_2) = " + (term2_1 + term2_2));
                    listValues.set(mPrevousAction.ordinal(), (term2_1 + term2_2));
                    tempPrevState.maxVal += Collections.max(listValues);
                    
                    mQMap.put(tempPrevState, listValues);

                     
                   retAction = giveMaxAction(currState);
                   
                    if(retAction != null){
                    	 mPreviousState = currState;
                         mPreviousReward = currReward;
                         mPrevousAction = retAction;
                    }
                }
            }        
        }
        
        return retAction;
    }
 
    
    
    static Action giveMaxAction(State currState) {
        int a = 0;
        ArrayList<Double> thresholdAction = mNsaMap.get(currState);
        ArrayList<Double> utilityValues = mQMap.get(currState);
        double max = Double.MIN_VALUE;        
        
        //check the result of moving in Action.values()[index] leads to wall        
        
        ArrayList<Double> possibleValues = new ArrayList<>();
        Map<Action, Double> actionMap = new HashMap<>();
        
        Action returnAction = null;
        
        Map<Action, Double> directionutilityValues = new HashMap<>();
        
        for(int foundIndex = 0; foundIndex < utilityValues.size(); foundIndex++) {
           //Check for the exploration function
           if(thresholdAction.get(foundIndex)  < mThresholdAction) {                
               max = mRPlus;                
           }
           else {                
               max = utilityValues.get(foundIndex);                                    
           }
           
           if(max == -100)
               max = currState.maxVal;
           
           directionutilityValues.put(Action.values()[foundIndex], max);
        }
        
        
        for(int foundIndex = 0; foundIndex < utilityValues.size(); foundIndex++) {
            returnAction = Action.values()[foundIndex];
            possibleValues.add(givePreferredDirectionValues(returnAction, directionutilityValues));                
        }
                
        //System.out.println("--" + "\n" + possibleValues);
                       
        //To generate random action
        double X=((double)Math.random()/(double)1.0);
      
        int minIndex = 0;
        double minVal = Double.MAX_VALUE;        
        
        for(int i = 0; i < possibleValues.size(); i++) {
            //double tempVal = possibleValues.get(i);
            if((possibleValues.get(i) - X) < minVal) {
                minVal = possibleValues.get(i) - X;
                minIndex = i;
                
            }
        }
        
        //System.out.println("MinIndex = " + minIndex);
        Action retAction = Action.values()[minIndex];
        
        //System.out.println("X = " + X);
        int i = currState.i, j = currState.j;
        if(retAction == Action.UP){
        	i = i-1;
        }
        else if (retAction == Action.DOWN){
        	i = i+1;
        }
        else if (retAction == Action.LEFT){
        	j = j-1;
        }
        else if (retAction == Action.RIGHT){
        	j = j+1;
        }
        
        if(i>=0 && i<inputLen && j>=0 && j<inputWidth){
                
	        //return Action of MinIndex value
        	mPreviousState = currState;
	        return Action.values()[minIndex];
        }
        
        else {
        	
        	return null;
        }
        
        
    }
    
    static Double givePreferredDirectionValues(Action action,  Map<Action, Double> directionutilityValues) {
        double val = 0.0;
         if(action == Action.UP || action == Action.DOWN) {
             val = 0.8 * directionutilityValues.get(action) + 0.1 * directionutilityValues.get(Action.LEFT) +  0.1 * directionutilityValues.get(Action.RIGHT) ;             
         }                       
        else if(action == Action.LEFT || action == Action.RIGHT) {
            val = 0.8 * directionutilityValues.get(action) + 0.1 * directionutilityValues.get(Action.UP) +  0.1 * directionutilityValues.get(Action.DOWN) ;             
        }
        return val;
    }
            
    static Action giveOppAction(Action action) {
        Action retAction = Action.UP;
        if(action == Action.UP)
            retAction = Action.DOWN;
        else if(action == Action.DOWN)
            retAction = Action.UP;
        else if(action == Action.LEFT)
            retAction = Action.RIGHT;
        else if(action == Action.RIGHT)
            retAction = Action.LEFT;
        
        return retAction;
    }
    
    static double giveMax(State state) {
        ArrayList<Float> vals = new ArrayList<>();
        vals.add(state.up);
        vals.add(state.down);
        vals.add(state.left);
        vals.add(state.right);
        
        Collections.sort(vals);
        Collections.reverse(vals);
        
        return vals.get(0);
    }
    
    
}
