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

    public void setFilterItems(List<FilterItem> filterItems){
        this.filterItems = filterItems;
    }

    public List<FilterItem> getFilterItems(){
        return filterItems;
    }

    public void clearFilterItems(){
        filterItems.clear();
    }




    @Override
    public boolean matches(ProxyHttpRequestResponse proxyHttpRequestResponse) {
        return filterItems.stream().anyMatch(filterItem -> Tools.isThisRequestWouldBeShown(filterItem, proxyHttpRequestResponse));
    }
}
