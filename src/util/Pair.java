package util;

import constants.*;

/**
 * @author Nikhil Chakravartula (nchakrav@usc.edu) (nikhilchakravartula@gmail.com)
 * Used to store a pair of integers.
 */
public class Pair
{
    int x,y;
    public Pair(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public boolean equals(Object obj)
    {
        if(obj==null)
            return false;
        if(this==(Pair)obj)
            return true;
        Pair p = (Pair)obj;
        if(this.x == p.x && this.y == p.y)
        {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return x*2 + y*3;
    }
    public String toString()
    {
        
        return (char)('a'+y) +""+ (Game.BOARDSIZE-x);
    }

}

