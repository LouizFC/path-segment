package br.louiz.path;

/**
 * A {@link PathSegment} with a backing {@code int} for type safety when dealing
 * with indexes.
 */
public final class IndexSegment extends PathSegment {

    private final transient int index;

    /**
     * Internal constructor. It's mainly used on {@link PathSegment#append(int)}. The newly
     * created instance will have it's {@link #getParent() parent} {@link #getDepth() depth} plus 1.
     *
     * @param index  the index this segment will represent.
     * @param parent the parent of this segment.
     */
    IndexSegment(int index, PathSegment parent) {
        super(String.valueOf(index), parent);
        if (index < 0) {
            throw new IllegalArgumentException("Index " + index + " should not be negative.");
        }
        this.index = index;
    }

    /**
     * {@link IndexSegment}'s created with this constructor will always have {@link MissingSegment}
     * as its {@link #getParent() parent} and zero as its {@link #getDepth() depth}.
     *
     * @param index the index of the created {@link IndexSegment}.
     */
    public IndexSegment(int index) {
        this(index, null);
    }

    /**
     * @return an int representing this segment index
     */
    public int getIndex() {
        return index;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PathSegment appendTo(PathSegment segment) {
        return segment.append(getIndex());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIndex() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isName() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMissing() {
        return false;
    }


}
