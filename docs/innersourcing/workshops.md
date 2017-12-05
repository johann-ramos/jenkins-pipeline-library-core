# Innersourcing session 2017-06-28

## Members
* Edwin Vinodj (Markets and Security)
    * contribute with the innersourcing
* Maja Adjoska (mobile native / Android)
    * bring + share knowledge to the team
* Rajesh Thirumanapalli(mobile native / Android)
    * bring + share knowledge to the team
* Dick van Maaren (frontend)
 * bring knowledge to the teams (avoid duplication of work / re-inventing the wheel)
 * have 1 single point of truth
* Secyz(?) (Channels)
    * learn best practices -> STPLs
    * share knowledge with teams
* Haresh(?) (Digital integration)
    * had heard good stories -> curious
    * best practices + whats new
* Haresh (Accounts + customers)
    * learn setup pipeline
* Allan Tony (SOLO / COESD)
    * spread the innersource word
* Devendra Sharma (?)
    * Gain and share knowledge
* Stefan Giatha (Corporate banking)
    * want to contribute
    * contribute without breaking it for other teams 
* Aranja (Corporate banking)
    * learn how to use implementing pipelines
* Joost v.d. Griendt / Wiebe de Roos (SOLO / COESD)
    * help others
    * spread innersource
    * work together + learn from each other
    * build innersourcing community
* Sanjif (Channels)
    * understand build java pipeline
    * learn front end pipeline next to java
* Tyrone Vriesde(Filtering + tikkie teams)
    * seeing teams struggeling
    * get better grip of what is going on (jenkins + pipeline)
    * having organisational stuff in place

## Questions (part 1)

### How to deal with offshore?

* start sharing knowledge in a decentralized way
* having good communications equipment
* fly to india to share knowledge

### Why create a fork instead of a branch?

* avoid adding special protection
* does not need access to BB
* better way to interact
* avoids naming discussions
* avoid overhead of maintenance

### How to add code to the core and not break anything?

* add versioning to all STPLs
* add validation to all STPLs
* add matrix with versions and features
* only master is "Live" (production)
* use proper release notes for every released version of the (core) pipeline

### Do we have a testing strategy?

* new OARD id => use it for examples project
* test using the examples project
* decide if we can use develop branch (continuous testing)
* there is no specialized test team for the core pipeline

### Can anyone contribute to the core library?

* yes, anyone can contribute to any STPL pipeline
* SOLO/COESD will check the changes to the core

## Questions (part 2)

### What is the average pipeline knowledge of the members?
* 50% has created a pipeline before
* 10% out of 50% has used declarative pipeline + used shared libraries 
* 50% has not (yet) created a PROD like pipeline

### Which programming language is declarative pipeline code?
* not a "real language"  

### Will Jenkins be used for all technologies?
* as of now: yes (at least for Java)
* MicroSoft => maybe VSTS in the future

### How can a team extend the STPL?
* we have to think of it (for example: support specialized STPLs for front end)

## Actions (SOLO/COESD)
* make glossary about naming conventions (node, slave, agent, etc)
* add link to syntax of declarative pipelines
* emphasize to use declarative pipeline code
* ask for intermediate feedback before moving to the next topic / level => (can ppl proceed with their pipelines?)
* prepare session for POs + scrum masters (to support the devs)
* make things more practical: build a working example together (centralized, handson session)
* Raoel: contact the 2 members of Android/Mobile. They would like to help pushing https://jira.nl.eu.abnamro.com/browse/SOLO-3144 forward
* explain/find a way how to know which features are already in the Java core library. As of now this is a bunch of code/methods inside specific java files. 
    * Suggestion: create groovyDoc + host output HTML and make it available to everyone.
* how to help devs convince the PO to work on STPL?
* make a valid business use case to convince PO about investing time on STPL 
* shift focus from SOLO => to other teams (to make work more visible)

