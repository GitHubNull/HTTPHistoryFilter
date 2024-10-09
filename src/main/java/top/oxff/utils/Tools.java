package top.oxff.utils;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import top.oxff.model.FilterItem;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tools {

    // dialog for user to select file(*.txt)
    public static String selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // .txt file only
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.getName().toLowerCase().endsWith(".txt") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Text File";
            }
        });

        fileChooser.setDialogTitle("Select File");
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            return null;
        }
    }
    public static List<String> readLinesFromFile(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.strip());
            }
        }
        return lines;
    }

    public static FilterItem parseFilterItem(String line) {
        if (line == null || line.isEmpty()){
            return null;
        }
        String[] split = line.split(" ");
        return new FilterItem(split[0], split[1], split[2]);
    }

    public static List<FilterItem> parseFilterItems(List<String> lines) {
        List<FilterItem> filterItems = new ArrayList<>();
        for (String line : lines) {
            FilterItem filterItem = parseFilterItem(line);
            if (filterItem != null) {
                filterItems.add(filterItem);
            }
        }
        Set<FilterItem> filterItemSet = new HashSet<>(filterItems);
        return new ArrayList<>(filterItemSet);
    }

    public static boolean isThisRequestWouldBeShown(FilterItem filterItem, ProxyHttpRequestResponse proxyHttpRequestResponse) {
        if (filterItem == null || proxyHttpRequestResponse == null){
            return false;
        }
        HttpRequest httpRequest = proxyHttpRequestResponse.request();
        if (null == httpRequest){
            return false;
        }
        String method = httpRequest.method();
        String path = httpRequest.path();
        String httpVersion = httpRequest.httpVersion();
        return filterItem.getMethod().equals(method) && filterItem.getPath().equals(path) && filterItem.getHttpVersion().equals(httpVersion);
    }
}
