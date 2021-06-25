import agent.Master;
import java.io.IOException;
/**
 * @author Nikhil Chakravartula (nchakrav@usc.edu) (nikhilchakravartula@gmail.com)
 * The entry point to the application
 */
public class Checkers {

  public static void main(String[] args) {
    //Create a master and start the play
    
    try {
        Master master = new Master();
        master.startPlay();
       
    }
    catch(IOException ioe)
    {
      ioe.printStackTrace();
    }
    
  }
}
