package nl.vpro.domain.api;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class ResultTest {
    @Test
    public void testGetList() throws Exception {
        Result<String> result = new Result<>(Arrays.asList("a", "b"), 10l, 5, 20l);
        assertEquals(Arrays.asList("a", "b"), result.getItems());
        assertEquals(Long.valueOf(10), result.getOffset());
        assertEquals(Long.valueOf(20), result.getTotal());
        assertEquals(Integer.valueOf(2), result.getSize());
        assertEquals(Integer.valueOf(5), result.getMax());

    }


    @Test
    public void testIterator() throws Exception {
        Result<String> result = new Result<>(Arrays.asList("a", "b"), 10l, 5, 20l);
        StringBuilder build = new StringBuilder();
        for(String s : result) {
            build.append(s);
        }
        assertEquals("ab", build.toString());
    }
}
