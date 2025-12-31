package es.bukkitbettermenus.configuration;

import es.bukkitbettermenus.Page;
import es.bukkitbettermenus.modules.async.config.AsyncTasksConfiguration;
import es.bukkitbettermenus.modules.confirmation.ConfirmationConfiguration;
import es.bukkitbettermenus.modules.messaging.MessagingConfiguration;
import es.bukkitbettermenus.modules.numberselector.NumberSelectorControllItem;
import es.bukkitbettermenus.modules.numberselector.NumberSelectorMenuConfiguration;
import es.bukkitbettermenus.modules.pagination.PaginationConfiguration;
import es.bukkitbettermenus.modules.sync.SyncMenuConfiguration;
import es.bukkitbettermenus.modules.timers.MenuTimer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

@AllArgsConstructor
public class MenuConfiguration {
    @Getter private final Map<Integer, List<ItemStack>> items;
    @Getter private final Map<Integer, ItemClickedListener> onLeftClickEventListeners;
    @Getter private final Map<Integer, ItemClickedListener> onRightClickEventListeners;
    @Getter private final Consumer<InventoryCloseEvent> onCloseEventListener;
    @Getter private final String title;
    @Getter private final boolean fixedItems;
    @Getter private final int breakpointItemNum;
    @Getter private final PaginationConfiguration paginationConfiguration;
    @Getter private final ConfirmationConfiguration confirmationConfiguration;
    @Getter private final boolean staticMenu;
    @Getter private final MessagingConfiguration messagingConfiguration;
    @Getter private final NumberSelectorMenuConfiguration numberSelectorMenuConfiguration;
    @Getter private final Map<String, Object> properties;
    @Getter private final SyncMenuConfiguration syncMenuConfiguration;
    @Getter private final Consumer<Page> onPageChanged;
    @Getter private final List<MenuTimer> timers;
    @Getter private final AsyncTasksConfiguration asyncTasksConfiguration;
    @Getter private final List<CollapseConfiguration> collapseConfiguration;

    public static MenuConfigurationBuilder builder(){
        return new MenuConfigurationBuilder();
    }

    public <T> Consumer<T> getMessageListener(Class<T> messageType){
        return (Consumer<T>) this.messagingConfiguration.getOnMessageEventListeners().get(messageType);
    }

    public boolean isSync(){
        return this.syncMenuConfiguration != null;
    }

    public boolean hasMessagingConfiguration(){
        return this.messagingConfiguration != null;
    }

    public boolean isPaginated(){
        return this.paginationConfiguration != null;
    }

    public boolean isConfirmation(){
        return this.confirmationConfiguration != null;
    }

    public boolean isNumberSelector(){
        return this.numberSelectorMenuConfiguration != null;
    }

    public static class MenuConfigurationBuilder{
        private Map<Integer, List<ItemStack>> items;
        private Map<Integer, ItemClickedListener> onLeftClickEventListeners;
        private Map<Integer, ItemClickedListener> onRightClickEventListeners;
        private Consumer<InventoryCloseEvent> onCloseEventListener;
        private PaginationConfiguration menuPaginationConfiguration;
        private ConfirmationConfiguration confirmationConfiguration;
        private String title;
        private boolean fixedItems;
        private int breakpointItemNum;
        private boolean staticMenu;
        private MessagingConfiguration messagingConfiguration;
        private NumberSelectorMenuConfiguration numberSelectorMenuConfiguration;
        private final Map<String, Object> properties;
        private SyncMenuConfiguration syncMenuConfiguration;
        private Consumer<Page> onPageChanged;
        private List<MenuTimer> timers;
        private AsyncTasksConfiguration asyncTasksConfiguration;
        private List<CollapseConfiguration> collapseConfiguration;

        public MenuConfigurationBuilder(){
            this.timers = new ArrayList<>();
            this.items = new HashMap<>();
            this.onLeftClickEventListeners = new HashMap<>();
            this.onRightClickEventListeners = new HashMap<>();
            this.breakpointItemNum = -1;
            this.properties = new HashMap<>();
            this.fixedItems = true;
        }

        public MenuConfiguration build(){
            return new MenuConfiguration(items, onLeftClickEventListeners, onRightClickEventListeners, onCloseEventListener,
                    title, fixedItems, breakpointItemNum, menuPaginationConfiguration, confirmationConfiguration,
                    staticMenu, messagingConfiguration, numberSelectorMenuConfiguration, properties,
                    syncMenuConfiguration, onPageChanged, timers, asyncTasksConfiguration, collapseConfiguration);
        }

        public MenuConfigurationBuilder collapse(int startItemNum, int endItemNum) {
            collapse(Material.AIR, startItemNum, endItemNum);
            return this;
        }

        public MenuConfigurationBuilder collapse(Material material, int startItemNum, int endItemNum) {
            this.collapseConfiguration.add(CollapseConfiguration.builder()
                    .collapseMaterial(material)
                    .startItemNum(startItemNum)
                    .endItemNum(endItemNum)
                    .build());
            return this;
        }

        public MenuConfigurationBuilder collapse(CollapseConfiguration collapseConfiguration) {
            this.collapseConfiguration.add(collapseConfiguration);
            return this;
        }

        public MenuConfigurationBuilder collapse(List<CollapseConfiguration> collapseConfigurations) {
            this.collapseConfiguration.addAll(collapseConfigurations);
            return this;
        }

        public MenuConfigurationBuilder asyncTasks(AsyncTasksConfiguration asyncConfiguration){
            this.asyncTasksConfiguration = asyncConfiguration;
            return this;
        }

        public MenuConfigurationBuilder timers(List<MenuTimer> timers){
            this.timers.addAll(timers);
            return this;
        }

        public MenuConfigurationBuilder timers(MenuTimer... timers){
            this.timers.addAll(Arrays.asList(timers));
            return this;
        }

        public MenuConfigurationBuilder sync(SyncMenuConfiguration configuration){
            this.syncMenuConfiguration = configuration;
            return this;
        }

        public MenuConfigurationBuilder onPageChanged(Consumer<Page> onPageChanged) {
            this.onPageChanged = onPageChanged;
            return this;
        }

        public MenuConfigurationBuilder property(String key, Object value){
            this.properties.put(key, value);
            return this;
        }

        public MenuConfigurationBuilder properties(Map<String, Object> properties){
            this.properties.putAll(properties);
            return this;
        }

        public MenuConfigurationBuilder numberSelector(NumberSelectorMenuConfiguration configuration){
            this.numberSelectorMenuConfiguration = configuration;
            this.properties.put(configuration.getValuePropertyName(), configuration.getInitialValue());

            for(Map.Entry<Integer, NumberSelectorControllItem> entry: configuration.getItems().entrySet()){
                this.items.put(entry.getKey(), Collections.singletonList(entry.getValue().getItemStack()));
            }

            return this;
        }

        public MenuConfigurationBuilder noFixedItems(){
            this.fixedItems = false;
            return this;
        }

        public MenuConfigurationBuilder fixedItems(){
            this.fixedItems = true;
            return this;
        }

        public MenuConfigurationBuilder messaging(MessagingConfiguration messagingConfiguration){
            this.messagingConfiguration = messagingConfiguration;
            return this;
        }

        public MenuConfigurationBuilder confirmation(ConfirmationConfiguration configuration){
            this.confirmationConfiguration = configuration;
            this.items.put(configuration.getAccept().getItemNum(), Collections.singletonList(configuration.getAccept().getItemStack()));
            this.onLeftClickEventListeners.put(configuration.getAccept().getItemNum(), configuration.getAccept().getOnClick());

            this.items.put(configuration.getCancel().getItemNum(), Collections.singletonList(configuration.getCancel().getItemStack()));
            this.onLeftClickEventListeners.put(configuration.getCancel().getItemNum(), configuration.getCancel().getOnClick());


            return this;
        }

        public MenuConfigurationBuilder paginated(PaginationConfiguration paginationConfiguration){
            this.menuPaginationConfiguration = paginationConfiguration;
            this.items.put(paginationConfiguration.getBackward().getItemNum(), Collections.singletonList(paginationConfiguration.getBackward().getItemStack()));
            this.items.put(paginationConfiguration.getForward().getItemNum(), Collections.singletonList(paginationConfiguration.getForward().getItemStack()));
            if(this.breakpointItemNum == -1)
                this.breakpointItemNum = paginationConfiguration.getBackward().getItemNum();

            return this;
        }

        public MenuConfigurationBuilder staticMenu(){
            this.staticMenu = true;
            return this;
        }

        public MenuConfigurationBuilder itemsMap(Map<Integer, ItemStack> items){
            for (Map.Entry<Integer, ItemStack> itemsEntry : items.entrySet())
                this.items.put(itemsEntry.getKey(), Collections.singletonList(itemsEntry.getValue()));

            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, ItemStack item){
            this.items.put(itemNum, Collections.singletonList(item));
            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, ItemStack item, ItemClickedListener onLeftClick){
            this.items.put(itemNum, Collections.singletonList(item));
            this.onLeftClickEventListeners.put(itemNum, onLeftClick);
            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, ItemStack item, ItemClickedListener onLeftClick, ItemClickedListener onRightClick){
            this.items.put(itemNum, Collections.singletonList(item));
            this.onLeftClickEventListeners.put(itemNum, onLeftClick);
            this.onRightClickEventListeners.put(itemNum, onRightClick);
            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, Material itemMaterial){
            this.items.put(itemNum, Collections.singletonList(new ItemStack(itemMaterial)));
            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, Material itemMaterial, ItemClickedListener onLeftClick){
            this.items.put(itemNum, Collections.singletonList(new ItemStack(itemMaterial)));
            this.onLeftClickEventListeners.put(itemNum, onLeftClick);
            return this;
        }

        public MenuConfigurationBuilder item(int itemNum, Material itemMaterial, ItemClickedListener onLeftClick, ItemClickedListener onRightClick){
            this.items.put(itemNum, Collections.singletonList(new ItemStack(itemMaterial)));
            this.onRightClickEventListeners.put(itemNum, onRightClick);
            this.onLeftClickEventListeners.put(itemNum, onLeftClick);
            return this;
        }

        public MenuConfigurationBuilder basicItemsMap(Map<Integer, Material> items){
            items.forEach((itemNum, itemMaterial) -> {
                this.items.put(itemNum, Collections.singletonList(new ItemStack(itemMaterial)));
            });

            return this;
        }

        public MenuConfigurationBuilder onLeftClick(int itemNum, ItemClickedListener onLeftClick){
            this.onLeftClickEventListeners.put(itemNum, onLeftClick);
            return this;
        }

        public MenuConfigurationBuilder onRightClick(int itemNum, ItemClickedListener onRightClick){
            this.onRightClickEventListeners.put(itemNum, onRightClick);
            return this;
        }

        public MenuConfigurationBuilder onClick(int itemNum, ItemClickedListener onLeftClick, ItemClickedListener onRightClick){
            this.onRightClickEventListeners.put(itemNum, onRightClick);
            this.onLeftClickEventListeners.put(itemNum, onLeftClick);
            return this;
        }

        public MenuConfigurationBuilder items(int itemNum, List<ItemStack> items){
            this.items.put(itemNum, items);
            return this;
        }

        public MenuConfigurationBuilder items(int itemNum, List<ItemStack> items, ItemClickedListener onLeftClick){
            this.items.put(itemNum, items);
            this.onLeftClickEventListeners.put(itemNum, onLeftClick);
            return this;
        }

        public MenuConfigurationBuilder items(int itemNum, List<ItemStack> items, ItemClickedListener onLeftClick, ItemClickedListener onRightClick){
            this.items.put(itemNum, items);
            this.onRightClickEventListeners.put(itemNum, onRightClick);
            this.onLeftClickEventListeners.put(itemNum, onLeftClick);
            return this;
        }

        public MenuConfigurationBuilder onClose(Consumer<InventoryCloseEvent> onClose){
            this.onCloseEventListener = onClose;
            return this;
        }

        public MenuConfigurationBuilder title(String title){
            this.title = title;
            return this;
        }

        public MenuConfigurationBuilder title(TextComponent textComponent){
            this.title = textComponent.getText();
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum){
            this.breakpointItemNum = itemNum;
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, ItemClickedListener leftClick){
            this.breakpointItemNum = itemNum;
            this.onLeftClickEventListeners.put(itemNum, leftClick);
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, ItemClickedListener leftClick, ItemClickedListener rightClick){
            this.onRightClickEventListeners.put(itemNum, rightClick);
            this.onLeftClickEventListeners.put(itemNum, leftClick);
            this.breakpointItemNum = itemNum;
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, Material material){
            this.breakpointItemNum = itemNum;
            this.items.put(itemNum, Collections.singletonList(new ItemStack(material)));
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, Material material, ItemClickedListener leftClick){
            this.breakpointItemNum = itemNum;
            this.items.put(itemNum, Collections.singletonList(new ItemStack(material)));
            this.onLeftClickEventListeners.put(itemNum, leftClick);
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, Material material, ItemClickedListener leftClick, ItemClickedListener rightClick){
            this.items.put(itemNum, Collections.singletonList(new ItemStack(material)));
            this.onRightClickEventListeners.put(itemNum, rightClick);
            this.onLeftClickEventListeners.put(itemNum, leftClick);
            this.breakpointItemNum = itemNum;
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, ItemStack item){
            this.breakpointItemNum = itemNum;
            this.items.put(itemNum, Collections.singletonList(item));
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, ItemStack item, ItemClickedListener leftClick){
            this.breakpointItemNum = itemNum;
            this.items.put(itemNum, Collections.singletonList(item));
            this.onLeftClickEventListeners.put(itemNum, leftClick);
            return this;
        }

        public MenuConfigurationBuilder breakpoint(int itemNum, ItemStack item, ItemClickedListener leftClick, ItemClickedListener rightClick){
            this.items.put(itemNum, Collections.singletonList(item));
            this.onRightClickEventListeners.put(itemNum, rightClick);
            this.onLeftClickEventListeners.put(itemNum, leftClick);
            this.breakpointItemNum = itemNum;
            return this;
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CollapseConfiguration {
        private final int startItemNum;
        private final int endItemNum;
        private final Material collapseMaterial;
    }
}
