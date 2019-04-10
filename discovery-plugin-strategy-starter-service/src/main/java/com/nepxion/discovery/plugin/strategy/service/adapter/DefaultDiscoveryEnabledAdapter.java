package com.nepxion.discovery.plugin.strategy.service.adapter;


/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */
 
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.plugin.strategy.adapter.AbstractDiscoveryEnabledAdapter;
import com.nepxion.discovery.plugin.strategy.service.context.ServiceStrategyContextHolder;
import com.netflix.loadbalancer.Server;

public class DefaultDiscoveryEnabledAdapter extends AbstractDiscoveryEnabledAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultDiscoveryEnabledAdapter.class);

    @Autowired
    private ServiceStrategyContextHolder serviceStrategyContextHolder;

    @Override
    protected String getVersionValue(Server server) {
        ServletRequestAttributes attributes = serviceStrategyContextHolder.getRestAttributes();
        if (attributes == null) {
            String serviceId = server.getMetaInfo().getAppName().toLowerCase();

            LOG.warn("The ServletRequestAttributes object is null, ignore to do version filter for service={}...", serviceId);

            return null;
        }

        return attributes.getRequest().getHeader(DiscoveryConstant.VERSION);
    }

    @Override
    public void setRegionValue(Server server, String region) {
        ServletRequestAttributes attributes = serviceStrategyContextHolder.getRestAttributes();
        if (attributes != null) {
            attributes.getRequest().setAttribute(DiscoveryConstant.PRIMARY_REGION, region);
        }
    }

    @Override
    public void revertRegionValue(Server server) {
        ServletRequestAttributes attributes = serviceStrategyContextHolder.getRestAttributes();
        if (attributes != null) {
            attributes.getRequest().removeAttribute(DiscoveryConstant.PRIMARY_REGION);
        }
    }

    @Override
    protected String getRegionValue(Server server) {
        ServletRequestAttributes attributes = serviceStrategyContextHolder.getRestAttributes();
        if (attributes == null) {
            String serviceId = server.getMetaInfo().getAppName().toLowerCase();

            LOG.warn("The ServletRequestAttributes object is null, ignore to do region filter for service={}...", serviceId);

            return null;
        }

        HttpServletRequest request = attributes.getRequest();
        Object primrayRegion = request.getAttribute(DiscoveryConstant.PRIMARY_REGION);
        if (primrayRegion != null) {
            return primrayRegion.toString();
        } else {
            return attributes.getRequest().getHeader(DiscoveryConstant.REGION);
        }
    }

    @Override
    protected String getBackUpRegionsValue(Server server) {
        ServletRequestAttributes attributes = serviceStrategyContextHolder.getRestAttributes();
        if (attributes == null) {
            return null;
        }

        return attributes.getRequest().getHeader(DiscoveryConstant.BACKUP_REGION);
    }

    @Override
    protected String getAddressValue(Server server) {
        ServletRequestAttributes attributes = serviceStrategyContextHolder.getRestAttributes();
        if (attributes == null) {
            String serviceId = server.getMetaInfo().getAppName().toLowerCase();

            LOG.warn("The ServletRequestAttributes object is null, ignore to do region filter for service={}...", serviceId);

            return null;
        }

        return attributes.getRequest().getHeader(DiscoveryConstant.ADDRESS);
    }
}