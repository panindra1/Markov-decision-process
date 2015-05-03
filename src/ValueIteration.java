/**
 * Created by darshan on 5/2/15.
 */
public class ValueIteration {
    Environment oldEnvironment;
    Environment newEnvironment;
    float gamma;
    public static final float DELTA = 0.0001f;
    public static final float INTENDED_DIRECTION_PROBABILITY = 0.8f;
    public static final float UNINTENDED_DIRECTION_PROBABILITY = 0.1f;
    public static final float GAMMA = 0.9f;

    public ValueIteration(Environment environment)
    {
        this.oldEnvironment = environment;
    }

    public void iterate()
    {
        boolean isErrorSatisfied = false;
        int iterations = 0;
        do
        {
            iterations ++;
            newEnvironment = constructNewEnvironment(oldEnvironment);
            System.out.println("Old environment : \n" + oldEnvironment);
            System.out.println("New environment : \n" + newEnvironment);
            isErrorSatisfied = isErrorSatisfied(oldEnvironment, newEnvironment);
            oldEnvironment = new Environment(newEnvironment.getEnvironmentCopy());
        } while(!isErrorSatisfied);
        System.out.println("Number of iterations needed : " + iterations);
    }


    public Environment constructNewEnvironment(Environment oldEnvironment)
    {
        newEnvironment = new Environment(oldEnvironment.getEnvironmentCopy());
        for(int x = 0 ; x < oldEnvironment.getEnvironment().length ; x ++)
        {
            for(int y = 0 ; y < oldEnvironment.getEnvironment()[x].length ; y ++)
            {
                if(!oldEnvironment.getEnvironment()[x][y].getCellType().equals(CellType.WALL))
                {
                    newEnvironment.getEnvironment()[x][y] =
                            setMaxUtilityForCell(oldEnvironment, newEnvironment.getEnvironment()[x][y], x, y);
                }
            }
        }
        return newEnvironment;
    }

    private boolean isErrorSatisfied(Environment oldEnvironment, Environment newEnvironment)
    {
        for(int x = 0 ; x < oldEnvironment.getEnvironment().length ; x ++)
        {
            for(int y = 0 ; y < oldEnvironment.getEnvironment()[x].length ; y ++)
            {
                float oldValue = oldEnvironment.getEnvironment()[x][y].getValue();
                float newValue = newEnvironment.getEnvironment()[x][y].getValue();
                float difference = Math.abs(newValue - oldValue);
                if(difference > DELTA)
                {
                    return false;
                }
            }
        }
        return true;
    }

    private Cell setMaxUtilityForCell(Environment environment, Cell cell, int x, int y)
    {
        Cell outputCell = new Cell(cell);
        Direction bestDirection = null;
        float maxValue = -Float.MAX_VALUE;
        if(!cell.cellType.equals(CellType.WALL))
        {
            for(Direction direction : Direction.values())
            {
                float value = calculateForIntendedDirection(environment, x, y, direction);
                if(value > maxValue)
                {
                    maxValue = value;
                    bestDirection = direction;
                }
            }
            outputCell.setDirection(bestDirection);
            outputCell.setValue(environment.getEnvironment()[x][y].getCellType().getRewardValue() +
                    GAMMA * maxValue);
        }
        return outputCell;
    }

    private float calculateForIntendedDirection(Environment environment, int x, int y, Direction intendedDirection)
    {
        System.out.println("Location : (" + x + ", " + y + "), Intended direction : " + intendedDirection);
        System.out.println("Calculating intended direction probability");
        float intendedDirectionProbability = calculateForDirection(environment, x, y, intendedDirection, INTENDED_DIRECTION_PROBABILITY);
        System.out.println("Intended direction probability : " + intendedDirectionProbability);
        float unintendedDirectionProbability = 0.0f;
        System.out.println("Calculate unintended direction probability");
        for(Direction direction : RightAngleDirections.valueOf(intendedDirection.toString()).getRightAngleDirections())
        {
            unintendedDirectionProbability += calculateForDirection(environment, x, y, direction, UNINTENDED_DIRECTION_PROBABILITY);
        }
        System.out.println("Unntended direction probability : " + unintendedDirectionProbability);
        System.out.println("(" + x + ", " + y + ") - " + intendedDirection + " : " + (intendedDirectionProbability + unintendedDirectionProbability));
        return intendedDirectionProbability + unintendedDirectionProbability;
    }

    private float calculateForDirection(Environment environment, int x, int y, Direction direction, float probability)
    {
        float calculatedValue = 0.0f;
        switch (direction)
        {
            case UP:
                if(x <= 0)
                {
                    //get current cell
                    System.out.println("At corner, cant go up");
                    calculatedValue = probability * environment.getEnvironment()[x][y].getValue();
                }
                else if(environment.getEnvironment()[x - 1][y].cellType.equals(CellType.WALL))
                {
                    //get current cell
                    System.out.println("Wall on up");
                    calculatedValue = probability * environment.getEnvironment()[x][y].getValue();
                }
                else
                {
                    //get value of UP
                    calculatedValue = probability * environment.getEnvironment()[x - 1][y].getValue();
                }
                break;
            case DOWN:
                if(x >= environment.getEnvironment().length - 1)
                {
                    //get current cell
                    System.out.println("At corner, cant go down");
                    calculatedValue = probability * environment.getEnvironment()[x][y].getValue();
                }
                else if(environment.getEnvironment()[x + 1][y].cellType.equals(CellType.WALL))
                {
                    //get current cell
                    System.out.println("wall, cant go down");
                    calculatedValue = probability * environment.getEnvironment()[x][y].getValue();
                }
                else
                {
                    //get value of DOWN
                    calculatedValue = probability * environment.getEnvironment()[x + 1][y].getValue();
                }
                break;
            case LEFT:
                if(y <= 0)
                {
                    //get current cell
                    System.out.println("at corner, cant go left");
                    calculatedValue = probability * environment.getEnvironment()[x][y].getValue();
                }
                else if(environment.getEnvironment()[x][y - 1].cellType.equals(CellType.WALL))
                {
                    //get current cell
                    System.out.println("wall, cant go left");
                    calculatedValue = probability * environment.getEnvironment()[x][y].getValue();
                }
                else
                {
                    //get value of LEFT
                    calculatedValue = probability * environment.getEnvironment()[x][y - 1].getValue();
                }
                break;
            case RIGHT:
                if(y >= environment.getEnvironment()[y].length - 1)
                {
                    //get current cell
                    System.out.println("at corner, cant go right");
                    calculatedValue = probability * environment.getEnvironment()[x][y].getValue();
                }
                else if(environment.getEnvironment()[x][y + 1].cellType.equals(CellType.WALL))
                {
                    //get current cell
                    System.out.println("wall, cant go right");
                    calculatedValue = probability * environment.getEnvironment()[x][y].getValue();
                }
                else
                {
                    //get value of RIGHT
                    calculatedValue = probability * environment.getEnvironment()[x][y + 1].getValue();
                }
                break;
        }
        return calculatedValue;
    }
}
