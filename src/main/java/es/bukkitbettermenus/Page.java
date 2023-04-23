package es.bukkitbettermenus;

import es.bukkitbettermenus.utils.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@ToString
public final class Page {
    @Getter private final Inventory inventory;
    @Getter private final int[][] itemsNums;
    private final int[][] baseItemNums;

    public void setItem(int slot, ItemStack newItem, int itemNum){
        this.inventory.setItem(slot, newItem);
        int row = SupportedInventoryType.getRowBySlot(slot, itemsNums);
        int column = SupportedInventoryType.getColumnBySlot(slot, itemsNums);

        itemsNums[row][column] = itemNum;
    }

    public void setItemLore(int slot, List<String> newLore){
        ItemStack itemToEdit = inventory.getItem(slot);
        ItemMeta itemToEditMeta = itemToEdit.getItemMeta();
        itemToEditMeta.setLore(newLore);
        itemToEdit.setItemMeta(itemToEditMeta);
        inventory.setItem(slot, itemToEdit);
    }

    public void setItemLore(int slot, int index, String newLore){
        ItemStack itemToEdit = inventory.getItem(slot);
        ItemStack itemEdited = ItemUtils.setLore(itemToEdit, index, newLore);
        inventory.setItem(slot, itemEdited);
    }

    public List<ItemStack> getItemsByItemNum(int itemNum){
        List<ItemStack> toReturn = new ArrayList<>();
        int maxRows = this.itemsNums.length;
        int maxCols = this.itemsNums[0].length;

        for (int rows = 0; rows < this.itemsNums.length; rows++) {
            for (int columns = 0; columns < this.itemsNums[rows].length; columns++) {
                if(itemsNums[rows][columns] == itemNum)
                    toReturn.add(inventory.getItem(
                            rows * maxCols + columns
                    ));
            }
        }

        return toReturn;
    }
}
