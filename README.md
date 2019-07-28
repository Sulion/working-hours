# Working Hours

A Ktor-based web-service which transforms rather specific restaraunt working hours information into 
a text you actually could put onto your web-site or even print

## Build

This project targets JDK 12.
The project is Gradle-based and pure Kotlin, so there is nothing special about the build: 

    ./gradlew build

## Run

The build produces a fatjar which can be run directly:

    java -jar build/libs/working-hours-fat-1.0-SNAPSHOT.jar  
    
    
After that try to send `POST` to `http://localhost:8080` with JSONs described in the task.

## Format improvement 

To improve a data format one needs to know lots of conditions which cannot be known inside test assignment. 

* How much data in this format we already have?
* Who are the producers of this data and how likely they are to change? (If it is Google or government, 
there is little chance they'll listen) 
* Who are we going to improve this data for - for producers or for consumers?
* What are statistical properties of the data? Is there some standard patterns we could cover with enumerations?
* What is more computationally harder - produce and transfer data or process it?

All those questions I would have asked in a real project. However, this one is a test assignment.

I will list things that are not right about this format from the point of view of its consumer

* Day of the weeks in lowercase - it's easier to parse UPPERCASE strings or numbers
* Too verbose. To list a single period one needs at least 60-65 bytes.
* Too prone to corruption.
** It's too easy to miss one "open" or one "close" section
** During processing the collection of open/close sections it _must_ be ordered, otherwise you lose info
** If opening periods overlap (clear error in data), it's hard to spot
* It's necessary to look ahead because of late closings
* It's hard to extend - what if we want to communicate some exceptions - like, 
"Open each day 8 to 5, except second Wednesday of the month"

If we are changing as little as possible, I would suggest the following format: 

    { 
       "MONDAY": [
                    {
                      "openAt": 32400,
                      "for": 7200
                    },
                    {
                      "openAt": 57600,
                      "for": 25200
                    }
                 ]
    }
    
This format

- Is three times more economical - one section with 2 fields bears the same information as 2 sections with 
3 fields each in old format
- Less corruptible: if periods overlap and the order is lost, you can restore order sorting by `openAt` and 
checking if each next `openAt` later than current `openAt + for`
- No need for "next day" magic: nobody cares if you close after midnight, just add whatever hours to 
your opening date

But then there are some radical suggestion

If we know that there is no restaurant which opens at odd minutes of the hour - at 8.15AM, say, or closes 
at 12.05PM, if we know that they all open/close at N o'clock or at half past N, then we can represent each 
day of the week as a 48-bit bitmap. Each bit is set, if the restaurant open at this half-hour, and unset if 
closed. It's just 6 bytes per day - neat, right? Put the whole week in this array - we don't need to 
communicate names of days of the week, they are the same everywhere. 

Then there are restaurants that work everyday by the same schedule. Depending on how many such restaurants count
themselves among our clients, we could introduce the eigth day of the week: EVERYDAY. Like this:

      { 
         "EVERYDAY": [
                      {
                        "openAt": 32400,
                        "for": 7200
                      },
                      {
                        "openAt": 57600,
                        "for": 25200
                      }
                   ]
      }

This way we can communicate the whole schedule 7 times more efficiently.

I can think of another way to improve bandwidth of the message loop. Consider this:  

      [ 
         {
           "timetable": [
                          {
                            "openAt": 32400,
                            "for": 7200
                          },
                          {
                            "openAt": 57600,
                            "for": 25200
                          }
                       ],
           "days": [
                      MONDAY,
                      TUESDAY,
                      WEDNESDAY
                   ]
         },
         {
           "timetable": [],
           "days": [ THURSDAY ] 
                          
         }
      ]

Namely, define some kind of day timetable and then list the days it's applicable to. Days on which the place is
closed have empty timetable array. It's simple and space-efficient and we don't have to invent new weekdays.


Now, let us improve the format for parsing speed.

      { 
         "MONDAY": [
                      {
                        "openAt": "9 AM",
                        "closeAt": "11.30 AM"
                      },
                      {
                        "openAt": "6 PM",
                        "closeAt": "12.30 PM"
                      }
                   ]
      }

This format can be almost directly printed into the required result text, while still being parseable and 
verifiable at need.