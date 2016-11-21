package com.han.wsdemo.boot;

import com.han.wsdemo.model.Department;
import com.han.wsdemo.ws.WSServerClientInterface;
import com.han.wsdemo.ws.WSServerInterface;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;

/**
 * webService 客户端
 *
 * @author <a href="mailto: 393803588@qq.com">刘涵(Hanl)</a>
 *         By 2016/11/21
 */
public class Client {

    public static void main(String[] args) {
        /**
         * 启动spring
         */
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-ws-client.xml");

        /**
         * 这里就可以注入webService的客户端代理类
         */
        WSServerClientInterface server = (WSServerClientInterface) context.getBean("wsService");
        System.out.println("=================== XML Response ===================");
        System.out.println(server.getDeptListAsXML());

        List<Department> list =server.getDeptList();    // 这里说明webService可以支持list类型的值传递

        System.out.println("===================== List Response ==================");
        list.forEach(System.out::println);
    }
}
