package com.vaadin.contextmenu.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.vaadin.client.VConsole;
import com.vaadin.client.ui.VMenuBar.CustomMenuItem;

import java.util.logging.Logger;

public class VExtMenuItem extends CustomMenuItem {

    public VExtMenuItem() {
        Logger.getLogger("VExtMenuItem").info("VExtMenuItem constructor");
    }

    public void setSeparator(boolean separator) {
        isSeparator = separator;
        updateStyleNames();
        if (!separator) {
            setEnabled(enabled);
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
