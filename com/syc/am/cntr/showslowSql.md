
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
                
# IN in mysql/oracle/sql_server
       oracle和sql_server的in后的集合若为字面量时有长度限制,最多不可超过大概5000个元素, 而mysql只受制于整条sql长度的限制
       
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
        INFORMATION_SCHEMA.CHARACTER_SETS
        INFORMATION_SCHEMA.COLLATIONS
        INFORMATION_SCHEMA.COLLATION_CHARACTER_SET_APPLICABILITY
        INFORMATION_SCHEMA.KEY_COLUMN_USAGE ---存放数据库里所有具有约束的键信息
        INFORMATION_SCHEMA.ROUTINES
        INFORMATION_SCHEMA.VIEWS  --存放所有视图信息
        INFORMATION_SCHEMA.TRIGGERS  --触发器信息"
