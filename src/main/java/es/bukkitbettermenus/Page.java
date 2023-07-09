package es.bukkitbettermenus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@AllArgsConstructor
@ToString
public final class Page {
    @Getter private final List<ItemStack> items;
    @Getter private final int[][] itemsNums;
    @Getter private final int pageId;
    @Getter private boolean alreadyVisited;

    public int getItemNumByRowAndColumn(int row, int column) {
        return this.itemsNums[row][column];
    }

    public int getSlotByItemNum(int itemNum) {
        int maxCols = this.itemsNums[0].length;

        for (int rows = 0; rows < itemsNums.length; rows++) {
            for (int columns = 0; columns < itemsNums[rows].length; columns++) {
                if(itemsNums[rows][columns] == itemNum){
                    return rows * maxCols + columns + 1; //slot
                }
            }
        }

        return -1;
    }

    public void setItem(ItemStack item, int slot, int itemNum) {
        int row = SupportedInventoryType.getRowBySlot(slot, itemsNums);
        int column = SupportedInventoryType.getColumnBySlot(slot, itemsNums);
        int itemIndex = slot - 1;

        items.set(itemIndex, item);
        itemsNums[row][column] = itemNum;
    }

    public void updateItem(ItemStack item, int slot) {
        int itemIndex = slot - 1;

        items.set(itemIndex, item);
    }

    public void clearItem(int slot) {
        int row = SupportedInventoryType.getRowBySlot(slot, itemsNums);
        int column = SupportedInventoryType.getColumnBySlot(slot, itemsNums);
        int itemIndex = slot - 1;

        items.set(itemIndex, new ItemStack(Material.AIR));
        itemsNums[row][column] = 0;
    }

    public void forEachItemByItemNum(int itemNum, BiConsumer<ItemStack, Integer> consumer){
        int maxCols = this.itemsNums[0].length;

        for (int rows = 0; rows < this.itemsNums.length; rows++) {
            for (int columns = 0; columns < this.itemsNums[rows].length; columns++) {
                if(itemsNums[rows][columns] == itemNum) {
                    int itemIndex = rows * maxCols + columns;
                    int slot = itemIndex + 1;
                    ItemStack item = items.get(itemIndex);

                    consumer.accept(item, slot);
                }
            }
        }
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
