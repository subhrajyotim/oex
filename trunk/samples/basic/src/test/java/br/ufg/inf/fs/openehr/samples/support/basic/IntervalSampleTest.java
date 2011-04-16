package br.ufg.inf.fs.openehr.samples.support.basic;

import org.junit.Test;
import org.openehr.rm.support.basic.Interval;
import static org.junit.Assert.*;

/**
 * Just to learn some basics about Interval.
 * @author kyriosdata
 */
public class IntervalSampleTest {

    @Test
    public void testHasANotB() {
        Interval<String> i = new Interval<String>("a","a");
        assertTrue(i.has("a"));
        assertFalse(i.has("b"));
    }

    @Test
    public void testHasNotAAndB() {
        Interval<String> i = new Interval<String>("a","b",false,false);
        assertFalse(i.has("a"));
        assertFalse(i.has("b"));
        assertFalse(i.isLowerIncluded());
        assertFalse(i.isUpperIncluded());
        assertFalse(i.isUpperUnbounded());
        assertFalse(i.isLowerUnbounded());
    }

    @Test
    public void testc() {
        Interval<String> i = new Interval<String>("a",null);
        assertFalse(i.isLowerUnbounded());
        assertTrue(i.isUpperUnbounded());
        assertTrue(i.isLowerIncluded());
        assertFalse(i.isUpperIncluded());
    }
}