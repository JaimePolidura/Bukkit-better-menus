package es.bukkitbettermenus;

import es.bukkitbettermenus.configuration.MenuConfiguration;
import es.bukkitbettermenus.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Menu<T> {
    @Getter private final Inventory inventory;
    @Getter private final Map<String, Object> properties;
    @Getter private final UUID menuId;
    @Getter private final int[][] baseItemNums;
    @Getter private int actualPageNumber;
    @Setter private List<Page> pages;
    private MenuConfiguration configuration;

    @Getter @Setter private T state;
    @Getter @Setter private Player player;

    public Menu() {
        this.configuration = configuration();
        this.baseItemNums = this.items();
        this.actualPageNumber = 0;
        this.pages = new ArrayList<>();
        this.menuId = UUID.randomUUID();
        this.properties = new HashMap<>();
        this.inventory = createBaseInventory();
    }

    public abstract int[][] items();
    public abstract MenuConfiguration configuration();

    public void close() {
        player.closeInventory();
    }

    public MenuConfiguration getConfiguration() {
        return this.configuration == null ? this.configuration = configuration() : this.configuration;
    }

    public final void initializeFirstPage() {
        replaceAllItems(getActualPage().getItems());
    }

    public final void replaceAllItems(List<ItemStack> items) {
        inventory.clear();
        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(i, items.get(i));
        }
    }

    public final void deleteItem(int slot){
        this.inventory.clear(slot);
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

    public final int[][] getActualItemNums() {
        return this.pages.get(this.actualPageNumber).getItemsNums();
    }

    public void setItem(int pageNumber, int slotItem, ItemStack newItem, int itemNum) {
        Page page = getActualPage();
        inventory.setItem(slotItem, newItem);

        int row = SupportedInventoryType.getRowBySlot(slotItem, page.getItemsNums());
        int column = SupportedInventoryType.getColumnBySlot(slotItem, page.getItemsNums());

        page.getItemsNums()[row][column] = itemNum;
    }

    public int getItemNumBySlot(int slot) {
        int row = SupportedInventoryType.getRowBySlot(slot, inventory.getType());
        int column = SupportedInventoryType.getColumnBySlot(slot, inventory.getType());

        return getActualPage().getItemNumBySlot(row, column);
    }

    public final void setItemLoreActualPage(int itemSlot, List<String> newLore) {
        ItemStack itemToEdit = inventory.getItem(itemSlot);
        ItemMeta itemToEditMeta = itemToEdit.getItemMeta();
        itemToEditMeta.setLore(newLore);
        itemToEdit.setItemMeta(itemToEditMeta);

        inventory.setItem(itemSlot, itemToEdit);
    }

    public void setItemLore(int slot, int index, String newLore) {
        ItemStack itemToEdit = inventory.getItem(slot);
        ItemStack itemEdited = ItemUtils.setLore(itemToEdit, index, newLore);
        inventory.setItem(slot, itemEdited);
    }

    public final List<ItemStack> getActualItemsByItemNum(int itemNum) {
        return this.getActualPage().getItemsByItemNum(itemNum);
    }

    public final List<ItemStack> getAllItemsByItemNum(int itemNum) {
        return this.getPages().stream()
                .map(page -> page.getItemsByItemNum(itemNum))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public final Page nextPage() {
        if (actualPageNumber + 1 >= pages.size()) {
            return pages.get(pages.size() - 1);
        }

        this.actualPageNumber++;
        Page newPage = this.pages.get(this.actualPageNumber);

        callOnPageChangedCallback(newPage);

        newPage.setVisited();

        return newPage;
    }

    public final Page backPage() {
        if (actualPageNumber == 0) {
            return pages.get(0);
        }

        this.actualPageNumber--;
        Page newPage = pages.get(actualPageNumber);

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

    private Inventory createBaseInventory() {
        SupportedInventoryType supportedInventoryType = SupportedInventoryType.getByArray(items());

        return supportedInventoryType.getSize() % 9 == 0 ?
                Bukkit.createInventory(null, supportedInventoryType.getSize(), configuration.getTitle()) :
                Bukkit.createInventory(null, supportedInventoryType.getBukkitInventoryType(), configuration.getTitle());
    }
}
