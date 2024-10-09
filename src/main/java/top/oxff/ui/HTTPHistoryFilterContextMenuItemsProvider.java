package top.oxff.ui;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.core.HighlightColor;
import burp.api.montoya.core.ToolType;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
        JMenu filterByImportFileMenu = new JMenu("导入文本文件过滤");
        JMenuItem filterByImportFileMenuItemWithDuplication = new JMenuItem("不去重");
        JMenuItem filterByImportFileMenuItemWithoutDuplication = new JMenuItem("去重");

        JMenu filterByClipboardMenu = new JMenu("粘贴板过滤");
        JMenuItem filterByClipboardMenuItemWithDuplication = new JMenuItem("不去重");
        JMenuItem filterByClipboardMenuItemWithoutDuplication = new JMenuItem("去重");

        initFilterByImportFileMenuItemWithDuplication(filterByImportFileMenuItemWithDuplication);
        initFilterByClipboardMenuItemWithDuplication(filterByClipboardMenuItemWithDuplication);

        initFilterByImportFileMenuItemWithoutDuplication(filterByImportFileMenuItemWithoutDuplication);
        initFilterByClipboardMenuItemWithoutDuplication(filterByClipboardMenuItemWithoutDuplication);

        filterByClipboardMenu.add(filterByClipboardMenuItemWithDuplication);
        filterByClipboardMenu.add(filterByClipboardMenuItemWithoutDuplication);
        filterByImportFileMenu.add(filterByImportFileMenuItemWithDuplication);
        filterByImportFileMenu.add(filterByImportFileMenuItemWithoutDuplication);

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
            List<String> lines;
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
            FilterMarkConfigDialog filterMarkConfigDialog = new FilterMarkConfigDialog(this, withoutDuplicationProxyHttpRequestResponses);
            filterMarkConfigDialog.setVisible(true);
        });
    }
    private void initFilterByImportFileMenuItemWithDuplication(JMenuItem filterByImportFileMenuItemWithDuplication) {
        filterByImportFileMenuItemWithDuplication.addActionListener(e -> {
            String path = Tools.selectFile();
            if (path == null) {
                return;
            }
            List<String> lines;
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
            FilterMarkConfigDialog filterMarkConfigDialog = new FilterMarkConfigDialog(this, proxyHttpRequestResponses);
            filterMarkConfigDialog.setVisible(true);
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
                    api.logging().logToOutput("HTTP History Filter: " + clipboardContent);
                    FilterMarkConfigDialog filterMarkConfigDialog = new FilterMarkConfigDialog(this, withoutDuplicationProxyHttpRequestResponses);
                    filterMarkConfigDialog.setVisible(true);
                }
            } catch (IOException | UnsupportedFlavorException ex) {
                throw new RuntimeException(ex);
            }
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
                    api.logging().logToOutput("HTTP History Filter: " + clipboardContent);
                    FilterMarkConfigDialog filterMarkConfigDialog = new FilterMarkConfigDialog(this, proxyHttpRequestResponses);
                    filterMarkConfigDialog.setVisible(true);
                }
            } catch (IOException | UnsupportedFlavorException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void note(List<ProxyHttpRequestResponse> proxyHttpRequestResponseList, String note, boolean clearOldNote) {
        SwingUtilities.invokeLater(()->{
            if (clearOldNote){
                api.proxy().history().forEach(proxyHttpRequestResponse -> proxyHttpRequestResponse.annotations().setNotes(""));
            }
            proxyHttpRequestResponseList.forEach(proxyHttpRequestResponse -> proxyHttpRequestResponse.annotations().setNotes(note));
        });
    }

    public void highlight(List<ProxyHttpRequestResponse> proxyHttpRequestResponseList, HighlightColor highlightColor, boolean clearOldHighlight) {
        SwingUtilities.invokeLater(()->{
            if (clearOldHighlight){
                api.proxy().history().forEach(proxyHttpRequestResponse -> proxyHttpRequestResponse.annotations().setHighlightColor(HighlightColor.NONE));
            }
            proxyHttpRequestResponseList.forEach(proxyHttpRequestResponse -> proxyHttpRequestResponse.annotations().setHighlightColor(highlightColor));
        });
    }
}
