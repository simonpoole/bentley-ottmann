package bentleyottmann;

import org.jetbrains.annotations.NotNull;

class Point implements IPoint {
    final private double x;
    final private double y;

    Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    Point(@NotNull Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }
}
