package br.louiz.path;

import java.util.Iterator;
import java.util.NoSuchElementException;

final class ReverseSegmentIterator implements Iterator<PathSegment> {
    private PathSegment currentPath;

    ReverseSegmentIterator(PathSegment path) {
        this.currentPath = path;
    }

    @Override
    public boolean hasNext() {
        return !currentPath.isMissing();
    }

    @Override
    public PathSegment next() {
        if (!hasNext()) throw new NoSuchElementException();
        PathSegment oldPath = currentPath;
        currentPath = currentPath.getParent();
        return oldPath;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove operation is not supported");
    }
}