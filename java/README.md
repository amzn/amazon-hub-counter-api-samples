
## Java Sample for Amazon Hub Counter API
This is a sample application that shows how to use the **amazon-hub-counter-sdk** module following the best practices for interacting with our API:

1. Creating or updating one or more Access Points.
2. Validating the data fields before submitting.
3. Submitting the Feed (the list of Access Points).
4. Store the returned Feed Id and keep logs of every action to help debugging.
5. Poll the API until the Feed processing is completed.
6. Fetch the Output Document and display and log any issues.

* The application logs information to the console output, and also more detailed information to a log file named amazon-counter-hub-api-cli.log.

### How to run:
1. Clone this repository.
2. Run:
```cd java/```
4. Compile the project:
    ```mvn clean compile assembly:single```
5. Create a copy of config.template.properties and name it config.properties in the location of the jar.
6. Set your client credentials in the config.properties file.
7. Run the jar:
    ```java -jar amazon-hub-counter-cli-1.0-jar-with-dependencies.jar```

#### Sample output
```
% java -jar amazon-hub-counter-cli
06 Aug 2019 12:26:28.966/UTC [INFO ] - Loading configuration file...
06 Aug 2019 12:26:29.237/UTC [INFO ] - Configuration loaded.
06 Aug 2019 12:26:29.244/UTC [INFO ] - Loading API configuration file...
06 Aug 2019 12:26:29.246/UTC [INFO ] - API configuration loaded.
06 Aug 2019 12:26:30.012/UTC [INFO ] - Atc|MQEBIGO...REDACTED
06 Aug 2019 12:26:30.504/UTC [INFO ] - Created Feed with FeedID: 8a1a350f-48fb-4305-a948-XXXXXXXXXXXX
06 Aug 2019 12:26:30.504/UTC [INFO ] - 8a1a350f-48fb-4305-a948-XXXXXXXXXXXX
06 Aug 2019 12:26:30.680/UTC [INFO ] - 50
06 Aug 2019 12:26:30.800/UTC [INFO ] - 8a1a350f-48fb-4305-a948-XXXXXXXXXXXX
06 Aug 2019 12:26:31.041/UTC [INFO ] - Feed: 8a1a350f-48fb-4305-a948-XXXXXXXXXXXX still Processing...
06 Aug 2019 12:26:46.239/UTC [INFO ] - Feed: 8a1a350f-48fb-4305-a948-XXXXXXXXXXXX still Processing...
06 Aug 2019 12:27:01.462/UTC [INFO ] - Feed: 8a1a350f-48fb-4305-a948-XXXXXXXXXXXX all stores were successfully processed.

Process finished with exit code 0
```

#### Sample output with errors
```
% java -jar amazon-hub-counter-cli
06 Aug 2019 12:28:46.285/UTC [INFO ] - Loading configuration file...
06 Aug 2019 12:28:46.431/UTC [INFO ] - Configuration loaded.
06 Aug 2019 12:28:46.454/UTC [INFO ] - Loading API configuration file...
06 Aug 2019 12:28:46.456/UTC [INFO ] - API configuration loaded.
06 Aug 2019 12:28:47.247/UTC [INFO ] - Atc|MQEBIO...REDACTED
06 Aug 2019 12:28:47.778/UTC [INFO ] - Created Feed with FeedID: 1cea5de6-f3ae-4863-9237-XXXXXXXXXXXX
06 Aug 2019 12:28:47.779/UTC [INFO ] - 1cea5de6-f3ae-4863-9237-XXXXXXXXXXXX
06 Aug 2019 12:28:47.955/UTC [INFO ] - 50
06 Aug 2019 12:28:48.100/UTC [INFO ] - 1cea5de6-f3ae-4863-9237-XXXXXXXXXXXX
06 Aug 2019 12:28:48.232/UTC [INFO ] - Feed: 1cea5de6-f3ae-4863-9237-XXXXXXXXXXXX still Processing...
06 Aug 2019 12:29:03.446/UTC [INFO ] - Feed: 1cea5de6-f3ae-4863-9237-XXXXXXXXXXXX still Processing...
06 Aug 2019 12:29:18.700/UTC [ERROR] - Feed: 1cea5de6-f3ae-4863-9237-XXXXXXXXXXXX Number of Failed stores: 1
06 Aug 2019 12:29:18.701/UTC [ERROR] - Feed: 1cea5de6-f3ae-4863-9237-XXXXXXXXXXXX Store ID: TEST-STORE-ID msg: [INVALID_OPENING_TIME] opening time 20:30:00 is greater than or equal to closing time.

Process finished with exit code 0
```

### FAQ
##### 1. Failed to load config file.

If you get the following error:
```
ERROR com.amazon.hub.counter.samples.Main - Failed to load config file. Could not locate: org.apache.commons.configuration2.io.FileLocator...
```
It's because the configuration file config.properties is not present in the directory of the jar.

##### 2. Max number of retries exceeded.
If the feeds never finish processing, contact Amazon (your assigned Technical Operations Manager or Technical Account Manager) supplying the feedId to help you troubleshoot the issue.

## Copyright and License
All content in this repository, unless otherwise stated, is Copyright © 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Except where otherwise noted, all examples in this collection are licensed under the MIT-0 license. The full license text is provided in the LICENSE file accompanying this repository.