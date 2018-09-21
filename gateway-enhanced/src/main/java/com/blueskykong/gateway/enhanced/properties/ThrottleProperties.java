package com.blueskykong.gateway.enhanced.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author keets
 * @data 2018/9/18.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThrottleProperties {

    int capacity;

    int refillTokens;

    int refillPeriod;

    TimeUnit refillUnit = TimeUnit.SECONDS;
}
