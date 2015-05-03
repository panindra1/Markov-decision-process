/**
 * Created by darshan on 5/2/15.
 */
public class Environment {
    Cell[][] environment;

    public  Environment(Cell[][] environment)
    {
        this.environment = environment;
    }

    public Cell[][] getEnvironment()
    {
        return environment;
    }

    public Cell[][] getEnvironmentCopy()
    {
        Cell[][] copy = new Cell[environment.length][environment[0].length];
        for(int i = 0 ; i < environment.length ; i ++)
        {
            copy[i] = new Cell[environment[i].length];
            for(int j = 0 ; j < environment[i].length ; j ++)
            {
                copy[i][j] = environment[i][j];
            }
        }
        return copy;
    }

    @Override
    public String toString()
    {
        StringBuffer out = new StringBuffer();
        for(int x = 0 ; x < environment.length ; x ++)
        {
            for(int y = 0 ; y < environment[x].length ; y ++)
            {
                out.append(environment[x][y].getValue() + "(" + environment[x][y].getDirection() + ")" + "\t");
            }
            out.append("\n");
        }
        return out.toString();
    }
}
