# qbio-metrics


The service is composed of 3 Docker containers:
- The `collector` container runs `collectl` in server mode. It sends data over socket connection at port 2655
- The `db` container runs a MySQL database. It is used to store and query the collected metrics
- The `metrics-service` container runs the web server in charge of data management (collection, tiering, deletion) and providing query APIs. It is implemented in Kotlin using Spring boot.

## How to start the service?
```bash
# Compile the code
./gradlew build

# Start collector and db containers in the background
./docker-compose up -d db collector

# Build service docker image and start container
./docker-compose up --build metrics-service
```

_Note_:
> My personal laptop is using M1 Chip, so the all the docker images used are for `ARMv8 64-bit` architecture. If your machine is not using this architecture, please update the images accordingly.

## How to run unit tests?
```bash
./gradlew test
```

## Usage Notes

### Metric Definition
The metrics to be collected are configured and stored in the DB. One metric is composed of a name and a formula. The formula specifies a combination of `collectl` headers used to compute the metric. It supports simple arithmetic operation with a single operator type (e.g. value1 + value2 + value3 or value1 / value2, etc.).
```bash
# This API returns all the existing metrics
curl -i -X GET http://localhost:8080/metrics

# This API can be used to configure new metrics
curl -i -X POST http://localhost:8080/metrics -d '{"name":"memory-available-percent", "formula": "[MEM]Free / [MEM]Tot"}' -H 'Content-Type: application/json'
```


### Data Collection
```bash
# This API opens a socket connection to collector server, and saves any received data in the DB.
curl -i -X PUT http://localhost:8080/collector/on

# This API closes any open connetion
curl -i -X PUT http://localhost:8080/collector/off

# This API returns all the headers currently available to use. It can be an helpful reference when entering new metrics
curl -i -X GET http://localhost:8080/collector/headers
```

### Query
#### Raw data
This API returns raw data for one metric. 
- The `metric-name` must match one of the metric configured, otherwise nothing is returned. 
- The `from` query parameter requires a timestamp in ISO format.
- The `to` query parameter is optional, if not provided all the data from `from` until now will be returned.
```
# GET /measurements/{metric-name}?from={timestamp_in_iso_format}&to={timestamp_in_iso_format}
curl -i "http://localhost:8080/measurements/memory-usage-percent?from=2022-02-26T04:02:11"
curl -i "http://localhost:8080/measurements/memory-usage-percent?from=2022-02-26T04:02:11&to=2022-02-26T04:12:11"
```

#### Aggregated result
This API returns raw data for one metric. 
- The `metric-name` must match one of the metric configured, otherwise nothing is returned. 
- The `aggregate-function` to be used, accepted values are `avg`, `min`, `max`.
- The `from` query parameter is mandatory. 
- The `to` query parameter is optional, if not provided all the data from `from` until now will be returned.
```
# GET /measurements/{metric-name}/aggregate/{aggregate-function}?from={timestamp_in_iso_format}&to={timestamp_in_iso_format}
curl -i "http://localhost:8080/measurements/memory-usage-percent/aggregate/avg?from=2022-02-26T04:02:11"
curl -i "http://localhost:8080/measurements/memory-usage-percent/aggregate/min?from=2022-02-26T04:02:11"
curl -i "http://localhost:8080/measurements/memory-usage-percent/aggregate/max?from=2022-02-26T04:02:11&to=2022-02-26T04:12:11"
```

#### Aggregated binned result
This API returns raw data for one metric. 
- The `metric-name` must match one of the metric configured, otherwise nothing is returned. 
- The `aggregate-function` to be used, accepted values are `avg`, `min`, `max`.
- The `bin-minute-amount` is the bin duration in minutes.
- The `from` query parameter is mandatory. 
- The `to` query parameter is optional, if not provided all the data from `from` until now will be returned.
```
# GET /measurements/{metric-name}/aggregate/{aggregate-function}/bin-minutes/{bin-minute-amount}?from={timestamp_in_iso_format}&to={timestamp_in_iso_format}
curl -i "http://localhost:8080/measurements/memory-usage-percent/aggregate/avg/bin-minutes/10?from=2022-02-26T04:02:11"
curl -i "http://localhost:8080/measurements/memory-usage-percent/aggregate/min/bin-minutes/30?from=2022-02-26T04:02:11"
curl -i "http://localhost:8080/measurements/memory-usage-percent/aggregate/max/bin-minutes/60?from=2022-02-26T04:02:11&to=2022-02-26T04:12:11"
```

### Data Retention
Data retention policy is defined by `data.retention.in.days` application property.
A scheduled task runs every hour on the `metrics-service` and deletes any data older than the configured retention policy.

### Tiering
Two levels of tiers are currently defined: minute-level and hour-level.
A scheduled task runs every minute to query raw data for the past minute, aggregate it and store it in DB.
A seperate scheduled task runs every hour to query raw data for the hour minute, aggregate it and store it in DB.

_Note_:
> I ran out of time to add much unit tests and make the query code leverage the tiered data. 
> 
> At a high level, here are my ideas for the tiered querying:
> - Add configurations in the application properties to define when to use each tier. For example, second level is for the data from the last 7 days, minute level is for data between 7 and 90 days and hour level is for data older than 90 days. These threshold can be adjusted to tune API response time.
> - At query time, service will query each tier of data based on the configuration and aggregate the data from each tier to return the full result set.
