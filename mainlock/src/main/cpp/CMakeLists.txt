cmake_minimum_required(VERSION 3.4.1)

set(FILE_DIR main_lock.cpp dlopen.h dlopen.c)

include_directories("${CMAKE_SOURCE_DIR}")
#aux_source_directory(${CMAKE_SOURCE_DIR} SRC_LIST)


add_library( # Specifies the name of the library.
        mainlockblock

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        main_lock.cpp
        dlopen.h
        dlopen.c
        ${FILE_DIR}
        )
add_library(include SHARED IMPORTED)


find_library(
        log-lib
        log
)





target_link_libraries(
        mainlockblock
        ${log-lib}
)


include_directories(

)