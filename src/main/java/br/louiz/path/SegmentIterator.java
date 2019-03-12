package br.louiz.path;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class SegmentIterator implements Iterator<PathSegment> {

    private final PathSegment[] stack;
    private int count = 0;

    SegmentIterator(PathSegment path) {
        this.stack = path.toArray();
    }

    @Override
    public boolean hasNext() {
        return count < stack.length;
    }

    @Override
    public PathSegment next() {
        if (!hasNext()) throw new NoSuchElementException();
        return stack[count++];
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove operation is not supported");
    }
}