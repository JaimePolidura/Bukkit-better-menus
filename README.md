# Bukkit-Menus

## Setup

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.JaimeTruman</groupId>
        <artifactId>Bukkit-better-menus</artifactId>
        <version>1.4.0</version>
    </dependency>
</dependencies>
```

```java
import es.bukkitbettermenus.BukkitBetterMenus;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        BukkitBetterMenus.setPlugin(this);
        BukkitBetterMenus.registerEventListeners(this, Bukkit.getPluginManager());
        BukkitBetterMenus.setInstanceProvider(/*Optional if you have dependency injection library*/);
    }
}
```

## Usage

1. [Simple example](#Simple-menu)
2. [Pagination](#Pagination)
3. [Auto synchronized](#Auto-synchronized-menu)
4. [Confirmation](#Confirmation)
5. [Number selector](#Number-selector)
6. [Timers](#Timers)
7. [Async tasks](#Async-tasks)
8. [Messaging](#Menu-messaging)

### Simple menu


```java
public class MenuOpenExample {
    public void open(Player player) {
        MenuService menuService = BukkitBetterMenus.MENU_SERVICE;
        Transaction transactionState = /**/;
        
        //A menu can have a state represented by Menu<class of state> 
        menuService.open(player, new SimpleMenu(), transactionState);
        
        //If you have dependency injection library. It will inject the dependencies of the constructor
        menuService.open(player, SimpleMenu.class, transactionState);
    }    
}

```

```java

import es.bukkitbettermenus.configuration.MenuConfiguration;
import es.bukkitbettermenus.utils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class SimpleMenu extends Menu<Transaction> {
    @Override
    public int[][] items() {
        return new int[][]{
                {1, 1, 1, 1, 1, 1, 1, 1, 1},
                {1, 0, 0, 0, 2, 0, 0, 9, 1},
                {1, 1, 1, 1, 1, 1, 1, 1, 1}
        };
    }

    @Override
    public MenuConfiguration configuration() {
        return MenuConfiguration.builder()
                .title("Simple menu")
                .fixedItems() //Players cannot take items
                .item(1, Material.BLACK_STAINED_GLASS_PANE, (player, event) -> player.sendMessage("Right"))
                .item(2, buildItemTransaction())
                .item(9, buildItemClose(), (player, event) -> player.closeInventory())
                .onClose(event -> event.getPlayer().sendMessage("You have closed the inventory"))
                .build();
    }
    
    private ItemStack buildItemTransaction() {
        return ItemBuilder.of(Material.PAPER)
                .title("Transaction")
                .lore(Arrays.asList(
                        "Payer: " + getState().payerName,
                        "Payee: " + getState().payeeName,
                        "money: " + getState().money
                ))
                .build();
    }

    private ItemStack buildItemClose() {
        return ItemBuilder.of(Material.BARRIER)
                .title(ChatColor.BOLD + "" + ChatColor.GOLD + "CLOSE")
                .build();
    }
}

public class Transaction {
    public String payerName;
    public String payeeName;
    public double money;
}
```

### Pagination

```java
import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.menustate.AfterShow;
import es.bukkitbettermenus.menustate.BeforeShow;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaginatedMenu extends Menu implements BeforeShow, AfterShow {
    @Override
    public int[][] items() {
        return new int[][]{
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 7, 8, 9}
        };
    }

    @Override
    public MenuConfiguration configuration() {
        return MenuConfiguration.builder()
                .title("Paginated")
                .fixedItems() //Players cannot take items
                .items(1, buildItems()) //Will return a list of items, which will fill all the inventory
                //The list of items will likely overflow the inventory. It will stop if it reaches the breakpoint (declared in config), 
                //When it reaches it, the remaining items will continue filling the next page
                .breakpoint(7)
                .paginated(PaginationConfiguration.builder()
                        .backward(8, Material.RED_WOOL)
                        .forward(9, Material.GREEN_WOOL)
                        .build())
                .onPageChanged(newPage -> getPlayer().sendMessage("You are in page " + (newPage.getPageId() + 1)))
                .build();
    }
    
    private List<ItemStack> buildItems() {
        return IntStream.range(0, 100)
                .mapToObj(i -> new ItemStack(Material.DIAMOND))
                .collect(Collectors.toList());
    }
    
    @Override
    public void afterShow(Player player) {
        //Called after inventory is opened
    }
    
    @Override
    public void beforeShow(Player player) {
        //Called before opening the inventory to the player
    }
}
```

### Auto synchronized menu

```java
import es.bukkitbettermenus.BukkitBetterMenus;
import es.bukkitbettermenus.MenuService;
import es.bukkitbettermenus.modules.sync.SyncMenuService;
import es.bukkitbettermenus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SynchronizedItemMarketMenu extends Menu {
    private ItemsMarketService itemsMarketService;

    @Override
    public int[][] items() {
        return new int[][]{
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 7, 8, 9}
        };
    }

    @Override
    public MenuConfiguration configuration() {
        return MenuConfiguration.builder()
                .title("titulo")
                .fixedItems("Synchronized")
                .items(1, oakLog(), (p, e) -> giveOneItemToPlayer(p, e.getCurrentItem(), e.getSlot()))
                .sync(SyncMenuConfiguration.builder()
                        .mapper(this::mapItemToSync)
                        .build())
                .breakpoint(7)
                .paginated(PaginationConfiguration.builder()
                        .backward(8, Material.RED_WOOL)
                        .forward(9, Material.GREEN_WOOL)
                        .build())
                .build();
    }
    
    private void giveOneItemToPlayer(Player player, ItemStack item, int slot) {
        super.decreaseActualQuantityOrRemove(slot);

        item = item.clone();
        item.setAmount(1);

        player.getInventory().addItem(item);
        
        //All the players who have open this menu will get updated.
        SyncMenuService syncMenuService = BukkitBetterMenus.SYNC_MENU_SERVICE;
        syncMenuService.sync(this);
        
        
        //If you need to sync the menu from outside the Menu class you can use:
        SyncMenuService syncMenuService = BukkitBetterMenus.SYNC_MENU_SERVICE;
        MenuService menuService = BukkitBetterMenus.MENU_SERVICE;
        syncMenuService.sync(SynchronizedItemMarketMenu.class, menuService.buildPages(/*any player*/, new SynchronizedItemMarketMenu()));
        //If you are using dependency injection
        syncMenuService.sync(SynchronizedItemMarketMenu.class, menuService.buildPages(/*any player*/, SynchronizedItemMarketMenu.class));
    }
    
    //When syncMenuService.sync(menu) is called, a copy of the menu's items will be displayed to all players, who have open the same menu.
    //If some of the items have to be different depending on the player, you can use this function. This function takes the original item from
    //the caller of syncMenuService.sync(menu) and returns the item to be displayed in the menu.
    private ItemStack mapSyncedOfertaItem(ItemStack itemToSync, Integer itemNum) {
        return itemToSync;
    }
    
    public ItemStack oakLog() {
        return ItemBuilder.of(Material.OAK_LOG)
                .amount(64)
                .build();
    }
}

```
### Confirmation

```java
import org.bukkit.Material;

public class ConfirmationMenu extends Menu {
    @Override
    public int[][] items() {
        return new int[][]{{1, 0, 3, 0, 2}};
    }
    
    @Override
    public MenuConfiguration configuration() {
        return MenuConfiguration.builder()
                .title("Confirmation")
                .fixedItems()
                .confirmation(ConfirmationConfiguration.builder()
                        //If set to true, when the player accepts/cancels player.closeInventory() will get called
                        //When ever the player accepts/cancels you want to open another menu, this should be set to false
                        //so that the new menu won't get closed
                        .closeOnAction(true)
                        .cancel(1, Material.RED_WOOL, this::onCancel)
                        .accept(2, Material.GREEN_WOOL, this::onAccept)
                        .build())
                .build();
    }

    public void onAccept(Player player, InventoryClickEvent event) {
    }

    public void onCancel(Player player, InventoryClickEvent event) {
    }
}
```

### Number selector
```java
import es.bukkitbettermenus.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class NumberSelectorMenu extends Menu<ConfirmationMenu.State> {
    @Override
    public int[][] items() {
        return new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {1, 2, 3, 0, 5, 0, 6, 7, 8},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }

    @Override
    public MenuConfiguration configuration() {
        return MenuConfiguration.builder()
                .title("Number selector")
                .fixedItems()
                .item(5, ItemBuilder.of(Material.PAPER)
                        .lore(Arrays.asList(String.valueOf(getState().initialValue)))
                        .build())
                .numberSelector(NumberSelectorMenuConfiguration.builder()
                        .initialValue(getState().initialValue)
                        .minValue(getState().minValue)
                        .maxValue(getState().maxValue)
                        .valuePropertyName("quantity")
                        .onValueChanged(this::onQuantityChanged)
                        .item(1, DECREASE, 10, buildItemNumberSelector(-10))
                        .item(2, DECREASE, 5, buildItemNumberSelector(-5))
                        .item(3, DECREASE, 1, buildItemNumberSelector(-1))
                        .item(6, INCREASE, 1, buildItemNumberSelector(1))
                        .item(7, INCREASE, 5, buildItemNumberSelector(5))
                        .item(8, INCREASE, 10, buildItemNumberSelector(10))
                        .build())
                .build();
    }

    private  void onQuantityChanged(double newValue) {
        int slotItemQuantity = super.getActualSlotByItemNum(5);
        
        super.setActualItemLore(slotItemQuantity, Arrays.asList(String.valueOf(newValue)));
        
        //You can access the value with:
        super.getPropertyDouble("quantity");
    }

    private ItemStack buildItemNumberSelector(int i) {
        Material material = i < 0 ? Material.RED_BANNER : Material.GREEN_BANNER;
        String title = i < 0 ? RED + "" + BOLD + i : GREEN + "" + BOLD + "+" + i;

        return ItemBuilder.of(material).title(title).build();
    }
    
    public class State {
        public double maxValue;
        public double minValue;
        public double initialValue;
    }
}
```
### Timers

```java
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class TimersMenu extends Menu {
    private final int maxSlots = 26;

    private int lastSlot = 0;

    @Override
    public int[][] items() {
        return new int[][]{
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }

    @Override
    public MenuConfiguration configuration() {
        return MenuConfiguration.builder()
                .title("Timers")
                .fixedItems()
                .item(1, Material.DIAMOND)
                .timers(MenuTimer.createTimer(TimerExecutionType.RUN_PERIODICALLY, 20L, this::onTimerTick))
                .build();
    }


    private void onTimerTick(BukkitRunnable selfTask, int numberTimesCalled) {
        ItemStack itemToMove = getActualItemBySlot(lastSlot);
        int nextSlot = lastSlot + 1;

        if (nextSlot > maxSlots) {
            nextSlot = 0;
        }

        super.setActualItem(nextSlot, itemToMove, 1);
        super.setActualItem(lastSlot, new ItemStack(Material.AIR), 0);
        
        getPlayer().playSound(getPlayer(), Sound.UI_BUTTON_CLICK, 10, 1);

        this.lastSlot = nextSlot;
    }
}

```
### Async tasks
```java
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

public class AsyncMenu extends Menu {
    @Override
    public int[][] items() {
        return new int[][]{
                {1, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0}
        };
    }

    @Override
    public MenuConfiguration configuration() {
        return MenuConfiguration.builder()
                .title("Async")
                .fixedItems()
                .item(1, Material.DIAMOND)
                .asyncTasks(AsyncTasksConfiguration.builder()
                        .onPageLoaded(1, this::onPageLoadedTask)
                        .wholeMenu(this::wholeMenuTask)
                        .build())
                .build();
    }
    
    private void wholeMenuTask() {
        //this will get run in other thread after the menu has been opened 
    }
    
    private void onPageLoadedTask(Page page, Integer slot, ItemStack item) {
        //This will get run in other thread for each item by a item num in a page when it is opened
    }
}

```
### Menu messaging

```java
import es.bukkitbettermenus.BukkitBetterMenus;
import es.bukkitbettermenus.modules.messaging.MessagingMenuService;
import es.bukkitbettermenus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CommunicationMenu extends Menu {
    @Override
    public int[][] items() {
        return new int[][]{{0, 0, 1, 0, 0}};
    }

    @Override
    public MenuConfiguration configuration() {
        return MenuConfiguration.builder()
                .title("Communication")
                .fixedItems()
                .item(1, buildItemSendMessage(), this::sendMessage)
                .messaging(MessagingConfiguration.builder()
                        .onMessage(ExampleMessage.class, this::onMessage)
                        .build())
                .build();
    }
    
    private void onMessage(ExampleMessage message) {
        getPlayer().sendMessage(message.sender + " has sent you this message");
    }
    
    private void sendMessage(Player player, InventoryClickEvent event) {
        MessagingMenuService messagingMenuService = BukkitBetterMenus.MESSAGING_MENU_SERVICE;
        
        ExampleMessage exampleMessage = new ExampleMessage();
        exampleMessage.sender = getPlayer().getName();
        
        messagingMenuService.broadCastMessage(CommunicationMenu.class, exampleMessage);
    }

    private ItemStack buildItemSendMessage() {
        return ItemBuilder.of(Material.OAK_BUTTON)
                .title("Send message")
                .build();
    }
    
    public class ExampleMessage {
        public String sender;
    }
}
```
