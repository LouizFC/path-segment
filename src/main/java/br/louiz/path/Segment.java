package br.louiz.path;

/**
 * Represent a series of values in an linear hierarchical order. This can be
 * used to implement paths on a tree, url, filesystem, etc.
 * <p>
 * Every method on this interface that returns a {@link Segment} should
 * not, under any circumstances, return <i>null</i>, instead, those methods
 * should instead return a {@link MissingSegment} if possible. Returning
 * <i>null</i> on such methods is unsupported and prone to undesired behavior.
 *
 * @param <T> the type of this segment {@link #getValue() value}.
 * @param <S> this segment type. This is an implementation detail, and is recommended to
 *            be hidden from user level API to make it less verbose.
 * @see MissingSegment
 */
public interface Segment<T, S extends Segment<?, S>> extends Iterable<S>, Comparable<S> {

    /**
     * @return the {@link String} value of the segment.
     */
    T getValue();

    /**
     * the parent of this segment, in other words, the previous segment.
     * If there is none, the default return value is a {@link MissingSegment}.
     *
     * @return the parent of this segment, if there is none, returns {@link MissingSegment}.
     */
    S getParent();

    /**
     * The current depth, or in other words, its the distance from its root,
     * <p>
     * This should return 0 for root or top-level segments, and `-1` as a rogue
     * value if depth is not applicable.
     *
     * @return the depth of this segment relative to the root, or -1 if not applicable.
     */
    int getDepth();

    /**
     * Segments are equal when all participants of each hierarchy have the same {@link #getValue() values}.
     * How the equality of such values is defined is implementation specific.
     * <p>
     * This method should also obey the contract of {@link Object#equals(Object)}
     */
    @Override
    boolean equals(Object other);

    @Override
    int hashCode();
}
