package com.nepxion.discovery.plugin.framework.adapter;

import java.util.List;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.netflix.eureka.EurekaClientConfigBean;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.web.client.RestTemplate;

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.common.exception.DiscoveryException;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

public class EurekaAdapter extends AbstractPluginAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(EurekaAdapter.class);

    private static final String URL_FORMATE = "%sapps/%s/%s/metadata?" + DiscoveryConstant.REGION + "=%s";

    @Autowired
    private EurekaInstanceConfigBean instanceConfig;

    @Autowired
    private EurekaClientConfigBean clientConfig;

    @Autowired
    @Qualifier("metadataUpdateRestTemplate")
    private RestTemplate rest;

    @Override
    public void setRegion(String region) {
        List<String> serverUrls = clientConfig.getEurekaServerServiceUrls(clientConfig.getAvailabilityZones(clientConfig.getRegion())[0]);
        for (String url : serverUrls) {
            try {
                rest.put(String.format(URL_FORMATE, url, instanceConfig.getAppname(), instanceConfig.getInstanceId(), region), null);
                super.setRegion(region);
                break;
            } catch (Exception e) {
                LOG.error("Upadte region error!", e);
            }
        }
    }

    @Override
    public Map<String, String> getServerMetadata(Server server) {
        if (server instanceof DiscoveryEnabledServer) {
            DiscoveryEnabledServer discoveryEnabledServer = (DiscoveryEnabledServer) server;

            return discoveryEnabledServer.getInstanceInfo().getMetadata();
        }

        throw new DiscoveryException("Server instance isn't the type of DiscoveryEnabledServer");
    }
}