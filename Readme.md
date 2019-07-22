
# Vitezlo

The main purpose is to display hiking routes on GoogleMaps of a specific area for 
a mass hiking event called Vitezlo. See more info on Vitezlo:

http://www.szendroitura.hu
 
It is also featuring a GPS position logger that can record the user's route and disaplay it on the map.

## Work In Progress
Originally I have developed this application some time in 2013, when I was young and foolish, 
just like the Android guidelines and ecosystem (remember AsyncTask, Eclipse and UIAutomator?)
I have only done some minor incremental changes, broke up the massive Activity to smaller components
and did some path updates every year for this event.

Since that time I have learned a lot about Android and architectures, I have done a lot of refactoring 
to achieve testability of mission critical apps.

So I decided to show how to _refactor_ a legacy app like this one, into a modern, testable app.

I plan to write a couple of articles on the concepts and how to introduce them, hopefully soon.

### Stack when I started to work:
* Unused, unreliable features (Logger)
* No unit tests at all
* Static content (paths, descriptions)
* Java 7
* Massively outdated GoogleMaps
* AsyncTasks
* Crashes
* There isn't a consistent structure for the app

### What I am aiming for
* Only features that are used should stay in app
* Structured code (Clean Architecture + MVVM)
* Well tested code
* Dynamic, cached content
* Kotlin
* Better thread handling with coroutines
* Possible base for multi platform with  a Kotlin Native
* Each refactoring step would leave me with a releasable code

And also, one or two articles on how to do all that, without starting from scratch.