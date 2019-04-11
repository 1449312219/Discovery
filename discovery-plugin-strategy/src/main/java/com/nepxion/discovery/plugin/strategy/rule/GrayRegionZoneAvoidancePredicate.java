package com.nepxion.discovery.plugin.strategy.rule;

import java.util.Collections;
import java.util.List;

import org.springframework.util.ObjectUtils;

import com.nepxion.discovery.plugin.strategy.adapter.RegionAdapter;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.CompositePredicate;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidancePredicate;

public class GrayRegionZoneAvoidancePredicate extends ZoneAvoidancePredicate {
    private RegionAdapter regionAdapter;
    private CompositePredicate compositePredicate;

    public GrayRegionZoneAvoidancePredicate(IRule rule, IClientConfig clientConfig) {
        super(rule, clientConfig);
    }

    public GrayRegionZoneAvoidancePredicate(LoadBalancerStats lbStats, IClientConfig clientConfig) {
        super(lbStats, clientConfig);
    }

    @Override
    public List<Server> getEligibleServers(List<Server> servers, Object loadBalancerKey) {
        String requestRegion = regionAdapter.getRegionValue(servers.get(0));
        String[] backupRegions = regionAdapter.getAllBackUpRegions(null);
        if (ObjectUtils.isEmpty(requestRegion) || ObjectUtils.isEmpty(backupRegions)) {
            return Collections.emptyList();
        }

        try {
            for (String region : backupRegions) {
                regionAdapter.setRegionValue(null, region);
                List<Server> selected = super.getEligibleServers(servers, loadBalancerKey);
                if (!ObjectUtils.isEmpty(selected)) {
                    return selected;
                }
            }
            return Collections.emptyList();
        } finally {
            regionAdapter.revertRegionValue(null);
        }
    }

    @Override
    public boolean apply(PredicateKey input) {
        boolean enabled = super.apply(input);
        if (!enabled) {
            return false;
        }

        if (regionAdapter == null) {
            return true;
        }

        return compositePredicate.apply(input);
    }

    public void setRegionAdapter(RegionAdapter regionAdapter) {
        this.regionAdapter = regionAdapter;
    }

    public void setCompositePredicate(CompositePredicate compositePredicate) {
        this.compositePredicate = compositePredicate;
    }

}