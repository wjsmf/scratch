查看MySQL是否启用了查看慢SQL的日志文件
1. 查看慢SQL日志是否启用

                mysql> show variables like 'log_slow_queries'; 
                +------------------+-------+
                | Variable_name    | Value |
                +------------------+-------+
                | log_slow_queries | ON    |
                +------------------+-------+
                1 row in set (0.00 sec)

2. 查看执行慢于多少秒的SQL会记录到日志文件中

                mysql> show variables like 'long_query_time';
                +-----------------+-------+
                | Variable_name   | Value |
                +-----------------+-------+
                | long_query_time | 1     |   
                +-----------------+-------+
                1 row in set (0.00 sec)
这里value=1, 表示1秒


3. 配置my.ini文件（linux下文件名为my.cnf）, 查找到[mysqld]区段,增加日志的配置,如下示例:

                [mysqld]
                log="C:/temp/mysql.log"
                log_slow_queries="C:/temp/mysql_slow.log"
                long_query_time=1
 
log指示日志文件存放目录；

log_slow_queries指示记录执行时间长的sql日志目录；

long_query_time指示多长时间算是执行时间长,单位s.
 
Linux下这些配置项应该已经存在,只是被注释掉了,可以去掉注释.但直接添加配置项也OK啦.

        BEGIN
        -- pls execute proc_foreign_category_x first;
        update cs.f_stat_tbl s,cs.f_tbl f 
        set s.pre_score_type=
        s.score_type,
        s.lastUpdated = now(),
        s.pre_score_value=
        s.s_score1_value,
        s.score_type=null,
        s.s_score1_value=null 
        where s.f_tbl_id=f.id and
        FIND_IN_SET(f.strategy_category,category);

        update cs.f_stat_tbl s, 
        cs.f_tbl_score_foreign_temp ff 
        set s.score_type = ff.total_score, 
        s.s_score1_value = ff.total_value 
        where s.f_tbl_id = ff.f_tbl_id and
        s.as_of_date_ = ff.as_of_date_ and
        FIND_IN_SET(ff.strategy_category,category);

        END
        
        FIND_IN_SET是一个能将形如'1,2,3,4'的字符串转变成 in (1,2,3,4)这样的查询条件
        ,奇妙的用只传一个参数的方法解决了in 后面不确定个数的参数的问题

# improve mysql performance
                mysql中最好将子查询替换为表连接
                将 `not in ()`改成 外连接(left or right)然后用 a.linkColumnName is null

                同时这种方式还可以用于替换minus操作,因为mysql不支持minus.
                
                由于MySQL delete操作不可为表取别名,所以无法使用join连接操作,也就无法使用上述优化措施
# IN in mysql
        mysql中的in后的集合若为字面量时有长度限制,最多不可超过大概5000个元素
# specify pk when create table as select
   create table new_tbl (PRIMARY KEY(`id`)) as select * from old_tbl;

# 从一个有嵌套外键约束的表中快速删除行

        set foreign_key_checks = 0;
        删完后
        set foreign_key_checks = 1;
        
# mysql meta info table

        mysql>show databases;
        字典库:information_schema,常用字典表:
        INFORMATION_SCHEMA.SCHEMATA  --数据库中所有数据库信息
        INFORMATION_SCHEMA.TABLES  --存放数据库中所有数据库表信息
        INFORMATION_SCHEMA.COLUMNS  --所有数据库表的列信息
        INFORMATION_SCHEMA.STATISTICS  --存放索引信息
        INFORMATION_SCHEMA.USER_PRIVILEGES  --
        INFORMATION_SCHEMA.SCHEMA_PRIVILEGES
        INFORMATION_SCHEMA.TABLE_PRIVILEGES
        INFORMATION_SCHEMA.COLUMN_PRIVILEGES
        INFORMATION_SCHEMA.CHARACTER_SETS
        INFORMATION_SCHEMA.COLLATIONS
        INFORMATION_SCHEMA.COLLATION_CHARACTER_SET_APPLICABILITY
        INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        INFORMATION_SCHEMA.KEY_COLUMN_USAGE ---存放数据库里所有具有约束的键信息
        INFORMATION_SCHEMA.ROUTINES
        INFORMATION_SCHEMA.VIEWS  --存放所有视图信息
        INFORMATION_SCHEMA.TRIGGERS  --触发器信息"
