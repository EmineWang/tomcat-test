package com.yanming.test;

import com.yanming.test.util.RequestUtil;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;

@Controller
@RequestMapping
public class EncodeControllerTest {
    private static final Logger log = LoggerFactory.getLogger(EncodeControllerTest.class);

    private static final String VAR = "测试111";


    @RequestMapping({"test/encode"})
    public void sendRecvByMermberId(@RequestParam String test,HttpServletRequest request, Writer writer) throws IOException {

        log.info("请求:{}", test, RequestUtil.buildRequestUrl(request));

        log.info(Base64.encodeBase64String(VAR.getBytes()));  //suLK1DExMQ==

        try {
            writer.write(Base64.encodeBase64String(VAR.getBytes()));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            writer.close();
        }
    }

    public static void main(String[] args){

        System.out.println(Base64.encodeBase64String(VAR.getBytes()));// 5rWL6K+VMTEx
    }


}
