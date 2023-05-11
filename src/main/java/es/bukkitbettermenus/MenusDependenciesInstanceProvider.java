package es.bukkitbettermenus;

@FunctionalInterface
public interface MenusDependenciesInstanceProvider {
    <I, O extends I> O get(Class<I> clazz);
}
