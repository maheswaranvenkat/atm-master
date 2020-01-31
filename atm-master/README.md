
# Automatic Teller Machine

##### Implements Feature Set

1. Device has a supply of bank notes

2. Device can be asked how many of each type of note it currently has

3. Device can be initialised, but only once.

4. Notes can be added

5. Notes can be withdrawn

6. $5, $10, $20 and $50 notes are supported

7. If a requested withdrawal cannot be met a message will be provided

8. Dispensing money reduces the amount of money remaining

9. If a requested withdrawal cannot be met the amount of money remaining is unchanged

10. Data is persisted (in a H2 in memory database which can be swapped for permanent persistence for other environments
by configuration.)

## URLS

### Local

http://localhost:8081

## Usage

### User Interface

The application starts at the Initialise Automatic Teller Machine screen. After this is used to load money, 
you are taken to the Automatic Teller Ready screen, where you can use the drop down buttons to perform 
various tasks. The link in the top left hand corner will take you back to the first screen. 

### API Usage
	   
	   Content-Type: application/json
	   
	   POST  http://localhost:8081/api/initialise  Body   Initialises the ATM with specified amount unless it is already initialised
	   
	   GET  http://localhost:8081/api/help/money   Returns an example of the JSON that needs to be sent to initialise the ATM
	   
	   GET http://localhost:8081/api/withdraw/100  Returns a Cash object containing a list of Money objects
	    e.g. {"money":[{"type":"FIFTY","number":1},{"type":"TWENTY","number":3}]}
	   
	   PUT http://localhost:8081/api/load/{type}/{amount}  Type can be either 5 or 10 or 20 or 50, amount is the number of notes to load. 
	   Responds with confirmation of the number and type of notes loaded e.g 20/20 would respond with
	    {"money":[{"type":"FIFTY","number":0},{"type":"TWENTY","number":20}]}
	   
	   GET http://localhost:8081/api/check/{type}  Type can be either 20 or 50. Response is the number avialable.


#


 











