package es.bukkitbettermenus.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class ItemUtils {
    public static String getLore(ItemStack item, int index){
        String rawString = item.getItemMeta().getLore().get(index);
        
        return rawString.replaceAll("§c", "")
                .replaceAll("§a", "")
                .replaceAll("§b", "")
                .replaceAll("§c", "")
                .replaceAll("§d", "")
                .replaceAll("§e", "")
                .replaceAll("§f", "")
                .replaceAll("§0", "")
                .replaceAll("§0'", "")
                .replaceAll("§1", "")
                .replaceAll("§2", "")
                .replaceAll("§3", "")
                .replaceAll("§4", "")
                .replaceAll("§5", "")
                .replaceAll("§6", "")
                .replaceAll("§7", "")
                .replaceAll("§9", "");
    }

    public static ItemStack setDisplayname(ItemStack item, String newDisplayName){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(newDisplayName);
        item.setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack setLore(ItemStack item, int index, String newLoreLine){
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        lore.set(index, newLoreLine);
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }
}
