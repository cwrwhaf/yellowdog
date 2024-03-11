# Documentation

Ive written this application in one folder for ease of sharing. Each folder works standalone and can be extracted to their own git repository.

## Run the application

### Start docker
``` 
cd docker
docker-compose up -d
```

### Start producer
```
cd producer
./mvnw spring-boot:run
```

### Trigger file ingestion
- NB path needs to be visible to the application. In this case the file is in the root of the project
```
curl --location --request GET 'http://localhost:8080/import?path=commodity_trade_statistics_data.csv&topic=global-commodity-trade-statistics-raw'
```

### Start consumer
```
cd consumer
./mvnw spring-boot:run
```

## Example Output
```#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [Canada : Re-Import] Aggregated Trade: [475111542799]
2024-03-10T14:29:08.788Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [Canada : Import] Aggregated Trade: [68914831085418]
2024-03-10T14:29:08.788Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [Canada : Export] Aggregated Trade: [84439095672731]
2024-03-10T14:29:08.788Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [Central African Rep. : Export] Aggregated Trade: [25481470695]
2024-03-10T14:29:08.788Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [Central African Rep. : Import] Aggregated Trade: [41611162437]
2024-03-10T14:29:08.788Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [Chad : Import] Aggregated Trade: [2242963111]
2024-03-10T14:29:08.788Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [Chile : Re-Import] Aggregated Trade: [1473946455]
2024-03-10T14:29:08.788Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [Chile : Import] Aggregated Trade: [8553848614332]
2024-03-10T14:29:08.788Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [Chile : Export] Aggregated Trade: [12048351737690]
2024-03-10T14:29:08.789Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [China : Re-Import] Aggregated Trade: [7334078301009]
2024-03-10T14:29:08.789Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [China : Import] Aggregated Trade: [160783247934200]
2024-03-10T14:29:08.790Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [China : Export] Aggregated Trade: [171620731940135]
2024-03-10T14:29:08.790Z  INFO 32310 --- [ntainer#0-0-C-1] c.yellowdog.listener.AggregatedListener  : Country: [China, Hong Kong SAR : Re-Import] Aggregated Trade: [10224915176]
```

# Assignment
## Step 1 (Configuration)
*Create a dockerfile to deploy a single container which will run a development instance of the
Apache Kafka streaming platform.* 

- Chose to go with apache/kafka:3.7.0 as its the official image. I figured plain vanilla would be better for a coding exercise.
There are a number of proprietary images which make it easier to create queues but one of them `wurstmeister/zookeeper` is already obsolete. 
I chose the KRaft configuration as they are deprecating the zookeeper configuration.
Ive used as much out of the box config as possible as this is a coding exercise. Using
`KAFKA_PROCESS_ROLES: broker,controller` which is the recomended configuration for a development setup
- Using compose.yml for speed, to use a dockerfile i would reference that in the compose.yml file and pass in the queue name


*Within the dockerfile include configuration that will also
create a single topic in kafka with a name of your choice. Don’t waste time looking at
scalability or security of this service.*

topic `global-commodity-trade-statistics-raw` is created in the docker compose file and
topic `global-commodity-trade-statistics-aggregated` is created by the application

## Step 2 – Development (Part 1)
*Write a single threaded producer application in Java which processes the csv formatted
contents of the global commodity trade statistics on Kaggle.com.*

- This requirement was the most ambiguous in my opinion. 
I chose to go with a solution that is triggered over a REST endpoint. 
Each endpoint runs in a single thread which i feel satisfies the requirement for single threaded.
The endpoint accepts a path to a file on the local filesystem which needs to be accessable to the application. 

*Publish each line item to your kafka topic as a json formatted message.*

- Assuming this just means take the csv headers as keys and each row is mapped to a json object


[dataset - global commodity trade statistics](https://www.kaggle.com/unitednations/global-commodity-trade-statistics)


## Development (Part 2)
*Write a consumer application in a language of your choice (it could be Java again) which
subscribes to your kafka topic and aggregates this data, reporting totals to the producer on
the import and export for each country_or_area in usd.*
- Went with spring kafka streams for this requirement becuase it makes it easy to aggregate data and pass on to another topic

*Extend the producer application to output the totals once all data has been processed.*
- Used spring kafka `@KafkaHandler`

## Report
*1) Clarify any assumptions you made whilst building the solution above.*
- Assuming a local file is acceptable for this assignment. This solution could be extended to pull the file from a url, unzip the file locally and pass the path to the rest of the application. I wrote with this in mind so we can easily extend it if needed.
- Assuming it's fine to use spring kafka based on the requirement to use well known and maintained libraries.


*2) Explain any questions would you have asked if you’d had the opportunity.*
- How are we triggering the file ingestion? 
- Is a rest endpoint acceptable to trigger?
- Do we want this to run periodically as this list is updated or as a one off? 
- Are there other csv files we want to ingest? This would affect how extendable we want to make the application.
- How long to we want the data to hang around in the kafka logs?
- Do we need this application to be highly available?
- Ive displayed all flows, including Re-Import and Re-Export, do we want to display this?


*3) Outline your options to extend this solution if we had asked you to also report on
   import/export by commodity.*
- The topology configuration looks like this is where we would extend this application if we want to add more aggregations

*4) Describe how you would improve both the performance and security of this
   application.*
### Security
- Kafka supports ssl out of the box by configuring the `ssl.` configuration which hooks up to the local keystore
- The rest endpoint can be configured using spring security `.authorised()` option using whichever mechanism Basic, Oauth is appropriate.

### Performance
- Im not really happy with my choice of csv parsing. I went with the library openCsv as ive used it in the past but not on such large files. 
It takes about 2 minutes before we start sending json becuase the library reads the whole file when converting to a bean. 
There is a line reader class in there but using this only saved 30 seconds and resulted in really ugly code.
- Kafka performance would have to play around with the number of partitions

*5) Assess the quality of your code and explain any improvements you might make given
   more time.*

I think this is still a way from production code and by far the largest tech test ive ever had to do, that said here are my thoughts on where i would improve. 
- First thing to address is the dtos, Ive copied and pasted the dtos to the same package in each project. These need to be extracted to a shared resource. I did it this way for speed of development.
- I would like to spend a bit more time working out the error handling in kafka.
- Currently im printing as soon as i get the result, would look nicer if i waited until the stream was fully ingested before printing out, I think configuring and waiting for an idle event? 
- I haven't really considered how to handle a cluster going down - reading the docs this is a very unlikely scenario but not impossible

- Theres nothing stopping us hitting the rest endpoint a number of times which results in the same data being sent.
The only way i can think of mitigating this is to supply a key based on the aggregation criteria so at least we'll only ingest the message once, so this needs a bit more of a look but left as is for easier development.

- The file read is unnacceptable, given more time i would look into some of the kafka connect libraries to read csv files.

- The aggregation syntax is new to me so theres probably a better way to do this. More time would give me a better understanding of this
- If this report isnt running all the time id look into starting and stopping a server `kafka.server.KafkaServer` looks like it does this
- application.properties and docker would be better if i used environment variables, especially application.properties as we could leak dev properties to prod with and embedded server, but i feel this is a bit of a stretch for a tech test
# Testing

### ImportController
- Testing rest exception handler returns correct status 404
- Bit more to setup to test the happy path

### ImportService
- Could test with csv files with blank values just to make sure we can handle this but as ive decided this isn't the best solution and it's a coding exercise ive ommitted these tests

### KafkaProducerService
- I always wonder what the point of these sorts of tests actually test but this was the most popular way to test the kafka template without creating an embedded server

### CommodityDataStreamListener
- Testing the aggregation logic is correct