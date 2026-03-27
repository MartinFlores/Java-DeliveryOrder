package com.martin.appserver.controllers;

import com.martin.appserver.routing.Controller;
import com.martin.appserver.routing.Get;
import com.martin.appserver.utils.StandaloneNetworkUtils;
import java.util.Map;

@Controller("/api/v1")
public class AppsController {
    public AppsController() {
    }

    @Get("/apps")
    public String getApps(Map<String, String> queryParams) {
        String ip = StandaloneNetworkUtils.getLocalIp();
        String baseUrl = "http://" + ip + ":" + 7979;
        return "{\"superAdmin\":\"" + baseUrl + "/super-admin\",\"cocina\":\"" + baseUrl + "/cocina\",\"cajero\":\""
                + baseUrl + "/cajero\",\"mesero\":\"" + baseUrl + "/mesero\",\"menu\":\"" + baseUrl + "/menu\"}";
    }
}
