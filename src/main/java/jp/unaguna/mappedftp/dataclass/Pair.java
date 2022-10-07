package jp.unaguna.mappedftp.dataclass;

public class Pair<T0, T1> {
    private final T0 first;
    private final T1 second;

    private Pair(T0 first, T1 second) {
        this.first = first;
        this.second = second;
    }

    public T0 getFirst() {
        return first;
    }

    public T1 getSecond() {
        return second;
    }

    public static <T0, T1> Pair<T0, T1> of(T0 first, T1 second) {
        return new Pair<>(first, second);
    }
}
