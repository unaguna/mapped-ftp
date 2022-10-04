package jp.unaguna.mappedftp.dataclass;

import java.util.function.Function;

public class Either<T0, T1> {
    private final T0 first;
    private final T1 second;

    public Object get() {
        if (first != null) {
            return first;
        } else {
            return second;
        }
    }

    public T0 getFirst() {
        return first;
    }

    public T1 getSecond() {
        return second;
    }

    public <R> R map(Function<T0, R> action0, Function<T1, R> action1) {
        if (first != null) {
            return action0.apply(first);
        }
        if (second != null) {
            return action1.apply(second);
        }
        return null;
    }

    protected Either(T0 first, T1 second) {
        this.first = first;
        this.second = second;

        if (first == null && second == null) {
            throw new IllegalArgumentException("Cannot create an Either object with null contents.");
        } else if (first != null && second != null) {
            throw new IllegalArgumentException("Cannot create an Either object with two non-null contents.");
        }
    }

    @Override
    public String toString() {
        return this.map(Object::toString, Object::toString);
    }

    public static <E0, E1> Either<E0, E1> first(E0 first) {
        return new Either<>(first, null);
    }

    public static <E0, E1> Either<E0, E1> second(E1 second) {
        return new Either<>(null, second);
    }
}
