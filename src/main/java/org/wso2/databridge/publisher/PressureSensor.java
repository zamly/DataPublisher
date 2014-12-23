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

package org.wso2.databridge.publisher;

import org.wso2.carbon.databridge.agent.thrift.DataPublisher;
import org.wso2.carbon.databridge.agent.thrift.exception.AgentException;
import org.wso2.carbon.databridge.commons.exception.*;
import org.wso2.databridge.publisher.util.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.logging.Logger;

public class PressureSensor {

    /** Stream name */
    public static final String STREAM_NAME = "org.wso2.lambda.pressuresensor";
    /** Stream version */
    public static final String VERSION = "1.0.0";

    private static Logger log = Logger.getLogger("org.wso2.databridge.publisher");

    static int NumberOfValues = 2500;

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


                String cepHost = args[0];
                String cepPort = args[1];
                String cepUsername = args[2];
                String cepPassword = args[3];
                String bamHost = args[4];
                String bamPort = args[5];
                String bamUsername = args[6];
                String bamPassword = args[7];
                
                try{
                    int events = Integer.parseInt(args[8]);
                    
                    if(events > 0) {
                        NumberOfValues = events;
                    }
                }catch(NumberFormatException e) {
                    log.warning("Pass a numeric value for events.");
                    NumberOfValues = 0;
                }
                
                
                
        setTrustStoreParams();

        //Agent agent = new Agent();

        DataPublisher dataPublisherCEP = new DataPublisher("tcp://"+cepHost+":"+cepPort, cepUsername, cepPassword);
        DataPublisher dataPublisherBAM = new DataPublisher("tcp://"+bamHost+":"+bamPort, bamUsername, bamPassword);

        String streamId = defineStreamID(dataPublisherCEP);
        String streamId2 = defineStreamID(dataPublisherBAM);

        log.info("stream defined in CEP: "+streamId);
        log.info("stream defined in BAM: "+streamId2);

        for (int i = 0; i < NumberOfValues; i++) {

            String sensorName = Utils.SENSOR_NAME;
            double randomValue = Utils.getRandomDouble();

            Date start = new Date();
            Long date = start.getTime();
            //log.info( "checking date: "+ new Timestamp(date));

            dataPublisherCEP.publish(streamId, new Object[]{"127.0.0.1"}, new Object[]{Integer.toString(i)},
                    new Object[]{sensorName, randomValue, date});
            log.info(String.valueOf(randomValue));
            log.info("Event published to CEP\n");

            dataPublisherBAM.publish(streamId2, new Object[]{"127.0.0.1"}, new Object[]{Integer.toString(i)},
                    new Object[]{sensorName, randomValue, date});
            log.info("Event published to BAM\n");

            Thread.sleep(1000);
        }

        dataPublisherCEP.stop();
        dataPublisherBAM.stop();
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
    private static String defineStreamID(DataPublisher dataPublisher)
            throws AgentException,
            MalformedStreamDefinitionException,
            StreamDefinitionException,
            DifferentStreamDefinitionAlreadyDefinedException {

        String streamId = null;

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



        return streamId;
    }


    /**
     * Sets the trust store parameters
     */
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
