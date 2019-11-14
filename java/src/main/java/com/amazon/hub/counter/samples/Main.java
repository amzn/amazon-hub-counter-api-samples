/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: MIT-0
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.amazon.hub.counter.samples;

import com.amazon.hub.counter.AmazonHubCounterFeedAPI;
import com.amazon.hub.counter.entities.*;
import com.amazon.hub.counter.login.ClientCredentials;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class Main {

    static Integer MAX_RETRIES = 10;
    static Integer DELAY = 15;

    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) {

        Configuration config = loadConfigOrExit();

        ClientCredentials clientCredentials = ClientCredentials.builder()
                .clientId(config.getString("client_id"))
                .clientSecret(config.getString("client_secret"))
                .build();

        AmazonHubCounterFeedAPI api = new AmazonHubCounterFeedAPI( clientCredentials,
                config.getString("api_endpoint"),
                config.getString("auth_endpoint"));

        AccessPointsFeedRequest accessPointsFeedRequest = generateFeedData();

        String accessToken = api.getAccessToken();
        logger.info(accessToken);

        String feedId = api.postFeed(accessPointsFeedRequest,
                FeedType.STORE_FEED,
                accessToken);

        logger.info(feedId);

        Feed feed = getFeedStatusOrExit(api, accessToken, feedId);

        readOutputDocument(api, accessToken, feed);

    }

    private static Configuration loadConfigOrExit() {
        Configurations configs = new Configurations();
        Configuration config = null;
        try {
            logger.info("Loading configuration file...");

            File confFile = new File("config.properties");
            if(!confFile.exists()){
                throw new ConfigurationException("config.properties does not exists.");
            }

            config = configs.properties("config.properties");
            logger.info("Configuration loaded.");

        }
        catch (ConfigurationException cex)
        {
            logger.error("Failed to load config file.", cex);
            System.exit(1);
        }
        return config;
    }

    private static void readOutputDocument(AmazonHubCounterFeedAPI api, String accessToken, Feed feed) {
        OutputDocument document = api.getOutputDocument(feed.getFeedId(),
                feed.getOutputDocuments()[0].getDocumentId(), accessToken);

        if(document.getNoOfAccessPointsFailedToProcess() > 0){
            logger.error("Feed: [{}] Number of Failed stores: [{}]", feed.getFeedId(),
                    document.getNoOfAccessPointsFailedToProcess());

            AccessPointProcessingDetail[] docDetails = document.getFailedAccessPointProcessingDetails();

            for(Integer i = 0; i < docDetails.length; i++){
                for(Integer j = 0; j < docDetails[i].getErrors().length; j++){
                    logger.error("Feed: [{}] Store ID: [{}] msg: [{}]", feed.getFeedId(),
                            docDetails[i].getAccessPointId(), docDetails[i].getErrors()[j].getErrorMessage());
                }
            }
        }else{
            logger.info("Feed: [{}] all stores were successfully processed.",
                    feed.getFeedId());
        }
    }

    private static Feed getFeedStatusOrExit(AmazonHubCounterFeedAPI api,
                                            String accessToken, String feedId) {
        Feed feed;
        Integer retries = 0;
        do {
            feed = api.getFeedById(feedId, accessToken);

            if(feed.getStatus().equals("Processing")){
                logger.info("Feed: [{}] still Processing...", feedId);
            }

            if(retries >= MAX_RETRIES){
                logger.error("Feed: [{}] Max number of retries exceeded. Feed"
                        + " stuck on: [{}]", feedId, feed.getStatus());
                System.exit(1);
            }

            try {
                TimeUnit.SECONDS.sleep(DELAY);
            } catch(InterruptedException ex) {
                throw new RuntimeException("Delay interrupted.", ex);
            }

            retries += 1;

        } while(feed.getStatus().equals("Processing"));

        if(feed.getStatus().equals("Failed")){
            logger.error("Feed: [{}] the feed processing failed.", feedId);

            System.exit(1);
        }
        return feed;
    }

    private static AccessPointsFeedRequest generateFeedData() {
        AccessPoint[] accessPoints = new AccessPoint[1];

        Address address = Address.builder()
                .addressFieldOne("1918 8th Ave, Seattle, WA 98101, USA")
                .city("Seattle")
                .postalCode("98101")
                .countryCode("US")
                .latitude("47.615564")
                .longitude("-122.335819")
                .build();

        String[] capabilities = {"PICK_UP", "DROP_OFF"};

        ExceptionalClosure[] exceptionalClosures = new ExceptionalClosure[1];
        exceptionalClosures[0] = ExceptionalClosure.builder()
                .startDateTime("2038-01-19")
                .endDateTime("2038-01-20")
                .build();

        CommunicationDetails communicationDetails = CommunicationDetails.builder()
                .phoneNumber("00 1 206-922-0880")
                .emailId("store-id@example.com")
                .build();

        StandardHours[] standardHours = new StandardHours[6];

        standardHours[0] = StandardHours.builder()
                .day("MONDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        standardHours[1] = StandardHours.builder()
                .day("TUESDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        standardHours[2] = StandardHours.builder()
                .day("WEDNESDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        standardHours[3] = StandardHours.builder()
                .day("THURSDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        standardHours[4] = StandardHours.builder()
                .day("FRIDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .build();
        MidDayClosure[] midDayClosures = new MidDayClosure[1];
        midDayClosures[0] = MidDayClosure.builder()
                .startTime("12:00:00")
                .endTime("13:00:00")
                .build();
        standardHours[5] = StandardHours.builder()
                .day("SATURDAY")
                .openingTime("08:30:00")
                .closingTime("20:00:00")
                .midDayClosures(midDayClosures)
                .build();

        accessPoints[0] = AccessPoint.builder()
                .accessPointId("AMAZON-US-HQ")
                .accessPointName("Amazon Hub Counter - Amazon US HQ")
                .isActive(true)
                .isRestrictedAccess(false)
                .timeZone("America/Los_Angeles")
                .address(address)
                .terminationDate("2199-12-31")
                .capabilities(capabilities)
                .standardHoursList(standardHours)
                .exceptionalClosures(exceptionalClosures)
                .communicationDetails(communicationDetails)
                .build();

        return AccessPointsFeedRequest.builder()
                .accessPoints(accessPoints)
                .build();
    }

}