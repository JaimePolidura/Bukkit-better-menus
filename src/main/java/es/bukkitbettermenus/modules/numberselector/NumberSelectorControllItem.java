package es.bukkitbettermenus.modules.numberselector;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public final class NumberSelectorControllItem {
    @Getter private final int itemNum;
    @Getter private ItemStack itemStack;
    @Getter private NumberSelectActionType actionType;
    @Getter private double valueToChange;
}
