CMAKE_MINIMUM_REQUIRED(VERSION 2.8)
PROJECT(gesturizer CXX)

FIND_PACKAGE(Java)
FIND_PACKAGE(JNI)

SET(INCLUDE_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/src/main/include")
SET(MLEENINGS_INCLUDE_DIRECTORY "${INCLUDE_DIRECTORY}/com/github/mleenings/gesture/solver")
SET(MLEENINGS_SOURCE_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/src/main/c++/com/github/mleenings/gesture/solver")
SET(AQUILA_INCLUDE_DIRECTORY "${INCLUDE_DIRECTORY}/dsp")

set(Aquila_VERSION_MAJOR 3)
set(Aquila_VERSION_MINOR 0)
set(Aquila_VERSION_PATCH 0)
set(Aquila_VERSION_SUFFIX "-dev")
set(Aquila_VERSION "${Aquila_VERSION_MAJOR}.${Aquila_VERSION_MINOR}.${Aquila_VERSION_PATCH}${Aquila_VERSION_SUFFIX}")


# library headers
set(Aquila_HEADERS
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/aquila.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/global.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/functions.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/Exceptions.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/tools.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/filter.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/ml.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/filter/MelFilter.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/filter/MelFilterBank.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/ml/DtwPoint.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/ml/Dtw.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/SignalSource.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/Frame.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/FramesCollection.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/PlainTextFile.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/RawPcmFile.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/WaveFile.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/WaveFileHandler.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/Generator.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/SineGenerator.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/SquareGenerator.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/TriangleGenerator.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/PinkNoiseGenerator.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/WhiteNoiseGenerator.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/BarlettWindow.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/BlackmanWindow.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/FlattopWindow.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/GaussianWindow.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/HammingWindow.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/HannWindow.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/RectangularWindow.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/Fft.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/Dft.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/AquilaFft.h
#    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/OouraFft.h
#    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/FftFactory.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/Dct.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/Mfcc.h
#    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/Spectrogram.h
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/tools/TextPlot.h
)

# library sources
set(Aquila_SOURCES
    ${AQUILA_INCLUDE_DIRECTORY}/lib/ooura/fft4g.c
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/filter/MelFilter.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/filter/MelFilterBank.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/ml/Dtw.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/SignalSource.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/Frame.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/FramesCollection.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/PlainTextFile.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/WaveFile.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/WaveFileHandler.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/Generator.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/SineGenerator.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/SquareGenerator.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/TriangleGenerator.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/PinkNoiseGenerator.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/generator/WhiteNoiseGenerator.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/BarlettWindow.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/BlackmanWindow.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/FlattopWindow.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/GaussianWindow.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/HammingWindow.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/source/window/HannWindow.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/Dft.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/AquilaFft.cpp
#    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/OouraFft.cpp
#    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/FftFactory.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/Dct.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/Mfcc.cpp
#    ${AQUILA_INCLUDE_DIRECTORY}/aquila/transform/Spectrogram.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/aquila/tools/TextPlot.cpp
    ${AQUILA_INCLUDE_DIRECTORY}/lib/ooura/fft4g.c
)


IF("${ANDROID_NATIVE_API_LEVEL}")
    INCLUDE_DIRECTORIES(
        "${MLEENINGS_INCLUDE_DIRECTORY}"
        "${INCLUDE_DIRECTORY}")
ELSE("${ANDROID_NATIVE_API_LEVEL}")
    INCLUDE_DIRECTORIES(
        "${JAVA_INCLUDE_PATH}"
        "${JAVA_INCLUDE_PATH}/linux"
        "${MLEENINGS_INCLUDE_DIRECTORY}"
        "${INCLUDE_DIRECTORY}")
ENDIF("${ANDROID_NATIVE_API_LEVEL}")

ADD_LIBRARY(gesturizer SHARED
    "${MLEENINGS_SOURCE_DIRECTORY}/aquila-dsp/Dtw-native-lib.cpp"
    ${Aquila_HEADERS}
    ${Aquila_SOURCES})

SET_TARGET_PROPERTIES (
    gesturizer
    PROPERTIES
        LINKER_LANGUAGE CXX
        LIBRARY_OUTPUT_DIRECTORY "${CLASSPATH_TARGET_DIRECTORY}"
)

ADD_DEFINITIONS(-std=c++11)