package com.nepxion.discovery.plugin.strategy.adapter;

import com.netflix.loadbalancer.Server;

interface RegionTempChangable {
    void setRegionValue(Server server, String region);

    void revertRegionValue(Server server);
}

public interface RegionAdapter extends RegionTempChangable {
    String[] getAllBackUpRegions(Server server);
}
