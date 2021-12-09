package bentleyottmann;

import org.jetbrains.annotations.NotNull;

public interface OnIntersectionListener {
    void onIntersection(@NotNull ISegment s1, @NotNull ISegment s2, @NotNull IPoint p);
}
