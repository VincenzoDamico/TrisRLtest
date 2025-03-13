package tris;

import tris.utils.Costants;
import tris.utils.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;



public class TrisGui implements KeyListener {
    private JFrame frame;
    private JButton[][] gridFields;
    private JLabel lblinfo;
    private JLabel lblTitle;
    private Qtable old_Qtable;
    private Qtable q_table;
    private Bot bot;
    private int botWin=0;
    private int playerWin=0;
    private int draw=0;
    private int nMatch=1;

    private String oldState;
    private Pair<Integer,Integer> oldAction;
    private boolean waitOpponent=false;



    public static void main(String[] args) {
        EventQueue.invokeLater(()->{
            try {
                TrisGui window =new TrisGui();
                window.frame.setVisible(true);
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public TrisGui(){
        loadQtable();
        this.old_Qtable=q_table.clone();
        creationGui();
        bot= new Bot(gridFields,q_table);

    }
    private void loadQtable() {
        File file = new File(Costants.NAME_FILE_BOT);
        if (!file.exists()) {
            q_table=new Qtable(Costants.GRID_DIMX,Costants.GRID_DIMY,Costants.PLAYER_SYMBOL,Costants.BOT_SYMBOL,Costants.EMPTY_SYMBOL);
        }else {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                // Lettura dell'oggetto dal file
                q_table = (Qtable)ois.readObject();
                if (q_table.getGridDimX()!=Costants.GRID_DIMX || q_table.getGridDimY()!=Costants.GRID_DIMY || !(Costants.BOT_SYMBOL.equals(q_table.getSymbol1())||Costants.BOT_SYMBOL.equals(q_table.getSymbol2())) ||
                        !(Costants.PLAYER_SYMBOL.equals(q_table.getSymbol1())||Costants.PLAYER_SYMBOL.equals(q_table.getSymbol2())) ||!Costants.EMPTY_SYMBOL.equals(q_table.getEmptySymbol())) {
                    q_table = new Qtable(Costants.GRID_DIMX, Costants.GRID_DIMY, Costants.PLAYER_SYMBOL,Costants.BOT_SYMBOL,Costants.EMPTY_SYMBOL);
                    JOptionPane.showMessageDialog(null, "Qtable si riferisce ad una griglia differente da quella che si sta esaminando", "Notifica", JOptionPane.INFORMATION_MESSAGE);
                }
                else{
                    JOptionPane.showMessageDialog(null, "Qtable letta con successo!", "Notifica", JOptionPane.INFORMATION_MESSAGE);

                }
                ois.close();
            }catch(IOException | ClassNotFoundException e){
                JOptionPane.showMessageDialog(null, "Errore durante la lettura del file: ", "Problema", JOptionPane.ERROR_MESSAGE);

                System.err.println("Errore durante la lettura del file: " + e.getMessage());
                q_table=new Qtable(Costants.GRID_DIMX,Costants.GRID_DIMY,Costants.PLAYER_SYMBOL,Costants.BOT_SYMBOL,Costants.EMPTY_SYMBOL);
            }
        }
    }
    private void setFrame(){
        frame.setResizable(false);
        frame.setSize(Costants.GRID_DIMY*Costants.MUL_WIDTH_FRAME,Costants.GRID_DIMX*Costants.MUL_HEIGHT_FRAME
                +Costants.INITIAL_HEIGHT +Costants.RECTANGLE_DIMENSION +Costants.FONT_RESTART_DIMENSION);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.addKeyListener(this);
        frame.setFocusable(true);
    }
    private void setTitle(){

        lblTitle.setForeground(Color.BLACK);
        lblTitle.setFont(new Font("Tahoma",Font.BOLD, Costants.FONT_TITLE_DIMENSION));
        lblTitle.setBounds(Costants.INITIAL_TITLE_WIDTH,Costants.INITIAL_TITLE_HEIGHT,Costants.TITLE_WIDTH,Costants.TITLE_HEIGHT);

    }
    private void setInfo(){

        lblinfo.setFont(new Font("Tahoma",Font.BOLD, Costants.FONT_INFO_DIMENSION));
        //80 e lo spazio 120 alteza rrettangoli 40 dim carettere 42 della scritta e 20 spazio iniziale
        lblinfo.setBounds(Costants.INITIAL_TITLE_WIDTH,Costants.GRID_DIMX*Costants.MUL_HEIGHT_FRAME +Costants.INITIAL_HEIGHT +Costants.RECTANGLE_DIMENSION -
                Costants.FONT_TITLE_DIMENSION -Costants.TITLE_HEIGHT -Costants.FINAL_LBLINFO_HEIGHT,Costants.TITLE_WIDTH,Costants.TITLE_HEIGHT);
        lblinfo.setForeground(Costants.PLAYER_COLOR);
        lblinfo.setText("La prima azione è del player -> O");
    }
    private void createSetRestartInfo() {
        JLabel restartInfo = new JLabel("Restart with F5");
        restartInfo.setForeground(Color.BLACK);
        restartInfo.setFont(new Font("Tahoma",Font.BOLD, Costants.FONT_RESTART_DIMENSION));
        //80 e lo spazio 120 alteza rrettangoli 40 dim carettere 42 della scritta e 20 spazio iniziale
        restartInfo.setBounds(Costants.INITIAL_TITLE_WIDTH,Costants.GRID_DIMX*Costants.MUL_HEIGHT_FRAME +Costants.INITIAL_HEIGHT +Costants.RECTANGLE_DIMENSION -
                Costants.FONT_RESTART_DIMENSION -Costants.TITLE_HEIGHT -Costants.FINAL_RESTART_HEIGHT,Costants.TITLE_WIDTH,Costants.FONT_RESTART_DIMENSION);
        frame.getContentPane().add(restartInfo);
    }
    private void createSetMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem saveItem = new JMenuItem("Salva Qtable");
        saveItem.addActionListener(e -> {
            try {
                bot.saveData();
                JOptionPane.showMessageDialog(null, "Operazione completata con successo!", "Notifica", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Salvantaggio non riuscito riprovare", "Problema", JOptionPane.ERROR_MESSAGE);

            }
        });

        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
    }
    private void  setGrid(){
        for (int i=0; i<gridFields.length; i++){
            for (int j=0; j< gridFields[0].length;j++){
                gridFields[i][j] = new JButton(" ");
                //qunto ti sponsti dal punto 0,0 che è lo spigolo in alto a destra del frame e poi la dimesione del quadrato
                gridFields[i][j].setBounds((j*Costants.MUL_HEIGHT_FRAME)+Costants.INITIAL_TITLE_WIDTH,(i*Costants.MUL_HEIGHT_FRAME)+Costants.INITIAL_HEIGHT,Costants.RECTANGLE_DIMENSION,Costants.RECTANGLE_DIMENSION);
                gridFields[i][j].setFont(new Font("Tahoma",Font.BOLD, Costants.FONT_GRID_FIELDS_DIMENSION));
                gridFields[i][j].setBackground(Color.WHITE);
                gridFields[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {

                        JButton temp = (JButton) e.getComponent();
                        int x = ((temp.getY() - Costants.INITIAL_HEIGHT) / Costants.RECTANGLE_DIMENSION);
                        int y = ((temp.getX() - Costants.INITIAL_TITLE_WIDTH) / Costants.RECTANGLE_DIMENSION);

                        manageTris(x, y);

                        gridFields[x][y].setFocusable(false);


                    }
                });
                frame.getContentPane().add(gridFields[i][j]);
            }
        }
    }
    private void creationGui() {
        frame =new JFrame("Tris");
        setFrame();
        lblTitle =new JLabel("Tris");
        setTitle();
        frame.getContentPane().add(lblTitle);
        lblinfo = new JLabel("");
        setInfo();
        frame.getContentPane().add(lblinfo);
        createSetRestartInfo();
        createSetMenu();
        gridFields=new JButton[Costants.GRID_DIMX][Costants.GRID_DIMY];
        setGrid();
    }

    private void manageTris(int x, int y) {
        if (nMatch>2)
            lblinfo.setText("BotWin: "+botWin+" PlayerWin: "+playerWin+" Draw :"+draw);

        if(gridFields[x][y].getText().equals(" ")&&!isWinner(Costants.BOT_SYMBOL)&&!isWinner(Costants.PLAYER_SYMBOL)&&!fullGrid()){
            if (waitOpponent) {
                System.out.println("-----------ASPETTO LA MOSSA DELL'OPPONENTE---------------");
            }else {
                System.out.println("-----------MOSSA EFFETUATA DAL GIOCATORE-----------------");
            }
            System.out.println("\nVecchio Stato :\n"+bot.extractState());
            gridFields[x][y].setText(Costants.PLAYER_SYMBOL);
            gridFields[x][y].setForeground(Costants.PLAYER_COLOR);
            System.out.println("Nuovo Stato :\n"+bot.extractState());
            System.out.println("---------------------------------------------------");

            float rewardOppMove=0;
            if (isWinner(Costants.PLAYER_SYMBOL) ||  fullGrid()){
                if (isWinner(Costants.PLAYER_SYMBOL) ){
                    playerWin++;
                    rewardOppMove=Costants.LOSE_REWARD;
                    JOptionPane.showMessageDialog(null, "Hai vinto!!", "Notifica", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    draw++;
                    rewardOppMove=Costants.DRAW_REWARD;
                    JOptionPane.showMessageDialog(null, "Hai pareggiato!!", "Notifica", JOptionPane.INFORMATION_MESSAGE);
                }
                lblinfo.setText("BotWin: "+botWin+" PlayerWin: "+playerWin+" Draw :"+draw);

                String newState=bot.extractState();
                bot.updateQtable(oldState,oldAction,newState, rewardOppMove);
                waitOpponent=false;
                JOptionPane.showMessageDialog(null, "Bot reward: "+bot.getReward(), "Notifica", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (waitOpponent){
                String newState = bot.extractState();
                bot.updateQtable(oldState,oldAction,newState, rewardOppMove);
                waitOpponent=false;
                System.out.println("------------FINE MOSSA BOT---------------");
            }

            System.out.println("----------------MOSSA BOT---------------");
            oldState = bot.extractState();
            oldAction = bot.epsilonGreedyPolicy(oldState);
            System.out.println("Azione effetuata dal bot: " +oldAction);


            if (isWinner(Costants.BOT_SYMBOL) ||  fullGrid()){
                float rewardMyMove;
                if (isWinner(Costants.BOT_SYMBOL) ){
                    botWin++;
                    rewardMyMove=Costants.WIN_REWARD;
                    JOptionPane.showMessageDialog(null, "il bot ha vinto!!", "Notifica", JOptionPane.INFORMATION_MESSAGE);
                }else{
                    draw++;
                    rewardMyMove=Costants.DRAW_REWARD;
                    JOptionPane.showMessageDialog(null, "Hai pareggiato!!", "Notifica", JOptionPane.INFORMATION_MESSAGE);
                }
                lblinfo.setText("BotWin: "+botWin+" PlayerWin: "+playerWin+" Draw :"+draw);

                String newState=bot.extractState();
                bot.updateQtable(oldState,oldAction,newState, rewardMyMove);
                JOptionPane.showMessageDialog(null, "Bot reward: "+bot.getReward(), "Notifica", JOptionPane.INFORMATION_MESSAGE);

                System.out.println("----------FINE MOSSA BOT---------------");

                waitOpponent=false;
            }else {
                waitOpponent = true;
            }

        }

    }

    public  boolean fullGrid(){
        for (int i = 0; i < gridFields.length; i++) {
            for (int j = 0; j < gridFields[0].length; j++) {
                if(gridFields[i][j].getText().equals(" "))
                    return false;
            }
        }
        return  true;
    }
    public  boolean isWinner(String  s) {
        for (int i = 0; i < gridFields.length; i++) {
            for (int j = 0; j <  gridFields[0].length; j++) {
                if( !gridFields[i][j].getText().equals(" ") && gridFields[i][j].getText().equals(s)) {
                    int[] countV= {Costants.WIN_COUNT-1,Costants.WIN_COUNT-1,Costants.WIN_COUNT-1,Costants.WIN_COUNT-1};
                    boolean[] exitFlag= new boolean[4];
                    for (int t=1; t<Costants.WIN_COUNT;t++) {
                        // analizzo la colonna
                        if( !exitFlag[0] &&i+t<gridFields[0].length && !gridFields[i+t][j].getText().equals(" ")&&  gridFields[i+t][j].getText().equals(s)) countV[0]--;
                        else exitFlag[0]=true;
                        // analizzo la riga
                        if( !exitFlag[1] && j+t<gridFields[0].length &&!gridFields[i][j+t].getText().equals(" ")&& gridFields[i][j+t].getText().equals(s)) countV[1]--;
                        else exitFlag[1]=true;
                        // analizzo la la diagonale primaria
                        if( !exitFlag[2] && j+t< gridFields[0].length &&i+t<gridFields.length && !gridFields[i+t][j+t].getText().equals(" ")&& gridFields[i+t][j+t].getText().equals(s)) countV[2]--;
                        else exitFlag[2]=true;
                        // analizzo la diagonale secondaria
                        if( !exitFlag[3] && j-t>=0 &&i+t<gridFields.length&&!gridFields[i+t][j-t].getText().equals(" ")&& gridFields[i+t][j-t].getText().equals(s)) countV[3]--;
                        else exitFlag[3]=true;

                        if(exitFlag[0]&&exitFlag[1]&&exitFlag[2]&&exitFlag[3]) break;
                    }
                    if ( countV[0]==0 ||countV[1]==0 ||countV[2]==0||countV[3]==0) {
                        return true;
                    }

                }
            }

        }

        return false;

    }
    private void reload() {
        JOptionPane.showMessageDialog(null, "Has learned: "+q_table.hasLearned(old_Qtable)+ " delta voluto: "+Costants.DELTA, "Notifica", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("Has learned: "+q_table.hasLearned(old_Qtable)+ "delta voluto"+Costants.DELTA);
      //  System.out.println("Qtable ottenuta fino a questo momento: \n"+q_table.toString());
        old_Qtable=q_table.clone();
        for (int i=0; i<gridFields.length; i++)
            for(int j=0; j<gridFields[0].length; j++)
                gridFields[i][j].setText(" ");
        nMatch++;

        if(nMatch%2!=0){
            lblinfo.setText("La prima azione è del player -> O");
            lblTitle.setText("Tris Match:" + nMatch);
        }else{
            //mossa bot
            oldState = bot.extractState();
            oldAction = bot.epsilonGreedyPolicy(oldState);
            waitOpponent = true;
            lblinfo.setText("La seconda azione è del player -> O");
            lblTitle.setText("Tris Match:" + nMatch);
        }

    }

    /**
     * Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if(code==KeyEvent.VK_F5)
            reload();

    }

    /**
     * Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     *
     * @param e the event to be processed
     */
    @Override
    public void keyReleased(KeyEvent e) {

    }
}