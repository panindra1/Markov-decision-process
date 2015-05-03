/**
 * Created by darshan on 5/1/15.
 */
public class Cell {
    CellType cellType;
    Direction direction;
    Float value;

    public Cell()
    {}

    public Cell(Cell copy)
    {
        this.cellType = copy.getCellType();
        this.direction = copy.getDirection();
        this.value = copy.getValue();
    }

    public Cell(CellType cellType, Direction direction, Float value) {
        this.cellType = cellType;
        this.direction = direction;
        this.value = value;
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Cell{" +
                "cellType=" + cellType +
                ", direction=" + direction +
                ", value=" + value +
                '}';
    }
}
