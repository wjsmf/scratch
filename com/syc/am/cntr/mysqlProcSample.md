# modify mysql default charset and default collation among client,server,table,column
                
                [client]
                default-character-set=utf8
 
                [mysql]
                default-character-set=utf8
 
                [mysqld]
                init_connect='SET collation_connection = utf8_unicode_ci'
                init_connect='SET NAMES utf8'
                character-set-server=utf8
                collation-server=utf8_unicode_ci
                skip-character-set-client-handshake

# another way for data import beside mysqldump/pump and into outfile 

          mysql -hyourmysqlhost  -uyouraccount -p  --execute="select * from schemaX.tableY where conditon..." > path2txt_file
          如果希望记录以竖直布局, 只需要加上 --vertical

        
