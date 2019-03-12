package br.louiz.path;

/**
 * A {@link PathSegment} for representing property names in a path.
 */
public final class NameSegment extends PathSegment {

    /**
     * Internal constructor. It's mainly used on {@link PathSegment#append(String)}. The newly
     * created instance will have it's {@link #getParent() parent} {@link #getDepth() depth} plus 1.
     *
     * @param name   the non-null name of the field that this segment will represent.
     * @param parent the parent of this segment.
     * @throws IllegalArgumentException if name is null.
     */
    NameSegment(String name, PathSegment parent) {
        super(name, parent);
    }

    /**
     * {@link NameSegment}'s created with this constructor will always have {@link MissingSegment}
     * as its {@link #getParent() parent} and zero as its {@link #getDepth() depth}.
     *
     * @param fieldName he name of the field that this segment will represent.
     */
    public NameSegment(String fieldName) {
        this(fieldName, null);
    }

    /**
     * This method is an alias for {@link #getValue()}
     *
     * @return the field name that this segment represents
     * @see #getValue()
     */
    public String getName() {
        return getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PathSegment appendTo(PathSegment segment) {
        return segment.append(getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isIndex() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isName() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMissing() {
        return false;
    }
}