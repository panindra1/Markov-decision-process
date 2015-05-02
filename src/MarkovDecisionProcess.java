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

/**
 *
 * @author panindra
 */

enum Action{UP, DOWN, RIGHT, LEFT};


class State {
    int i;
    int j;
    float right;
    float left;
    float up;
    float down;
    float maxVal;
    boolean isterminal;
    boolean iswall;
}

class UtilityValues {
    State state;
    Action action;    
};

public class MarkovDecisionProcess {
    static double mReward = -0.04;
    static int mTValue = 0;
    static Integer[][] mInput = null;
    static double mAlpha = 0.0;
    static double mGamma = 0.99;
    static Integer mRPlus = 1;
    static double mThresholdAction = 2.0;
        
    static State[][] mTotalStates = null;
    static Map<State, ArrayList<Integer>> mQMap = new HashMap<>();    
    static Map<State, ArrayList<Integer>> mNsaMap = new HashMap<>();
   
    static State mPreviousState = null;
    static Action mPrevousAction = Action.LEFT;
    static double mPreviousReward = 0.0;
    
    static int inputLen = 0;
    static int inputWidth = 0;
    
    //Formula
    //Q(s, a) ← Q(s, a) + α (R(s) + γ max a ' Q(s' , a' ) − Q(s, a))

    
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
        
        mTotalStates = new State[env.getLength()][env.getWidth()];
        
        for(int i = 0; i < env.getLength(); i++) {
            for(int j = 0; j< env.getWidth(); j++) {                            
                State s = new State();
                s.i = i;
                s.j = j;
                s.up = 0;
                s.down = 0;
                s.right = 0;
                s.left = 0;
                
                if(mInput[i][j] == 1 || mInput[i][j] == -1)
                    s.isterminal = true;
                else
                    s.isterminal = false;
                
                if(mInput[i][j] == Integer.MIN_VALUE)
                    s.iswall = true;
                else
                    s.iswall = false;
                
                ArrayList<Integer> listValues = new ArrayList<>();
                listValues.add(0);
                listValues.add(0);
                listValues.add(0);
                listValues.add(0);
                
                mQMap.put(s, listValues);
                mNsaMap.put(s, listValues);
                        
                mTotalStates[i][j] = s;
            }
        }
        int i = env.getStartY();
        int j = env.getStartX();
        
        mPreviousState = mTotalStates[i][j];        
        Action action = computeQlearning(mTotalStates[i][j], mInput[j][j]);
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
           
                action = computeQlearning(mTotalStates[i][j], mInput[j][j]);
                System.out.println("Current state = "  + i + ", " + j);
                System.out.println("Action = " + action);
                
            }
            num--;
        }
    }
    
    
    static Action computeQlearning(State currState, double currReward) {
        mTValue++;
        mAlpha  =  60/(59 + mTValue);
        
        if(currState.isterminal) {
            UtilityValues uv = new UtilityValues();
            uv.action = null;
            uv.state = currState;
            
        }
        if(currState != null) {
            if(mNsaMap.containsKey(mPreviousState)) {
                ArrayList<Integer> listValues = mNsaMap.get(mPreviousState);
                if(mPrevousAction!= null) {
                    int val = listValues.get(mPrevousAction.ordinal());
                    val += 1;
                    listValues.set(mPrevousAction.ordinal(), val);                
                    mNsaMap.put(mPreviousState, listValues);
                }
            }            
                
            UtilityValues uv = new UtilityValues();
            uv.action = mPrevousAction;
            uv.state = mPreviousState;
            
            if(mQMap.containsKey(currState)) {
                if(mPrevousAction!= null) {
                    ArrayList<Integer> listValues = mQMap.get(currState);
                    int val = listValues.get(mPrevousAction.ordinal());
                    listValues.set(mPrevousAction.ordinal(), val);

                    double term2_1 = mQMap.get(uv.state).get(val) +  mAlpha * mNsaMap.get(mPreviousState).get(mPrevousAction.ordinal()) ;
                    double term2_2 = mPreviousReward + (mGamma * giveMax(currState) - mQMap.get(uv.state).get(val));

                    listValues = mQMap.get(uv.state);
                    listValues.set(mPrevousAction.ordinal(), (int)(term2_1 + term2_2));
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
        ArrayList<Integer> thresholdAction = mNsaMap.get(currState);
        ArrayList<Integer> utilityValues = mQMap.get(currState);
        int max = Integer.MIN_VALUE;
        int max_2 = Integer.MIN_VALUE;
        
        int i = 0, index = 0;
        ArrayList<Integer> indexArr = new ArrayList<>();        
        
        for(;i < thresholdAction.size(); i++) {
            if(thresholdAction.get(i)  < mThresholdAction) {
                if(mRPlus > max) {                  
                    max = mRPlus;
                    index = i;                    
                }                 
            }
            else {
                if(utilityValues.get(i) > max) {                    
                    max = utilityValues.get(i);
                    index = i;
                }
            }
            
            if(indexArr.isEmpty()) {
                indexArr.add(i);
            }
            else { 
                boolean inserted = false;
                int indexArrSize = indexArr.size();
                
                for(int k = 0; k < indexArrSize;k++) {
                    if(utilityValues.get(i) > utilityValues.get(indexArr.get(k))) {
                        indexArr.add(k, i);
                        inserted = true;
                    }
                }
                if(!inserted)
                    indexArr.add(i);
            }
        }
        
        //check the result of moving in Action.values()[index] leads to wall
        Action returnAction = null;
        for(int foundIndex = 0; foundIndex < indexArr.size(); foundIndex++) {
           returnAction = Action.values()[foundIndex];

           int newI = currState.i, newJ = currState.j;
           if(returnAction.equals(Action.UP)){
               newI-=1;
           }
           else if(returnAction.equals(Action.DOWN)){
               newI+=1;
           }
           else if(returnAction.equals(Action.LEFT)){
               newJ-=1;
           }
           else if(returnAction.equals(Action.RIGHT)){
               newJ+=1;
           }
           if(newI >=0 && newI <= inputLen && newJ >= 0 && newJ <= inputWidth ){
               if(mInput[newI][newJ] != '%'){
                   returnAction = Action.values()[foundIndex];
                   return returnAction;
               }
           }
           
//           if(returnAction != null)
//               return returnAction;
        }
        return returnAction;        
    }
    
    static double giveMax(State state) {
        int max = Integer.MIN_VALUE;
        ArrayList<Float> vals = new ArrayList<>();
        vals.add(state.up);
        vals.add(state.down);
        vals.add(state.left);
        vals.add(state.right);
        
        Collections.sort(vals);
        
        return vals.get(0);
    }
    
    
}
