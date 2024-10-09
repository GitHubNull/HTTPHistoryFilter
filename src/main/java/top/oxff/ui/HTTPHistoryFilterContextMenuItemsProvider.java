package top.oxff.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.ToolType;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.proxy.ProxyHistoryFilter;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import burp.api.montoya.ui.contextmenu.ContextMenuEvent;
import burp.api.montoya.ui.contextmenu.ContextMenuItemsProvider;
import top.oxff.controler.MyProxyHistoryFilter;
import top.oxff.model.FilterItem;
import top.oxff.utils.Tools;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            if(lines.isEmpty()){
                return;
            }
            List<FilterItem> filterItems = Tools.parseFilterItems(lines);
            MyProxyHistoryFilter myProxyHistoryFilter = new MyProxyHistoryFilter(filterItems);
            List<ProxyHttpRequestResponse> proxyHttpRequestResponses = api.proxy().history(myProxyHistoryFilter);
            List<ProxyHttpRequestResponse> withoutDuplicationProxyHttpRequestResponses = new ArrayList<>();
            HashSet<String> requestMethodPathHttpVersionSet = new HashSet<>();
            for (ProxyHttpRequestResponse proxyHttpRequestResponse : proxyHttpRequestResponses) {
                String requestMethodPathHttpVersion = proxyHttpRequestResponse.request().method() + " " + proxyHttpRequestResponse.request().path() + " " + proxyHttpRequestResponse.request().httpVersion();
                if (!requestMethodPathHttpVersionSet.contains(requestMethodPathHttpVersion)) {
                    withoutDuplicationProxyHttpRequestResponses.add(proxyHttpRequestResponse);
                    requestMethodPathHttpVersionSet.add(requestMethodPathHttpVersion);
                }
            }
            SwingUtilities.invokeLater(()->{
                withoutDuplicationProxyHttpRequestResponses.forEach(proxyHttpRequestResponse -> proxyHttpRequestResponse.annotations().setNotes("[HTTPHistoryFilter]"));
            });
        });
    }

    private void initFilterByClipboardMenuItemWithoutDuplication(JMenuItem filterByClipboardMenuItemWithoutDuplication) {
        filterByClipboardMenuItemWithoutDuplication.addActionListener(e -> {
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)){
                    Transferable transferable = clipboard.getContents(null);
                    String clipboardContent = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    List<FilterItem> filterItems = Tools.parseFilterItems(clipboardContent.lines().collect(Collectors.toList()));
                    if (filterItems.isEmpty()){
                        return;
                    }
                    MyProxyHistoryFilter myProxyHistoryFilter = new MyProxyHistoryFilter(filterItems);
                    List<ProxyHttpRequestResponse> proxyHttpRequestResponses = api.proxy().history(myProxyHistoryFilter);
                    List<ProxyHttpRequestResponse> withoutDuplicationProxyHttpRequestResponses = new ArrayList<>();
                    HashSet<String> requestMethodPathHttpVersionSet = new HashSet<>();
                    for (ProxyHttpRequestResponse proxyHttpRequestResponse : proxyHttpRequestResponses) {
                        String requestMethodPathHttpVersion = proxyHttpRequestResponse.request().method() + " " + proxyHttpRequestResponse.request().path() + " " + proxyHttpRequestResponse.request().httpVersion();
                        if (!requestMethodPathHttpVersionSet.contains(requestMethodPathHttpVersion)) {
                            withoutDuplicationProxyHttpRequestResponses.add(proxyHttpRequestResponse);
                            requestMethodPathHttpVersionSet.add(requestMethodPathHttpVersion);
                        }
                    }
                    SwingUtilities.invokeLater(()->{
                        withoutDuplicationProxyHttpRequestResponses.forEach(proxyHttpRequestResponse -> proxyHttpRequestResponse.annotations().setNotes("[HTTPHistoryFilter]"));
                    });

                }
            } catch (IOException | UnsupportedFlavorException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void initFilterByImportFileMenuItemWithDuplication(JMenuItem filterByImportFileMenuItemWithDuplication) {
        filterByImportFileMenuItemWithDuplication.addActionListener(e -> {
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
            if(lines.isEmpty()){
                return;
            }
            List<FilterItem> filterItems = Tools.parseFilterItems(lines);
            MyProxyHistoryFilter myProxyHistoryFilter = new MyProxyHistoryFilter(filterItems);
            List<ProxyHttpRequestResponse> proxyHttpRequestResponses = api.proxy().history(myProxyHistoryFilter);
            proxyHttpRequestResponses.forEach(proxyHttpRequestResponse -> proxyHttpRequestResponse.annotations().setNotes("[HTTPHistoryFilter]"));
        });
    }

    private void initFilterByClipboardMenuItemWithDuplication(JMenuItem filterByClipboardMenuItemWithDuplication) {
        filterByClipboardMenuItemWithDuplication.addActionListener(e -> {
            try {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)){
                    Transferable transferable = clipboard.getContents(null);
                    String clipboardContent = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                    List<FilterItem> filterItems = Tools.parseFilterItems(clipboardContent.lines().collect(Collectors.toList()));
                    if (filterItems.isEmpty()){
                        return;
                    }
                    MyProxyHistoryFilter myProxyHistoryFilter = new MyProxyHistoryFilter(filterItems);
                    List<ProxyHttpRequestResponse> proxyHttpRequestResponses = api.proxy().history(myProxyHistoryFilter);
                    proxyHttpRequestResponses.forEach(proxyHttpRequestResponse -> proxyHttpRequestResponse.annotations().setNotes("[HTTPHistoryFilter]"));
                }
            } catch (IOException | UnsupportedFlavorException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
