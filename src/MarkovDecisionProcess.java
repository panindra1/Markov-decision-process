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
    static double mThresholdAction = 1.0;
        
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
        
        mPreviousState = mTotalStates[i][j- 1];
        mPrevousAction = Action.RIGHT;
        Action action = computeQlearning(mTotalStates[i][j], mInput[i][j]);
        System.out.println("Current state = "  + i + ", " + j);
        System.out.println("Action = " + action);
        
        int num = 1000;
        
        while(num > 0) {
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
            
            if(i >=0 && i <= inputLen && j >= 0 && j <= inputWidth ){           
                action = computeQlearning(mTotalStates[i][j], mInput[i][j]);
                System.out.println("Current state = "  + i + ", " + j);
                System.out.println("Action = " + action);
            }
            num--;
        }
    }
    
    
    static Action computeQlearning(State currState, double currReward) {
        mTValue++;
        mAlpha  =  60.0/(59 + mTValue);
            
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
                    
                    listValues.set(mPrevousAction.ordinal(), (term2_1 + term2_2));
                    mPreviousState.maxVal = Collections.max(listValues);
                    
                    mQMap.put(mPreviousState, listValues);

                    mPreviousState = currState;
                    mPreviousReward = currReward;
                    mPrevousAction = giveMaxAction(currState);
                }
            }        
        }
        
        return mPrevousAction;
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
                
        //return Action of MinIndex value
        return Action.values()[minIndex];
        
        
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
