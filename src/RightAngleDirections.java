import java.util.HashSet;
import java.util.Set;

/**
 * Created by darshan on 5/2/15.
 */
public enum RightAngleDirections {
    UP(Direction.LEFT, Direction.RIGHT),
    DOWN(Direction.LEFT, Direction.RIGHT),
    LEFT(Direction.UP, Direction.DOWN),
    RIGHT(Direction.UP, Direction.DOWN);

    Set<Direction> rightAngles;

    RightAngleDirections(Direction d1, Direction d2)
    {
        rightAngles = new HashSet<Direction>();
        rightAngles.add(d1);
        rightAngles.add(d2);
    }

    public Set<Direction> getRightAngleDirections()
    {
        return rightAngles;
    }
}
