package top.oxff.controler;

import burp.api.montoya.proxy.ProxyHistoryFilter;
import burp.api.montoya.proxy.ProxyHttpRequestResponse;
import top.oxff.model.FilterItem;
import top.oxff.utils.Tools;

import java.util.List;

public class MyProxyHistoryFilter implements ProxyHistoryFilter {
    private List<FilterItem> filterItems;

    public MyProxyHistoryFilter(List<FilterItem> filterItems)
    {
        this.filterItems = filterItems;
    }


    @Override
    public boolean matches(ProxyHttpRequestResponse proxyHttpRequestResponse) {
        return filterItems.stream().anyMatch(filterItem -> Tools.isThisRequestWouldBeShown(filterItem, proxyHttpRequestResponse));
    }
}
