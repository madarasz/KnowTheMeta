#!/bin/bash
sftp root@78.46.121.67 <<END_SFTP
put -r output/* /var/www/alwaysberunning/public/ktm
END_SFTP