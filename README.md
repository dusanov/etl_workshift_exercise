# ETL Workshift Exercise

## Abstract
Task is to collect work shift data from the REST endpoint. The specs for
the endpoint are here: https://my.tanda.co/api/v2/documentation#shifts.
The endpoint generates random shift data for the previous week. The endpoint just
returns the data, no need to back it with the database. No need to provide authentication.

ETL job that fetches the data from the REST endpoint, transforms the
data and loads it into a database (H2). The resulting
database contains 4 tables:
* breaks - which containс all the `breaks` fetched from the shift data from the API.
* allowances - which contains all the `allowances` fetched from the shift data from
the API
* award_interpretations - which contains all the `award_interpretations` fetched
from the shift data from the API
* shifts - which should contain everything it does except for breaks, allowances,
awar_interpretation properties (arrays);

All the timestamps should be converted to EST timezone;

Breaks, allowances, award_interpretation should be enriched with shift_id
(corresponds to ‘id’ column in the shift object), shift_date (corresponds to ‘date’ in
shift object), sheet_id (corresponds to ‘sheet_id’ in shift object);

## Solution design
This projects consists of two Spring Boot apps:
* WorkShiftRestEndpoint - endpoint for generating random work shift data
* ETLJob - Console app to consume the endpoint, transform and load it to the H2 database.

### WorkShiftRestEndpoint
At boot time, app will randomly generate shift data for the previous week. This data will be served through the rest controller. There will be only two rest methods:
* get - serves all records
* get/{id} - serves one particular shift

#### Models
* shift

#### Startup
At boot time app will generate 5 shift objects and write out ids to the console:
`generated random id: 9894
 generated random id: 13414
 ...`
App is started with:

`mvn spring-boot:run`

or

`
java -jar dist/workshiftendpoint-0.0.1-SNAPSHOT.jar 
`
### ETLJob
Console app - consumes the endpoint, transform and loads it to the H2 database.

#### Models
* break
* allowance
* award_interpretation
* shift

#### Startup
To start and retrive one specific shift object:

`mvn spring-boot:run -Dspring-boot.run.arguments=1231`

or
`mvn package
java -jar target/etljobapp-0.0.1-SNAPSHOT.jar 2133`

To retrive more than one shift object:

`mvn spring-boot:run -Dspring-boot.run.arguments=1231,1234,1324`

or
`mvn package
java -jar target/etljobapp-0.0.1-SNAPSHOT.jar 2133,1324,431`

or
`java -jar dist/etljobapp-0.0.1-SNAPSHOT.jar 2133,1324,431`

no args means get all the shifts

## Testing
Since this is implemented with in memory H2, I understand this is a bit hard to test/confirm the data. When run with `mvn spring-boot:run  -Dspring-boot.run.arguments=95299,20447` so with the arguments, Spring boot gets stuck and does not shutdown which allows a loggin to H2 console:
http://localhost:8090/console
`user / password: sa / sa`
conn string:
`jdbc:h2:mem:work_shift?options=DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE`