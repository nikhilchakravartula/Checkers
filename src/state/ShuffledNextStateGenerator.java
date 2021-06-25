package state;

import java.util.ArrayList;
import java.util.Collections;
import constants.Game;
import constants.Mode;

/**
 * @author Nikhil Chakravartula (nchakrav@usc.edu) (nikhilchakravartula@gmail.com)
 * Generates next states and shuffles them
 */
public class ShuffledNextStateGenerator extends NextStateGenerator{

  public ArrayList<State> generateNextStates(State state,int mode,boolean shouldBuildMoves)
  {
      ArrayList<State> nextStates = new ArrayList<State>();
      
      //Generate jump moves
      for(int i=0;i<Game.BOARDSIZE;i++)
      {
          for(int j=0;j<Game.BOARDSIZE;j++)
          {
              if(state.isPlayerPiece(i,j))
              {
                  generateJumpMove(state,i,j,nextStates,mode,shouldBuildMoves);
                  if(mode==Mode.SINGLE && nextStates.size()!=0)
                      return nextStates;
              }
          }
      }
      
      //Generate regular moves only if a jump isn't possible.
      if(nextStates.size() == 0) {
          for(int i=0;i<Game.BOARDSIZE;i++)
          {
              for(int j=0;j<Game.BOARDSIZE;j++)
              {
                  if(state.isPlayerPiece(i, j))
                  {
                      generateRegularMove(state,i,j,nextStates,mode,shouldBuildMoves);        
                      if(mode==Mode.SINGLE && nextStates.size()!=0)
                          return nextStates;
                  }
              }
          }
          
      }
      Collections.shuffle(nextStates);

      return nextStates;
  }     

}
