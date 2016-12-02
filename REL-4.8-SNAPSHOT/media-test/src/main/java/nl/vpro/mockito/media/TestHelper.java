/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.mockito.media;

import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import nl.vpro.domain.media.*;
import nl.vpro.mockito.media.answer.FirstArgument;
import nl.vpro.mockito.media.matcher.*;

public class TestHelper {

    public static <T> FirstArgument firstArgument(Class<T> clazz) {
        return new FirstArgument<T>();
    }

    public static FirstArgument withSameMediaObject() {
        return new FirstArgument<MediaObject>();
    }

    public static FirstArgument withSameSchedule() {
        return new FirstArgument<Schedule>();
    }

    public static <T> Answer argument(final int pos, Class<T> clazz) {
        return new Answer<T>() {
            @Override
            public T answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (T)args[pos];
            }
        };
    }

    public static MediaObject anyMediaObject() {
        return Matchers.argThat(new IsAnyMediaObject());
    }

    public static Program anyProgram() {
        return Matchers.argThat(new IsAnyProgram());
    }

    public static Group anyGroup() {
        return Matchers.argThat(new IsAnyGroup());
    }

    public static Segment anySegment() {
        return Matchers.argThat(new IsAnySegment());
    }

    public static Schedule anySchedule() {
        return Matchers.argThat(new IsAnySchedule());
    }
}
