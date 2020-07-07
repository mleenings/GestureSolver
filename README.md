# Gesture Solver
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/98ca663bec894044971be473ef6f8069)](https://www.codacy.com/manual/mleenings/gestureSolver?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=mleenings/gestureSolver&amp;utm_campaign=Badge_Grade)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) 
[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://github.com/mleenings/gestureSolver/graphs/commit-activity)
[![HitCount](http://hits.dwyl.io/mleenings/gestureSolver.svg)](http://hits.dwyl.io/mleenings/gestureSolver)

This is an example to recognize gestures with the Android Sensors.
The doing stuff:
1) Noise reducing of the sensor data (Pre-Gesture Classification)
2) Centroid Classification with the help of DTW, because we have time series (sensor data)
    -  Hierarchical clustering (gesture are far apart (for example, a small and a tall person makes a gesture))
    - Centroid Calculation with DBA (DTW Barycenter Averaging)
    - Calculation Average distance to the to the Centroid for every Centroid
3) real-time-data classified with step 2 and the result is a recognized gesture

(!) It's more a collection of useful Code pieces than a working/runnable App (backend-side). 

## Include in Android APK

Because this project relies on JNI the JNI libs have to be included in the APK. Add following lines to your `build.gralde`.
This will extract the shared object files from the JAR file into the intermediate directory.

```
task copyLocationSolverSharedObjectFiles(type: Copy)

copyLocationSolverSharedObjectFiles {
    description = 'Copies required shared object files into android jniLibs'
    from zipTree(System.getProperty("user.home") + "/.m2/repository/com/github/mleenings/navi/location-solver/$naviVersion/location-solver-${naviVersion}.jar")
    into "$buildDir/intermediates/locationSolverJniLibs/"
    include 'android/**'
}

preBuild.dependsOn copyLocationSolverSharedObjectFiles

```

And make sure that from the intermediate directory the files will be included as JNI libs. Add following lines to `android {...}`
```
android {
    sourceSets {
         main {
             jniLibs.srcDirs "$buildDir/intermediates/locationSolverJniLibs/android"
         }
     }
}
```
