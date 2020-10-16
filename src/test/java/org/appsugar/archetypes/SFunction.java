package org.appsugar.archetypes;


import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.util.function.Function;

@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {

    default SerializedLambda writeReplace() {
        return null;
    }
}