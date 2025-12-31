package es.bukkitbettermenus.menubuilder;

import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.Page;
import es.bukkitbettermenus.configuration.MenuConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.Queue;

public class CollapseService {
    public void collapse(Menu<?> menu) {
        for (Page page : menu.getPages()) {
            for (MenuConfiguration.CollapseConfiguration collapseConfiguration : menu.getConfiguration().getCollapseConfiguration()) {
                collapse(page, collapseConfiguration);
            }
        }
    }

    private void collapse(Page page, MenuConfiguration.CollapseConfiguration collapseConfiguration) {
        int startSlot = page.getSlotByItemNum(collapseConfiguration.getStartItemNum());
        int endSlot = page.getSlotByItemNum(collapseConfiguration.getStartItemNum());
        Queue<Integer> emptySlotsToCollapse = new LinkedList<>();

        for (int currentSlot = startSlot; currentSlot <= endSlot; currentSlot++) {
            ItemStack itemInCurrentSlot = page.getItemBySlot(currentSlot);

            if (itemInCurrentSlot.getType() == collapseConfiguration.getCollapseMaterial()) {
                emptySlotsToCollapse.add(currentSlot);
            } else {
                if (!emptySlotsToCollapse.isEmpty()) {
                    int newSlot = emptySlotsToCollapse.poll();
                    page.swapItemBySlot(newSlot, currentSlot);
                }
            }
        }
    }
}
