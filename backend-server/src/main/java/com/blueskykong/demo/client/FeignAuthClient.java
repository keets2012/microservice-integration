package com.blueskykong.demo.client;

import com.blueskykong.demo.entity.Permission;
import com.blueskykong.demo.entity.UserAccess;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author keets
 * @date 2017/11/21
 */
@FeignClient(name = "auth")
public interface FeignAuthClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/userPermissions?userId={userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<Permission> getUserPermissions(@RequestParam("userId") String userId);

    @RequestMapping(method = RequestMethod.GET, value = "/api/userAccesses?userId={userId}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<UserAccess> getUserAccessList(@RequestParam("userId") String userId);
}
