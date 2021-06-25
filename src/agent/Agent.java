package agent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import evaluation.*;
import state.*;
import constants.*;

/**
 * @author Nikhil Chakravartula (nchakrav@usc.edu) (nikhilchakravartula@gmail.com)
 * The game playing agent
 */
public class Agent
{
    EvaluationFunction eval;
    String inputPath;
    String outputPath;
    int mode;
    BufferedReader br;
    BufferedWriter bw;
    int turn;
    State currentState;
    double remainingTime;
    NextStateGenerator nextStateGenerator;

    
    /**
     * @param inputPath
     * @param outputPath
     * @param nextStateGenerator
     * @param eval
     */
    Agent(String inputPath,String outputPath,NextStateGenerator nextStateGenerator,EvaluationFunction eval)
    {
        try {
            this.inputPath = inputPath;
            this.outputPath = outputPath;
            br = new BufferedReader(new InputStreamReader(new FileInputStream(inputPath)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
            
            String modeString = br.readLine();
            modeString = modeString.trim();
            
            if(modeString.equalsIgnoreCase("SINGLE"))
            {
                mode = Mode.SINGLE;
            }
            else mode = Mode.GAME;
            String turn = br.readLine();
            turn.trim();
            remainingTime = Double.parseDouble(br.readLine());
            
            if(turn.equalsIgnoreCase("WHITE"))
            {
                 currentState = new State(br,Turn.WHITETURN);
                 this.turn = Turn.WHITETURN;
            }
            else
            {
                 currentState = new State(br,Turn.BLACKTURN);
                 this.turn = Turn.BLACKTURN;
            }
            
            this.nextStateGenerator = nextStateGenerator;
            this.eval = eval;
            br.close();
        }
        catch(IOException ioe)
            {
                ioe.printStackTrace();
                System.exit(0);
            }
    }
    
    /**
     * @param time
     * @return the maximum depth that can be searched based on the remaining time
     */
    int getDepth(double time)
    {
        if(time >= 25.0)
        {
            return 10;
        }
        else if(time>=10)
        {
            return 5;
        }
        else if(time >=5)
        {
            return 3;
        }
        else return 1;
    }
    
    /**
     * @param state the current state
     * @param alpha 
     * @param beta
     * @param turn
     * @param depth max depth that can be searched
     * @param max whether this is the max node or the min node
     * @param shouldBuildMoves
     * @return the best successor state possible from state using minimax and alpha-beta pruning.
     */
    State minimax(State state,
            double alpha, 
            double beta,
            int turn,
            int depth,
            int max,
            boolean shouldBuildMoves)
    {
        
        //Generate all next states
        ArrayList<State> nextStates =nextStateGenerator.
                generateNextStates(state,Mode.GAME,shouldBuildMoves);
        //Base case
        if(nextStates.size() == 0)
        {
            if(turn==state.getTurn())
            {
                state.setEvaluation(Integer.MIN_VALUE);
            }
            else state.setEvaluation(  Integer.MAX_VALUE);
            return state;
        }
        
        //Max depth reached
        if(depth ==0)
        {
            state.setEvaluation(eval.getEvaluation(state, turn));
            return state;
        }

        State bestStateToGoNext = null;
        if(max==1)
        {
            //Max node. 
            state.setEvaluation(-Double.MAX_VALUE);
            
            //for all the next states, find the best possible evaluation
            for(State nextState:nextStates)
            {
                minimax(nextState,alpha,beta,turn,depth-1,max^1,false);
                if(state.getEvaluation() < nextState.getEvaluation())
                {
                    state.setEvaluation(nextState.getEvaluation());
                    bestStateToGoNext = nextState;
                }

                //Update alpha
                if(state.getEvaluation() > alpha)
                {
                    alpha = state.getEvaluation();
                }
                
                //alpha-beta pruning
                if(alpha >= beta)
                {
                    break;
                }
                
            }
            return bestStateToGoNext;
            

            
        }
        else
        {
            //min node
            state.setEvaluation(Double.MAX_VALUE);
            //for all the next states, find the best possible evaluation
            for(State nextState:nextStates)
            {
                
                minimax(nextState,alpha,beta,turn,depth-1,max^1,false);
                if(state.getEvaluation() > nextState.getEvaluation())
                {
                    state.setEvaluation(nextState.getEvaluation());
                    bestStateToGoNext = nextState;
                }
                //Update beta
                if(state.getEvaluation() < beta)
                {
                    beta = state.getEvaluation();
                }
                //Alpha beta pruning
                if(alpha >= beta)
                {
                    break;
                }
                
            }
            return bestStateToGoNext;
        }
        
        
    }
    
    
  
    /**
     * @throws IOException
     * Read the current state from file, generate best possible move, write this move to the output.
     */
    void play() throws IOException
    {
        
        State nextState;
        if(mode==Mode.SINGLE)
        {
            //Write a single move randomly.
            ArrayList<State> nextStates = 
                    nextStateGenerator.generateNextStates(currentState, mode,true);
            nextState = nextStates.get(0);
            bw.write(nextState.getMoveString().toString());
            bw.flush();
            bw.close();
            
        }
        else{
            //Find best state
            nextState = minimax(this.currentState,
                -Double.MAX_VALUE, 
                Double.MAX_VALUE,
                this.turn,
                getDepth(remainingTime),
                1,
                true);
            //If no moves possible, return
            if(nextState.getMoveString()==null)
            {
                return ;
            }
            //Write the next state to the output file.
            bw.write(nextState.getMoveString().toString());
            bw.flush();
            bw.close();
        }

    }
    
}
