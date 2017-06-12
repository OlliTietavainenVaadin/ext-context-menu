package com.vaadin.contextmenu.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.communication.URLReference;

@SuppressWarnings("serial")
public class ExtMenuSharedState extends AbstractComponentState {

    public List<ExtMenuItemState> menuItems;
    public boolean htmlContentAllowed;

    public static class ExtMenuItemState implements Serializable {
        public int id;
        public boolean separator;
        public String text;
        public boolean command;
        public URLReference icon;
        public boolean enabled;
        public String description;
        public boolean checkable;
        public boolean checked;
        public List<ExtMenuItemState> childItems;
        public String styleName;
        public boolean link;
        public static final String locationResource = "url";
        public String target = "_blank";
        public String features;
        public String url;
        public Map<String, String> parameters = new HashMap<>();

    }
}
