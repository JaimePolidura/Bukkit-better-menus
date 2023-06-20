package es.bukkitbettermenus;

import io.vavr.control.Try;

import java.lang.reflect.Constructor;

public final class MenuConstructorResolver {
    public Menu getMenu(Class<? extends Menu> menuClass) {
        MenusDependenciesInstanceProvider instanceProvider = BukkitBetterMenus.INSTANCE_PROVIDER;
        if(instanceProvider == null){
            throw new RuntimeException("Instance provider not provided");
        }

        Constructor<? extends Menu> constructor = (Constructor<? extends Menu>) menuClass.getConstructors()[0];
        Class<?>[] constructorParameters = constructor.getParameterTypes();
        Object[] resolvedConstructorParameters = new Object[constructorParameters.length];

        for (int i = 0; i < constructorParameters.length; i++) {
            Class<?> constructorParameter = constructorParameters[i];

            Object instance = instanceProvider.get(constructorParameter);

            if(instance == null){
                throw new RuntimeException(String.format("Unknown dependency %s when resolving menu's constructor %s",
                        constructorParameter.getName(), menuClass));
            }

            resolvedConstructorParameters[i] = instance;
        }

        return Try.of(() -> constructor.newInstance(resolvedConstructorParameters)).get();
    }
}
