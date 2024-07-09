cmake_minimum_required(VERSION 3.22.1)

project(VirtualFittingRoom)

set(VUFORIA_ENGINE ${vuforia_engine_path})

find_library(ANDROID_LIBRARY android)
find_library(GLES3_LIBRARY GLESv3)
find_library(LOG_LIBRARY log)

# locate vuforia engine library
add_library(VUFORIA_LIBRARY SHARED IMPORTED)
set_property(
        TARGET
        VUFORIA_LIBRARY
        PROPERTY
        IMPORTED_LOCATION
        ${VUFORIA_ENGINE}/build/lib/${ANDROID_ABI}/libVuforiaEngine.so
)

# locate arcore library
add_library(ARCORE_LIBRARY SHARED IMPORTED)
set_target_properties(
        ARCORE_LIBRARY
        PROPERTIES
        IMPORTED_LOCATION
        ${arcore_libpath}/${ANDROID_ABI}/libarcore_sdk_c.so
        INTERFACE_INCLUDE_DIRECTORIES ${arcore_include}
)

# configure building the app library
add_library(
        VirtualFittingRoom
        SHARED
        CrossPlatform/AppController.cpp
        CrossPlatform/tiny_obj_loader.cpp
        GLESRenderer.cpp
        GLESUtils.cpp
        VuforiaWrapper.cpp
)

# include directories
target_include_directories(
        VirtualFittingRoom
        PUBLIC
        CrossPlatform
        ${vuforia_engine_path}/build/include
        ${GLES3_INCLUDE_DIR}
)

# linker
target_link_libraries(
        VirtualFittingRoom
        # List libraries link to the target library
        android
        log
        ${ANDROID_LIBRARY}
        ${LOG_LIBRARY}
        ${GLES3_LIBRARY}
        ARCORE_LIBRARY
        VUFORIA_LIBRARY
)

