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
    @Getter private final List<ItemStack> items;
    @Getter private final int[][] itemsNums;
    @Getter private final int pageId;
    @Getter private boolean alreadyVisited;

    public int getItemNumBySlot(int row, int column) {
        return this.itemsNums[row][column];
    }

    public void setItem(ItemStack item, int slot, int itemNum) {
        int row = SupportedInventoryType.getRowBySlot(slot, itemsNums);
        int column = SupportedInventoryType.getColumnBySlot(slot, itemsNums);

        this.items.set(slot, item);
        itemsNums[row][column] = itemNum;
    }

    public List<ItemStack> getItemsByItemNum(int itemNum){
        List<ItemStack> toReturn = new ArrayList<>();
        int maxCols = this.itemsNums[0].length;

        for (int rows = 0; rows < this.itemsNums.length; rows++) {
            for (int columns = 0; columns < this.itemsNums[rows].length; columns++) {
                if(itemsNums[rows][columns] == itemNum)
                    toReturn.add(items.get(
                            rows * maxCols + columns
                    ));
            }
        }

        return toReturn;
    }

    public void setVisited() {
        this.alreadyVisited = true;
    }
}
