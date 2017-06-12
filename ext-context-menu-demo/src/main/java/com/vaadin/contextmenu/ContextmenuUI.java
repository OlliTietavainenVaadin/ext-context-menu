package com.vaadin.contextmenu;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({ "serial", "unchecked" })
@Theme("contextmenu")
public class ContextmenuUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ContextmenuUI.class, widgetset = "com.vaadin.contextmenu.demo.DemoWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        Button button = new Button("Button 1");
        layout.addComponent(button);

        ExtContextExtMenu contextMenu = new ExtContextExtMenu(this, false);
        fillMenu(contextMenu);

        contextMenu.setAsContextMenuOf(button);
    }

    private void fillMenu(ExtMenu extMenu) {
        extMenu.addItem("Link", true, "https://www.vaadin.com");
        extMenu.addItem("Not a link", e -> Notification.show("clicked on 'Not a link'"));


    }
}