package bentleyottmann;

import java.util.Iterator;
import java.util.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class SweepLine extends TreeSet<SweepSegment> {
    SweepLine() {
        super((SweepSegment s1, SweepSegment s2) -> Double.compare(s1.position(), s2.position()));
    }

    void remove(@NotNull SweepSegment s) {
        final Iterator<SweepSegment> it = iterator();
        while (it.hasNext()) {
            if (s.nearlyEqual(it.next())) {
                it.remove();
            }
        }
    }

    void swap(@NotNull SweepSegment s1, @NotNull SweepSegment s2) {
        remove(s1);
        remove(s2);

        final double swap = s1.position();
        s1.setPosition(s2.position());
        s2.setPosition(swap);

        add(s1);
        add(s2);
    }

    @Nullable
    SweepSegment above(@NotNull SweepSegment s) {
        return higher(s);
    }

    @Nullable
    SweepSegment below(@NotNull SweepSegment s) {
        return lower(s);
    }

    void updatePositions(double x) {
        for (SweepSegment s : this) {
            s.updatePosition(x);
        }
    }
}
