package bentleyottmann;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// See:
// http://geomalgorithms.com/a09-_intersect-3.html
// https://en.wikipedia.org/wiki/Bentley%E2%80%93Ottmann_algorithm
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

                // System.out.println("LEFT POINT -> seg: "
                // + segE.segment().name()
                // + ", above: " + (segA != null ? segA.segment().name() : "")
                // + ", below: " + (segB != null ? segB.segment().name() : ""));

                addEventIfIntersection(segE, segA, E, false);
                addEventIfIntersection(segE, segB, E, false);
            } else if (E.type() == Event.Type.POINT_RIGHT) {
                final SweepSegment segE = E.firstSegment();
                final SweepSegment segA = mSweepLine.above(segE);
                final SweepSegment segB = mSweepLine.below(segE);

                // System.out.println("RIGHT POINT -> seg: "
                // + segE.segment().name()
                // + ", above: " + (segA != null ? segA.segment().name() : "")
                // + ", below: " + (segB != null ? segB.segment().name() : ""));

                mSweepLine.remove(segE);

                addEventIfIntersection(segA, segB, E, true);
            } else {
                mIntersections.add(E.point());

                final SweepSegment segE1 = E.firstSegment();
                final SweepSegment segE2 = E.secondSegment();

                if (mListener != null) {
                    mListener.onIntersection(segE1.segment(), segE2.segment(), E.point());
                }

                mSweepLine.swap(segE1, segE2);

                final SweepSegment segA = mSweepLine.above(segE2);
                final SweepSegment segB = mSweepLine.below(segE1);

                // System.out.println("INTERSECTION POINT -> seg1: "
                // + segE1.segment().name()
                // + ", seg2: " + segE2.segment().name()
                // + ", above " + segE2.segment().name() + ": " + (segA != null ? segA.segment().name() : "")
                // + ", below " + segE1.segment().name() + ": " + (segB != null ? segB.segment().name() : ""));

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
                final Event e = new Event(i, s1, s2);
                if (check) {
                    if (mEventQueue.contains(e)) {
                        return;
                    }
                }

                mEventQueue.add(e);
            }
        }
    }
}
