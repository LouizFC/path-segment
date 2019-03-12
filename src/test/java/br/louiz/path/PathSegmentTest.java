package br.louiz.path;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PathSegmentTest {

    void testConsistency(PathSegment pathSegment, String stringValue) {
        assertEquals(
                MissingSegment.INSTANCE, pathSegment.getParent(),
                "new paths should always have ${MissingSegment::class.java.name} as its parent."
        );
        assertEquals(
                stringValue, pathSegment.getValue(),
                "should have the value it was assigned with."
        );
        assertEquals(
                0, pathSegment.getDepth(),
                "new paths should always have initial depth of 0."
        );
    }

    @Test
    void testFactoryCreationMethods() {
        testConsistency(PathSegment.create(0), "0");
        testConsistency(PathSegment.create("field"), "field");
    }

    @Test
    void testEquals() {
        List<PathSegment> paths = new ArrayList<>();

        PathSegment pathOne = PathSegment.create("one");
        PathSegment pathTwo = PathSegment.create("one");
        assertEquals(pathOne, pathTwo, "should be equal even with different referential identities.");

        PathSegment newPathOne = pathOne.append(2);
        PathSegment newPathTwo = pathTwo.append(2);
        assertEquals(newPathOne, newPathTwo, "should be equal even with parent different object references.");

        PathSegment newPathOneClone = pathOne.append(2);
        assertEquals(newPathOne, newPathOneClone, "should be equal if parents are the same object reference.");

        PathSegment stringPath = newPathOne.append("3");
        PathSegment indexPath = newPathTwo.append(3);
        assertEquals(stringPath, indexPath, "should be equal if path have same value, even if different types.");
    }

    @Test
    void testAppendName() {
        PathSegment name = PathSegment.create("foo");
        PathSegment appendOnName = name.append("bar");

        assertEquals("/foo/bar", appendOnName.toString());

        PathSegment index = PathSegment.create(0);
        PathSegment appendOnIndex = index.append("bar");

        assertEquals("/0/bar", appendOnIndex.toString());

        PathSegment missing = MissingSegment.INSTANCE;
        PathSegment appendOnMissing = missing.append("bar");

        assertEquals("/bar", appendOnMissing.toString());
    }

    @Test
    void testAppendIndex() {
        PathSegment name = PathSegment.create("foo");
        PathSegment appendOnName = name.append(0);

        assertEquals("/foo/0", appendOnName.toString());
        assertEquals(1, appendOnName.getDepth());

        PathSegment index = PathSegment.create(0);
        PathSegment appendOnIndex = index.append(0);

        assertEquals("/0/0", appendOnIndex.toString());
        assertEquals(1, appendOnIndex.getDepth());

        PathSegment missing = MissingSegment.INSTANCE;
        PathSegment appendOnMissing = missing.append(0);

        assertEquals("/0", appendOnMissing.toString());
        assertEquals(0, appendOnMissing.getDepth());
    }

    @Test
    void testAppendPath() {
        PathSegment created = PathSegment.create("foo");
        PathSegment appendField = created.append("bar");
        PathSegment appendIndex = appendField.append(0);

        assertEquals("/foo", created.toString());
        assertEquals("/foo/bar", appendField.toString());
        assertEquals("/foo/bar/0", appendIndex.toString());

        PathSegment appendPath = appendIndex.append(appendIndex);
        assertEquals("/foo/bar/0/foo/bar/0", appendPath.toString());

        PathSegment appendMissing = appendIndex.append(MissingSegment.INSTANCE);
        assertEquals("/foo/bar/0", appendMissing.toString());
        assertEquals(appendMissing, appendIndex);

        PathSegment[] array = appendPath.toArray();
        List<PathSegment> parents = new ArrayList<>(array.length + 1);
        Collections.addAll(parents, array);

        Iterator<PathSegment> parentsIterator = parents.iterator();
        for (PathSegment pathSegment : appendPath) {
            assertEquals(parentsIterator.next(), pathSegment);
        }

    }

    @Test
    @DisplayName("should throw exception when using get method with a index out of bounds")
    void testIndexOutOfBoundsException() {
        PathSegment foo = new NameSegment("foo");
        PathSegment bar = foo.append("bar");
        PathSegment path = bar.append(0);
        assertThrows(IndexOutOfBoundsException.class, () -> path.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> path.get(-2));
        assertThrows(IndexOutOfBoundsException.class, () -> path.get(4));
        assertThrows(IndexOutOfBoundsException.class, () -> path.get(3));
    }

    @Test
    void testGet() {
        PathSegment foo = new NameSegment("foo");
        PathSegment bar = foo.append("bar");
        PathSegment path = bar.append(0);
        assertEquals(foo, path.get(0));
        assertEquals(bar, path.get(1));
        assertEquals(path, path.get(2));
    }

    @Test
    void testFind() {
        PathSegment test = PathSegment.create("test");
        PathSegment one = test.append(1);
        PathSegment two = one.append(2);
        PathSegment three = two.append(3);
        PathSegment end = three.append("end");

        assertEquals(test, end.find("test"));
        assertEquals(one, end.find(1));
        assertEquals(two, end.find(2));
        assertEquals(three, end.find(3));
        assertEquals(end.find("end"), end);

        assertTrue(end.find("testValue").isMissing());
    }

    @Test
    void testReverseIterator() {
        PathSegment foo = new NameSegment("foo");
        PathSegment bar = foo.append("bar");
        PathSegment path = bar.append(0);

        Iterator<PathSegment> pathReverseIterator = path.reverseIterator();

        assertEquals(path, pathReverseIterator.next());
        assertEquals(bar, pathReverseIterator.next());
        assertEquals(foo, pathReverseIterator.next());
        assertThrows(NoSuchElementException.class, () -> pathReverseIterator.next());
    }

    @Test
    void testIterator() {
        PathSegment foo = new NameSegment("foo");
        PathSegment bar = foo.append("bar");
        PathSegment path = bar.append(0);
        Iterator<PathSegment> pathIterator = path.iterator();

        assertEquals(foo, pathIterator.next());
        assertEquals(bar, pathIterator.next());
        assertEquals(path, pathIterator.next());
        assertThrows(NoSuchElementException.class, () -> pathIterator.next());
    }

    @Test
    void testIsIndex() {
        PathSegment minus = PathSegment.create("-1");
        PathSegment one = PathSegment.create(1);
        PathSegment two = PathSegment.create(2);
        PathSegment three = PathSegment.create("3");
        PathSegment four = PathSegment.create(4);

        assertFalse(minus.isIndex(), "given a string, it cannot be a index.");
        assertTrue(one.isIndex(), "given a index, it should be a index.");
        assertTrue(two.isIndex(), "given a index, it should be a index.");
        assertFalse(three.isIndex(), "given a string, it cannot be a index.");
        assertTrue(four.isIndex(), "given a index, it should be a index.");
    }

    @Test
    void testIsField() {
        PathSegment foo = PathSegment.create("foo");
        PathSegment bar = PathSegment.create("bar");
        PathSegment minus = PathSegment.create("-1");
        PathSegment five = PathSegment.create(5);

        assertTrue(foo.isName(), "given a string, should be a name.");
        assertTrue(bar.isName(), "given a string, should be a name.");
        assertTrue(minus.isName(), "given a string, should be a name.");
        assertFalse(five.isName(), "given a index, it cannot be a name.");
    }

    @Test
    void testContainsName() {
        PathSegment foo = new NameSegment("foo");
        PathSegment bar = foo.append("bar");
        PathSegment path = bar.append(0);

        assertTrue(path.contains("foo"));
        assertFalse(path.contains("3"));
        assertTrue(path.contains("0"));
    }

    @Test
    void testContainsInt() {
        PathSegment foo = PathSegment.create(2);
        PathSegment bar = foo.append(1);
        PathSegment path = bar.append(0);

        assertTrue(path.contains(2));
        assertTrue(path.contains(1));
        assertTrue(path.contains(0));
        assertFalse(path.contains(5));
    }

    @Test
    void testContainsSegment() {
        PathSegment root = PathSegment.create("root");  // /root
        PathSegment one = root.append(1);               // /root/1
        PathSegment two = one.append(2);                // /root/1/2
        PathSegment three = two.append(3);              // /root/1/2/3
        PathSegment four = three.append("four");        // /root/1/2/3/four
        PathSegment last = four.append("last");         // /root/1/2/3/four/last

        PathSegment pathTwo = PathSegment.create(2); // /2
        PathSegment pathThree = pathTwo.append(3);   // /2/3

        assertTrue(last.contains(pathThree));
        assertTrue(four.contains(pathThree));
        assertTrue(three.contains(pathThree));
        assertFalse(two.contains(pathThree));
        assertFalse(one.contains(pathThree));
        assertFalse(root.contains(pathThree));

        PathSegment foo = PathSegment.create("foo"); // /foo
        PathSegment bar = foo.append("bar");         // /foo/bar
        PathSegment barbaz = foo.append("barbaz");   // /foo/barbaz

        assertFalse(barbaz.contains(bar));
    }

    @Test
    void testToArray() {
        PathSegment root = PathSegment.create("root");
        PathSegment one = root.append(1);
        PathSegment two = one.append(2);
        PathSegment three = two.append(3);
        PathSegment four = three.append("four");
        PathSegment last = four.append("last");

        PathSegment[] array = last.toArray();
        assertEquals(array[0], root);
        assertEquals(array[1], one);
        assertEquals(array[2], two);
        assertEquals(array[3], three);
        assertEquals(array[4], four);
        assertEquals(array[5], last);
    }

    @Test
    void testToString() {
        PathSegment test = PathSegment.create("test").append("~/").append("/");
        assertEquals("/test/~0~1/~1", test.toString());
    }

    /*@Test
    void testToStringNoRFC() {
        PathSegment test = PathSegment.create("test").append("~/").append("/");
        assertEquals("/test/~///", test.toString(false));
        assertEquals("/test/~0~1/~1", test.toString(true));
    }*/

    @Test
    void testFromString() {
        PathSegment fooBar = PathSegment.create("foo").append("bar");
        PathSegment fooBarFromString = PathSegment.fromString("/foo/bar");

        assertEquals(fooBar, fooBarFromString);

        PathSegment tilFooBar = PathSegment.create("~foo").append("bar");
        assertEquals(tilFooBar, PathSegment.fromString("/~foo/bar"));
        assertEquals("/~0foo/bar", PathSegment.fromString("/~foo/bar").toString());
        assertNotEquals(tilFooBar, PathSegment.fromString("/~0foo/bar"));

        PathSegment unescapedTilFooBar = PathSegment.create("~0foo").append("bar");
        assertEquals(unescapedTilFooBar, PathSegment.fromString("/~0foo/bar"));
        assertEquals("/~00foo/bar", PathSegment.fromString("/~0foo/bar").toString());

        PathSegment fooEmptyBar = PathSegment.create("foo").append("").append("bar");
        assertEquals(fooEmptyBar, PathSegment.fromString("/foo//bar"));
        assertEquals("/foo//bar", PathSegment.fromString("/foo//bar").toString());

        PathSegment fooSlashBar = PathSegment.create("foo").append("/bar");
        assertNotEquals(fooSlashBar, PathSegment.fromString("/foo/~1bar"));
        assertNotEquals("/foo/~1bar", PathSegment.fromString("/foo//bar").toString());

        PathSegment unescapedFooTilSlashBar = PathSegment.create("foo").append("~1bar");
        assertEquals(unescapedFooTilSlashBar, PathSegment.fromString("/foo/~1bar"));
        assertEquals("/foo/~01bar", PathSegment.fromString("/foo/~1bar").toString());
    }

}