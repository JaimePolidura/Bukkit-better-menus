package es.bukkitbettermenus.modules.numberselector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@AllArgsConstructor
public final class NumberSelectorMenuConfiguration {
    public static final String DEFAULT_NUMBER_SELECTOR_PROPERTY_NAME = "numberselector.value";

    @Getter private final String valuePropertyName;
    @Getter private final double initialValue;
    @Getter private final double minValue;
    @Getter private final double maxValue;
    @Getter private final Map<Integer, NumberSelectorControllItem> items;
    @Getter private final Consumer<Double> onValueChanged;

    public static NumberSelectorMenuConfigurationBuilder builder(){
        return new NumberSelectorMenuConfigurationBuilder();
    }

    public static class NumberSelectorMenuConfigurationBuilder {
        private double initialValue;
        private double minValue;
        private double maxValue;
        private String valuePropertyName;
        private Map<Integer, NumberSelectorControllItem> items;
        private Consumer<Double> onValueChanged;

        public NumberSelectorMenuConfigurationBuilder(){
            this.items = new HashMap<>();
            this.initialValue = 0;
            this.minValue = Double.MIN_VALUE;
            this.maxValue = Double.MAX_VALUE;
            this.valuePropertyName = "numberselector.value";
        }

        public NumberSelectorMenuConfigurationBuilder onValueChanged(Consumer<Double> onValueChanged){
            this.onValueChanged = onValueChanged;
            return this;
        }

        public NumberSelectorMenuConfigurationBuilder item(int itemNum, NumberSelectActionType actionType, double valueToChange, Material material){
            this.items.put(itemNum, new NumberSelectorControllItem(
                    itemNum, new ItemStack(material), actionType, valueToChange
            ));
            return this;
        }

        public NumberSelectorMenuConfigurationBuilder item(int itemNum, NumberSelectActionType actionType, double valueToChange, ItemStack item){
            this.items.put(itemNum, new NumberSelectorControllItem(
                    itemNum, item, actionType, valueToChange
            ));
            return this;
        }

        public NumberSelectorMenuConfigurationBuilder valuePropertyName(String valuePropertyName){
            this.valuePropertyName = valuePropertyName;
            return this;
        }

        public NumberSelectorMenuConfigurationBuilder initialValue(double initialValue){
            this.initialValue = initialValue;
            return this;
        }

        public NumberSelectorMenuConfigurationBuilder minValue(double minValue){
            this.minValue = minValue;
            return this;
        }

        public NumberSelectorMenuConfigurationBuilder maxValue(double maxValue){
            this.maxValue = maxValue;
            return this;
        }

        public NumberSelectorMenuConfiguration build(){
            return new NumberSelectorMenuConfiguration(valuePropertyName, initialValue, minValue, maxValue,
                    items, onValueChanged);
        }
    }
}
