/**
 * Created by darshan on 5/1/15.
 */
public enum CellType {
    REWARD(1f),
    PENALTY(-1f),
    BLANK(-0.04f),
    WALL(0f);

    float value;

    CellType(float value)
    {
        this.value = value;
    }

    public float getRewardValue()
    {
        return value;
    }
}
