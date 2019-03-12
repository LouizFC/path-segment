package br.louiz.path;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexSegmentTest {
    @Test
    void testIndexSegmentCreation() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new IndexSegment(-1),
                "should throw if given index is negative."
        );
    }

    @Test
    void testAppendValue() {
        PathSegment zero = new IndexSegment(0);
        PathSegment one = new IndexSegment(1);
        PathSegment two = new IndexSegment(2);

        PathSegment zeroOne = zero.appendValue(one);
        PathSegment zeroOneTwo = zero.append(one).append(two);

        assertEquals(new IndexSegment(0).append(1), zeroOne);
        assertEquals(new IndexSegment(0).append(2), zero.appendValue(zeroOneTwo));
        assertEquals(new IndexSegment(0).append(1).append(2), zero.appendValue(zeroOne).appendValue(zeroOneTwo));
    }

    @Test
    void testAppendTo() {
        PathSegment zero = new IndexSegment(0);
        PathSegment one = new IndexSegment(1);

        PathSegment zeroOne = one.appendTo(zero);

        assertEquals(new IndexSegment(0).append(1), zeroOne);
        assertEquals(new IndexSegment(0).append(0), zero.appendTo(zero));
        assertEquals(new IndexSegment(0).append(1).append(1), zeroOne.appendTo(zeroOne));
    }
}