package es.bukkitbettermenus;

import es.bukkitbettermenus.configuration.MenuConfiguration;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Menu<T> {
    @Getter private final Map<String, Object> properties;
    @Getter private final UUID menuId;
    @Getter private final int[][] baseItemNums;
    @Getter private int actualPageNumber;
    @Setter private List<Page> pages;
    private MenuConfiguration configuration;
    
    @Getter @Setter private T state;
    @Getter @Setter private Player player;

    public Menu() {
        this.baseItemNums = this.items();
        this.actualPageNumber = 0;
        this.pages = new ArrayList<>();
        this.menuId = UUID.randomUUID();
        this.properties = new HashMap<>();
    }

    public abstract int[][] items();
    public abstract MenuConfiguration configuration();

    public void close() {
        player.closeInventory();
    }

    public MenuConfiguration getConfiguration() {
        return this.configuration == null ? this.configuration = configuration() : this.configuration;
    }

    public final void deleteItem(int slot, int pageNumber){
        this.getPage(pageNumber).deleteItem(slot);
    }

    public final List<Page> getPages() {
        return new ArrayList<>(this.pages);
    }

    public final Page getPage(int pageNumber) {
        return this.pages.get(pageNumber);
    }

    public final Page getLastPage() {
        return this.pages.get(this.pages.size() - 1);
    }

    public final Page getActualPage() {
        return this.pages.get(actualPageNumber);
    }

    public final void addPages(List<Page> pages) {
        this.pages.addAll(pages);
    }

    public final Inventory getInventory() {
        return this.pages.get(this.actualPageNumber).getInventory();
    }

    public final int[][] getActualItemNums() {
        return this.pages.get(this.actualPageNumber).getItemsNums();
    }

    public void setItem(int pageNumber, int slotItem, ItemStack newItem, int itemNum) {
        this.getPage(pageNumber).setItem(slotItem, newItem, itemNum);
    }

    public void setItemActualPage(int slotItem, ItemStack newItem, int itemNum) {
        this.getPage(actualPageNumber).setItem(slotItem, newItem, itemNum);
    }

    public final void setItemLore(int pageNumber, int itemSlot, List<String> newLore) {
        this.getPage(pageNumber).setItemLore(itemSlot, newLore);
    }

    public final void setItemLoreActualPage(int itemSlot, List<String> newLore) {
        this.getPage(actualPageNumber).setItemLore(itemSlot, newLore);
    }

    public final List<ItemStack> getItemsByItemNum(int itemNum) {
        return this.getPages().stream()
                .map(page -> page.getItemsByItemNum(itemNum))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public final void setItemLore(int pagenNumer, int itemSlot, int indexItemLore, String newLore) {
        this.pages.get(pagenNumer).setItemLore(itemSlot, indexItemLore, newLore);
    }

    public final void setItemLoreActualPage(int itemSlot, int indexItemLore, String newLore) {
        this.pages.get(actualPageNumber).setItemLore(itemSlot, indexItemLore, newLore);
    }

    public final Page forward() {
        if (this.actualPageNumber + 1 >= this.pages.size()) return this.pages.get(this.pages.size() - 1);

        this.actualPageNumber++;
        return this.pages.get(this.actualPageNumber);
    }

    public final Page backward() {
        if (this.actualPageNumber == 0) return this.pages.get(0);

        this.actualPageNumber--;
        return this.pages.get(this.actualPageNumber);
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
}
