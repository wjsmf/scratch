# cursor usage example for mysql procedure

        drop PROCEDURE if EXISTS soxes.testCursor;
        CREATE PROCEDURE soxes.testCursor()
        BEGIN
            DECLARE id1 LONG;
            DECLARE from1_ VARCHAR(100);
            DECLARE recipients1 VARCHAR(100);
            DECLARE subject1_ VARCHAR(90);
            DECLARE receive_date1 TIMESTAMP;
            DECLARE maxid long default -3;	
            DECLARE s int DEFAULT 1;
            DECLARE cur CURSOR FOR select id,from_,receive_date
        ,recipients,subject_ from soxes.mail_fund;
            DECLARE CONTINUE HANDLER for not found set s=0;
            open cur;
            WHILE s DO
                FETCH cur into id1,from1_,receive_date1,recipients1,subject1_;
                if	maxid < id1 THEN
                        set maxid = id1;
                END IF;
                
            end WHILE;
            CLOSE cur;
        select maxid,id1;
            
        END;

        call soxes.testCursor();
        
# incrementally update fund weekly performance data

        INSERT INTO cbaas.fund_performance_weekly (
          fund_id,
          return_,
          nav,
          distribution,
          dist2,
          aum,
          month_end,
          createdBy,
          dateAdded,
          lastUpdatedBy,
          lastUpdated
        ) SELECT
          ff.id,
          ff.ret,
          ff.nav,
          ff.distribution,
          ff.dist2,
          ff.asset,
          ff.dt,
          'doFundImport',
          now(),
          'doFundImport',
          now()
        FROM
          (
            SELECT
              f.id,
              v.ret,
              CASE
            WHEN v.type = 'HF'
            AND v.cumulative_nav IS NOT NULL THEN
              v.cumulative_nav
            ELSE
              v.nav
            END `nav`,
            v.distribution,
            v.dist2,
            v.asset,
            v.dt,
            max(p.month_end) `month_end`
          FROM
            sycamore.vw_fund_value_weekly v
          JOIN cbaas.fund f ON f.stage_key = v.fund_id
          LEFT JOIN cbaas.fund_performance_weekly p ON f.id = p.fund_id
          WHERE
            v.isvalid = 1
          AND f.valid = 1
          AND f.date_source IN (9, 10, 11, 12)
          GROUP BY
            v.fund_id,
            v.dt
          ) ff
        WHERE
          ff.dt > ff.month_end
        OR ff.month_end IS NULL;
