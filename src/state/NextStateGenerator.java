package state;

import java.util.ArrayList;
import java.util.LinkedList;

import constants.*;
import util.*;

/**
 * @author Nikhil Chakravartula (nchakrav@usc.edu) (nikhilchakravartula@gmail.com)
 * Provides contract to generate next/adjacent/successor states. 
 */
public abstract class NextStateGenerator
{
        /**
         * @param moves
         * @return a string that represents a series of jump moves.
         */
        String buildJumpMoves(LinkedList<Pair> moves)
        {
            StringBuilder sb = new StringBuilder();

            Pair prev = null;
            for(Pair move:moves)
            {
                if(prev == null)
                {
                    prev = move;
                    continue;
                }
                sb.append('J');
                sb.append(' ');
                sb.append(prev.toString());
                sb.append(' ');
                sb.append(move.toString());
                sb.append("\n");
                prev = move;
            }
            //Delete extra \n at the end.
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
        }
        
        
        /**
         * @param x current x position
         * @param y current y position
         * @param newX the newX to move to
         * @param newY the newY to move to
         * @return a string that represents a move from (x,y) to (newX,newY)
         */
        String buildRegularMoves(int x, int y, int newX, int newY)
        {
            StringBuilder sb = new StringBuilder();
                sb.append('E');
                sb.append(' ');
                sb.append((char)('a'+y) +""+ (Game.BOARDSIZE-x));
                sb.append(' ');
                sb.append((char)('a'+newY) +""+ (Game.BOARDSIZE-newX));
                sb.append("\n");
    
            //Delete extra \n at the end.
            sb.deleteCharAt(sb.length()-1);
            return sb.toString();
            
        }
        
        
        /**
         * @param state the current state
         * @param i the current x position on the board
         * @param j the current y position on the board
         * @param moves helper list that is used in backtracking 
         * @param nextStates out variable to keep track of nextStates
         * @param jumpCount Keep track of count 
         * @param mode  Game mode. If SINGLE, break when a single jump sequence is generated.
         * @param shouldBuildMoves true if string of moves need to be generated.
         * @return true if game mode is SINGLE. Used to break the recursion sooner.
         */
        boolean jumpMoveHelper(State state,
                int i,
                int j,
                LinkedList<Pair> moves,
                ArrayList<State> nextStates,
                int jumpCount,
                int mode,
                boolean shouldBuildMoves)
        {
            
            int opponentI;
            int opponentJ;
            int jumpI;
            int jumpJ;

            boolean jumpPossible = false;
            

                //Jump moves for regular pieces.
                for(int move=0;move<Game.MOVESX[state.turn].length;move++)
                {
                    //opponent position
                    opponentI = i+Game.MOVESX[state.turn][move];
                    opponentJ = j+Game.MOVESY[state.turn][move];
                    
                    //jump position
                    jumpI = opponentI + Game.MOVESX[state.turn][move];
                    jumpJ = opponentJ + Game.MOVESY[state.turn][move];
                    
                    //If adjacent cell is opponent and jump position is empty, make the jump
                    if(state.isOpponent(opponentI, opponentJ) && 
                            state.isEmpty(jumpI,jumpJ))
                    {
                        jumpPossible = true;
                        
                        //add next position of the player to moves.
                        moves.addLast(new Pair(jumpI,jumpJ));

                        //Update the board after this jump move
                        state.board[jumpI][jumpJ] = state.board[i][j];
                        state.board[i][j] = PieceType.EMPTY;
                        
                        char opponent = state.board[opponentI][opponentJ];
                        state.board[opponentI][opponentJ]=PieceType.EMPTY;
                        
                        //recurse, and return true if a move is generated and mode is SINGLE.
                        if(jumpMoveHelper(state,jumpI,jumpJ,moves,nextStates,jumpCount+1,mode,shouldBuildMoves) &&
                                mode == Mode.SINGLE)
                        {
                            return true;
                        }
                        //backtrack after the recursion
                        moves.removeLast();
                        state.board[i][j] = state.board[jumpI][jumpJ];
                        state.board[jumpI][jumpJ] =  PieceType.EMPTY;
                        state.board[opponentI][opponentJ] = opponent;
                        
                    }
                }
                
                //Jump moves for kings.
                if(state.isKing(i,j))
                {
                    for(int move=0;move<Game.MOVESX[state.turn^1].length;move++)
                    {
                        opponentI = i+Game.MOVESX[state.turn^1][move];
                        opponentJ = j+Game.MOVESY[state.turn^1][move];
                        jumpI = opponentI + Game.MOVESX[state.turn^1][move];
                        jumpJ = opponentJ + Game.MOVESY[state.turn^1][move];
                        if(state.isOpponent(opponentI, opponentJ) && 
                                state.isEmpty(jumpI,jumpJ))
                        {
                            //If adjacent cell is opponent and jump position is empty, make the jump
                            jumpPossible = true;
                            
                            //add next position of the player to moves.
                            moves.addLast(new Pair(jumpI,jumpJ));

                            //Update board after the jump is made.
                            state.board[jumpI][jumpJ] = state.board[i][j];
                            state.board[i][j] =  PieceType.EMPTY;
                            
                            char opponent = state.board[opponentI][opponentJ];
                            state.board[opponentI][opponentJ]=  PieceType.EMPTY;
                            
                            //recurse, and return true if a move is generated and mode is SINGLE.
                            if(jumpMoveHelper(state,jumpI,jumpJ,moves,nextStates,jumpCount+1,mode,shouldBuildMoves) &&
                                    mode == Mode.SINGLE)
                            {
                                return true;
                            }
                            //backtrack
                            moves.removeLast();
                            state.board[i][j] = state.board[jumpI][jumpJ];
                            state.board[jumpI][jumpJ] =  PieceType.EMPTY;
                            state.board[opponentI][opponentJ] = opponent;
                            
                        }
                    }
                }

                
                
                if(jumpCount != 0 && jumpPossible == false)
                {
                    //a jump is possible
                    State nextState = 
                        createNextJumpState(state,i,j);
                    if(shouldBuildMoves)
                    {
                        nextState.moveString = buildJumpMoves(moves);
                    }
                    
                    nextStates.add(nextState);
                        
                    //If mode SINGLE one possible move is sufficient. Return
                    if(mode == Mode.SINGLE)
                    {
                        return true;
                    }
                }


            //Jump not possible from i,j
            return false;
            
        }
        
        
        
        /**
         * @param state The current state
         * @param i The x position of the player
         * @param j The y position of the player
         * @param nextStates out variable to store next states.
         * @param mode SINGLE or GAME
         * @param shouldBuildMoves true if moves are to be encoded as string
         */
        void generateJumpMove(State state, 
                int i, 
                int j,
                ArrayList<State> nextStates,
                int mode,
                boolean shouldBuildMoves)
        {
            LinkedList<Pair> moves = new LinkedList<Pair>();
            //add current position
            moves.add(new Pair(i,j));
            //Generate all jump moves. Successor states are stored in nextStates
            jumpMoveHelper(state, i, j,moves, nextStates,0,mode,shouldBuildMoves);      
        }
        
        /**
         * @param state The current state
         * @param i The x position of the player
         * @param j The y position of the player
         * @param nextStates Out variable to store next states.
         * @param mode GAME or SINGLE
         * @param shouldBuildMoves true if moves are to be encoded as string
         */
        void generateRegularMove(State state, 
                int i, 
                int j,
                ArrayList<State> nextStates,
                int mode,
                boolean shouldBuildMoves)
        {

                //Generate non-jump next states.
                for(int move=0;move<Game.MOVESX[state.turn].length;move++)
                {
                    if(state.isEmpty(i+Game.MOVESX[state.turn][move], j+Game.MOVESY[state.turn][move]))
                    {
                        //Can move only if adjacent cell is empty
                        State nextState = createNextRegularState(state,
                                i,
                                j,
                                i+Game.MOVESX[state.turn][move], 
                                j+Game.MOVESY[state.turn][move],
                                shouldBuildMoves
                                );
                        nextStates.add(nextState);
                        
                        if(mode==Mode.SINGLE)
                            return;
                    }
                }
                
                //If king, generate non-jump states by allowing backward moves.
                if(state.isKing(i,j))
                {
                    for(int move=0;move<Game.MOVESX[state.turn^1].length;move++)
                    {
                        
                        if(state.isEmpty(i+Game.MOVESX[state.turn^1][move], j+Game.MOVESY[state.turn^1][move]))
                        {
                            //Can move only if adjacent state is empty

                            State nextState = createNextRegularState(state,
                                    i,
                                    j,
                                    i+Game.MOVESX[state.turn^1][move], 
                                    j+Game.MOVESY[state.turn^1][move],
                                    shouldBuildMoves
                                    );
                            nextStates.add(nextState);
                            if(mode==Mode.SINGLE)
                                return;
                        }
                    }
                    
                }
        }
        
        
        /**
         * @param currentState
         * @param i The x pos of the player
         * @param j The y pos of the player
         * @return a new state with the player in (i,j) position
         */
        State createNextJumpState(State currentState,int i,int j)
        {
        
            State nextState = new State(currentState, currentState.turn^1);
            //Crown the king if any
            if(currentState.turn == Turn.BLACKTURN && i==Game.BOARDSIZE-1)
            {
                nextState.board[i][j]='B';
            }
            else if(currentState.turn ==Turn.WHITETURN && i==0)
            {
                nextState.board[i][j]='W';
            }
            return nextState;
        }
        
        /**
         * @param currentState
         * @param x The x pos of the player 
         * @param y The y pos of the player
         * @param newX The x pos of the player after he made the move
         * @param newY The y pos of the player after he made the move
         * @param shouldBuildMoves true if moves are to be encoded in string
         * @return a new state with the player in (newX,newY) position
         */
        State createNextRegularState(State currentState, 
                int x, 
                int y, 
                int newX, 
                int newY,
                boolean shouldBuildMoves
                )
        {
            
            
            State nextState = new State(currentState, currentState.turn^1);
            
            if(shouldBuildMoves)
            {
                nextState.moveString = buildRegularMoves(x,y,newX,newY);
            }
            
            nextState.board[newX][newY] 
                    =currentState.board[x][y];
            
            nextState.board[x][y]= PieceType.EMPTY;
            
            //Crown kings if newX is at either homes of the board.
            if(currentState.turn == Turn.BLACKTURN && newX == Game.BOARDSIZE-1)
            {
                nextState.board[newX][newY] 
                        = PieceType.BLACKKING;
            }
            if(currentState.turn==Turn.WHITETURN && newX == 0)
            {
                nextState.board[newX][newY] 
                        = PieceType.WHITEKING;
            }
            return nextState;

        }
        
        
        /**
         * @param state The current state
         * @param mode SINGLE or GAME
         * @param shouldBuildMoves true if moves are to be encoded as string
         * @return an array of successor/next/child states.
         */
        public abstract ArrayList<State> generateNextStates(State state,int mode,boolean shouldBuildMoves);
}
