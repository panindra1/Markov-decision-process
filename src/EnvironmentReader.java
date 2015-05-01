import java.io.*;

/**
 * Created by darshan on 5/1/15.
 */
public class EnvironmentReader {
    BufferedReader br;
    File environmentFile;
    Integer[][] environment;
    int length, width;


    public EnvironmentReader(File file) throws FileNotFoundException {
        init(file);
    }

    private void init(File file) throws FileNotFoundException {
        this.environmentFile = file;
        br = new BufferedReader(new FileReader(environmentFile));
    }

    private void read() throws IOException {
        br.reset();
        environment = new Integer[length][width];
        for(int i = 0 ; i < length ; i ++)
        {
            String line = br.readLine();
            for(int j = 0 ; j < width ; j ++)
            {
                char character = line.charAt(0);
                if(character == '%')
                {
                    environment[i][j] = Integer.MIN_VALUE;
                }
                else if(character == ' ')
                {
                    environment[i][j] = 0;
                }
                else
                {
                    environment[i][j] = Integer.parseInt(Character.toString(character));
                }
            }
        }

    }

    private void getDimensions() throws IOException {
        br.reset();
        String line = br.readLine();
        width = line.length();
        int i = 1;
        while(br.readLine() != null)
        {
            i ++;
        }
        length = i;
    }


}
