package es.bukkitbettermenus.menubuilder;

import es.bukkitbettermenus.Page;
import es.bukkitbettermenus.SupportedInventoryType;
import es.bukkitbettermenus.configuration.MenuConfiguration;
import es.bukkitbettermenus.utils.CollectionUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public final class MenuBuilderService {
    public List<Page> createPages(MenuConfiguration configuration, int[][] baseItemNums, Player player){
        List<Page> pages = new LinkedList<>();

        this.addToItemMapsItemsFunctions(configuration, player);

        Queue<ItemStack> variousItemsItemStack = this.findVariousItems(configuration);
        int variousItemStack = this.findVariousItemsItemNum(configuration);
        BuildItemNumsReult buildItemNumsResult = createItemNumsArrayForPage(configuration, baseItemNums, variousItemsItemStack, variousItemStack);
        pages.add(new Page(buildItemNumsResult.inventory, buildItemNumsResult.itemNums));

        while (!variousItemsItemStack.isEmpty()){
            BuildItemNumsReult result = createItemNumsArrayForPage(configuration, baseItemNums, variousItemsItemStack, variousItemStack);
            pages.add(new Page(result.inventory, result.itemNums));
        }

        return pages;
    }

    private void addToItemMapsItemsFunctions(MenuConfiguration configuration, Player player) {
        configuration.getItemFunctions().forEach((itemNum, itemFunction) -> {
            ItemStack itemStack = itemFunction.apply(player);
            configuration.getItems().put(itemNum, Collections.singletonList(itemStack));
        });
        configuration.getItemsFunctions().forEach((itemNum, itemsFunction) -> {
            List<ItemStack> items = itemsFunction.apply(player);
            configuration.getItems().put(itemNum, items);
        });
    }

    private Queue<ItemStack> findVariousItems(MenuConfiguration configuration) {
        for(Map.Entry<Integer, List<ItemStack>> entry : configuration.getItems().entrySet())
            if(entry.getValue() != null && entry.getValue().size() > 1)
                return new LinkedList<>(entry.getValue());

        return new LinkedList<>();
    }

    private int findVariousItemsItemNum(MenuConfiguration configuration) {
        for(Map.Entry<Integer, List<ItemStack>> entry : configuration.getItems().entrySet())
            if(entry.getValue() != null && entry.getValue().size() > 1)
                return entry.getKey();

        return -1;
    }

    public BuildItemNumsReult createItemNumsArrayForPage(MenuConfiguration configuration, int[][] baseItemNumsArray,
                                                          Queue<ItemStack> variousItemsPendingToAdd, int variousImtesNum){
        List<Integer> itemNums = CollectionUtils.bidimensionalArrayToLinearArray(baseItemNumsArray);
        Inventory inventoryOfPage = this.createBaseInventory(configuration, SupportedInventoryType.getByArray(baseItemNumsArray));
        int[][] newItemNums = new int[baseItemNumsArray.length][baseItemNumsArray[0].length];
        SupportedInventoryType supportedInventoryType = SupportedInventoryType.getByArray(baseItemNumsArray);
        Map<Integer, List<ItemStack>> itemMap = configuration.getItems();

        for(int i = 0; i < itemNums.size(); i++){
            int actualItemNum = itemNums.get(i);
            int row = SupportedInventoryType.getRowBySlot(i, supportedInventoryType.getBukkitInventoryType());
            int column = SupportedInventoryType.getColumnBySlot(i, supportedInventoryType.getBukkitInventoryType());
            List<ItemStack> itemsToAdd = itemMap.get(actualItemNum);

            if(itemsToAdd == null){
                newItemNums[row][column] = 0;
                continue;
            }
            if(itemsToAdd.size() == 1){
                newItemNums[row][column] = actualItemNum;
                inventoryOfPage.setItem(i, configuration.getItems().get(actualItemNum).get(0));

                continue;
            }
            if(variousItemsPendingToAdd == null || variousItemsPendingToAdd.isEmpty()){
                continue;
            }

            //The las has size() > 1
            int sizeItemPendingToAdd = variousItemsPendingToAdd.size();
            for (int j = 0; j < sizeItemPendingToAdd; j++) {
                if(j > 0) i++;
                if(i >= itemNums.size()) break;

                row = SupportedInventoryType.getRowBySlot(i, supportedInventoryType.getBukkitInventoryType());
                column = SupportedInventoryType.getColumnBySlot(i, supportedInventoryType.getBukkitInventoryType());

                if(isBreakpoint(configuration, baseItemNumsArray, row, column)){
                    newItemNums[row][column] = actualItemNum;
                    int itemNumBreakpoint = itemNums.get(i);
                    inventoryOfPage.setItem(i, itemMap.get(itemNumBreakpoint) == null ? new ItemStack(Material.AIR) : itemMap.get(itemNumBreakpoint).get(0));
                    break;
                }

                //No breakpoint
                newItemNums[row][column] = actualItemNum;
                inventoryOfPage.setItem(i, variousItemsPendingToAdd.poll());
            }
        }

        return new BuildItemNumsReult(inventoryOfPage, newItemNums, variousItemsPendingToAdd, variousImtesNum);
    }

    @AllArgsConstructor
    private static class BuildItemNumsReult {
        @Getter private final Inventory inventory;
        @Getter private final int[][] itemNums;
        @Getter private final Queue<ItemStack> itemsOverflow;
        @Getter private final int itemsNumOverflow;
    }

    private boolean isBreakpoint(MenuConfiguration configuration, int[][] originalItemNums, int row, int column) {
        return originalItemNums[row][column] == configuration.getBreakpointItemNum();
    }

    private Inventory createBaseInventory(MenuConfiguration configuration, SupportedInventoryType supportedInventoryType) {
        return supportedInventoryType.getSize() % 9 == 0 ?
                Bukkit.createInventory(null, supportedInventoryType.getSize(), configuration.getTitle()) :
                Bukkit.createInventory(null, supportedInventoryType.getBukkitInventoryType(), configuration.getTitle());
    }
}
