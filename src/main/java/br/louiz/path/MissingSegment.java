package br.louiz.path;

/**
 * This segment class is used to denote a "missing segment" or otherwise a
 * null segment, representing segments of a path or hierarchy that either
 * do not exists, are undefined or cannot be represented.
 * <p>
 * The intended usage of this class is as a return value of methods that
 * could return a otherwise nullable {@link PathSegment}, instead it is recommended
 * to return this class or a more meaningful subclass of this class.
 * <p>
 * Subclassing this class is encouraged if meaningful information need to be added.
 * <p>
 * The main use case (and the recommended one) for this class is root segments.
 * <p>
 * A {@link PathSegment} used as a default return type instead of {@code null}.
 * It has -1 {@link #getDepth() depth} and return a empty {@link String} both
 * on {@link #toString} and {@link #getValue() value}.
 */
public class MissingSegment extends PathSegment {

    public static final PathSegment INSTANCE = new MissingSegment();

    protected MissingSegment() {
        super("", null, -1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PathSegment appendTo(PathSegment segment) {
        return segment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PathSegment getParent() {
        return INSTANCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDepth() {
        return -1;
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
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isMissing() {
        return true;
    }

    @Override
    public String toString() {
        return "";
    }
}
