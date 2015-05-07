#! /usr/bin

alias sbtclean="find . -name target -type d -exec rm -rf {} \;"

# prod start script
/home/liubin/temp/shop-1.0.0-SNAPSHOT/bin/shop -Dhttp.port=9000 -Dconfig.file=/home/liubin/temp/shop-1.0.0-SNAPSHOT/conf/env/production.conf -Dlogger.file=/home/liubin/temp/shop-1.0.0-SNAPSHOT/conf/env/prod_logger.xml