// See:
// http://geomalgorithms.com/a09-_intersect-3.html
// https://en.wikipedia.org/wiki/Bentley%E2%80%93Ottmann_algorithm
package bentleyottmann;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BentleyOttmann {
    @NotNull
    private final EventQueue mEventQueue = new EventQueue();

    @NotNull
    private final SweepLine mSweepLine = new SweepLine();

    @NotNull
    private final List<IPoint> mIntersections = new ArrayList<>();

    @NotNull
    private final IPointFactory mPointFactory;

    @Nullable
    private OnIntersectionListener mListener;

    public BentleyOttmann(@NotNull IPointFactory pointFactory) {
        mPointFactory = pointFactory;
    }

    public void addSegments(@NotNull List<ISegment> segments) {
        for (ISegment s : segments) {
            final SweepSegment ss = new SweepSegment(s);
            mEventQueue.add(ss.leftEvent());
            mEventQueue.add(ss.rightEvent());
        }
    }

    public void findIntersections() {
        while (!mEventQueue.isEmpty()) {
            final Event E = mEventQueue.poll();
            if (E.type() == Event.Type.POINT_LEFT) {
                final SweepSegment segE = E.firstSegment();

                mSweepLine.updatePositions(E.point().x());
                mSweepLine.add(segE);

                final SweepSegment segA = mSweepLine.above(segE);
                final SweepSegment segB = mSweepLine.below(segE);

                addEventIfIntersection(segE, segA, E, false);
                addEventIfIntersection(segE, segB, E, false);

                // Check against ALL other segments in sweep line to catch non-adjacent intersections
                for (SweepSegment s : mSweepLine) {
                    if (s != segE && s != segA && s != segB) {
                        addEventIfIntersection(segE, s, E, false);
                    }
                }
            } else if (E.type() == Event.Type.POINT_RIGHT) {
                final SweepSegment segE = E.firstSegment();
                final SweepSegment segA = mSweepLine.above(segE);
                final SweepSegment segB = mSweepLine.below(segE);

                mSweepLine.remove(segE);

                addEventIfIntersection(segA, segB, E, true);
            } else {
                final SweepSegment segE1 = E.firstSegment();
                final SweepSegment segE2 = E.secondSegment();

                // Skip if this intersection is just a shared endpoint
                if (!isSharedEndpoint(segE1, segE2, E.point())) {
                    mIntersections.add(E.point());

                    if (mListener != null) {
                        mListener.onIntersection(segE1.segment(), segE2.segment(), E.point());
                    }
                }

                mSweepLine.swap(segE1, segE2);

                final SweepSegment segA = mSweepLine.above(segE2);
                final SweepSegment segB = mSweepLine.below(segE1);

                addEventIfIntersection(segE2, segA, E, true);
                addEventIfIntersection(segE1, segB, E, true);
            }
        }
    }

    @NotNull
    public List<IPoint> intersections() {
        return Collections.unmodifiableList(mIntersections);
    }

    public void setListener(@NotNull OnIntersectionListener listener) {
        mListener = listener;
    }

    public void reset() {
        mIntersections.clear();
        mEventQueue.clear();
        mSweepLine.clear();
    }

    private void addEventIfIntersection(@Nullable SweepSegment s1, @Nullable SweepSegment s2, @NotNull Event E, boolean check) {
        if (s1 != null && s2 != null) {
            final IPoint i = SweepSegment.intersection(s1, s2, mPointFactory);
            if (i != null && i.x() > E.point().x()) {
                // Check if intersection point matches any endpoint of either segment
                if (isSharedEndpoint(s1, s2, i)) {
                    return;
                }

                final Event e = new Event(i, s1, s2);
                if (check && mEventQueue.contains(e)) {
                    return;
                }
                mEventQueue.add(e);
            }
        }
    }

    private boolean isSharedEndpoint(@NotNull SweepSegment s1, @NotNull SweepSegment s2, @NotNull IPoint p) {
        boolean s1Left = Event.nearlyEqual(s1.leftEvent().point(), p);
        boolean s1Right = Event.nearlyEqual(s1.rightEvent().point(), p);
        boolean s2Left = Event.nearlyEqual(s2.leftEvent().point(), p);
        boolean s2Right = Event.nearlyEqual(s2.rightEvent().point(), p);

        return (s1Left || s1Right) && (s2Left || s2Right);
    }
}
