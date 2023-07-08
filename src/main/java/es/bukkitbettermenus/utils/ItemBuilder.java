package es.bukkitbettermenus.utils;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ItemBuilder {
    private final ItemStack itemStack;
    private final ItemMeta itemMeta;

    private ItemBuilder(Material material) {
        this.itemStack = new ItemStack(material);
        this.itemMeta = itemStack.getItemMeta();
    }

    public static ItemBuilder of(@NonNull Material material) {
        return new ItemBuilder(material);
    }

    public ItemBuilder title(String title) {
        this.itemMeta.setDisplayName(title);

        return this;
    }

    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);

        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.itemMeta.setLore(lore);

        return this;
    }

    public ItemBuilder lore(String lore) {
        this.itemMeta.setLore(Collections.singletonList(lore));

        return this;
    }

    public ItemBuilder duration(short duration) {
        this.itemStack.setDurability(duration);

        return this;
    }

    public ItemBuilder addEnchanments(Map<Enchantment, Integer> enchantments) {
        for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()){
            addEnchantment(entry.getKey(), entry.getValue());
        }

        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
        if(this.itemStack.getType() == Material.ENCHANTED_BOOK){
            ((EnchantmentStorageMeta) itemMeta).addStoredEnchant(enchantment, level, true);
        }else{
            this.itemMeta.addEnchant(enchantment, level, true);
        }

        return this;
    }

    public ItemBuilder inbreakable() {
        this.itemMeta.setUnbreakable(true);

        return this;
    }

    public ItemStack build() {
        this.itemStack.setItemMeta(itemMeta);

        return itemStack;
    }

    public ItemStack buildAddInventory(Inventory inventory, int index) {
        this.itemStack.setItemMeta(itemMeta);

        inventory.setItem(index, this.itemStack);

        return itemStack;
    }

    public ItemStack buildAddInventory(Inventory inventory) {
        this.itemStack.setItemMeta(itemMeta);

        inventory.addItem(this.itemStack);

        return itemStack;
    }
}
