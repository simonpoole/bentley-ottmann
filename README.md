# Bentley-Ottmann
Java implementation of Bentley-Ottmann segments intersection finding algorithm.
The algorithm has n * log(n) time complexity, a naive approach has n^2.

Original version from Stanislav Antonov

This version ignores existing intersections and is compatible with older Android versions 
that do not support some Java 8 features that were used in the original code.

## Usage

``` java
    final BentleyOttmann bentleyOttmann = new BentleyOttmann(Coordinates::new);
    final List<ISegment> segments = new ArrayList<>();
    ...    
    // add segment (an implementation of ISegment)
    segments.add(....);
    ...
    bentleyOttmann.addSegments(segments);
    bentleyOttmann.findIntersections();
    List<IPoint> intersections = bentleyOttmann.intersections();
```

## Including in your project

We publish releases to [Maven Central](https://repo1.maven.org/maven2/ch/poole/misc/bentley-ottmann/).
The following snippets for `build.gradle` will make the library available in your Gradle project:

``` groovy
repositories {
    mavenCentral()
}
```

``` groovy
dependencies {
    implementation 'ch.poole.misc:bentley-ottmann:0.1.1'
}
``` 
