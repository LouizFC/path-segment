package br.louiz.path;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameSegmentTest {
    @Test
    void testAppendValue() {
        PathSegment foo = new NameSegment("foo");
        PathSegment bar = new NameSegment("bar");
        PathSegment baz = new NameSegment("baz");

        PathSegment fooBar = foo.appendValue(bar);
        PathSegment fooBarBaz = foo.append(bar).append(baz);

        assertEquals(new NameSegment("foo").append("bar"), fooBar);
        assertEquals(new NameSegment("foo").append("baz"), foo.appendValue(fooBarBaz));
        assertEquals(new NameSegment("foo").append("bar").append("baz"), foo.appendValue(fooBar).appendValue(fooBarBaz));
    }

    @Test
    void testAppendTo() {
        PathSegment foo = new NameSegment("foo");
        PathSegment bar = new NameSegment("bar");

        PathSegment fooBar = new NameSegment("foo").append("bar");
        PathSegment fooBarBaz = new NameSegment("foo").append("bar").append("baz");

        assertEquals(fooBar, bar.appendTo(foo));
        assertEquals(fooBar.append("baz"), fooBarBaz.appendTo(fooBar));
    }
}