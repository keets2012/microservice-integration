package com.blueskykong.demo.service;

import com.blueskykong.demo.client.FeignAuthClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by keets on 2017/12/7.
 */
@Service
public class AuthService {
    @Autowired
    private FeignAuthClient authClient;

    public boolean checkPermission(String url, long affairId, long roleId, long resourceId) {
/*        try {
            return authClient.checkResourcePermission(url, affairId, roleId, resourceId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }*/
        return false;
    }
}
