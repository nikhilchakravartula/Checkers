package agent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import state.*;
import constants.*;
import evaluation.AggressiveEvaluationFunction;

/**
 * @author Nikhil Chakravartula (nchakrav@usc.edu) (nikhilchakravartula@gmail.com)
 * Master that repeatedly calls an agent to play the game until a winner is decided.
 */
public class Master
{
    State state;
    BufferedWriter writer;
    BufferedReader reader;
    BufferedReader readerMoves;
    BufferedReader readerIn;
    BufferedWriter logger;
    String readPath = "./output.txt";
    String writePath = "./input.txt";
    String blackInitPath = "./init/black.txt";
    String whiteInitPath = "./init/white.txt";
    int winner;

    int turn;
    
    
    public Master()
    {

        try
        {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(readPath)));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writePath)));
            logger = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("./logs.txt")));
            turn =  (int) Math.round( Math.random() )  ;
            BufferedReader def ;
            
            String outputString="";
            def = new BufferedReader(new InputStreamReader(new FileInputStream(blackInitPath)));
            if(turn == Turn.BLACKTURN)
            {
                
                outputString+="GAME\nBLACK\n300\n";
            }
            else {
                outputString+="GAME\nWHITE\n300\n";
            }
            
            writer.write(outputString);
            
            
            String s;
            def.close();
            //Initial configuration of the board.
            def = new BufferedReader(new InputStreamReader(new FileInputStream(blackInitPath)));
            while((s = def.readLine() ) != null)
            {
                writer.write(s);
                writer.write("\n");
            }
            writer.flush();
            writer.close();
            def.close();
            winner=-1;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    /**
     * @return
     * @throws IOException
     * Apply the move present in output.txt file
     */
    boolean applyMove() throws IOException
    {
        readerMoves = new BufferedReader(new InputStreamReader(new FileInputStream(readPath)));
        readerIn = new BufferedReader(new InputStreamReader(new FileInputStream(writePath)));
        readerIn.readLine();
        readerIn.readLine();
        readerIn.readLine();
        
        state = new State(readerIn, turn);
        
        
        String move;
        
        int i,j;
        int x,y;
        i=j=x=y=-1;
        boolean moveApplied = false;
        while( (move = readerMoves.readLine())!= null )
        {
            logger.write(move);
            logger.write("\n");
            moveApplied = true;
            String[] moves= move.split(" ");
            if(moves[0].equals("E"))
            {
                //Regular move
                j = (int)(moves[1].charAt(0)-'a');
                i = Game.BOARDSIZE - (moves[1].charAt(1)-'0');
                
                y = (int)(moves[2].charAt(0)-'a');
                x = Game.BOARDSIZE - (moves[2].charAt(1)-'0');
                //Update board. Moved from (i,j) to (x,y)
                state.setBoardValueAt(x,y, state.getBoardValueAt(i,j));;
                state.setBoardValueAt(i,j,PieceType.EMPTY);
                
            }
            else if(moves[0].equals("J"))
            {
                //Jump move
                j = (int)(moves[1].charAt(0)-'a');
                i = Game.BOARDSIZE - (moves[1].charAt(1)-'0');
                
                y = (int)(moves[2].charAt(0)-'a');
                x = Game.BOARDSIZE - (moves[2].charAt(1)-'0');
                //Update board after making this jump move
                state.setBoardValueAt(x,y,state.getBoardValueAt(i,j));
                state.setBoardValueAt(i,j,PieceType.EMPTY);
                state.setBoardValueAt((i+x)/2,(y+j)/2,PieceType.EMPTY);
                
                
            }
            if(moveApplied) {
                //Check if the piece becomes the king after the move is applied, 
                if(x==0 || x==Game.BOARDSIZE-1)
                {
                    state.setBoardValueAt(x,y, Character.toUpperCase(state.getBoardValueAt(x,y))); 
                }
            }
            
            
        }
        readerMoves.close();
        readerIn.close();
        return moveApplied;
        
        
    }
    /**
     * @throws Exception
     * Start the play and repeatedly call the respective players to execute moves
     * until the game is finished.
     */
    public void startPlay() throws IOException
    {
        Agent black ;
        Agent white ;
        String blackTurn = "BLACK";
        String whiteTurn = "WHITE";
        String nextTurn="";
        double blackTime=0;
        double whiteTime = 0;
        double maxBlack = Double.MIN_VALUE;
        double maxWhite = Double.MIN_VALUE;
        double timeTaken = 0;
        double remainingTime = 0;
        
        for(;;)
        {
            if(turn == Turn.BLACKTURN)
            {
                //let black play, keep track of time.
                logger.append("\nBLACK PLAYING\n");
                black = new Agent(writePath,readPath,
                        new ShuffledNextStateGenerator(),new AggressiveEvaluationFunction());
                
                long startTime = System.nanoTime();
                black.play();
                long endTime = System.nanoTime();
                blackTime+=(timeTaken=(endTime-startTime)/1e9);
                nextTurn = whiteTurn;
                maxBlack = Math.max(maxBlack, (endTime-startTime)/1e9);
                remainingTime = black.remainingTime - timeTaken;
                logger.append("black time :"+(blackTime)+"\n");
                
            }
            else if(turn == Turn.WHITETURN)
            {
                //let white play, keep track of time.
                logger.append("\nWHITE PLAYING\n");
                white= new Agent(writePath,readPath,
                        new ShuffledNextStateGenerator(),new AggressiveEvaluationFunction());
                
                long startTime = System.nanoTime();
                white.play();
                long endTime = System.nanoTime();
                whiteTime+=(timeTaken=(endTime-startTime)/1e9);
                nextTurn = blackTurn;
                maxWhite = Math.max(maxWhite, (endTime-startTime)/1e9);
                remainingTime = white.remainingTime - timeTaken;
                logger.append("white time :"+(whiteTime)+"\n");
            }
            //If a move can't be applied, opponent wins
            if(applyMove()==false || remainingTime < 0)
            {
                winner = turn^1;
                break;
            }
            
            logger.write(state.toString()+"\n");
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(writePath)));
            //Write the board for the next player to use
            String opString ="GAME\n"+nextTurn+"\n"+remainingTime+"\n"+state.toString(); 
            writer.write(opString);
            writer.flush();
            writer.close();
            turn = turn^1;
            
            logger.flush();
            
            
        }
        if(winner!=-1)
        {
          if(turn==Turn.BLACKTURN)
          {
            System.out.println("BLACK wins");
          }
          else System.out.println("WHITE wins");
        }
        

    }
    
}
