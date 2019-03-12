package br.louiz.path;

import java.util.Iterator;

/**
 * Represent a series of values in an linear hierarchical order.
 * This can be used to implement "paths" on a tree or filesystem.
 *
 * @see NameSegment
 * @see IndexSegment
 * @see MissingSegment
 */
abstract class PathSegment implements Segment<String, PathSegment> {

    /**
     * Backing property used only for {@link PathSegment#toString}.
     *
     * @see PathSegment#toString
     */
    private final String rfcString;
    private final String value;
    private final PathSegment parent;
    private final int depth;

    /**
     * The primary constructor. When subclassing, should be noted that if null
     * is passed for the {@code parent} parameter, it will be replaced with a
     * {@link MissingSegment} internally.
     *
     * @param value  a non-null {@link String} representation of the segment value.
     * @param parent the parent of the new {@link PathSegment}, if null, will be reverted to
     *               {@link MissingSegment#INSTANCE}.
     * @param depth  the current depth of this segment.
     * @throws IllegalArgumentException if value is null.
     */
    protected PathSegment(String value, PathSegment parent, int depth) {
        if (value == null) throw new IllegalArgumentException("parameter value should not be null");
        this.value = value;
        this.parent = parent == null ? MissingSegment.INSTANCE : parent;
        this.depth = depth;
        this.rfcString = this.parent + "/" + encode(value);
    }

    protected PathSegment(String value, PathSegment parent) {
        this(value, parent, parent != null ? parent.getDepth() + 1 : 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PathSegment getParent() {
        return parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDepth() {
        return depth;
    }

    /**
     * Appends only the value of {@code this} {@link PathSegment} to the given
     * parameter.
     * <p>
     * This is useful for when the type of the {@link PathSegment} to be appended
     * is unknown but need to be maintained.
     * <p>
     * This method is similar to {@link #appendValue(PathSegment)}, the difference is that this
     * method is "reversed", in other words, this segment is appended to the parameter.
     * <p>
     * Example:
     * <p>
     * {@code /foo#appendTo(/bar) }
     * <br>results into {@code `/bar/foo`}
     * <p>
     * {@code /foo/bar#appendTo(/the/value/to/be/appended/is)}
     * <br>results into {@code `/the/value/to/be/appended/is/bar`}
     * <p>
     * Should be noted that if the given parameter is a null or a {@link MissingSegment}, this method
     * will return {@code this} instance without modification, otherwise will return a new {@link PathSegment}.
     *
     * @param segment a non-null {@link PathSegment} which this {@link #getValue() value} will be appended to.
     * @return a new {@link PathSegment} similar to the given parameter but with this {@link #getValue() value}
     * appended to it. If the given parameter is a {@link MissingSegment}, it will return {@code this} segment
     * without modification.
     * @throws IllegalArgumentException if parameter segment is null.
     * @see #appendValue(PathSegment)
     */
    public abstract PathSegment appendTo(PathSegment segment);

    /**
     * Appends only the value of {@code this} {@link PathSegment} to the given
     * parameter.
     * <p>
     * This is useful for when the type of the {@link PathSegment} to be appended
     * is unknown but need to be maintained.
     * <p>
     * Example:
     * <p>
     * /foo#appendValue(/bar) becomes `/foo/bar`
     * <p>
     * /foo#appendValue(/the/value/to/be/appended/is/this) becomes `/foo/this`
     * <p>
     * Should be noted that if {@code this} instance is a {@link MissingSegment}, this method
     * will return the given parameter (segment) without any modification, otherwise will return
     * a new {@link PathSegment}
     */
    public final PathSegment appendValue(PathSegment segment) {
        if (segment == null) throw new IllegalArgumentException("parameter segment should not be null");
        return segment.appendTo(this);
    }

    /**
     * Returns a new {@link NameSegment} with the given field name. The constructed
     * {@link NameSegment} will have `this` {@link PathSegment} as its {@link #getParent() parent}.
     *
     * @param name the name of the field that the new segment will represent.
     * @return a new {@link NameSegment} with the given field name.
     */
    public NameSegment append(String name) {
        if (name == null) throw new IllegalArgumentException("parameter name should not be null");
        return new NameSegment(name, this);
    }

    /**
     * Returns a new {@link IndexSegment} with the given index. The constructed
     * {@link IndexSegment} will have `this` {@link PathSegment} as its {@link #getParent() parent}.
     *
     * @param index the index that the new segment will represent.
     * @return a new {@link IndexSegment} with the given index.
     */
    public IndexSegment append(int index) {
        return new IndexSegment(index, this);
    }

    /**
     * Constructs a new {@link PathSegment} with all the {@link PathSegment}s
     * found in {@code this} segment and the parameter, constructing a new
     * {@link PathSegment} without mutating {@code this} or the parameter.
     * <p>
     * The difference is that this methods ensure that the type safety of
     * {@link IndexSegment}s are preserved.
     *
     * @param path a non-null {@link PathSegment} that will be appended.
     * @return a new {@link PathSegment} based on the parameter, but with its
     * {@link #getParent() parent} and {@link #getDepth() depth} adjusted to the
     * new context. If the parameter is a {@link MissingSegment} this method will
     * return {@code this}(the current instance).
     */
    public final PathSegment append(PathSegment path) {
        if (path == null) throw new IllegalArgumentException("parameter path should not be null");
        if (path.isMissing()) return this;
        PathSegment result = this;
        for (PathSegment pathSegment : path) {
            result = pathSegment.appendTo(result);
        }
        return result;
    }

    /**
     * Returns the {@link PathSegment} at the given {@link #getDepth() depth}.
     * <p>
     * {@link PathSegment}s have only references to the immediate {@link #getParent() parent},
     * this means that lookup is made "backwards", from the highest depth to the root.
     *
     * @return the first {@link PathSegment} among this segment or its parents that
     * has a matching {@link #getDepth() depth}
     * @throws IndexOutOfBoundsException if the given [depth] is negative or greater
     *                                   than {@code this}.{@link #getDepth()}.
     */
    public final PathSegment get(int depth) {
        if (depth >= 0 && depth <= this.depth) {
            PathSegment result = this;
            if (depth == this.depth) return result;
            while (result.depth > depth) {
                result = result.parent;
            }
            return result;
        } else {
            String message = "Given depth " + depth + " is out of bounds (0 to " + this.depth + ")";
            throw new IndexOutOfBoundsException(message);
        }
    }

    /**
     * Tries to find a {@link #getValue() value} that matches the given parameter
     * within this {@link PathSegment} references, and if it succeeds, return the
     * matching {@link PathSegment} (including itself), otherwise returns a {@link MissingSegment}.
     * <p>
     * This method will follow the same order as the {@link #reverseIterator} to find
     * the matching {@link PathSegment}.
     *
     * @param name the value to be searched among this {@link PathSegment} references.
     * @return the first found {@link #getParent() parent} that matches the parameter,
     * if none found returns a {@link MissingSegment}.
     * @see #reverseIterator()
     */
    public final PathSegment find(String name) {
        Iterator<PathSegment> iterator = reverseIterator();
        while (iterator.hasNext()) {
            PathSegment next = iterator.next();
            if (next.getValue().equals(name)) {
                return next;
            }
        }
        return MissingSegment.INSTANCE;
    }

    /**
     * Tries to find a {@link #getValue() value} that matches the given parameter
     * within this {@link PathSegment} references, and if it succeeds, return the
     * matching {@link PathSegment} (including itself), otherwise returns a {@link MissingSegment}.
     * <p>
     * This method will follow the same order as the {@link #reverseIterator} to find
     * the matching {@link PathSegment}.
     *
     * @param index the value to be searched among this {@link PathSegment} references.
     * @return the first found {@link #getParent() parent} that matches the parameter,
     * if none found returns a {@link MissingSegment}.
     * @see #reverseIterator()
     */
    public final PathSegment find(int index) {
        return find(String.valueOf(index));
    }

    /**
     * Returns `true` if this {@link PathSegment} is a {@link IndexSegment}, `false` otherwise.
     *
     * @see IndexSegment
     * @see #isName
     * @see #isMissing
     */
    public abstract boolean isIndex();

    /**
     * Returns `true` if this {@link PathSegment} is a {@link NameSegment}, `false` otherwise.
     *
     * @see NameSegment
     * @see #isIndex
     * @see #isMissing
     */
    public abstract boolean isName();

    /**
     * Returns `true` if this {@link PathSegment} is a {@link MissingSegment}, `false` otherwise.
     *
     * @see MissingSegment
     * @see #isName
     * @see #isIndex
     */
    public abstract boolean isMissing();

    /**
     * Returns {@code false} if this {@link PathSegment} has a {@link MissingSegment}
     * as its {@link #getParent() parent}, {@code true} otherwise.
     */
    public boolean hasParent() {
        return !parent.isMissing();
    }

    public boolean contains(String name) {
        return !find(name).isMissing();
    }

    public boolean contains(int index) {
        return !find(index).isMissing();
    }

    public boolean contains(PathSegment path) {
        PathSegment thisPath = this;
        PathSegment current = path;
        while (path.getDepth() <= thisPath.getDepth()) {
            if (current.getValue().equals(thisPath.getValue())) {
                if (current.getDepth() == 0) {
                    return true;
                }
                current = current.getParent();
            } else {
                current = path;
            }
            thisPath = thisPath.getParent();
        }
        return false;
    }

    /**
     * Creates an array containing all parents contained in this {@link PathSegment},
     * in crescent order based on its {@link #getDepth() depth}.
     */
    public final PathSegment[] toArray() {
        PathSegment[] result = new PathSegment[getDepth() + 1];
        PathSegment currentSegment = this;
        while (!currentSegment.equals(MissingSegment.INSTANCE)) {
            result[currentSegment.getDepth()] = currentSegment;
            currentSegment = currentSegment.getParent();
        }
        return result;
    }

    /**
     * Iterator for iterating from {@code this} to the {@link #getParent() parent} with
     * {@link #getDepth() depth} 0.
     * <p>
     * This method performs better than [iterator].
     *
     * @see ReverseSegmentIterator
     */
    public Iterator<PathSegment> reverseIterator() {
        return new ReverseSegmentIterator(this);
    }

    /**
     * Iterator for iterating from the {@link #getParent() parent} with
     * {@link #getDepth() depth} 0 to {@code this} instance.
     *
     * @see SegmentIterator
     */
    @Override
    public Iterator<PathSegment> iterator() {
        return new SegmentIterator(this);
    }

    @Override
    public int compareTo(PathSegment other) {
        final int before = -1;
        final int equal = 0;
        final int after = 1;

        if (this == other) return equal;

        if (this.depth < other.depth) return before;
        if (this.depth > other.depth) return after;

        return Integer.compare(this.toString().compareTo(other.toString()), 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathSegment)) return false;

        PathSegment that = (PathSegment) o;

        if (getDepth() != that.getDepth()) return false;
        return toString().equals(that.toString());
    }

    @Override
    public int hashCode() {
        int result = rfcString.hashCode();
        result = 31 * result + getDepth();
        return result;
    }

    /**
     * Returns a {@link String} representation of this {@link PathSegment}, the
     * representation is compatible with the JavaScript Object Notation (JSON)
     * Pointer RFC 6901.
     */
    @Override
    public String toString() {
        return rfcString;
    }

    private static String encode(String value) {
        if (value == null) throw new IllegalStateException("value should never be null.");
        if (value.isEmpty()) return value;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '~':
                    result.append("~0");
                    break;
                case '/':
                    result.append("~1");
                    break;
                default:
                    result.append(c);
            }
        }
        return result.toString();
    }

    private static String decode(String value) {
        if (value == null) throw new IllegalStateException("value should never be null.");
        if (value.isEmpty()) return value;
        StringBuilder stringBuilder = new StringBuilder();
        int i = 0;
        while (i < value.length()) {
            char c = value.charAt(i++);
            if (c == '~') {
                char nextChar = value.charAt(i);
                switch (nextChar) {
                    case '0':
                        stringBuilder.append('~');
                        break;
                    case '1':
                        stringBuilder.append('/');
                        break;
                    default:
                        stringBuilder.append(c).append(nextChar);
                }
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Utility factory method for creating a [NameSegment].
     *
     * @see NameSegment
     */
    public static NameSegment create(String name) {
        return new NameSegment(name);
    }

    /**
     * Utility factory method for creating a {@link IndexSegment}.
     *
     * @see IndexSegment
     */
    public static IndexSegment create(int index) {
        return new IndexSegment(index);
    }

    /**
     * Creates a {@link PathSegment} from a {@link String}.
     *
     * @param path the {@link String} to be converted.
     * @return a new [PathSegment] from the given [String]
     * @throws IllegalArgumentException if the given [path] does not begin with a `/`
     * @see PathSegment#toString
     */
    public static PathSegment fromString(String path) {
        if (path.isEmpty()) return MissingSegment.INSTANCE;
        if (path.charAt(0) != '/') throw new IllegalArgumentException("Path must start with '/'");

        PathSegment result = MissingSegment.INSTANCE;
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < path.length(); i++) {
            char c = path.charAt(i);
            if (c == '/') {
                result = result.append(builder.toString());
                builder.setLength(0);
                continue;
            }
            builder.append(c);
        }
        return result.append(builder.toString());
    }


}