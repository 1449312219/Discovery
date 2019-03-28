package com.nepxion.discovery.plugin.admincenter.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nepxion.discovery.common.constant.DiscoveryConstant;
import com.nepxion.discovery.plugin.framework.adapter.PluginAdapter;
import com.nepxion.discovery.plugin.framework.context.PluginContextAware;
import com.nepxion.discovery.plugin.framework.event.PluginEventWapper;
import com.nepxion.discovery.plugin.framework.event.RegionUpdatedEvent;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(path = "/region")
@Api(tags = { "Region接口" })
@RestControllerEndpoint(id = "region")
@ManagedResource(description = "Region Endpoint")
public class RegionEndpoint {
    @Autowired
    private PluginContextAware pluginContextAware;

    @Autowired
    private PluginAdapter pluginAdapter;

    @Autowired
    private PluginEventWapper pluginEventWapper;

    @RequestMapping(path = "/update-async", method = RequestMethod.POST)
    @ApiOperation(value = "异步推送更新Region配置", notes = "", response = ResponseEntity.class, httpMethod = "POST")
    @ResponseBody
    @ManagedOperation
    public ResponseEntity<?> updateAsync(@RequestBody @ApiParam(value = "Region", required = true) String region) {
        return update(region, true);
    }

    @RequestMapping(path = "/update-sync", method = RequestMethod.POST)
    @ApiOperation(value = "同步推送更新Region配置", notes = "", response = ResponseEntity.class, httpMethod = "POST")
    @ResponseBody
    @ManagedOperation
    public ResponseEntity<?> updateSync(@RequestBody @ApiParam(value = "Region", required = true) String region) {
        return update(region, false);
    }

    @RequestMapping(path = "/view", method = RequestMethod.GET)
    @ApiOperation(value = "查看Region配置信息", notes = "", response = ResponseEntity.class, httpMethod = "GET")
    @ResponseBody
    @ManagedOperation
    public ResponseEntity<String> view() {
        return ResponseEntity.ok().body(pluginAdapter.getRegion());
    }

    private ResponseEntity<?> update(String region, boolean async) {
        Boolean discoveryControlEnabled = pluginContextAware.isDiscoveryControlEnabled();
        if (!discoveryControlEnabled) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Discovery control is disabled");
        }

        Boolean isConfigRestControlEnabled = pluginContextAware.isConfigRestControlEnabled();
        if (!isConfigRestControlEnabled) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Config rest control is disabled");
        }

        pluginEventWapper.fireRegionUpdated(new RegionUpdatedEvent(region), async);

        return ResponseEntity.ok().body(DiscoveryConstant.OK);
    }

}