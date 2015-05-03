import java.util.HashSet;
import java.util.Set;

/**
 * Created by darshan on 5/1/15.
 */
public enum Direction {
    UP("\u2191"),
    DOWN("\u2193"),
    LEFT("\u2190"),
    RIGHT("\u2192");

    String arrow;

    Direction(String arrow)
    {
        this.arrow = arrow;
    }

    public String getArrow()
    {
        return arrow;
    }
}
