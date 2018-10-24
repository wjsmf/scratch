package com.syc.am.cntr;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;

import static javax.imageio.ImageIO.read;

/**
 * Created by miles.shi on 2017/8/3.
 */
public class Bgew {

    public static void main(String args[]) throws IOException {
        HttpServer hser= HttpServer.create(new InetSocketAddress(8000),8000);
        HttpContext context=hser.createContext("/myapp", t -> {
            InputStream is = t.getRequestBody();
            read(is); // .. read the request body
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
        Map<String,Object>attrs = context.getAttributes();
        for(Iterator<String>its = attrs.keySet().iterator();its.hasNext();){
            String key = its.next();
            System.out.println(attrs.get(key));
        }
        hser.setExecutor(null);
        hser.start();
    }
}
