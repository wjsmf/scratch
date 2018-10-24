--表结构
CREATE TABLE `address` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`code_value` varchar(32) DEFAULT NULL COMMENT '区域编码',
`name` varchar(128) DEFAULT NULL COMMENT '区域名称',
`remark` varchar(128) DEFAULT NULL COMMENT '说明',
`pid` varchar(32) DEFAULT NULL COMMENT 'pid是code_value',
PRIMARY KEY (`id`),
KEY `ix_name` (`name`,`code_value`,`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=1033 DEFAULT CHARSET=utf8 COMMENT='行政区域表';
 

--mysql 实现树结构查询
--方法一
CREATE PROCEDURE sp_showChildLst(IN rootId varchar(20))
BEGIN
CREATE TEMPORARY TABLE IF NOT EXISTS tmpLst
(sno int primary key auto_increment,code_value VARCHAR(20),depth int);
DELETE FROM tmpLst;

CALL sp_createChildLst(rootId,0);

select tmpLst.*,address.* from tmpLst,address where tmpLst.code_value=address.code_value order by tmpLst.sno;
END
 
CREATE PROCEDURE sp_createChildLst(IN rootId varchar(20),IN nDepth INT)
BEGIN
DECLARE done INT DEFAULT 0;
DECLARE b VARCHAR(20);
DECLARE cur1 CURSOR FOR SELECT code_value FROM address where pid=rootId;
DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;

insert into tmpLst values (null,rootId,nDepth);
SET @@max_sp_recursion_depth = 10;
OPEN cur1;

FETCH cur1 INTO b;
WHILE done=0 DO
CALL sp_createChildLst(b,nDepth+1);
FETCH cur1 INTO b;
END WHILE;

CLOSE cur1;
END
--方法二（此方法有线程安全问题）
CREATE PROCEDURE sp_getAddressChild_list(in idd varchar(36))
begin
declare lev int;
set lev=1;
drop table if exists tmp1;
CREATE TABLE tmp1(code_value VARCHAR(36),`name` varchar(50),pid varchar(36) ,levv INT);
INSERT tmp1 SELECT code_value,`name`,pid,1 FROM address WHERE pid=idd;
while row_count()>0
do
set lev=lev+1;
INSERT tmp1 SELECT t.code_value,t.`name`,t.pid,lev from address t join tmp1 a on t.pid=a.code_value AND levv=lev-1;
end while ;
INSERT tmp1 SELECT code_value,`name`,pid,0 FROM address WHERE code_value=idd;
SELECT * FROM tmp1;
end
--方法三
CREATE FUNCTION fn_getAddress_ChildList_test(rootId INT) RETURNS varchar(1000) CHARSET utf8 #rootId为你要查询的节点
BEGIN
#声明两个临时变量
DECLARE temp VARCHAR(1000);
DECLARE tempChd VARCHAR(1000);
SET temp = '$';
SET tempChd=CAST(rootId AS CHAR);#把rootId强制转换为字符
WHILE tempChd is not null DO
SET temp = CONCAT(temp,',',tempChd);#循环把所有节点连接成字符串。
SELECT GROUP_CONCAT(code_value) INTO tempChd FROM address where FIND_IN_SET(pid,tempChd)>0;
END WHILE;
RETURN temp;

END
--方法四
CREATE PROCEDURE sp_findAddressChild(iid varchar(50),layer bigint(20))
BEGIN 
/*创建接受查询的临时表 */ 
create temporary table if not exists tmp_table(id varchar(50),code_value varchar(50),name varchar(50),pid VARCHAR(50)) ENGINE=InnoDB DEFAULT CHARSET=utf8; 
/*最高允许递归数*/ 
SET @@max_sp_recursion_depth = 10 ; 
call sp_iterativeAddress(iid,layer);/*核心数据收集*/ 
select * from tmp_table ;/* 展现 */ 
drop temporary table if exists tmp_table ;/*删除临时表*/ 
END

CREATE PROCEDURE sp_iterativeAddress(iid varchar(50),layer bigint(20))
BEGIN 
DECLARE t_id INT;
declare t_codeValue varchar(50) default iid ; 
declare t_name varchar(50) character set utf8; 
declare t_pid varchar(50) character set utf8; 

/* 游标定义 */ 
declare cur1 CURSOR FOR select id,code_value,`name`,pid from address where pid=iid ; 
declare CONTINUE HANDLER FOR SQLSTATE '02000' SET t_codeValue = null; 

/* 允许递归深度 */ 
if layer>0 then 
OPEN cur1 ; 
FETCH cur1 INTO t_id,t_codeValue,t_name,t_pid ; 
WHILE ( t_codeValue is not null ) 
DO 
/* 核心数据收集 */ 
insert into tmp_table values(t_id,t_codeValue,t_name,t_pid); 
call sp_iterativeAddress(t_codeValue,layer-1); 
FETCH cur1 INTO t_id,t_codeValue,t_name,t_pid ; 
END WHILE; 
end if; 
END
--方法五 SQL实现
SELECT `name`,code_value AS code_value,pid AS 父ID ,levels AS 父到子之间级数, paths AS 父到子路径 FROM (
SELECT `name`,code_value,pid,
@le:= IF (pid = 0 ,0, 
IF( LOCATE( CONCAT('|',pid,':'),@pathlevel) > 0 , 
SUBSTRING_INDEX( SUBSTRING_INDEX(@pathlevel,CONCAT('|',pid,':'),-1),'|',1) +1
,@le+1) ) levels
, @pathlevel:= CONCAT(@pathlevel,'|',code_value,':', @le ,'|') pathlevel
, @pathnodes:= IF( pid =0,',0', 
CONCAT_WS(',',
IF( LOCATE( CONCAT('|',pid,':'),@pathall) > 0 , 
SUBSTRING_INDEX( SUBSTRING_INDEX(@pathall,CONCAT('|',pid,':'),-1),'|',1)
,@pathnodes ) ,pid ) )paths
,@pathall:=CONCAT(@pathall,'|',code_value,':', @pathnodes ,'|') pathall 
FROM address, 
(SELECT @le:=0,@pathlevel:='', @pathall:='',@pathnodes:='') vv
ORDER BY pid,code_value
) src
ORDER BY pid
--方法6  存储过程（SQL实现）
create procedure query_all_add_children(in inPid varchar(50))
begin
select id,code_value,name,remark,pid,p2id,p3id,p4id,p5id 
from(
select a1.id,a1.code_value,a1.name,a1.remark,
a1.pid,a2.pid p2id,a3.pid p3id,a4.pid p4id,a5.pid p5id 
from 
address a1 left join address a2
on(a1.pid=a2.code_value)
left join address a3
on(a2.pid=a3.code_value)
left join address a4
on(a3.pid=a4.code_value)
left join address a5
on(a4.pid=a5.code_value)
) al
where 
(pid=inPid
or p2id=inPid
or p3id=inPid
or p4id=inPid
or p5id=inPid
);
end
