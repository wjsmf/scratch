1. [在结果集第一列增加序号](#add-seqnum-on-the-mysql-resultset)
2. [mysql sum() over(partition by)](#mysql-sum-over-partition-by)
3. [max() over()](#max-over)
3. [特殊的行转列需求](#row-convert-to-column)
3. [mysql wm_concat](#mysql-wm-concat)
3. [mysql之累加](#mysql-cumsum)
3. [mysql 之 rank() over()](#mysql-rank-over)
3. [mysq 之 dense_rank() over()](#mysq-dense-rank-over)
3. [mysql 之 row_number() over ()](#mysql-row-number-over)
3. [mysql 生成 rownum](#mysql-rownum)

# add seqNum on the mysql resultSet

第一种方法:

    select   (@i:=@i+1)  as   i,table_name.*   from   table_name,(select   @i:=0)   as  it

第二种方法:

    set @rownum=0;
    select @rownum:=@rownum+1 as rownum, t.username from auth_user t limit 1,5;

# mysql sum over partition by
      SELECT empno, ename, sal, IF(@deptno = deptno, @sal := @sal, @sal := s_sal) AS s_sal,
         @deptno := deptno   FROM ( SELECT empno, ename, sal, IF(@deptno = deptno, @sal := @sal + sal, @sal := sal) AS s_sal, @deptno := deptno AS deptno FROM (SELECT @sal := 0, @deptno := 0) a0, emp t ORDER BY deptno, empno )e ORDER BY deptno, empno DESC;

     +-------+--------+------+-------+-------------------+
     | empno | ename  | sal  | s_sal | @deptno := deptno |
     +-------+--------+------+-------+-------------------+
     |  7934 | MILLER | 1300 |  8750 |                10 |
     |  7839 | KING   | 5000 |  8750 |                10 |
     |  7782 | CLARK  | 2450 |  8750 |                10 |
     |  7902 | FORD   | 3000 | 10875 |                20 |
     |  7876 | ADAMS  | 1100 | 10875 |                20 |
     |  7788 | SCOTT  | 3000 | 10875 |                20 |
     |  7566 | JONES  | 2975 | 10875 |                20 |
     |  7369 | SMITH  |  800 | 10875 |                20 |
     |  7900 | JAMES  |  950 |  9400 |                30 |
     |  7844 | TURNER | 1500 |  9400 |                30 |
     |  7698 | BLAKE  | 2850 |  9400 |                30 |
     |  7654 | MARTIN | 1250 |  9400 |                30 |
     |  7521 | WARD   | 1250 |  9400 |                30 |
     |  7499 | ALLEN  | 1600 |  9400 |                30 |
     +-------+--------+------+-------+-------------------+
     14 rows in set (0.01 sec)

# max over
     mysql> set @max_sal=0;
     Query OK, 0 rows affected (0.00 sec)
    
     mysql> select if(@max_sal=0,@max_sal:=sal,@max_sal) as max_sal,sal from emp order by sal desc;
     +---------+------+
     | max_sal | sal  |
     +---------+------+
     |    5000 | 5000 |
     |    5000 | 3000 |
     |    5000 | 3000 |
     |    5000 | 2975 |
     |    5000 | 2850 |
     |    5000 | 2450 |
     |    5000 | 1600 |
     |    5000 | 1500 |
     |    5000 | 1300 |
     |    5000 | 1250 |
     |    5000 | 1250 |
     |    5000 | 1100 |
     |    5000 |  950 |
     |    5000 |  800 |
     +---------+------+
     14 rows in set (0.00 sec)
# row convert to column
     mysql> select * from ddd;
     +------+------+------+------+
     | stat | yi   | er   | san  |
     +------+------+------+------+
     | a    |    1 |    2 |    3 |
     | a    |    4 |    5 |    6 |
     | b    |   11 |   22 |    3 |
     | b    |    5 |   61 |   82 |
     +------+------+------+------+
     4 rows in set (0.00 sec)

把上面的数据行转列，结果如下

                  +------+------+------+------+------+------+------+------+------+------+
                  | stat | c1   | c2   | c3   | c4   | c5   | c6   | c7   | c8   | c9   |
                  +------+------+------+------+------+------+------+------+------+------+
                  | a    |    1 |    2 |    3 |    4 |    5 |    6 | NULL | NULL | NULL |
                  | b    |    3 |    5 |   11 |   22 |   61 |   82 | NULL | NULL | NULL |
                  +------+------+------+------+------+------+------+------+------+------+
                  2 rows in set (0.01 sec)

这种需求在oracle中比较容易实现，因为oracle中有row_number可以生成序号.mysql中就只有另找方法了

     set @rn=0;
     set @stat='';
     select stat,
     max(case when rn = 1 then yi end) as c1,
     max(case when rn = 2 then yi end) as c2,
     max(case when rn = 3 then yi end) as c3,
     max(case when rn = 4 then yi end) as c4,
     max(case when rn = 5 then yi end) as c5,
     max(case when rn = 6 then yi end) as c6,
     max(case when rn = 7 then yi end) as c7,
     max(case when rn = 8 then yi end) as c8,
     max(case when rn = 9 then yi end) as c9
     from
     (
     SELECT stat,
            yi,
            IF(@stat = stat, @rn := @rn + 1, @rn := 1) AS rn,
            @stat := stat AS last_stat
     from
     (
     select stat,yi from ddd
     union all
     select stat,er from ddd
     union all
     select stat,san from ddd
     ) as a
     order by stat,yi
     ) as a
     group by stat;

通过分组生成的序号来定位，就可以把对应的值转到相应位置了

     mysql> set @rn=0;
     Query OK, 0 rows affected (0.00 sec)
     mysql> set @stat='';
     Query OK, 0 rows affected (0.00 sec)

     mysql> select stat,
         -> max(case when rn = 1 then yi end) as c1,
         -> max(case when rn = 2 then yi end) as c2,
         -> max(case when rn = 3 then yi end) as c3,
         -> max(case when rn = 4 then yi end) as c4,
         -> max(case when rn = 5 then yi end) as c5,
         -> max(case when rn = 6 then yi end) as c6,
         -> max(case when rn = 7 then yi end) as c7,
         -> max(case when rn = 8 then yi end) as c8,
         -> max(case when rn = 9 then yi end) as c9
         -> from
         -> (
         -> SELECT stat,
         ->        yi,
         ->        IF(@stat = stat, @rn := @rn + 1, @rn := 1) AS rn,
         ->        @stat := stat AS last_stat
         -> from
         -> (
         -> select stat,yi from ddd
         -> union all
         -> select stat,er from ddd
         -> union all
         -> select stat,san from ddd
         -> ) as a
         -> order by stat,yi
         -> ) as a
         -> group by stat;
     +------+------+------+------+------+------+------+------+------+------+
     | stat | c1   | c2   | c3   | c4   | c5   | c6   | c7   | c8   | c9   |
     +------+------+------+------+------+------+------+------+------+------+
     | a    |    1 |    2 |    3 |    4 |    5 |    6 | NULL | NULL | NULL |
     | b    |    3 |    5 |   11 |   22 |   61 |   82 | NULL | NULL | NULL |
     +------+------+------+------+------+------+------+------+------+------+
     2 rows in set (0.01 sec)
# mysql wm concat
     SET @enames='';
     SET @last_deptno=-1;
     SELECT deptno, MAX(enames) AS enames
       FROM (SELECT deptno,
                    IF(@last_deptno = deptno, @rn := @rn + 1, @rn := 1) AS rn,
                    IF(@last_deptno = deptno,
                       @enames := concat(@enames, ',', ename),
                       @enames := ename) AS enames,
                    @last_deptno := deptno AS last_deptno
               FROM emp
              ORDER BY deptno, empno)a
      GROUP BY deptno;

     +--------+--------------------------------------+
     | deptno | enames                               |
     +--------+--------------------------------------+
     |     10 | CLARK,KING,MILLER                    |
     |     20 | SMITH,JONES,SCOTT,ADAMS,FORD         |
     |     30 | ALLEN,WARD,MARTIN,BLAKE,TURNER,JAMES |
     +--------+--------------------------------------+
     3 rows in set (0.01 sec)
# mysql cumsum

     SET @add_sal=0;
     SELECT deptno, empno, ename, sal, @add_sal := @add_sal + sal AS add_sal
       FROM emp
      ORDER BY empno;

     +--------+-------+--------+------+---------+
     | deptno | empno | ename  | sal  | add_sal |
     +--------+-------+--------+------+---------+
     |     20 |  7369 | SMITH  |  800 |     800 |
     |     30 |  7499 | ALLEN  | 1600 |    2400 |
     |     30 |  7521 | WARD   | 1250 |    3650 |
     |     20 |  7566 | JONES  | 2975 |    6625 |
     |     30 |  7654 | MARTIN | 1250 |    7875 |
     |     30 |  7698 | BLAKE  | 2850 |   10725 |
     |     10 |  7782 | CLARK  | 2450 |   13175 |
     |     20 |  7788 | SCOTT  | 3000 |   16175 |
     |     10 |  7839 | KING   | 5000 |   21175 |
     |     30 |  7844 | TURNER | 1500 |   22675 |
     |     20 |  7876 | ADAMS  | 1100 |   23775 |
     |     30 |  7900 | JAMES  |  950 |   24725 |
     |     20 |  7902 | FORD   | 3000 |   27725 |
     |     10 |  7934 | MILLER | 1300 |   29025 |
     +--------+-------+--------+------+---------+
     14 rows in set (0.01 sec)

     SET @add_sal=0;
     SET @last_deptno=-1;
     SELECT deptno,
            empno,
            ename,
            sal,
            IF(@last_deptno = deptno, @rn := @rn + 1, @rn := 1) AS rn,
            IF(@last_deptno = deptno, @add_sal := @add_sal + sal, @add_sal := sal) AS add_sal,
            @last_deptno := deptno AS last_deptno
       FROM emp
      ORDER BY deptno, sal;

     +--------+-------+--------+------+------+---------+-------------+
     | deptno | empno | ename  | sal  | rn   | add_sal | last_deptno |
     +--------+-------+--------+------+------+---------+-------------+
     |     10 |  7934 | MILLER | 1300 |    1 |    1300 |          10 |
     |     10 |  7782 | CLARK  | 2450 |    2 |    3750 |          10 |
     |     10 |  7839 | KING   | 5000 |    3 |    8750 |          10 |
     |     20 |  7369 | SMITH  |  800 |    1 |     800 |          20 |
     |     20 |  7876 | ADAMS  | 1100 |    2 |    1900 |          20 |
     |     20 |  7566 | JONES  | 2975 |    3 |    4875 |          20 |
     |     20 |  7788 | SCOTT  | 3000 |    4 |    7875 |          20 |
     |     20 |  7902 | FORD   | 3000 |    5 |   10875 |          20 |
     |     30 |  7900 | JAMES  |  950 |    1 |     950 |          30 |
     |     30 |  7654 | MARTIN | 1250 |    2 |    2200 |          30 |
     |     30 |  7521 | WARD   | 1250 |    3 |    3450 |          30 |
     |     30 |  7844 | TURNER | 1500 |    4 |    4950 |          30 |
     |     30 |  7499 | ALLEN  | 1600 |    5 |    6550 |          30 |
     |     30 |  7698 | BLAKE  | 2850 |    6 |    9400 |          30 |
     +--------+-------+--------+------+------+---------+-------------+
     14 rows in set (0.05 sec)

# mysql rank over
         SET @rn=0;
         SET @dense_rank=0;
         SET @last_deptno=-1;
         SET @last_sal=-1;
         SET @ADD = 0;
         SELECT deptno,
                empno,
                ename,
                sal,
                IF(@last_deptno = deptno, @rn := @rn + 1, @rn := 1) AS rn,
                IF(@last_sal = sal,@dense_rank := @dense_rank,@dense_rank := @rn) AS rank,
                IF(@last_deptno = deptno, @last_sal := sal, @last_sal := -1) AS last_sal,
                @last_deptno := deptno AS last_deptno
           FROM EMP
          ORDER BY deptno, sal;

         +--------+-------+--------+------+------+------------+----------+-------------+
         | deptno | empno | ename  | sal  | rn   | dense_rank | last_sal | last_deptno |
         +--------+-------+--------+------+------+------------+----------+-------------+
         |     10 |  7934 | MILLER | 1300 |    1 |          1 |       -1 |          10 |
         |     10 |  7782 | CLARK  | 2450 |    2 |          2 |     2450 |          10 |
         |     10 |  7839 | KING   | 5000 |    3 |          3 |     5000 |          10 |
         |     20 |  7369 | SMITH  |  800 |    1 |          1 |       -1 |          20 |
         |     20 |  7876 | ADAMS  | 1100 |    2 |          2 |     1100 |          20 |
         |     20 |  7566 | JONES  | 2975 |    3 |          3 |     2975 |          20 |
         |     20 |  7788 | SCOTT  | 3000 |    4 |          4 |     3000 |          20 |
         |     20 |  7902 | FORD   | 3000 |    5 |          4 |     3000 |          20 |
         |     30 |  7900 | JAMES  |  950 |    1 |          1 |       -1 |          30 |
         |     30 |  7654 | MARTIN | 1250 |    2 |          2 |     1250 |          30 |
         |     30 |  7521 | WARD   | 1250 |    3 |          2 |     1250 |          30 |
         |     30 |  7844 | TURNER | 1500 |    4 |          4 |     1500 |          30 |
         |     30 |  7499 | ALLEN  | 1600 |    5 |          5 |     1600 |          30 |
         |     30 |  7698 | BLAKE  | 2850 |    6 |          6 |     2850 |          30 |
         +--------+-------+--------+------+------+------------+----------+-------------+
         14 rows in set (0.00 sec)
# mysq dense rank over
         SET @rn=0;
         SET @last_deptno=-1;
         SET @last_sal=-1;
         SELECT deptno,
                empno,
                ename,
                sal,
                IF(@last_deptno = deptno, @rn := @rn + IF(@last_sal = sal,0,1), @rn := 1) AS dense_rank,
                IF(@last_deptno = deptno, @last_sal := sal, @last_sal := -1) AS last_sal,
                @last_deptno := deptno AS last_deptno
           FROM EMP
          ORDER BY deptno, sal;

         +--------+-------+--------+------+------------+----------+-------------+
         | deptno | empno | ename  | sal  | dense_rank | last_sal | last_deptno |
         +--------+-------+--------+------+------------+----------+-------------+
         |     10 |  7934 | MILLER | 1300 |          1 |       -1 |          10 |
         |     10 |  7782 | CLARK  | 2450 |          2 |     2450 |          10 |
         |     10 |  7839 | KING   | 5000 |          3 |     5000 |          10 |

         |     20 |  7369 | SMITH  |  800 |          1 |       -1 |          20 |
         |     20 |  7876 | ADAMS  | 1100 |          2 |     1100 |          20 |
         |     20 |  7566 | JONES  | 2975 |          3 |     2975 |          20 |
         |     20 |  7788 | SCOTT  | 3000 |          4 |     3000 |          20 |
         |     20 |  7902 | FORD   | 3000 |          4 |     3000 |          20 |

         |     30 |  7900 | JAMES  |  950 |          1 |       -1 |          30 |
         |     30 |  7654 | MARTIN | 1250 |          2 |     1250 |          30 |
         |     30 |  7521 | WARD   | 1250 |          2 |     1250 |          30 |
         |     30 |  7844 | TURNER | 1500 |          3 |     1500 |          30 |
         |     30 |  7499 | ALLEN  | 1600 |          4 |     1600 |          30 |
         |     30 |  7698 | BLAKE  | 2850 |          5 |     2850 |          30 |
         +--------+-------+--------+------+------------+----------+-------------+
         14 rows in set (0.00 sec)
# mysql row number over
         SET @rn=0;
         SET @last_deptno=0;
         SELECT deptno,
                empno,
                ename,
                sal,
                IF(@last_deptno = deptno, @rn := @rn + 1, @rn := 1) AS rn,
                @last_deptno := deptno AS last_deptno
           FROM EMP
          ORDER BY deptno, sal;

         +--------+-------+--------+------+------+-------------+
         | deptno | empno | ename  | sal  | rn   | last_deptno |
         +--------+-------+--------+------+------+-------------+
         |     10 |  7934 | MILLER | 1300 |    1 |          10 |
         |     10 |  7782 | CLARK  | 2450 |    2 |          10 |
         |     10 |  7839 | KING   | 5000 |    3 |          10 |

         |     20 |  7369 | SMITH  |  800 |    1 |          20 |
         |     20 |  7876 | ADAMS  | 1100 |    2 |          20 |
         |     20 |  7566 | JONES  | 2975 |    3 |          20 |
         |     20 |  7788 | SCOTT  | 3000 |    4 |          20 |
         |     20 |  7902 | FORD   | 3000 |    5 |          20 |

         |     30 |  7900 | JAMES  |  950 |    1 |          30 |
         |     30 |  7654 | MARTIN | 1250 |    2 |          30 |
         |     30 |  7521 | WARD   | 1250 |    3 |          30 |
         |     30 |  7844 | TURNER | 1500 |    4 |          30 |
         |     30 |  7499 | ALLEN  | 1600 |    5 |          30 |
         |     30 |  7698 | BLAKE  | 2850 |    6 |          30 |
         +--------+-------+--------+------+------+-------------+
         14 rows in set (0.01 sec)
# mysql rownum

     SET @rn=0;
     SELECT deptno,
            empno,
            ename,
            @rn := @rn + 1 AS rn
       FROM EMP
      ORDER BY 1, 2;

     mysql> SET @rn=0;
     Query OK, 0 rows affected (0.00 sec)

     mysql> SELECT deptno,
         ->        empno,
         ->        ename,
         ->        @rn := @rn + 1 AS rn
         ->   FROM EMP
         ->  ORDER BY 1, 2;
     +--------+-------+--------+------+
     | deptno | empno | ename  | rn   |
     +--------+-------+--------+------+
     |     10 |  7782 | CLARK  |    1 |
     |     10 |  7839 | KING   |    2 |
     |     10 |  7934 | MILLER |    3 |
     |     20 |  7369 | SMITH  |    4 |
     |     20 |  7566 | JONES  |    5 |
     |     20 |  7788 | SCOTT  |    6 |
     |     20 |  7876 | ADAMS  |    7 |
     |     20 |  7902 | FORD   |    8 |
     |     30 |  7499 | ALLEN  |    9 |
     |     30 |  7521 | WARD   |   10 |
     |     30 |  7654 | MARTIN |   11 |
     |     30 |  7698 | BLAKE  |   12 |
     |     30 |  7844 | TURNER |   13 |
     |     30 |  7900 | JAMES  |   14 |
     +--------+-------+--------+------+
     14 rows in set (0.00 sec)