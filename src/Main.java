import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by darshan on 5/1/15.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        File input = new File("1.1Env.txt");
        EnvironmentReader reader = new EnvironmentReader(input);
        Environment environment = reader.getEnvironment();
        System.out.println(environment);
        ValueIteration iteration = new ValueIteration(environment);
        iteration.iterate();
    }
}
