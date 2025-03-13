package tris;
import tris.utils.Pair;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class Qtable implements Cloneable,Serializable{
    private static final long serialVersionUID = -6290169170984430443L;
    private HashMap<String ,Float[]> qtable;
    private int gridDimX;
    private int gridDimY;
    private String symbol1;
    private String symbol2;
    private String emptySymbol;


    public Qtable(int gridDimX, int gridDimY,String symbol1, String symbol2, String emptySymbol) {
        if(symbol1.equals(emptySymbol)||symbol2.equals(symbol1)||emptySymbol.equals(symbol2))
            throw new IllegalArgumentException();
        this.gridDimX=gridDimX;
        this.gridDimY=gridDimY;
        this.symbol1=symbol1;
        this.symbol2=symbol2;
        this.emptySymbol=emptySymbol;
        qtable= new HashMap<String,Float[]>();
    }
    public boolean acceptable_configuration(String config ){
        String cleanedString=config.replaceAll("\\s","");
        if(!cleanedString.replaceAll("[" + symbol1 + symbol2 + emptySymbol + "]", "").trim().isEmpty() )
            return false;
        int count2 = 0, count1 = 0;
        for (char c: cleanedString.toCharArray()){
            if (c==symbol1.charAt(0))
                count1++;
            if (c==symbol2.charAt(0))
                count2++;
        }
        return count1 == count2 || count2 + 1 == count1 || count2 == count1 + 1;
    }


    public int getGridDimX() {
        return gridDimX;
    }

    public int getGridDimY() {
        return gridDimY;
    }

    //devo esccludere le azioni non possibili?
    public float maxValue(String configuration) {
        if(!acceptable_configuration(configuration))
            throw new IllegalArgumentException();

        Float[] f=qtable.get(configuration);
        if (f==null) {
            return 0f;
        }
        float max=(float)Double.NEGATIVE_INFINITY;
        for(int i=0; i<f.length; i++) {
            if(f[i]!=null&&f[i]>max)
                max=f[i];
        }

        return max;

    }

    public  Pair<Integer,Integer> maxKnowAction(String configuration,List<Pair<Integer,Integer>>possibleAction) {
        if(!acceptable_configuration(configuration))
            throw new IllegalArgumentException();
        for (Pair<Integer,Integer> a: possibleAction){
            if(a.getFirst()<0 || a.getSecond()<0 || a.getSecond()>gridDimY || a.getFirst()>gridDimX)
                throw new IllegalArgumentException();
        }

        Float[] f=qtable.get(configuration);

        System.out.println("PossibleAction:");
        System.out.println(Arrays.toString(possibleAction.toArray()));
        System.out.println("Configuration:");
        System.out.println("key:" );
        System.out.println(configuration);
        System.out.println("Elementi già memorizzati:");
        System.out.println(Arrays.toString(f));

        if (f==null) {
            f = new Float[gridDimX*gridDimY];
            Arrays.fill(f, null);    // Riempie tutti gli elementi con il valore -1
            //seleziono dalle azioni possibili un elemento
            int el=new Random().nextInt(possibleAction.size());
            Pair<Integer,Integer> p=possibleAction.get(el);
            f[p.getFirst()*(gridDimY)+p.getSecond()]=0f;//new Random().nextFloat()/10f;
            qtable.put(configuration,f);

            System.out.println("Posizione greedy scelta "+p+" valore: "+ f[p.getFirst()*(gridDimY)+p.getSecond()]);

            return p;
        }
        else {

            List<Pair<Integer,Integer>>explored=new LinkedList<>(possibleAction);
            for (Pair<Integer,Integer>p:possibleAction) {
                if(f[p.getFirst()*(gridDimY)+p.getSecond()]==null) {
                    explored.remove(p);
                }
            }
            if (explored.isEmpty()){
                int el=new Random().nextInt(possibleAction.size());
                Pair<Integer, Integer> ret=possibleAction.get(el);
                f[ret.getFirst()*(gridDimY)+ret.getSecond()]=0f;//new Random().nextFloat()/10f;
                System.out.println("Posizione greedy scelta a caso: " + ret + " valore: " + f[ret.getFirst() * (gridDimY) + ret.getSecond()]);
                return ret;
            }else {
                float max = 0f;
                List<Pair<Integer, Integer>> list_ret = new LinkedList<>();
                for (Pair<Integer, Integer> p : explored) {
                    float val=f[p.getFirst() * (gridDimY) + p.getSecond()];
                    if ( val>= max || list_ret.isEmpty()) {
                        if(val>max) {
                            max = val;
                            list_ret.clear();
                            list_ret.add(p);
                        }else {
                            max = val; //lo faccio perchè entro qui anche se la lista è vuota e comunque se non lo è non mi cambia niete dato che i valori sono uguali
                            list_ret.add(p);
                        }
                    }
                }

                int el=new Random().nextInt(list_ret.size());
                Pair<Integer, Integer> ret= list_ret.get(el);
                System.out.println("Posizione greedy scelta " + ret + " valore: " + f[ret.getFirst() * (gridDimY) + ret.getSecond()]);
                System.out.println("PossibleAction:");
                System.out.println(Arrays.toString(possibleAction.toArray()));
                System.out.println("Configuration:");
                System.out.println("key:" );
                System.out.println(configuration);
                System.out.println("Elementi già memorizzati:");
                System.out.println(Arrays.toString(f));
                return ret;
            }
        }
    }
    public String getSymbol1() {
        return symbol1;
    }

    public String getSymbol2() {
        return symbol2;
    }

    public String getEmptySymbol() {
        return emptySymbol;
    }

    public float getValue(String configuration,Pair<Integer,Integer> action) {
        if(!acceptable_configuration(configuration) || action.getFirst()<0 || action.getSecond()<0 || action.getSecond()>gridDimY || action.getFirst()>gridDimX)
            throw new IllegalArgumentException();

        if(!qtable.containsKey(configuration))
            return -1;
        return qtable.get(configuration)[action.getFirst()*(gridDimY)+action.getSecond()];
    }
    public void setValue(String configuration,Pair<Integer,Integer> action,float value) {
        if(!acceptable_configuration(configuration) || action.getFirst()<0 || action.getSecond()<0 || action.getSecond()>gridDimY || action.getFirst()>gridDimX)
            throw new IllegalArgumentException();

        if(qtable.containsKey(configuration)) {
            qtable.get(configuration)[action.getFirst()*(gridDimY)+action.getSecond()]=value;
        }

    }
    public Float[] getKnowAction(String configuration){
        if(!acceptable_configuration(configuration))
            throw new IllegalArgumentException();

        return qtable.get(configuration);
    }

    public  Pair<Integer,Integer> explore(String configuration,List<Pair<Integer,Integer>>possibleAction) {
        if(!acceptable_configuration(configuration))
            throw new IllegalArgumentException();
        for (Pair<Integer,Integer> a: possibleAction){
            if(a.getFirst()<0 || a.getSecond()<0 || a.getSecond()>gridDimY || a.getFirst()>gridDimX)
                throw new IllegalArgumentException();
        }

        Float[] f=qtable.get(configuration);

        System.out.println("PossibleAction:");
        System.out.println(Arrays.toString(possibleAction.toArray()));
        System.out.println("Configuration:");
        System.out.println("key: ");
        System.out.println(configuration);
        System.out.println("Elementi già memorizzati:");
        System.out.println(Arrays.toString(f));

        if (f==null) {
            f = new Float[gridDimX*gridDimY];
            Arrays.fill(f, null);      // Riempie tutti gli elementi con il valore -1
            //seleziono dalle azioni possibili un elemento
            int el=new Random().nextInt(possibleAction.size());
            Pair<Integer,Integer> p=possibleAction.get(el);
            int val=p.getFirst()*(gridDimY)+p.getSecond();
            System.out.println("Posizione nell'array "+val+" Rappresentata come Pair: "+p);
            f[p.getFirst()*(gridDimY)+p.getSecond()]=0f;//new Random().nextFloat()/10f;
            qtable.put(configuration,f);

            System.out.println("Posizione esporata "+p+" valore: "+ f[p.getFirst()*(gridDimY)+p.getSecond()]);

            return p;
        }
        else {
            List<Pair<Integer,Integer>>notExplored=new LinkedList<>(possibleAction);
            for (Pair<Integer,Integer>p:possibleAction) {
                if(f[p.getFirst()*(gridDimY)+p.getSecond()]!=null) {
                    notExplored.remove(p);
                }
            }
            Pair<Integer,Integer> ret;
            if(!notExplored.isEmpty()) {
                System.out.println("Esploro una nuova azione");
                //inzialmente esploro cercando di conoscere tutte le possibili azioni
                int el=new Random().nextInt(notExplored.size());
                ret=notExplored.get(el);
                f[ret.getFirst()*(gridDimY)+ret.getSecond()]=0f;//new Random().nextFloat()/10f;
            }else {
                System.out.println("Prendo una azione a caso");
                //poi l'esporazione diviene un modo per cambiare le carte in tavola
                int el=new Random().nextInt(possibleAction.size());
                ret=possibleAction.get(el);
            }
            System.out.println("Posizione esporata "+ret+" valore: "+ f[ret.getFirst()*(gridDimY)+ret.getSecond()]);
            System.out.println("PossibleAction:");
            System.out.println(Arrays.toString(possibleAction.toArray()));
            System.out.println("Configuration:");
            System.out.println("key:" );
            System.out.println(configuration);
            System.out.println("Elementi già memorizzati:");
            System.out.println(Arrays.toString(f));
            return ret;
        }

    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Qtable other = (Qtable) obj;
        return Objects.equals(emptySymbol, other.emptySymbol) && gridDimX == other.gridDimX
                && gridDimY == other.gridDimY && Objects.equals(qtable, other.qtable)
                && Objects.equals(symbol1, other.symbol1) && Objects.equals(symbol2, other.symbol2);
    }

    @Override
    public int hashCode() {
        int result = qtable.hashCode();
        result = 31 * result + gridDimX;
        result = 31 * result + gridDimY;
        result = 31 * result + symbol1.hashCode();
        result = 31 * result + symbol2.hashCode();
        result = 31 * result + emptySymbol.hashCode();
        return result;
    }

    @Override
    public Qtable clone(){
        Qtable qClone=null;
        try {
            super.clone();
            qClone= new Qtable(gridDimX,gridDimY,symbol1,symbol2,emptySymbol);
            if (!qtable.isEmpty()) {
                for (String key:qtable.keySet()) {
                    Float[] val =new Float[gridDimX*gridDimY];
                    Float[] array=qtable.get(key);
                    for(int i=0; i<array.length; i++) {
                        val[i]=array[i];
                    }
                    qClone.qtable.put(key, val);
                }
            }
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return qClone;
    }

    @Override
    public String toString() {
        String ret="";
        String title="Key:";
        for (int i=0;i< gridDimY+1;i++) {
            title+="\t";
        }
        title+="Value:\n";
        for (String key:qtable.keySet()) {
            ret+=title;
            String[] keyPart = key.split("\n");

            for (int i=0;i< keyPart.length;i++) {
                String elKey=keyPart[i];
                String elValue="";
                Float[] f=qtable.get(key);
                for (int j=gridDimY*i;j<gridDimY*(i+1);j++) {
                    elValue+=f[j]==null?f[j]+" \t":String.format("%.3f",f[j])+"\t";
                }

                ret+=elKey+"\t|\t"+elValue+"\n";
            }
            ret+="\n";
        }
        return ret;
    }

    public float hasLearned(Qtable oldqtable) {
        if( oldqtable.gridDimX!=gridDimX || oldqtable.gridDimY!=gridDimY || !oldqtable.emptySymbol.equals(emptySymbol) ||
        !oldqtable.symbol1.equals(symbol1)||!oldqtable.symbol2.equals(symbol2) )
            throw new IllegalArgumentException();

        /*System.out.println("Confronto, qtable: ");
		System.out.println(this);
		System.out.println("e Oldqtable: ");
		System.out.println(oldqtable);*/


        float count=0;
        for (String config: qtable.keySet()) {
            Float[] val=this.qtable.get(config);
            Float[] oldVal=oldqtable.qtable.get(config);
            if (oldVal!=null) {
                for (int i=0; i<gridDimX*gridDimY-1;i++) {
                    if(val[i]!=null && oldVal[i]!=null) {
                        count+= (float) Math.pow(val[i]-oldVal[i],2);
                    }else {
                        if(val[i]!=null && oldVal[i]==null) {
                            count+= (float) Math.pow(val[i],2);
                        }
                    }

                }
            }else {
                for (int i=0; i<gridDimX*gridDimY-1;i++) {
                    if(val[i]!=null) {
                        count+= (float) Math.pow(val[i],2);
                    }
                }
            }
        }
        return count;
    }
}
