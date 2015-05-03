import java.io.*;

/**
 * Created by darshan on 5/1/15.
 */
public class EnvironmentReader {
    BufferedReader br;
    File environmentFile;
    Environment environment;
    int length, width;
    int startX = 0;
    int startY = 0;


    public EnvironmentReader(File file) throws IOException {
        init(file);
    }

    private void init(File file) throws IOException {
        this.environmentFile = file;
        br = new BufferedReader(new FileReader(environmentFile));
        getDimensions();
        read();
    }

    private void read() throws IOException {
        Cell[][] environmentCells;
        br = new BufferedReader(new FileReader(environmentFile));
        environmentCells = new Cell[length][width];
        for(int i = 0 ; i < length ; i ++)
        {
            environmentCells[i] = new Cell[width];
            String line = br.readLine();
            for(int j = 0 ; j < width ; j ++)
            {
                char character = line.charAt(j);
                if(character == '%')
                {
                    environmentCells[i][j] = new Cell(CellType.WALL, null, 0f);
                }
                else if(character == ' ')
                {
                    environmentCells[i][j] = new Cell(CellType.BLANK, null, 0f);
                }
                else if(character == 'R')
                {
                    environmentCells[i][j] = new Cell(CellType.REWARD, null, CellType.REWARD.value);
                }
                else if(character == 'P')
                {
                    environmentCells[i][j] = new Cell(CellType.PENALTY, null, CellType.PENALTY.value);
                }
                else if(character == 'S')
                {
                    startX = j;
                    startY = i;
                    environmentCells[i][j] = new Cell(CellType.BLANK, null, 0f);
                }
            }
        }
        environment = new Environment(environmentCells);
    }

    private void getDimensions() throws IOException {
        String line = br.readLine();
        width = line.length();
        int i = 1;
        while(br.readLine() != null)
        {
            i ++;
        }
        length = i;
        System.out.println("Width : " + width);
        System.out.println("Length : " + length);
    }

    public Environment getEnvironment()
    {
        return environment;
    }

}
