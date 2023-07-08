package es.bukkitbettermenus.utils;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
    void consume(A a, B b, C c);
}
