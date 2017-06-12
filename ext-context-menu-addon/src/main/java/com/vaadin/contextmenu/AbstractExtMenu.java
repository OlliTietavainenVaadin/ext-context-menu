package com.vaadin.contextmenu;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.ClientConnector;
import com.vaadin.server.Resource;

public class AbstractExtMenu implements ExtMenu {

    private final List<ExtMenuItem> extMenuItems = new ArrayList<ExtMenuItem>();
    private boolean htmlContentAllowed;
    private ClientConnector connector;

    private void markAsDirty() {
        // FIXME check if it can be removed at all, or find a way to call it
        // when needed
        if (connector != null)
            connector.markAsDirty();
    }

    public AbstractExtMenu() {
    }

    public AbstractExtMenu(ClientConnector connector) {
        this.connector = connector;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.ExtMenu#addItem(java.lang.String,
     * com.example.contextmenu.AbstractExtMenu.Command)
     */
    @Override
    public ExtMenuItem addItem(String caption, Command command) {
        return addItem(caption, null, command);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.ExtMenu#addItem(java.lang.String,
     * com.vaadin.server.Resource, com.example.contextmenu.AbstractExtMenu.Command)
     */
    @Override
    public ExtMenuItem addItem(String caption, Resource icon, Command command) {
        if (caption == null) {
            throw new IllegalArgumentException("caption cannot be null");
        }
        ExtMenuItem newItem = new ExtMenuItemImpl(caption, icon, command);
        extMenuItems.add(newItem);
        markAsDirty();

        return newItem;

    }

    @Override
    public ExtMenuItem addItem(String caption, boolean link, String url) {
        if (caption == null) {
            throw new IllegalArgumentException("caption cannot be null");
        }
        ExtMenuItem newItem = new ExtMenuItemImpl(caption, null, null);
        newItem.setLink(link);
        newItem.setUrl(url);
        extMenuItems.add(newItem);
        markAsDirty();

        return newItem;

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.ExtMenu#addItemBefore(java.lang.String,
     * com.vaadin.server.Resource, com.example.contextmenu.AbstractExtMenu.Command,
     * com.example.contextmenu.AbstractExtMenu.ExtMenuItem)
     */
    @Override
    public ExtMenuItem addItemBefore(String caption, Resource icon,
            Command command, ExtMenuItem itemToAddBefore) {
        if (caption == null) {
            throw new IllegalArgumentException("caption cannot be null");
        }

        ExtMenuItem newItem = new ExtMenuItemImpl(caption, icon, command);
        if (extMenuItems.contains(itemToAddBefore)) {
            int index = extMenuItems.indexOf(itemToAddBefore);
            extMenuItems.add(index, newItem);

        } else {
            extMenuItems.add(newItem);
        }

        markAsDirty();

        return newItem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.ExtMenu#getItems()
     */
    @Override
    public List<ExtMenuItem> getItems() {
        return extMenuItems;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.ExtMenu#removeItem(com.example.contextmenu.
     * AbstractExtMenu .ExtMenuItem)
     */
    @Override
    public void removeItem(ExtMenuItem item) {
        if (item != null) {
            extMenuItems.remove(item);
        }
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.ExtMenu#removeItems()
     */
    @Override
    public void removeItems() {
        extMenuItems.clear();
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.ExtMenu#getSize()
     */
    @Override
    public int getSize() {
        return extMenuItems.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.ExtMenu#setHtmlContentAllowed(boolean)
     */
    @Override
    public void setHtmlContentAllowed(boolean htmlContentAllowed) {
        this.htmlContentAllowed = htmlContentAllowed;
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.ExtMenu#isHtmlContentAllowed()
     */
    @Override
    public boolean isHtmlContentAllowed() {
        return htmlContentAllowed;
    }

    void itemClicked(int itemId) {
        ExtMenuItem clickedItem = findItemById(itemId);
        if (clickedItem != null) {
            if (clickedItem.isCheckable())
                clickedItem.setChecked(!clickedItem.isChecked());

            if (clickedItem.getCommand() != null)
                clickedItem.getCommand().menuSelected(clickedItem);
        }
    }

    private ExtMenuItem findItemById(int id) {
        // TODO: create a map to avoid that?
        return findItemById(getItems(), id);
    }

    private ExtMenuItem findItemById(List<ExtMenuItem> items, int id) {
        if (items == null)
            return null;

        for (ExtMenuItem item : items) {
            if (item.getId() == id)
                return item;
            else {
                ExtMenuItem subItem = findItemById(item.getChildren(), id);
                if (subItem != null)
                    return subItem;
            }
        }

        return null;
    }
}
