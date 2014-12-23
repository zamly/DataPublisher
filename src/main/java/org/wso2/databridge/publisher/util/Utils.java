/*
*  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.databridge.publisher.util;

import java.util.Random;

public class Utils {

    /** Name of the sensor */
    public static final String SENSOR_NAME = "SensorZ";

    /** Name of the sensor */
    public static final double UPPER_RANGE = 210;

    /** Name of the sensor */
    public static final double LOWER_RANGE = 160;

    private static final Random random = new Random();

    public static double getRandomDouble() {
        return LOWER_RANGE
                + (random.nextDouble() * (UPPER_RANGE - LOWER_RANGE));

    }

}
