# ETL Workshift Exercise

## Abstract
Task is to collect work shift data from the REST endpoint. The specs for
the endpoint are here: https://my.tanda.co/api/v2/documentation#shifts.
The endpoint generates random shift data for the previous week. The endpoint just
returns the data, no need to back it with the database. No need to provide authentication.

ETL job that fetches the data from the REST endpoint, transforms the
data and loads it into a database (Postgres). The resulting
database contains 4 tables:
* breaks - which containс all the `breaks` fetched from the shift data from the API.
* allowances - which contains all the `allowances` fetched from the shift data from
the API
* award_interpretations - which contains all the `award_interpretations` fetched
from the shift data from the API
* shifts - which should contain everything it does except for breaks, allowances,
awar_interpretation properties (arrays);
* batches - wrapper for all shift objects extracted in one run

All the timestamps should be converted to EST timezone;

Breaks, allowances, award_interpretation should be enriched with shift_id
(corresponds to ‘id’ column in the shift object), shift_date (corresponds to ‘date’ in
shift object), sheet_id (corresponds to ‘sheet_id’ in shift object);

## Solution design
This projects consists of two Spring Boot apps:
* WorkShiftRestEndpoint - endpoint for generating random work shift data
* ETLJob - Console app to consume the endpoint, transform and load it to the Postgres database.

### WorkShiftRestEndpoint
At boot time, app will randomly generate shift data for the previous week. This data will be served through the rest controller. There will be only two rest methods:
* get - serves all records
* get/{id} - serves one particular shift
* get/?ids={ids} - serves list of shift records based on comma separated list of shift ids

#### Models
* shift

#### Startup
At boot time app will generate 5 shift objects and write out ids to the console:
`generated random shift id: 9894
 generated random shift id: 13414
 ...`
App is started with:

`mvn spring-boot:run`

or

`
java -jar dist/workshiftendpoint-0.0.1-SNAPSHOT.jar 
`
### ETLJob
Console app - consumes the endpoint, transform and loads it to the Postgres database.

#### Models
* break
* allowance
* award_interpretation
* shift
* batch
* batch_shift_failed

#### Startup
At boot time Etl job will do get all shifts to the rest service and compare all the shift ids to ids from local list of shift ids plus local list of failed shift ids.
If there are new ids found, job will create new batch object, retrieve shift objects based on a list of comma separated ids and try to save them into the DB.
If save operation fails (Transform or Load) shift will be saved to batches_shifts_failed table.

`mvn spring-boot:run`

or
`mvn package
java -jar target/etljobapp-0.0.1-SNAPSHOT.jar`

or
`java -jar dist/etljobapp-0.0.1-SNAPSHOT.jar`
