package com.syc.am.cntr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by miles.shi on 2017/6/6.
 */
@Service
class FundValImportImpl implements IFundValImport{
    private Log log = LogFactory.getLog(getClass());

    @Autowired
    ApplicationContext context;

    @Autowired
    IDdFundListService ddFundListService;

    @Autowired
    IFundValueDdService iFundValueDdService;

    public void handExcelSource(InputStream inputStream,String from,Date sentDate,String subject){

        Map<String, List<List<Object>>> sheets = ExcelImportHelper.readXlsx(inputStream);
        processor(sheets,from,sentDate,subject);
    }

    private void processor(Map<String, List<List<Object>>> sheets,String from,Date sentDate,String subject) {
        for(String key:sheets.keySet()){ //key是sheet名字
            String plainKey = key.trim();
            DdFundList ddfund=ddFundListService.queryByNameLike(plainKey,null);
            List<List<Object>> lines=sheets.get(key);
            int count=0;
            List<DdFundList> vickies= null;
            for(List<Object> line:lines){ //get every line in sheet
                if(line.size()>=2){
                    if(ddfund!=null){ 
                        Object ele=line.get(0);
                        Object ele1=line.get(1);
                        Date dt;
                        if(ele instanceof Date && (dt=(Date)ele)!=null || ele1 instanceof  Date && (dt=(Date)ele1)!=null){
                            String[]beans=context.getBeanNamesForType(FundValueDdFiller.class);
                            for(String beaName:beans){
                                if(beaName.endsWith(String.valueOf(ddfund.getFundId()))){
                                    FundValueDdFiller filler=context.getBean(beaName,FundValueDdFiller.class);
                                    FundValueDd fvd = filler.fillNavOrReg(line);
                                    if(fvd!=null){
                                        fvd.setFundId(ddfund.getFundId());
                                        fvd.setName(ddfund.getName());
                                        fvd.setType(ddfund.getType());
                                        fvd.setSource(ddfund.getSource());
                                        fvd.setCrncyCode(ddfund.getCurrency());
                                        fvd.setIsvalid(1);
                                        fvd.setDt(new java.sql.Date(dt.getTime()));
                                        iFundValueDdService.mergeInsert(fvd);
                                    }else{}
                                    break;
                                }
                            }
                        }else{}
                    }else{ //邮件附件中的excel,格式混乱
                        if(from != null){
                            int len =  line.size();
                            if(from.contains("vicky@evolutionlabs.com.cn")||from.contains("miles.shi@sycamoreinvest.com")){ 
                                if(len<8){
                                    break;
                                }
                                if(count==0){    
                                    vickies=new ArrayList();
                                   for(int i=3;i<len;i+=2){
                                       String fundnamesss=String.valueOf(line.get(i)).trim();
                                       String fundname=fundnamesss.substring(0,fundnamesss.length()-4);
                                       DdFundList ddfunt = ddFundListService.queryByNameLike(fundname,"0x23abe2d9c");
                                       vickies.add(ddfunt);
                                   }
                                }else{
                                    if(vickies!=null&&vickies.size()>0){
                                        int idx=0;
                                        for(int i=2; i<len; i+=2){
                                            DdFundList ddfuna=vickies.get(idx);
                                            if(line.get(i) instanceof Double && ddfuna!=null){
                                                FundValueDd fvd = new FundValueDd();
                                                fvd.setDt(new java.sql.Date(((Date)line.get(0)).getTime()));
                                                fvd.setFundId(ddfuna.getFundId());
                                                fvd.setName(ddfuna.getName());
                                                fvd.setType(ddfuna.getType());
                                                fvd.setSource(ddfuna.getSource());
                                                fvd.setCrncyCode(ddfuna.getCurrency());
                                                fvd.setIsvalid(1);
                                                Double nav=(Double)line.get(i);
                                                fvd.setNav(nav);
                                                fvd.setNavAdj(nav);
                                                if(line.get(i+1) instanceof Double){
                                                    fvd.setCumulativeNav((Double)line.get(i+1));
                                                }else{
                                                    fvd.setCumulativeNav(nav);
                                                }
                                                iFundValueDdService.mergeInsert(fvd);
                                            }
                                            idx += 1;
                                        }
                                    }
                                }
                            }else if(from.contains("yywbfa@cmschina.com.cn")){
                                int size=line.size();
                                Object nav=line.get(size-2);
                                try{
                                    Double dnav = Double.parseDouble(String.valueOf(nav));
                                    DdFundList dfl = ddFundListService.queryByNameLike(String.valueOf(line.get(2)),null);
                                    FundValueDd fvd = new FundValueDd();
                                    String dttt = String.valueOf(line.get(0));
                                    fvd.setDt(java.sql.Date.valueOf(dttt.replace('年','-').replace('月','-').replace('日',' ').trim()));
                                    fvd.setFundId(dfl.getFundId());
                                    fvd.setName(dfl.getName());
                                    fvd.setType(dfl.getType());
                                    fvd.setSource(dfl.getSource());
                                    fvd.setCrncyCode(dfl.getCurrency());
                                    fvd.setIsvalid(1);
                                    fvd.setNavAdj(dnav);
                                    fvd.setNav(dnav);
                                    fvd.setCumulativeNav(Double.parseDouble(String.valueOf(line.get(size-1))));
                                    iFundValueDdService.mergeInsert(fvd);
                                }catch(NumberFormatException e){}
                            }else if(from.contains("maxiaojie@taoli.sh")){ 
                                DdFundList dfl = ddFundListService.queryByNameLike("淘利多策略量化","淘利资产");
                                FundValueDd fvd = new FundValueDd();
                                Object dateee=line.get(0);
                                if(dateee instanceof Date){
                                    fvd.setDt(new java.sql.Date(((Date)dateee).getTime()));
                                    fvd.setFundId(dfl.getFundId());
                                    fvd.setName(dfl.getName());
                                    fvd.setType(dfl.getType());
                                    fvd.setSource(dfl.getSource());
                                    fvd.setCrncyCode(dfl.getCurrency());
                                    fvd.setIsvalid(1);
                                    fvd.setNavAdj((Double)line.get(2));
                                    fvd.setNav((Double)line.get(2));
                                    fvd.setCumulativeNav((Double)line.get(1));
                                    iFundValueDdService.mergeInsert(fvd);
                                }
                            }else if(from.contains("jeff.li@sycamoreinvest.com")){ 
                                if(count==1){
                                    DdFundList dflst = ddFundListService.queryByNameLike("Commodity Asset Management",null);
                                    if(dflst==null){
                                       System.out.println("dd fund list is null...");
                                       log.error("dd fund list is null...");
                                        break;
                                    }else{
                                        vickies = new ArrayList();
                                        vickies.add(dflst);
                                        log.info("vickies is set");
                                    }
                                }else if(count>2){
                                    FundValueDd fvd = new FundValueDd();
                                    Date dttt = (Date)line.get(0);
                                    fvd.setDt(new java.sql.Date(dttt.getTime()));
                                    fvd.setFundId(vickies.get(0).getFundId());
                                    fvd.setName(vickies.get(0).getName());
                                    fvd.setType(vickies.get(0).getType());
                                    fvd.setSource(vickies.get(0).getSource());
                                    fvd.setCrncyCode(vickies.get(0).getCurrency());
                                    fvd.setIsvalid(1);
                                    fvd.setNav((Double)line.get(1)/100);
                                    fvd.setNavAdj(fvd.getNav());
                                    fvd.setCumulativeNav(fvd.getNav());
                                    fvd.setRet((Double)line.get(2));
                                    iFundValueDdService.mergeInsert(fvd);
                                }
                            }
                        }else{
                            log.error("from is null");
                        }
                    }
                }
                count+=1;
            }
        }
    }

}
