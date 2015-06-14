package com.lumpofcode.annotation;

import java.lang.annotation.*;

/**
 * NOTE: It would be nice to have this build into Java.
 *
 * TODO: figure out how to get this processed by FindBugs http://findbugs.sourceforge.net/
 *
 * Created by emurphy on 5/29/15.
 */
@Documented
@Target(value=ElementType.TYPE)
@Retention(value=RetentionPolicy.CLASS)
public @interface Immutable
{
}
