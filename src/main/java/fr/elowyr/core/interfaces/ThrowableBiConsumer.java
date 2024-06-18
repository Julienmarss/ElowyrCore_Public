package fr.elowyr.core.interfaces;

@FunctionalInterface
public interface ThrowableBiConsumer<A, B>
{
    void accept(final A p0, final B p1) throws Throwable;
}
