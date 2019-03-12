package br.louiz.path;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MissingSegmentTest {

    @Test
    void testAppendTo() {
        PathSegment base = PathSegment.create(0).append(1).append(2);
        assertEquals(base, MissingSegment.INSTANCE.appendTo(base));

        PathSegment missing = MissingSegment.INSTANCE.appendTo(MissingSegment.INSTANCE);
        assertEquals(MissingSegment.INSTANCE, missing);
    }

    @Test
    void testEquals() {
        MissingSegment missingSegment1 = new MissingSegment();
        MissingSegment missingSegment2 = new MissingSegment();

        assertEquals(missingSegment1, missingSegment2);
        assertEquals(MissingSegment.INSTANCE, missingSegment1);
    }
}