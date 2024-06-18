package fr.elowyr.core.interfaces;

@FunctionalInterface
public interface ThrowableFunction<T, R>
{
    R apply(final T p0) throws Throwable;
}
