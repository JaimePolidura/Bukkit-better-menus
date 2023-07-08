package es.bukkitbettermenus;

import es.bukkitbettermenus.configuration.MenuConfiguration;
import es.bukkitbettermenus.utils.ItemUtils;
import es.bukkitbettermenus.utils.TriConsumer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public abstract class Menu<T> {
    @Getter private Inventory inventory;
    @Getter private final Map<String, Object> properties;
    @Getter private final UUID menuId;
    @Getter private final int[][] baseItemNums;
    @Getter private int actualPageId;
    @Setter private List<Page> pages;
    private MenuConfiguration configuration;

    @Getter @Setter private T state;
    @Getter @Setter private Player player;

    public Menu() {
        this.baseItemNums = this.items();
        this.actualPageId = 0;
        this.pages = new ArrayList<>();
        this.menuId = UUID.randomUUID();
        this.properties = new HashMap<>();
    }

    public abstract int[][] items();
    public abstract MenuConfiguration configuration();

    public final void close() {
        player.closeInventory();
    }

    public final MenuConfiguration getConfiguration() {
        return this.configuration == null ? this.configuration = configuration() : this.configuration;
    }

    public final void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public final void initializeInventoryWithFirstPage() {
        replaceAllInventoryItems(getActualPage().getItems());
    }

    public final void replaceAllInventoryItems(List<ItemStack> items) {
        inventory.clear();
        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(i, items.get(i));
        }
    }

    public final List<Page> getPages() {
        return new ArrayList<>(this.pages);
    }

    public final Page getPage(int pageId) {
        return this.pages.get(pageId);
    }

    public final Page getLastPage() {
        return this.pages.get(this.pages.size() - 1);
    }

    public final Page getActualPage() {
        return this.pages.get(actualPageId);
    }

    public final void addPages(List<Page> pages) {
        this.pages.addAll(pages);
    }

    public final int[][] getActualItemNums() {
        return this.pages.get(this.actualPageId).getItemsNums();
    }

    public final void decreaseActualQuantityOrRemove(int slot) {
        ItemStack item = inventory.getItem(slot);
        int itemNum = getActualItemNumBySlot(slot);
        int oldQuantity = item.getAmount();
        int newQuantity = oldQuantity - 1;

        if(newQuantity > 0) {
            setActualItem(slot, item, itemNum);
        }else{
            clearActualItem(slot);
        }
    }

    public final void setActualItem(int slotItem, ItemStack newItem, int itemNum) {
        setItem(actualPageId, slotItem, newItem, itemNum);
    }

    public final void setItem(int pageId, int slotItem, ItemStack newItem, int itemNum) {
        Page page = pages.get(pageId);

        inventory.setItem(slotItem, newItem);
        page.setItem(newItem, slotItem, itemNum);
    }

    public final int getActualItemNumBySlot(int slot) {
        int row = SupportedInventoryType.getRowBySlot(slot, inventory.getType());
        int column = SupportedInventoryType.getColumnBySlot(slot, inventory.getType());

        return getActualPage().getItemNumBySlot(row, column);
    }

    public final void setActualItemLore(int slot, List<String> newLore) {
        ItemStack itemToEdit = inventory.getItem(slot);
        ItemMeta itemToEditMeta = itemToEdit.getItemMeta();
        itemToEditMeta.setLore(newLore);
        itemToEdit.setItemMeta(itemToEditMeta);
        int itemNum = getActualItemNumBySlot(slot);

        setActualItem(slot, itemToEdit, itemNum);
    }

    public final void setActualItemLore(int slot, int index, String newLore) {
        ItemStack itemToEdit = inventory.getItem(slot);
        ItemStack itemEdited = ItemUtils.setLore(itemToEdit, index, newLore);
        int itemNum = getActualItemNumBySlot(slot);

        setActualItem(slot, itemEdited, itemNum);
    }

    public final void setItemLore(int pageId, int slot, int index, String newLore) {
        Page page = this.pages.get(pageId);
        ItemStack item = page.getItems().get(slot);
        ItemUtils.setLore(item, index, newLore);

        page.updateItem(item, slot);

        if(page.getPageId() == getActualPageId()){
            inventory.setItem(slot, item);
        }
    }

    public final List<ItemStack> getActualItemsByItemNum(int itemNum) {
        return this.getActualPage().getItemsByItemNum(itemNum);
    }

    public final void forEachAllItemsByItemNum(int itemNum, TriConsumer<ItemStack, Integer, Integer> consumerItemPageSlot) {
        for (Page page : this.pages) {
            page.forEachItemByItemNum(itemNum, (item, slot) -> {
                consumerItemPageSlot.consume(item, page.getPageId(), slot);
            });
        }
    }

    public final void forEachActualItemsByItemNum(int itemNum, BiConsumer<ItemStack, Integer> consumerItemSlot) {
        getActualPage().forEachItemByItemNum(itemNum, consumerItemSlot);
    }

    public final List<ItemStack> getAllItemsByItemNum(int itemNum) {
        return this.getPages().stream()
                .map(page -> page.getItemsByItemNum(itemNum))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public final void clearActualItem(int slot) {
        pages.get(getActualPageId()).clearItem(slot);
        inventory.clear(slot);
    }

    public final void clearItem(int pageId, int slot) {
        pages.get(pageId).clearItem(slot);
        inventory.clear(slot);
    }

    public final Page nextPage() {
        if (actualPageId + 1 >= pages.size()) {
            return pages.get(pages.size() - 1);
        }

        this.actualPageId++;
        Page newPage = this.pages.get(this.actualPageId);

        callOnPageChangedCallback(newPage);

        newPage.setVisited();

        return newPage;
    }

    public final Page backPage() {
        if (actualPageId == 0) {
            return pages.get(0);
        }

        this.actualPageId--;
        Page newPage = pages.get(actualPageId);

        callOnPageChangedCallback(newPage);

        newPage.setVisited();

        return newPage;
    }

    public final Menu<T> setProperty(String key, Object value) {
        if (this.properties.isEmpty()) this.properties.putAll(this.getConfiguration().getProperties());

        this.properties.put(key, value);
        return this;
    }

    public final Object getProperty(String key) {
        if (this.properties.isEmpty()) this.properties.putAll(this.getConfiguration().getProperties());

        return this.properties.get(key);
    }

    public final double getPropertyDouble(String key) {
        if (this.properties.isEmpty()) this.properties.putAll(getConfiguration().getProperties());

        Object propertyObject = this.properties.get(key);

        return propertyObject == null ? 0 : Double.parseDouble(String.valueOf(propertyObject));
    }

    private void callOnPageChangedCallback(Page newPage) {
        if(configuration.getOnPageChanged() != null) {
            configuration.getOnPageChanged().accept(newPage);
        }
    }
}
