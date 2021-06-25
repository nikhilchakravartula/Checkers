package state;

import java.io.BufferedReader;
import java.io.IOException;
import constants.*;

/**
 * @author Nikhil Chakravartula (nchakrav@usc.edu) (nikhilchakravartula@gmail.com)
 * Encodes state information of the board.
 */
public class State
{
    //8*8 board
    char[][] board;
    int turn;
    //The evaluation of the current state
    double evaluation;
    
    //Fields used to store metadata of the state.
    int noBlacks;
    int noWhites;
    int noWhiteKings;
    int noBlackKings;
    //Moves to get to this state
    String moveString;
    
    public String getMoveString() {
      return moveString;
    }

    public void setMoveString(String moveString) {
      this.moveString = moveString;
    }

    /**
     * @param br The handle to the input file which has the board.
     * @param turn WHITE or BLACK
     */
    public State(BufferedReader br, int turn)
    {
        this(turn);
        try 
        {
            for(int i=0;i<Game.BOARDSIZE;i++)
            {
                String line = br.readLine();
                for(int j=0;j<Game.BOARDSIZE;j++)
                {
                    board[i][j]=line.charAt(j);
                    if(isBlack(i, j))
                    {
                        if(isBlackKing(i, j))
                            noBlackKings++;
                        noBlacks++;
                    }
                    else if(isWhite(i, j))
                    {
                        if(isWhite(i, j))
                            noWhiteKings++;
                        noWhites++;
                    }
                    
                    
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public State(int turn)
    {
        board = new char[Game.BOARDSIZE][Game.BOARDSIZE];
        this.turn = turn;
        evaluation = 0;
        noBlacks = noWhites = noBlackKings = noWhiteKings =0;
    }
    

    /**
     * @param currentState The state to be replicated
     * @param turn The player turn for this state
     */
    public State(State currentState,int turn)
    {
        this(turn);
        for(int i=0;i<Game.BOARDSIZE;i++)
        {
            for(int j=0;j<Game.BOARDSIZE;j++)
            {
                board[i][j] = currentState.board[i][j];
                if(isBlack(i, j))
                {

                    if(isBlackKing(i, j))
                        noBlackKings++;
                    noBlacks++;
                }
                else if(isWhite(i, j))
                {
                    if(isWhite(i, j))
                        noWhiteKings++;
                    noWhites++;
                }
            }
        }
    }
    public int calculateEvaluation()
    {
        return 0;
    }
    
    public char getBoardValueAt(int i, int j)
    {
      return board[i][j];
    }
    
    public double getEvaluation()
    {
        return evaluation;
    }
    
   
    /**
     * @param turn
     * @return the home row of the player.
     */
    public int getHomeRow(int turn)
    {
        if(turn == Turn.BLACKTURN)
            return 0;
        else return Game.BOARDSIZE-1;
    }
    
    public int getTurn()
    {
      return turn;
    }
    
    /**
     * @param i
     * @param j
     * @return true if (i,j) in the board is black
     */
    public boolean isBlack(int i, int j)
    {
        return isValid(i,j) && (board[i][j]=='b' || board[i][j]=='B');
    }
    /**
     * @param i
     * @param j
     * @return true if (i,j) in the board is a black king.
     */
    public boolean isBlackKing(int i, int j)
    {
        return isValid(i,j) &&  board[i][j]=='B';
    }
    
    /**
     * @param i
     * @param j
     * @return  true if (i,j) is empty in the board.
     */
    public boolean isEmpty(int i,int j)
    {
        return ( isValid(i,j) && board[i][j]== PieceType.EMPTY);
    }
    /**
     * @param i
     * @param j
     * @return true if (i,j) in the board is a king, black or white.
     */
    public boolean isKing(int i, int j)
    {
        return isBlackKing(i, j) || isWhiteKing(i, j);
    }
    /**
     * @param i
     * @param j
     * @param turn
     * @return true if (i,j) in the board has the current player's king, given the player turn.
     */
    public boolean isKing(int i, int j, int turn)
    {
        if(turn==Turn.BLACKTURN)
        {
            return isBlackKing(i, j);
        }
        else return isWhiteKing(i, j);
    }
    
    /**
     * @param i
     * @param j
     * @return true if (i,j) in the board has an opponent piece.
     */
    public boolean isOpponent(int i,int j)
    {
        if(turn==Turn.BLACKTURN)
        {
            return isWhite(i, j);
        }
        
        if(turn==Turn.WHITETURN)
        {
            return isBlack(i, j);
        }
        return false;
        
    }
    /**
     * @param i
     * @param j
     * @param turn
     * @return true if (i,j) has an opponent piece, given the current player turn.
     */
    public boolean isOpponent(int i, int j, int turn)
    {
        if(turn==Turn.BLACKTURN)
        {
            return isWhite(i,j);
        }
        
        else return isBlack(i,j);
    }
    
    
    /**
     * @param i
     * @param j
     * @return true if (i,j) in the board has the current player's piece. 
     */
    public boolean isPlayerPiece(int i, int j)
    {
        if(turn == Turn.BLACKTURN)
        {
            return isBlack(i, j);
        }
        else return isWhite(i, j);
    }
    
    /**
     * @param i
     * @param j
     * @param turn
     * @return true if (i,j) in the board has the current player's piece, given the player turn.
     */
    public boolean isPlayerPiece(int i, int j, int turn)
    {
        if(turn == Turn.BLACKTURN)
        {
            return isBlack(i,j);
        }
        else return isWhite(i,j);
    }
    /**
     * @param i
     * @param j
     * @return true if (i,j) is a valid position in the board.
     */
    public boolean isValid(int i, int j)
    {
        return (i>=0 && j>=0 && i< Game.BOARDSIZE && j<Game.BOARDSIZE);
    }
    /**
     * @param i
     * @param j
     * @return true if (i,j) in the board is white
     */
    public boolean isWhite(int i,int j)
    {
        return isValid(i,j) && (board[i][j]=='w' || board[i][j]=='W');
        
    }

    /**
     * @param i
     * @param j
     * @return true if (i,j) in the board is a white king.
     */
    public boolean isWhiteKing(int i, int j)
    {
        return isValid(i,j) &&  board[i][j]=='W';
        
    }
    
    public void setBoardValueAt(int i,int j, char value)
    {
      board[i][j]=value;
    }
    
    public void setEvaluation(double evaluation) {
      this.evaluation = evaluation;
    }
    public String toString()
    {   
        StringBuilder s = new StringBuilder();
        for(int i=0;i<Game.BOARDSIZE;i++)
        {
            for(int j=0;j<Game.BOARDSIZE;j++)
            {
                s.append(board[i][j]);
            }
            s.append("\n");
        }
        return s.toString();
    }
    
}
