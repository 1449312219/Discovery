package com.nepxion.discovery.plugin.strategy.rule;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import com.nepxion.discovery.plugin.framework.decorator.ZoneAvoidanceRuleDecorator;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.CompositePredicate;

public class DiscoveryEnabledZoneAvoidanceRule extends ZoneAvoidanceRuleDecorator {
    private CompositePredicate compositePredicate;
    private DiscoveryEnabledZoneAvoidancePredicate discoveryEnabledPredicate;
    private GrayRegionZoneAvoidancePredicate grayRegionZoneAvoidancePredicate;

    public DiscoveryEnabledZoneAvoidanceRule() {
        super();
        discoveryEnabledPredicate = new DiscoveryEnabledZoneAvoidancePredicate(this, null);
        grayRegionZoneAvoidancePredicate = new GrayRegionZoneAvoidancePredicate(this, null);
        AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(this, null);

        compositePredicate = createCompositePredicate(discoveryEnabledPredicate, availabilityPredicate, grayRegionZoneAvoidancePredicate);
    }

    private CompositePredicate createCompositePredicate(DiscoveryEnabledZoneAvoidancePredicate discoveryEnabledPredicate, AvailabilityPredicate availabilityPredicate,
            GrayRegionZoneAvoidancePredicate grayRegionZoneAvoidancePredicate) {
        return CompositePredicate.withPredicates(discoveryEnabledPredicate, availabilityPredicate).addFallbackPredicate(grayRegionZoneAvoidancePredicate)
                // .addFallbackPredicate(AbstractServerPredicate.alwaysTrue())
                .build();
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        discoveryEnabledPredicate = new DiscoveryEnabledZoneAvoidancePredicate(this, clientConfig);
        grayRegionZoneAvoidancePredicate = new GrayRegionZoneAvoidancePredicate(this, null);
        AvailabilityPredicate availabilityPredicate = new AvailabilityPredicate(this, clientConfig);
        compositePredicate = createCompositePredicate(discoveryEnabledPredicate, availabilityPredicate, grayRegionZoneAvoidancePredicate);
    }

    @Override
    public AbstractServerPredicate getPredicate() {
        return compositePredicate;
    }

    public DiscoveryEnabledZoneAvoidancePredicate getDiscoveryEnabledPredicate() {
        return discoveryEnabledPredicate;
    }

    public GrayRegionZoneAvoidancePredicate getGrayRegionZoneAvoidancePredicate() {
        return grayRegionZoneAvoidancePredicate;
    }

}