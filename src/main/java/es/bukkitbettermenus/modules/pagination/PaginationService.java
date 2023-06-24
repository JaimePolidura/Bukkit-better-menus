package es.bukkitbettermenus.modules.pagination;

import es.bukkitbettermenus.Menu;
import es.bukkitbettermenus.Page;

public final class PaginationService {
    public void goForward(Menu menu) {
        Page page = menu.nextPage();
        menu.replaceAllItems(page.getItems());
    }

    public void goBackward(Menu menu) {
        Page page = menu.backPage();
        menu.replaceAllItems(page.getItems());
    }
}
