package com.nepxion.discovery.plugin.strategy.adapter;

import com.netflix.loadbalancer.Server;

public interface RegionAdapter {

    String getRegion(Server server);

}
