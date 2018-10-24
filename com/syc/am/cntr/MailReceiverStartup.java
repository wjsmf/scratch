package com.sycamoreinvest.quartz.repository;

import com.sycamoreinvest.model.product.inbound.MailFund;
import com.sycamoreinvest.service.IFundValImport;
import com.sycamoreinvest.service.MailFundService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.web.context.ContextLoader;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by miles.shi on 2017/6/6.
 */
class MailReceiverStartup {
    private Log log = LogFactory.getLog(getClass());

    @Autowired
    private MailFundService mailFundService;

    @Autowired
    ApplicationContext ac;

    public void test()  {
        DirectChannel inputChannel = ac
                .getBean("receiveChannel", DirectChannel.class);

        //inputChannel.send(new GenericMessage<String>("@mailAdapter.start()"));

        inputChannel.subscribe(message -> {
            MimeMessage mimeMessage= (MimeMessage)message.getPayload();

//            UIDFolder uf = (UIDFolder)mimeMessage.getFolder();
            try {
//                Long messageId = uf.getUID(mimeMessage);
                    String subject = mimeMessage.getSubject();
                    Address[] froms = mimeMessage.getFrom();
                    String from = "";
                    String recipients = "";
                    if (froms != null && froms.length > 0) {
                        from = mimeMessage.getFrom()[0].toString();
                    }
                    Address[] tos = mimeMessage.getRecipients(Message.RecipientType.TO);
                    if (tos != null && tos.length > 0) {
                        recipients = tos[0].toString();
                    }
                    Date date = mimeMessage.getSentDate();
                    MailFund mfund = mailFundService.queryBySenderAndSentDate(from, date, subject);
                    if (mfund == null) {
                        Object o = mimeMessage.getContent();
                        if (o instanceof Multipart) {
                            Multipart multipart = (Multipart) o;
                            reMultipart(multipart,from,date,subject);
                        } else if (o instanceof Part) {
                            Part part = (Part) o;
                            rePart(part,from,date,subject);
                        } else {
                            System.out.println(o);
                        }
                        MailFund mailFund = new MailFund(subject, from, recipients, date);
                        mailFund.setLastUpdated(new Date());
                        mailFundService.saveOrUpdate(mailFund);
                    } else {
                        //本封邮件已经处理过
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }


    private void reMultipart(Multipart multipart,String from,Date sentDate,String subject) throws MessagingException, IOException {
        for (int j = 0, n = multipart.getCount(); j < n; j++) {
            Part part = multipart.getBodyPart(j);
            if (part.getContent() instanceof Multipart) {
                Multipart p = (Multipart) part.getContent();
                reMultipart(p,from,sentDate,subject);
            } else {
                rePart(part,from,sentDate,subject);
            }
        }
    }

    private void rePart(Part part,String from,Date sentDate,String subject) throws MessagingException,IOException{
        if (part.getDisposition() != null) {
            String strFileName = MimeUtility.decodeText(part.getFileName());
            System.out.println("发现附件: " + strFileName);
            if(strFileName!=null&&(strFileName.endsWith(".xlsx")||strFileName.endsWith(".xls")||strFileName.endsWith(".csv"))){
                System.out.println("内容类型: " + MimeUtility.decodeText(part.getContentType()));
//            System.out.println("附件内容:" + part.getContent());
                InputStream in = part.getInputStream();
                try{
                    ac.getBean(IFundValImport.class).handExcelSource(in,from,sentDate,subject);
//                    writeToFile(strFileName, in);
                }catch(Exception e){
                    e.printStackTrace();
                }finally{
                    if(in != null){
                        in.close();
                    }
                }
            }
        } else {
//            if(part.getContentType().startsWith("text/plain")) {
//                System.out.println("文本内容：" + part.getContent());
//            } else {
//                System.out.println("HTML内容：" + part.getContent());
//            }
        }
    }



    private static void writeToFile(String strFileName, InputStream in) throws IOException {
        FileOutputStream out = new FileOutputStream(strFileName);
        int data;
        while((data = in.read()) != -1) {
            out.write(data);
        }
        out.close();
    }
}
