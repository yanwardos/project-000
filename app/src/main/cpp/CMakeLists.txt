cmake_minimum_required(VERSION 3.22.1)

project(VFR)

set(VUFORIA_ENGINE ${vuforia_engine_path})


target_link_libraries(
        TryVuforia
        # List libraries link to the target library
        android
        log
        ${ANDROID_LIBRARY}
        ${LOG_LIBRARY}
        ${GLES3_LIBRARY}
        ARCORE_LIBRARY
        VUFORIA_LIBRARY
)
