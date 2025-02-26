package tris;

import tris.utils.Pair;

import java.util.ArrayList;

public abstract class QlearnigTemplate {
    public  Pair<Integer,Integer> epsilonGreedyPolicy(String state){
        Pair<Integer,Integer>retAction;
        if(epsilonPolicy(state)) {
            retAction=exploreAction(state);
        }else {
            retAction=greedyAction(state);
        }
        return retAction;
    }

    protected abstract boolean epsilonPolicy( String state);

    protected abstract   Pair<Integer,Integer> exploreAction( String state);

    protected abstract   Pair<Integer,Integer> greedyAction( String state);
    protected abstract void updateQtable(String oldState, Pair<Integer,Integer> oldAction,String newState, float reward);

}
