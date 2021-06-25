package evaluation;
import state.State;
import constants.*;
public class AggressiveEvaluationFunction implements EvaluationFunction 
{   
     /**
     * @param currentState
     * @param i The x pos the player
     * @param j The y pos of the player
     * @param turn The turn
     * @param visited Initially all false. Used to not calculate same pieces more than once.
     * @return Number of opponent pieces that are under attack.
     */
    int attackCount(State currentState, 
                int i,
                int j, 
                int turn,
                boolean visited[][])
        {
            
            int ans = 0;
            int opponentI;
            int opponentJ;
            int jumpI;
            int jumpJ;
            
            //Simulate jump moves to calculate total number of opponent pieces under attack
            for(int move=0;move<Game.MOVESX[turn].length;move++)
            {
                opponentI = i+Game.MOVESX[turn][move];
                opponentJ = j+Game.MOVESY[turn][move];
                jumpI = opponentI + Game.MOVESX[turn][move];
                jumpJ = opponentJ + Game.MOVESY[turn][move];
                if(currentState.isOpponent(opponentI, opponentJ,turn) && 
                        currentState.isEmpty(jumpI,jumpJ))
                {
                    if(visited[opponentI][opponentJ] == false) {
                        ans++;
                        visited[opponentI][opponentJ] = true;
                    }
                    currentState.setBoardValueAt(jumpI,jumpJ,currentState.getBoardValueAt(i,j)); 
                    currentState.setBoardValueAt(i,j,PieceType.EMPTY); 
                    
                    char opponent = currentState.getBoardValueAt(opponentI,opponentJ);
                    currentState.setBoardValueAt(opponentI,opponentJ,PieceType.EMPTY);
                    
                    
                    ans+=attackCount(currentState,jumpI,jumpJ,turn,visited);
                    currentState.setBoardValueAt(i,j,currentState.getBoardValueAt(jumpI,jumpJ));
                    currentState.setBoardValueAt(jumpI,jumpJ,PieceType.EMPTY);  
                    currentState.setBoardValueAt(opponentI,opponentJ,opponent);
                    
                }
            }
            
            if(currentState.isKing(i,j,turn))
            {
                for(int move=0;move<Game.MOVESX[turn^1].length;move++)
                {
                    opponentI = i+Game.MOVESX[turn^1][move];
                    opponentJ = j+Game.MOVESY[turn^1][move];
                    jumpI = opponentI + Game.MOVESX[turn^1][move];
                    jumpJ = opponentJ + Game.MOVESY[turn^1][move];
                    if(currentState.isOpponent(opponentI, opponentJ,turn) && 
                            currentState.isEmpty(jumpI,jumpJ))
                    {

                        if(visited[opponentI][opponentJ] == false) {
                            ans++;
                            visited[opponentI][opponentJ] = true;
                        }

                        currentState.setBoardValueAt(jumpI,jumpJ,currentState.getBoardValueAt(i,j));
                        currentState.setBoardValueAt(i,j,PieceType.EMPTY);
                        
                        char opponent = currentState.getBoardValueAt(opponentI,opponentJ);
                        currentState.setBoardValueAt(opponentI,opponentJ,PieceType.EMPTY);
                        
                        
                        ans+=attackCount(currentState,jumpI,jumpJ,turn,visited);
                        
                        currentState.setBoardValueAt(i,j,currentState.getBoardValueAt(jumpI,jumpJ));
                        currentState.setBoardValueAt(jumpI,jumpJ,PieceType.EMPTY);
                        currentState.setBoardValueAt(opponentI,opponentJ,opponent);
                        
                    }
                }
            }
            return ans;
        }

        /**
         * @param state
         * @param turn
         * @return the evaluation function of the state.
         */
        @Override
        public double getEvaluation(State state,int turn)
        {
            double evaluation=0;
            int attack = 0;
            int attackO = 0;
            boolean visited[][] = new boolean[Game.BOARDSIZE][Game.BOARDSIZE];
            for(int i=0;i<Game.BOARDSIZE;i+=2)
            {
                for(int j=1;j<Game.BOARDSIZE;j+=2)
                {
                    if(state.isPlayerPiece(i, j, turn))
                    {
                        //Player piece here. All evaluation +ve
                        //+5 for pawn
                        evaluation+=5;
                        if(state.isKing(i, j,turn))
                        {
                            //+ 5 + 4 for king
                            evaluation+=4;
                        }
                        
                        
                        if(i==3 || i==4)
                        {
                            //Preference to center of the board
                            evaluation+=0.5;
                            if(j==2 || j==5)
                            {
                                //More preference to concentrated center
                                evaluation+=2;
                            }
                        }
                        if(i == state.getHomeRow(turn))
                        {
                            //Safe guard home pieces so that opponent doesnt become king
                            evaluation+=3;
                        }
                        //#pieces of opponent under attack
                        attack += attackCount(state, i, j, turn, visited);
                        
                        
                    }
                    else if(!state.isEmpty(i, j))
                    {
                        //Opponent piece. All evaluations -ve
                        //-5 if opponent pawn is present
                        evaluation-=5;
                        if(state.isKing(i, j,turn^1))
                        {
                            //-5 -4 if opponent king is present
                            evaluation-=4;
                        }
                        
                        
                        if(i==3 || i==4)
                        {
                            //-0.5 if opponent controls center
                            evaluation-=0.5;
                            
                            if(j==2 || j==5)
                            {
                                //-0.5 - 2 if opponent controls concentrated center.
                                evaluation-=2;
                            }
                        }
                            
                        if(i == state.getHomeRow(turn))
                        {
                            //-3 if opponent defends home row and doesnt allow to become king.
                            evaluation-=3;
                        }
                        //-#player pieces under attack
                        attackO += attackCount(state, i, j, turn^1, visited);
                    }
                }
            }
            
            
            //Repeat the above for remaining board cells. 
            for(int i=1;i<Game.BOARDSIZE;i+=2)
            {
                for(int j=0;j<Game.BOARDSIZE;j+=2)
                {
                    if(state.isPlayerPiece(i, j, turn))
                    {
                        evaluation+=5;
                        if(state.isKing(i, j,turn))
                        {
                            evaluation+=4;
                        }
                        
                        
                        if(i==3 || i==4)
                        {
                            evaluation+=0.5;
                            if(j==2 || j==5)
                            {
                                evaluation+=2;
                            }
                        }
                        if(i == state.getHomeRow(turn))
                        {
                            evaluation+=3;
                        }
                        attack += attackCount(state, i, j, turn, visited);
                        
                        
                    }
                    else if(!state.isEmpty(i, j))
                    {
                        evaluation-=5;
                        if(state.isKing(i, j,turn^1))
                        {
                            evaluation-=2.75;
                        }
                        
                        if(i==3 || i==4)
                        {
                            evaluation-=0.5;
                            if(j==2 || j==5)
                            {
                                evaluation-=2;
                            }
                        }
                        if(i == state.getHomeRow(turn))
                        {
                            evaluation-=3;
                        }
                        attackO += attackCount(state, i, j, turn^1, visited);
                        
                        
                    }
                }
            }

            
            if(turn == state.getTurn())
            {
                evaluation+=2*attack;
            }
            else evaluation-=2*attackO;
            return evaluation;
            
        }
 
}

