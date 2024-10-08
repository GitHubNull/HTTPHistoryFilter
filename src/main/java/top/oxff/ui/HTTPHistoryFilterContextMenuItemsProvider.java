package top.oxff.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import top.oxff.controler.MyProxyHistoryFilter;
import top.oxff.model.FilterItem;
import top.oxff.utils.Tools;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class HTTPHistoryFilterContextMenuItemsProvider implements ContextMenuItemsProvider {
    private final MontoyaApi api;
    public HTTPHistoryFilterContextMenuItemsProvider(MontoyaApi api) {
        this.api = api;
    }

    @Override
    public java.util.List<Component> provideMenuItems(ContextMenuEvent event) {
        if (!event.isFromTool(ToolType.PROXY, ToolType.LOGGER)) {
            return null;
        }
        List<Component> menuItemList = new ArrayList<>();
        JMenu filterByImportFileMenu = new JMenu("filterByImportFile");
        JMenuItem filterByImportFileMenuItemWithDuplication = new JMenuItem("filterByImportFileMenuItemWithDuplication");
        JMenuItem filterByImportFileMenuItemWithoutDuplication = new JMenuItem("filterByImportFileMenuItemWithoutDuplication");

        JMenu filterByClipboardMenu = new JMenu("filterByClipboard");
        JMenuItem filterByClipboardMenuItemWithDuplication = new JMenuItem("filterByClipboardMenuItemWithDuplication");
        JMenuItem filterByClipboardMenuItemWithoutDuplication = new JMenuItem("filterByClipboardMenuItemWithoutDuplication");

        initFilterByImportFileMenuItemWithDuplication(filterByImportFileMenuItemWithDuplication);
        initFilterByClipboardMenuItemWithDuplication(filterByClipboardMenuItemWithDuplication);

        initFilterByImportFileMenuItemWithoutDuplication(filterByImportFileMenuItemWithoutDuplication);
        initFilterByClipboardMenuItemWithoutDuplication(filterByClipboardMenuItemWithoutDuplication);

        menuItemList.add(filterByImportFileMenu);
        menuItemList.add(filterByClipboardMenu);
        return menuItemList;
    }

    private void initFilterByImportFileMenuItemWithoutDuplication(JMenuItem filterByImportFileMenuItemWithoutDuplication) {
        filterByImportFileMenuItemWithoutDuplication.addActionListener(e -> {
            String path = Tools.selectFile();
            if (path == null) {
                return;
            }
            List<String> lines = null;
            try {
                lines = Tools.readLinesFromFile(path);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            List<FilterItem> filterItems = Tools.parseFilterItems(lines);
            MyProxyHistoryFilter myProxyHistoryFilter = new MyProxyHistoryFilter(filterItems);
        });
    }

    private void initFilterByClipboardMenuItemWithoutDuplication(JMenuItem filterByClipboardMenuItemWithoutDuplication) {
    }

    private void initFilterByImportFileMenuItemWithDuplication(JMenuItem filterByImportFileMenuItemWithDuplication) {
    }

    private void initFilterByClipboardMenuItemWithDuplication(JMenuItem filterByClipboardMenuItemWithDuplication) {
    }
}
