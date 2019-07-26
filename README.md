# Getting Started

### Reference Documentation

### Guides
#####初始化下载后需要在maven下添加如下包:
      cmd进入bin/libs目录下执行如下指令：
      mvn install:install-file -DgroupId=com.oracle -DartifactId=ojdbc -Dversion=8 -Dpackaging=jar -Dfile=ojdbc8.jar  
mvn install:install-file -DgroupId=com.microsoft.sqlserver -DartifactId=mssql-jdbc -Dversion=7.2.0.jre8 -Dpackaging=jar -Dfile=mssql-jdbc-7.2.0.jre8.jar  
