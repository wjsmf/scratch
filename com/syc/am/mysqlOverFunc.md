2. [mysql sum() over(partition by)](#mysql-sum-over-partition-by)
3. [max() over()](#max-over)
3. [mysql之累加](#mysql-cumsum)
3. [mysql 之 rank() over()](#mysql-rank-over)
3. [mysq 之 dense_rank() over()](#mysq-dense-rank-over)
3. [mysql 之 row_number() over ()](#mysql-row-number-over)
3. [mysql 生成 rownum](#mysql-rownum)

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
     
     分组之后再带上每组的序号
     SET @rn := 1;
     set @strat := null;
     select case @strat when strategy then @rn := @rn + 1 else @rn := 1 end `rownum` , @strat := strategy `strategy` from fund 
     where asset_type = 10 and strategy_catalog = 15 order by strategy;

