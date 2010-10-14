SGClient v0.1.1
================================================================================

ABSTRACT:
--------------------------------------------------------------------------------

The android version of the [SimpleGeo Java Client](http://github.com/simplegeo/simplegeo-java-client).

REQUIREMENTS:
--------------------------------------------------------------------------------

Android SDK Version 5 (2.0)

USAGE:
--------------------------------------------------------------------------------

The project is setup to work within an Eclipse environment. Since there is a dependency on the 
*simplegeo-java-client* jar, an ant task has been provided that will clone the HEAD of
[simplegeo-java-client](http://github.com/simplegeo/simplegeo-java-client).

Before running any of the ant targets, make sure that the sdk.dir value in *local.properties* is set to point to the android sdk on the local machine. 

<code>ant init</code>

You can produce the proper distribution directory by running the following ant task:

<code>ant dist</code>

Running the unit tests can only be done from Eclipse at the moment.

CHANGES FROM PREVIOUS VERSIONS:
--------------------------------------------------------------------------------
Version 0.1.1
* Created a LocationService class that will register itself with location providers. This allows
the SimpleGeo LocationService to provide a wrapper around location updates
* Callback notifications that announce when the device moves in and out of different regions
based on the SimpleGeo's PushPin service
* When a location notification is received, IRecords can now be updated automatically by
pre-registering the objects with the LocationService.

Version 0.1.0
* Initial commit

### Copyright (C) 2010 SimpleGeo Inc. All rights reserved.
