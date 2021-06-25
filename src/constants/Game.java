package constants;



/**
 * @author Nikhil Chakravartula (nchakrav@usc.edu) (nikhilchakravartula@gmail.com)
 */
public class Game
{
    public final static int BOARDSIZE = 8;
    //Possibe moves across X and Y axes
    public final static int MOVESX[][] = {{1,1},{-1,-1}};
    public final static int MOVESY[][] = {{-1,1},{-1,1}};
    
    
    public final static int[] BLACKMOVESX = {1,1};
    public final static int[] BLACKMOVESY = {-1,1};

    public final static int[] WHITEMOVESX = {-1,-1};
    public final static int[] WHITEMOVESY = {-1,1};

}