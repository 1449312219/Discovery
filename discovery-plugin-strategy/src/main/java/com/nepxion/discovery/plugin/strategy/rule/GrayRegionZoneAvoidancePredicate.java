package com.nepxion.discovery.plugin.strategy.rule;

import org.apache.commons.lang.StringUtils;

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.plugin.framework.adapter.PluginAdapter;
import com.nepxion.discovery.plugin.strategy.adapter.RegionAdapter;
import com.nepxion.discovery.plugin.strategy.constant.RegionConstant;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidancePredicate;

public class GrayRegionZoneAvoidancePredicate extends ZoneAvoidancePredicate {
    private PluginAdapter pluginAdapter;
    private RegionAdapter regionAdapter;

    public GrayRegionZoneAvoidancePredicate(IRule rule, IClientConfig clientConfig) {
        super(rule, clientConfig);
    }

    public GrayRegionZoneAvoidancePredicate(LoadBalancerStats lbStats, IClientConfig clientConfig) {
        super(lbStats, clientConfig);
    }

    @Override
    public boolean apply(PredicateKey input) {
        boolean enabled = super.apply(input);
        if (!enabled) {
            return false;
        }

        return apply(input.getServer());
    }

    protected boolean apply(Server server) {
        if (regionAdapter == null) {
            return true;
        }

        String requestRegion = regionAdapter.getRegion(server);

        if (isEquals(requestRegion, RegionConstant.GRAY)) {
            String serverRegion = pluginAdapter.getServerMetadata(server).get(DiscoveryConstant.REGION);
            return isEquals(serverRegion, RegionConstant.PRDT);
        }

        return false;
    }

    private boolean isEquals(String actualRegion, RegionConstant expectedRegion) {
        return StringUtils.isNotEmpty(actualRegion) && expectedRegion.name().equalsIgnoreCase(actualRegion);
    }

    public void setPluginAdapter(PluginAdapter pluginAdapter) {
        this.pluginAdapter = pluginAdapter;
    }

    public void setRegionAdapter(RegionAdapter regionAdapter) {
        this.regionAdapter = regionAdapter;
    }

}