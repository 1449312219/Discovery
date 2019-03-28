package com.nepxion.discovery.plugin.framework.event;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

import com.nepxion.discovery.common.exception.DiscoveryException;

public class RegionUpdatedEvent implements Serializable {
    private static final long serialVersionUID = 8068199967532940742L;
    
    private String region;

    public RegionUpdatedEvent(String region) {
        if (StringUtils.isNotEmpty(region)) {
            this.region = region.trim();
        } else {
            throw new DiscoveryException("Region can't be null or empty while updating");
        }
    }

    public String getRegion() {
        return region;
    }

}