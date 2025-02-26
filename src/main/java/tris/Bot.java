package tris;

import tris.utils.Costants;
import tris.utils.Pair;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Bot extends QlearnigTemplate{
    private JButton[][] grid ;
    private Qtable q_table;
    private float totReward;
    private List<Pair<Integer,Integer>> possibleAction;


    public Bot(JButton[][] grid, Qtable q_table ) {
        this.grid = grid ;
        this.q_table=q_table;
        totReward=0;
    }

    @Override
    protected boolean epsilonPolicy(String state) {
        possibleAction=extractPossibleAction();
        float el=(float) Math.random();
        return q_table.getKnowAction(state)==null||el< Costants.EPSILON;
    }

    private List<Pair<Integer, Integer>> extractPossibleAction() {
        ArrayList<Pair<Integer, Integer>> ret=new ArrayList<>();
        for(int i=0;i<Costants.GRID_DIMX;i++){
            for(int j=0;j<Costants.GRID_DIMY;j++){
                if(grid[i][j].getText().equals(" ")){
                    ret.add(new Pair<>(i,j));
                }
            }
        }
        return ret;
    }

    public String extractState() {
        String ret="";
        for(int i=0;i<Costants.GRID_DIMX;i++){
            for(int j=0;j<Costants.GRID_DIMY;j++){
                ret+=grid[i][j].getText().equals(" ") ?"- \t":grid[i][j].getText()+" \t";
            }
            ret+="\n";
        }
        return ret;
    }

    @Override
    protected Pair<Integer,Integer> exploreAction(String state) {
        System.out.println("Mossa bot:");
        System.out.println("BOT "+Costants.BOT_SYMBOL + " sta esporando.......");
        //shelgo di esporare un elemento a caso

        Pair<Integer,Integer> action=q_table.explore(state,possibleAction);
        System.out.println("Nuova azione scoperta -> "+action);
        possibleAction.remove(action);

        grid[action.getFirst()][action.getSecond()].setText(Costants.BOT_SYMBOL);
        grid[action.getFirst()][action.getSecond()].setForeground(Costants.BOT_COLOR);
        return action;
    }
    @Override
    protected Pair<Integer,Integer> greedyAction(String configuration) {
        System.out.println("Mossa bot:");
        System.out.println("BOT "+Costants.BOT_SYMBOL +  " sto applicando greedy policy.......");

        Pair<Integer,Integer> action=q_table.maxKnowAction(configuration,possibleAction);
        System.out.println("Azione greedy selezionata -> "+action);

        possibleAction.remove(action);

        grid[action.getFirst()][action.getSecond()].setText(Costants.BOT_SYMBOL);
        grid[action.getFirst()][action.getSecond()].setForeground(Costants.BOT_COLOR);
        return action;

    }
    @Override
    protected void updateQtable(String oldState, Pair<Integer,Integer> oldAction,String newState,float reward) {
        float oldValue=q_table.getValue(oldState, oldAction);
        addReward(reward);
        float val=oldValue+Costants.ALPHA*(reward+Costants.DISCOUNT_FACTOR*q_table.maxValue(newState) - oldValue);
        q_table.setValue(oldState,oldAction,val);
        System.out.println("UPDATE QTABLE BOT:");
        System.out.println("OldState: \n"+oldState+"\nOld value : "+oldValue+" Action : "+oldAction+" New value : "+val+"\nNew State: \n"+newState);



    }
    public void addReward(float r){
        totReward+=r;
    }
    public float getReward(){return totReward;}
    public void saveData() throws IOException {
        try(ObjectOutputStream pw =new ObjectOutputStream(new FileOutputStream(Costants.NAME_FILE_BOT))){
            pw.writeObject(q_table);
        }
        catch(IOException e) {
            System.out.println("Si è verificato un errore durante la scrittura nel file.");
            throw new IOException();
        }
    }

}

