package es.bukkitbettermenus.modules.numberselector;

import es.bukkitbettermenus.Menu;

import java.util.function.Consumer;

public final class NumberSelectorService {
    public void performNumberSelectorClicked(Menu menu, int itemNum){
        NumberSelectorMenuConfiguration configuration = menu.getConfiguration().getNumberSelectorMenuConfiguration();
        NumberSelectorControllItem controllItem = configuration.getItems().get(itemNum);
        String valuePropertyName = configuration.getValuePropertyName();
        double actualValue = menu.getPropertyDouble(valuePropertyName);
        boolean isIncrease = controllItem.getActionType() == NumberSelectActionType.INCREASE;

        double newValue = isIncrease ? actualValue + controllItem.getValueToChange() : actualValue - controllItem.getValueToChange();
        boolean newValueInsideBounds = newValue >= configuration.getMinValue() && newValue <= configuration.getMaxValue();

        if(newValueInsideBounds){
            applyNewValueProperty(menu, valuePropertyName, newValue);
            callOnValueChanged(configuration, newValue);
        }
    }

    private void applyNewValueProperty(Menu menu, String valuePropertyName, double newValue) {
        menu.setProperty(valuePropertyName, newValue);
    }

    private void callOnValueChanged(NumberSelectorMenuConfiguration configuration, double newValue) {
        Consumer<Double> onValueChangedConsumer = configuration.getOnValueChanged();

        if(onValueChangedConsumer != null)
            onValueChangedConsumer.accept(newValue);
    }

}
