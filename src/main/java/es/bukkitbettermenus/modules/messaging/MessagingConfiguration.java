package es.bukkitbettermenus.modules.messaging;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@AllArgsConstructor
public final class MessagingConfiguration {
    @Getter private final Map<Class<?>, Consumer<?>> onMessageEventListeners;

    public static MessagingConfigurationBuilder builder(){
        return new MessagingConfigurationBuilder();
    }

    public static class MessagingConfigurationBuilder{
        private final Map<Class<?>, Consumer<?>> onMessageEventListeners;

        public MessagingConfigurationBuilder(){
            this.onMessageEventListeners = new HashMap<>();
        }

        public <T> MessagingConfigurationBuilder onMessage(Class<T> messageClass, Consumer<T> messageEventListener){
            this.onMessageEventListeners.put(messageClass, messageEventListener);
            return this;
        }

        public MessagingConfiguration build(){
            return new MessagingConfiguration(this.onMessageEventListeners);
        }
    }
}
