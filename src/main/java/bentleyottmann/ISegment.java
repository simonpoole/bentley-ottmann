package bentleyottmann;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ISegment {
    @NotNull
    IPoint p1();

    @NotNull
    IPoint p2();

    @Nullable
    String name();
}
