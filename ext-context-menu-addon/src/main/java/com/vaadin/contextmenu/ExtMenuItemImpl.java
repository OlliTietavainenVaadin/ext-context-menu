package com.vaadin.contextmenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.vaadin.contextmenu.ExtMenu.Command;
import com.vaadin.server.Resource;

/**
 * A composite class for menu items and sub-menus. You can set commands to be
 * fired on user click by implementing the {@link ExtMenu.Command} interface. You
 * can also add multiple MenuItems to a ExtMenuItem and create a sub-menu.
 * 
 */
@SuppressWarnings("serial")
class ExtMenuItemImpl implements Serializable, ExtMenuItem {

    /** Private members * */
    private final int itsId;
    private Command itsCommand;
    private String itsText;
    private List<ExtMenuItem> itsChildren;
    private Resource itsIcon;
    private ExtMenuItem itsParent;
    private boolean enabled = true;
    private boolean visible = true;
    private boolean isSeparator = false;
    private String styleName;
    private String description;
    private boolean checkable = false;
    private boolean checked = false;
    private boolean link = false;
    private String target = "_blank";

    public void setLink(boolean link) {
        this.link = link;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    private String url = "";

    private void markAsDirty() {
        // FIXME we need to delegate this to the ExtMenuBar or better convert the
        // menubar to Vaadin 7 communication
        // and remove this method
    }

    /**
     * Constructs a new menu item that can optionally have an icon and a command
     * associated with it. Icon and command can be null, but a caption must be
     * given.
     * 
     * @param caption
     *            The text associated with the command
     * @param command
     *            The command to be fired
     * @throws IllegalArgumentException
     */
    public ExtMenuItemImpl(String caption, Resource icon, Command command) {
        if (caption == null) {
            throw new IllegalArgumentException("caption cannot be null");
        }

        itsId = getNextId();
        itsText = caption;
        itsIcon = icon;
        itsCommand = command;
    }

    public ExtMenuItemImpl(ExtMenuItem parent, String trim, Resource icon,
            Command object) {
        this(trim, icon, object);
        setParent(parent);
    }

    protected int getNextId() {
        // FIXME is this good enough? maybe just random?
        return UUID.randomUUID().hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#hasChildren()
     */
    @Override
    public boolean hasChildren() {
        return !isSeparator() && itsChildren != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#addSeparator()
     */
    @Override
    public ExtMenuItem addSeparator() {
        ExtMenuItem item = addItem(true, "", null, null, false, null);
        return item;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.example.contextmenu.menubar.ExtMenuItem#addSeparatorBefore(com.example
     * .contextmenu.menubar.ExtMenuItem)
     */
    @Override
    public ExtMenuItem addSeparatorBefore(ExtMenuItem itemToAddBefore) {
        ExtMenuItem item = addItemBefore(true, "", null, null, itemToAddBefore);
        return item;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#addItem(java.lang.String,
     * com.example.contextmenu.menubar.ExtMenu.Command)
     */
    @Override
    public ExtMenuItem addItem(String caption, Command command) {
        return addItem(caption, null, command);
    }

    @Override
    public ExtMenuItem addItem(String caption, String url) {
        return addItem(false, caption, null, null,true, url);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#addItem(java.lang.String,
     * com.vaadin.server.Resource, com.example.contextmenu.menubar.ExtMenu.Command)
     */
    @Override
    public ExtMenuItem addItem(String caption, Resource icon, Command command)
            throws IllegalStateException {
        return addItem(false, caption, icon, command, false, null);
    }

    private ExtMenuItem addItem(boolean separator, String caption, Resource icon,
            Command command, boolean isLink, String url) throws IllegalStateException {
        if (isSeparator()) {
            throw new UnsupportedOperationException(
                    "Cannot add items to a separator");
        }
        if (isCheckable()) {
            throw new IllegalStateException(
                    "A checkable item cannot have children");
        }
        if (caption == null) {
            throw new IllegalArgumentException("Caption cannot be null");
        }

        if (itsChildren == null) {
            itsChildren = new ArrayList<ExtMenuItem>();
        }

        ExtMenuItemImpl newItem = new ExtMenuItemImpl(this, caption, icon, command);
        newItem.setLink(isLink);
        newItem.setUrl(url);
        newItem.setSeparator(separator);

        itsChildren.add(newItem);

        markAsDirty();

        return newItem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.example.contextmenu.menubar.ExtMenuItem#addItemBefore(java.lang.String,
     * com.vaadin.server.Resource, com.example.contextmenu.menubar.ExtMenu.Command,
     * com.example.contextmenu.menubar.ExtMenuItem)
     */
    @Override
    public ExtMenuItem addItemBefore(String caption, Resource icon,
            Command command, ExtMenuItem itemToAddBefore) {
        return addItemBefore(false, caption, icon, command, itemToAddBefore);
    }

    private ExtMenuItem addItemBefore(boolean separator, String caption,
            Resource icon, Command command, ExtMenuItem itemToAddBefore)
            throws IllegalStateException {
        if (isCheckable()) {
            throw new IllegalStateException(
                    "A checkable item cannot have children");
        }
        ExtMenuItem newItem = null;

        if (hasChildren() && itsChildren.contains(itemToAddBefore)) {
            int index = itsChildren.indexOf(itemToAddBefore);
            newItem = new ExtMenuItemImpl(this, caption, icon, command);
            itsChildren.add(index, newItem);
        } else {
            newItem = addItem(caption, icon, command);
        }

        markAsDirty();

        return newItem;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#getCommand()
     */
    @Override
    public Command getCommand() {
        return itsCommand;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#getIcon()
     */
    @Override
    public Resource getIcon() {
        return itsIcon;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#getParent()
     */
    @Override
    public ExtMenuItem getParent() {
        return itsParent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#getChildren()
     */
    @Override
    public List<ExtMenuItem> getChildren() {
        return itsChildren;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#getText()
     */
    @Override
    public java.lang.String getText() {
        return itsText;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#getSize()
     */
    @Override
    public int getSize() {
        if (itsChildren != null) {
            return itsChildren.size();
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#getId()
     */
    @Override
    public int getId() {
        return itsId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#setCommand(com.example.
     * contextmenu .menubar.ExtMenu.Command)
     */
    @Override
    public void setCommand(Command command) {
        itsCommand = command;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#setIcon(com.vaadin.server.
     * Resource )
     */
    @Override
    public void setIcon(Resource icon) {
        itsIcon = icon;
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#setText(java.lang.String)
     */
    @Override
    public void setText(java.lang.String text) {
        if (text != null) {
            itsText = text;
        }
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#removeChild(com.example.
     * contextmenu .menubar.ExtMenuItem)
     */
    @Override
    public void removeChild(ExtMenuItem item) {
        if (item != null && itsChildren != null) {
            itsChildren.remove(item);
            if (itsChildren.isEmpty()) {
                itsChildren = null;
            }
            markAsDirty();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#removeChildren()
     */
    @Override
    public void removeChildren() {
        if (itsChildren != null) {
            itsChildren.clear();
            itsChildren = null;
            markAsDirty();
        }
    }

    /**
     * Set the parent of this item. This is called by the addItem method.
     * 
     * @param parent
     *            The parent item
     */
    protected void setParent(ExtMenuItem parent) {
        itsParent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#isVisible()
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    protected void setSeparator(boolean isSeparator) {
        this.isSeparator = isSeparator;
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#isSeparator()
     */
    @Override
    public boolean isSeparator() {
        return isSeparator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.example.contextmenu.menubar.ExtMenuItem#setStyleName(java.lang.String)
     */
    @Override
    public void setStyleName(String styleName) {
        this.styleName = styleName;
        markAsDirty();
    }

    @Override
    public boolean isLink() {
        return link;
    }

    @Override
    public String getUrl() {
        return url;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#getStyleName()
     */
    @Override
    public String getStyleName() {
        return styleName;
    }

    @Override
    public String getTarget() {
        return target;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.example.contextmenu.menubar.ExtMenuItem#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#isCheckable()
     */
    @Override
    public boolean isCheckable() {
        return checkable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#setCheckable(boolean)
     */
    @Override
    public void setCheckable(boolean checkable) throws IllegalStateException {
        if (hasChildren()) {
            throw new IllegalStateException(
                    "A menu item with children cannot be checkable");
        }
        this.checkable = checkable;
        markAsDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#isChecked()
     */
    @Override
    public boolean isChecked() {
        return checked;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.example.contextmenu.menubar.ExtMenuItem#setChecked(boolean)
     */
    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        markAsDirty();
    }

    protected void setChildren(List<ExtMenuItem> children) {
        this.itsChildren = children;
    }
}// class ExtMenuItem