package bentleyottmann;

import java.util.PriorityQueue;

import org.jetbrains.annotations.NotNull;

class EventQueue extends PriorityQueue<Event> {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean contains(@NotNull Object o) {
        boolean result = false;
        for (Event e : this) {
            if (e.nearlyEqual((Event) o)) {
                result = true;
                break;
            }
        }

        return result;
    }
}
