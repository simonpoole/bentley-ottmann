package bentleyottmann;

import org.jetbrains.annotations.NotNull;

public interface IPointFactory {
    @NotNull
    IPoint create(double x, double y);
}
