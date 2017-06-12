package com.vaadin.contextmenu;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import com.vaadin.contextmenu.ExtContextExtMenu.ContextMenuOpenListener.ContextMenuOpenEvent;
import com.vaadin.contextmenu.client.ContextMenuClientRpc;
import com.vaadin.contextmenu.client.ContextMenuServerRpc;
import com.vaadin.contextmenu.client.ExtMenuSharedState;
import com.vaadin.contextmenu.client.ExtMenuSharedState.ExtMenuItemState;
import com.vaadin.event.ContextClickEvent;
import com.vaadin.event.ContextClickEvent.ContextClickListener;
import com.vaadin.event.ContextClickEvent.ContextClickNotifier;
import com.vaadin.server.AbstractExtension;
import com.vaadin.server.Resource;
import com.vaadin.server.ResourceReference;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Component;
import com.vaadin.util.ReflectTools;

@SuppressWarnings("serial")
public class ExtContextExtMenu extends AbstractExtension implements ExtMenu {

    private AbstractExtMenu menu = new AbstractExtMenu();

    private ContextClickListener contextClickListener = new ContextClickListener() {
        @Override
        public void contextClick(ContextClickEvent event) {
            fireEvent(new ContextMenuOpenEvent(ExtContextExtMenu.this, event));

            open(event.getClientX(), event.getClientY());
        }
    };

    /**
     * @param parentComponent
     *            The component to whose lifecycle the context menu is tied to.
     * @param setAsMenuForParentComponent
     *            Determines if this menu will be shown for the parent
     *            component.
     */
    public ExtContextExtMenu(AbstractComponent parentComponent,
            boolean setAsMenuForParentComponent) {
        extend(parentComponent);

        registerRpc(new ContextMenuServerRpc() {
            @Override
            public void itemClicked(int itemId, boolean menuClosed) {
                menu.itemClicked(itemId);
            }
        });

        if (setAsMenuForParentComponent) {
            setAsContextMenuOf(parentComponent);
        }
    }

    /**
     * Sets this as a context menu of the component. You can set one menu to as
     * many components as you wish.
     *
     * @param component
     *            the component to set the context menu to
     */
    public void setAsContextMenuOf(ContextClickNotifier component) {
        component.addContextClickListener(contextClickListener);
    }

    public void addContextMenuOpenListener(
            ContextMenuOpenListener contextMenuComponentListener) {
        addListener(ContextMenuOpenEvent.class, contextMenuComponentListener,
                ContextMenuOpenListener.MENU_OPENED);
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);

        // FIXME: think about where this is supposed to be

        /*
         * This should also be used by ExtMenuBar, upgrading it from Vaadin 6 to
         * Vaadin 7 communication mechanism. Thus to be moved e.g. to the
         * AbstractExtMenu.
         */
        ExtMenuSharedState extMenuSharedState = getState();
        extMenuSharedState.htmlContentAllowed = isHtmlContentAllowed();
        extMenuSharedState.menuItems = convertItemsToState(getItems());
    }

    public void open(int x, int y) {
        getRpcProxy(ContextMenuClientRpc.class).showContextMenu(x, y);
    }

    private List<ExtMenuItemState> convertItemsToState(List<ExtMenuItem> items) {
        if (items == null || items.size() == 0) {
            return null;
        }

        List<ExtMenuItemState> state = new ArrayList<>();

        for (ExtMenuItem item : items) {
            ExtMenuItemState extMenuItemState = new ExtMenuItemState();

            if (!item.isVisible()) {
                continue;
            }

            extMenuItemState.id = item.getId();
            extMenuItemState.text = item.getText();
            extMenuItemState.checkable = item.isCheckable();
            extMenuItemState.checked = item.isChecked();
            extMenuItemState.description = item.getDescription();
            extMenuItemState.enabled = item.isEnabled();
            extMenuItemState.separator = item.isSeparator();
            extMenuItemState.icon = ResourceReference.create(item.getIcon(), this,
                    "");
            extMenuItemState.styleName = item.getStyleName();

            extMenuItemState.childItems = convertItemsToState(item.getChildren());
            extMenuItemState.link = item.isLink();
            extMenuItemState.url = item.getUrl();

            state.add(extMenuItemState);
        }

        return state;
    }

    @Override
    protected ExtMenuSharedState getState() {
        return (ExtMenuSharedState) super.getState();
    }

    protected ContextClickListener getContextClickListener() {
        return contextClickListener;
    }

    // Should these also be in MenuInterface and then throw exception for
    // ExtMenuBar?
    public ExtMenuItem addSeparator() {
        // FIXME: this is a wrong way
        ExtMenuItemImpl item = (ExtMenuItemImpl) addItem("", null);
        item.setSeparator(true);
        return item;
    }

    public ExtMenuItem addSeparatorBefore(ExtMenuItem itemToAddBefore) {
        // FIXME: this is a wrong way
        ExtMenuItemImpl item = (ExtMenuItemImpl) addItemBefore("", null, null,
                itemToAddBefore);
        item.setSeparator(true);
        return item;
    }

    /**** Delegates to AbstractExtMenu ****/

    @Override
    public ExtMenuItem addItem(String caption, Command command) {
        return menu.addItem(caption, command);
    }

    @Override
    public ExtMenuItem addItem(String caption, Resource icon, Command command) {
        return menu.addItem(caption, icon, command);
    }

    @Override
    public ExtMenuItem addItem(String caption, boolean link, String url) {
        return menu.addItem(caption, link, url);
    }

    @Override
    public ExtMenuItem addItemBefore(String caption, Resource icon,
            Command command, ExtMenuItem itemToAddBefore) {
        return menu.addItemBefore(caption, icon, command, itemToAddBefore);
    }

    @Override
    public List<ExtMenuItem> getItems() {
        return menu.getItems();
    }

    @Override
    public void removeItem(ExtMenuItem item) {
        menu.removeItem(item);
    }

    @Override
    public void removeItems() {
        menu.removeItems();
    }

    @Override
    public int getSize() {
        return menu.getSize();
    }

    @Override
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        menu.setHtmlContentAllowed(htmlContentAllowed);
    }

    @Override
    public boolean isHtmlContentAllowed() {
        return menu.isHtmlContentAllowed();
    }

    /**** End of delegates to AbstractExtMenu ****/

    public interface ContextMenuOpenListener
            extends EventListener, Serializable {

        public static final Method MENU_OPENED = ReflectTools.findMethod(
                ContextMenuOpenListener.class, "onContextMenuOpen",
                ContextMenuOpenEvent.class);

        public void onContextMenuOpen(ContextMenuOpenEvent event);

        public static class ContextMenuOpenEvent extends EventObject {
            private final ExtContextExtMenu extContextMenu;

            private final int x;
            private final int y;

            private ContextClickEvent contextClickEvent;

            public ContextMenuOpenEvent(ExtContextExtMenu extContextMenu,
                    ContextClickEvent contextClickEvent) {
                super(contextClickEvent.getComponent());

                this.extContextMenu = extContextMenu;
                this.contextClickEvent = contextClickEvent;
                x = contextClickEvent.getClientX();
                y = contextClickEvent.getClientY();
            }

            /**
             * @return ExtContextExtMenu that was opened.
             */
            public ExtContextExtMenu getExtContextMenu() {
                return extContextMenu;
            }

            /**
             * @return Component which initiated the context menu open request.
             */
            public Component getSourceComponent() {
                return (Component) getSource();
            }

            /**
             * @return x-coordinate of open position.
             */
            public int getX() {
                return x;
            }

            /**
             * @return y-coordinate of open position.
             */
            public int getY() {
                return y;
            }

            public ContextClickEvent getContextClickEvent() {
                return contextClickEvent;
            }
        }
    }
}
