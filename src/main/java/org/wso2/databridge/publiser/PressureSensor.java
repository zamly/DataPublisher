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

package org.wso2.databridge.publiser;

import org.wso2.carbon.databridge.agent.thrift.Agent;
import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.*;
import org.wso2.databridge.publisher.util.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;
/**
 * Created by zamly-PC on 11/27/14.
 */
public class PressureSensor {

    public static final String STREAM_NAME = "org.wso2.lambda.pressuresensor";
    public static final String VERSION = "1.0.0";

    Logger log = Logger.getLogger("org.wso2.drawbridge.publisher");
    final int NumberOfValues = 100;
    final Date start = new Date();
    final Random random = new Random();


    /**
     * The main method.
     *
     * @param args
     *            the arguments
     * @throws MalformedURLException
     *             the malformed url exception
     * @throws AgentException
     *             the agent exception
     * @throws AuthenticationException
     *             the authentication exception
     * @throws TransportException
     *             the transport exception
     * @throws MalformedStreamDefinitionException
     *             the malformed stream definition exception
     * @throws StreamDefinitionException
     *             the stream definition exception
     * @throws DifferentStreamDefinitionAlreadyDefinedException
     *             the different stream definition already defined exception
     * @throws InterruptedException
     *             the interrupted exception
     * @throws UndefinedEventTypeException
     */
    public static void main(String[] args)
            throws UndefinedEventTypeException, AgentException,
            MalformedURLException, AuthenticationException,
            MalformedStreamDefinitionException, StreamDefinitionException,
            TransportException, InterruptedException,
            DifferentStreamDefinitionAlreadyDefinedException {


        PressureSensor pressureSensor = new PressureSensor();
        pressureSensor.testSendingEvent();
    }


    public void testSendingEvent()
            throws MalformedURLException, AuthenticationException, TransportException,
            AgentException, UndefinedEventTypeException,
            DifferentStreamDefinitionAlreadyDefinedException,
            InterruptedException,
            MalformedStreamDefinitionException,
            StreamDefinitionException {

        setTrustStoreParams();
        Thread.sleep(2000);

        Agent agent = new Agent();
        //according to the convention the authentication port will be 7611+100= 7711 and its host will be the same
        DataPublisher dataPublisher = new DataPublisher("tcp://localhost:7611", "admin", "admin", agent);
        //DataPublisher dataPublisherInshaf = new DataPublisher("tcp://10.100.5.185:7611", "admin", "admin");

        String streamId = getStreamID(dataPublisher);
        //String streamId2 = getStreamID(dataPublisherInshaf);

        log.info("1st stream defined: "+streamId);
        //log.info("2nd stream defined: "+streamId2);

        for (int i = 0; i < NumberOfValues; i++) {

            String sensorName = Utils.SENSOR_NAME;
            double randomValue = Utils.LOWER_RANGE
                    + (random.nextDouble() * (Utils.UPPER_RANGE - Utils.LOWER_RANGE));

            Long date = start.getTime();

            dataPublisher.publish(streamId, new Object[]{"127.0.0.1"}, new Object[]{Integer.toString(i)},
                    new Object[]{sensorName, randomValue, date});
            log.info("Event published to 1st stream");

//            dataPublisherInshaf.publish(streamId2, new Object[]{"127.0.0.1"}, new Object[]{Integer.toString(i)},
//                    new Object[]{sensorName, randomValue, date});

            Thread.sleep(1000);
        }


        Thread.sleep(3000);
        dataPublisher.stop();
    }


    /**
     * Gets the stream id if already defined
     * or else created a new one.
     *
     * @param dataPublisher
     *            the data publisher
     * @return the stream id
     * @throws AgentException
     *             the agent exception
     * @throws MalformedStreamDefinitionException
     *             the malformed stream definition exception
     * @throws StreamDefinitionException
     *             the stream definition exception
     * @throws DifferentStreamDefinitionAlreadyDefinedException
     *             the different stream definition already defined exception
     */
    private static String getStreamID(DataPublisher dataPublisher)
            throws AgentException,
            MalformedStreamDefinitionException,
            StreamDefinitionException,
            DifferentStreamDefinitionAlreadyDefinedException {

        String streamId = null;

        try {
            streamId = dataPublisher.findStreamId(STREAM_NAME, VERSION);
        } catch (Exception e) {

            streamId =
                    dataPublisher.defineStream("{" +
                            "  'name':'" + STREAM_NAME + "'," +
                            "  'version':'" + VERSION + "'," +
                            "  'nickName': 'Pressure Sensing Information'," +
                            "  'description': 'Some Desc'," +
                            "  'tags':['foo', 'bar']," +
                            "  'metaData':[" +
                            "          {'name':'ipAdd','type':'STRING'}" +
                            "  ]," +
                            "  'correlationData':[" +
                            "          {'name':'correlationId','type':'STRING'}" +
                            "  ]," +
                            "  'payloadData':[" +
                            "          {'name':'name','type':'STRING'}," +
                            "          {'name':'value','type':'DOUBLE'}," +
                            "          {'name':'date','type':'LONG'}" +
                            "  ]" +
                            "}");
        }

        return streamId;
    }


    public static void setTrustStoreParams() {
        File filePath = new File("src/main/resources");
        if (!filePath.exists()) {
            filePath = new File("resources");
        }
        String trustStore = filePath.getAbsolutePath();
        System.setProperty("javax.net.ssl.trustStore", trustStore + "/client-truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");

    }
}
