cmake_minimum_required(VERSION 3.9)
project(com)

set(CMAKE_CXX_STANDARD 11)

include_directories(.)
include_directories(syc)
include_directories(syc/am)
include_directories(syc/cproj)

add_executable(com
        syc/am/cntr/addBashInContextMenu.reg
        syc/am/cntr/ajs.md
        syc/am/cntr/Bgew.java
        syc/am/cntr/c05features.md
        syc/am/cntr/cfeatures.md
        syc/am/cntr/charArrAndcharPointer.md
        syc/am/cntr/charset.md
        syc/am/cntr/cmake.PNG
        syc/am/cntr/Const.java
        syc/am/cntr/corstaticlib.md
        syc/am/cntr/cpp150Advise.md
        syc/am/cntr/createStaticLibrary.md
        syc/am/cntr/dlTable.md
        syc/am/cntr/dockerLog.md
        syc/am/cntr/FundValImportImpl.java
        syc/am/cntr/gitUsage.md
        syc/am/cntr/MailReceiverStartup.java
        syc/am/cntr/mysqlDateTime.md
        syc/am/cntr/mysqlProcSample.md
        syc/am/cntr/notesOnConsArrInClang.md
        syc/am/cntr/one.js
        syc/am/cntr/outlink.md
        syc/am/cntr/README.md
        syc/am/cntr/recursively.md
        syc/am/cntr/relative.PNG
        syc/am/cntr/showslowSql.md
        syc/am/cntr/sortUsage.md
        syc/am/cntr/varargsInClang.md
        syc/am/cntr/varBeforeFor.jsp
        syc/am/cntr/warDirConsInMavenProj.md
        syc/am/usefulSnippets.md
        syc/cproj/projectUsingDll.md)
