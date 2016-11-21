# Spring-WS-Demo

这是一个Spring 集成JAX-WS的小栗子
---

## 代码解析
### 先来看下服务端
`WSServerInterface.java` 
```
@WebService
public interface WSServerInterface {

    @WebMethod(operationName = "getAsXml")
    String getDeptListAsXML();
    @WebMethod(operationName = "getAsList")
    List<Department> getDeptList();
}

```
通过@WebService注解标注这是一个用于暴露WebService的接口。
同时，接口声明了两个方法，这两个方法将会是WebService所暴露出去的服务方法。


`WSServer.java`
```
/**
 * webService服务提供类的实现，
 * 主要在这里实现webService返回类型的转换， 例子中是从List转换成xml格式
 *
 * @author <a href="mailto: 393803588@qq.com">刘涵(Hanl)</a>
 *         By 2016/11/21
 */
@Component
@WebService(serviceName = "hanTest",
        endpointInterface = "com.han.wsdemo.ws.WSServerInterface",
        targetNamespace = "com.han.wsdemo.ws.WSServerInterface"
)
public class WSServer implements WSServerInterface {

    /**
     * 注入业务Service获取数据
     */
    @Resource
    BizService bizService;

    /**
     * 获取xml格式的数据
     *
     * @return
     */
    @Override
    public String getDeptListAsXML() {
        List<Department> list = bizService.getDeptList();
        return XMLUtils.convertToXML("departments", list);
    }

    /**
     * 获取list格式的数据
     *
     * @return
     */
    @Override
    public List<Department> getDeptList() {
        return bizService.getDeptList();
    }
```
这里同样使用了@WebService注解进行标注，不过重点参数是`endpointInterface`，这个参数指明了该服务的实现类实现的是哪个服务接口。
而其他的几个属性参数主要是为了自定义一些WebService的元信息，方便客户端调用时使用。

@Component注解主要是将该类注册为Spring管理的bean，同样可以使用xml的方式去配置。

`spring-ws-server.xml`
```
<bean class="org.springframework.remoting.jaxws.SimpleJaxWsServiceExporter">
        <property name="baseAddress" value="http://127.0.0.1:8089/"/>
</bean>
```
在服务端的spring配置文件中，我们使用`SimpleJaxWsServiceExporter`来为我们的WebService暴露HTTP服务，这个类会启动一个内部的HttpServer，
因此服务的暴露其实并不依赖于像SpringMVC这样的web服务，但需要注意的是它同样需要依赖servlet-api

有了以上的准备之后，就可以启动spring，是WebService运行起来。

`Server.java`
```
public class Server {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring-ws-server.xml");
        // 这里有个小技巧，让main程序一直监听控制台输入，异步的代码就可以一直在执行。不同于while(ture)的是，按回车或esc可退出
        new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
}
```
我这里为了方便，直接使用了ClassPathXmlApplicationContext的方式加载，实际上也可以集成SpringMVC通过web容器加载启动Spring上下文。

成功暴露出服务了：`http://localhost:8089/hanTest?wsdl`
```
<definitions xmlns:wsu="http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd" xmlns:wsp="http://www.w3.org/ns/ws-policy" xmlns:wsp1_2="http://schemas.xmlsoap.org/ws/2004/09/policy" xmlns:wsam="http://www.w3.org/2007/05/addressing/metadata" xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/" xmlns:tns="com.han.wsdemo.ws.WSServerInterface" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns="http://schemas.xmlsoap.org/wsdl/" targetNamespace="com.han.wsdemo.ws.WSServerInterface" name="hanTest">
    <import namespace="http://ws.wsdemo.han.com/" location="http://localhost:8089/hanTest?wsdl=1"/>
    <binding xmlns:ns1="http://ws.wsdemo.han.com/" name="WSServerPortBinding" type="ns1:WSServerInterface">
        <soap:binding transport="http://schemas.xmlsoap.org/soap/http" style="document"/>
        <operation name="getAsList">
            <soap:operation soapAction=""/>
            <input>
                <soap:body use="literal"/>
            </input>
            <output>
                <soap:body use="literal"/>
            </output>
        </operation>
        <operation name="getAsXml">
            <soap:operation soapAction=""/>
            <input>
                <soap:body use="literal"/>
            </input>
            <output>
                <soap:body use="literal"/>
            </output>
        </operation>
    </binding>
    <service name="hanTest">
        <port name="WSServerPort" binding="tns:WSServerPortBinding">
            <soap:address location="http://localhost:8089/hanTest"/>
        </port>
    </service>
</definitions>
```

以上一个简单的WebService服务端就完成了。

### 下来看客户端
`spring-ws-client.xml`
```
    <bean id="wsService" class="org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean">
        <property name="serviceInterface" value="com.han.wsdemo.ws.WSServerClientInterface"/>
        <property name="wsdlDocumentUrl" value="http://localhost:8089/hanTest?wsdl"/>
        <property name="namespaceUri" value="com.han.wsdemo.ws.WSServerInterface"/>
        <property name="serviceName" value="hanTest"/>
        <property name="portName" value="WSServerPort" />
    </bean>
```
这里通过注册一个JaxWsPortProxyFactoryBean类型的Bean来获取WebService的代理，之后就可以使用这个代理对象来完成webService的调用。
这里有几个必须选的配置参数

serviceInterface - 要代理的服务接口

wsdlDocumentUrl - wsdl的访问地址

namespaceUri - definitions节点的targetNamespace

serviceName - service节点的name

portName - port节点的name

`WSServerClientInterface.java`
```
@WebService
public interface WSServerClientInterface {
    @WebMethod(operationName = "getAsXml")
    String getDeptListAsXML();
    @WebMethod(operationName = "getAsList")
    List<Department> getDeptList();
}
```
这里要代理的服务接口可以根据wsdl直接生成，也可以参照wsdl的描述来自己写出来。
@WebService注解表示这是一个WebService的接口
@WebMethod指定了方法对应的wsdl中的operation

有了服务定义的接口，以及Spring配置的代理对象，就可以注入使用这个bean来完成WebService的调用了。
```
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
```
这里为了方便，直接使用了ClassPathXmlApplicationContext的方式加载，实际应用中也可能是通过web容器加载启动的spring上下文。

运行结果：
```
/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/bin/java -Didea.launcher.port=7538 "-Didea.launcher.bin.path=/Applications/IntelliJ IDEA.app/Contents/bin" -Dfile.encoding=UTF-8 -classpath "/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/charsets.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/deploy.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/ext/cldrdata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/ext/dnsns.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/ext/jaccess.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/ext/jfxrt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/ext/localedata.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/ext/nashorn.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/ext/sunec.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/ext/sunjce_provider.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/ext/sunpkcs11.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/ext/zipfs.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/javaws.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/jce.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/jfr.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/jfxswt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/jsse.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/management-agent.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/plugin.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/resources.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/lib/rt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/lib/ant-javafx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/lib/dt.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/lib/javafx-mx.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/lib/jconsole.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/lib/packager.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/lib/sa-jdi.jar:/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/lib/tools.jar:/Users/hanl/Documents/workspace/ideaWork/spring-webservice/target/classes:/Users/hanl/.m2/repository/org/springframework/spring-core/4.3.3.RELEASE/spring-core-4.3.3.RELEASE.jar:/Users/hanl/.m2/repository/commons-logging/commons-logging/1.2/commons-logging-1.2.jar:/Users/hanl/.m2/repository/org/springframework/spring-web/4.3.3.RELEASE/spring-web-4.3.3.RELEASE.jar:/Users/hanl/.m2/repository/org/springframework/spring-aop/4.3.3.RELEASE/spring-aop-4.3.3.RELEASE.jar:/Users/hanl/.m2/repository/org/springframework/spring-beans/4.3.3.RELEASE/spring-beans-4.3.3.RELEASE.jar:/Users/hanl/.m2/repository/org/springframework/spring-context/4.3.3.RELEASE/spring-context-4.3.3.RELEASE.jar:/Users/hanl/.m2/repository/org/springframework/spring-expression/4.3.3.RELEASE/spring-expression-4.3.3.RELEASE.jar:/Users/hanl/.m2/repository/org/springframework/spring-webmvc/4.3.3.RELEASE/spring-webmvc-4.3.3.RELEASE.jar:/Users/hanl/.m2/repository/org/springframework/ws/spring-ws-core/2.4.0.RELEASE/spring-ws-core-2.4.0.RELEASE.jar:/Users/hanl/.m2/repository/org/springframework/ws/spring-xml/2.4.0.RELEASE/spring-xml-2.4.0.RELEASE.jar:/Users/hanl/.m2/repository/org/springframework/spring-oxm/4.3.3.RELEASE/spring-oxm-4.3.3.RELEASE.jar:/Users/hanl/.m2/repository/javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar:/Users/hanl/.m2/repository/org/slf4j/slf4j-api/1.7.5/slf4j-api-1.7.5.jar:/Users/hanl/.m2/repository/ch/qos/logback/logback-classic/1.0.13/logback-classic-1.0.13.jar:/Users/hanl/.m2/repository/ch/qos/logback/logback-core/1.0.13/logback-core-1.0.13.jar:/Users/hanl/.m2/repository/com/alibaba/fastjson/1.2.20/fastjson-1.2.20.jar:/Users/hanl/.m2/repository/dom4j/dom4j/1.6.1/dom4j-1.6.1.jar:/Users/hanl/.m2/repository/xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar" com.intellij.rt.execution.application.AppMain com.han.wsdemo.boot.Client
十一月 22, 2016 12:03:13 上午 org.springframework.context.support.ClassPathXmlApplicationContext prepareRefresh
信息: Refreshing org.springframework.context.support.ClassPathXmlApplicationContext@7aec35a: startup date [Tue Nov 22 00:03:13 CST 2016]; root of context hierarchy
十一月 22, 2016 12:03:13 上午 org.springframework.beans.factory.xml.XmlBeanDefinitionReader loadBeanDefinitions
信息: Loading XML bean definitions from class path resource [spring-ws-client.xml]
=================== XML Response ===================
<?xml version="1.0" encoding="UTF-8"?>
<departments><department><name>xx部门</name><id>1</id><users><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user></users></department><department><name>xx部门</name><id>1</id><users><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user></users></department><department><name>xx部门</name><id>1</id><users><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user></users></department><department><name>xx部门</name><id>1</id><users><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user></users></department><department><name>xx部门</name><id>1</id><users><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user></users></department><department><name>xx部门</name><id>1</id><users><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user></users></department><department><name>xx部门</name><id>1</id><users><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user></users></department><department><name>xx部门</name><id>1</id><users><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user></users></department><department><name>xx部门</name><id>1</id><users><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user></users></department><department><name>xx部门</name><id>1</id><users><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user><user><name>liuhan</name><userId>1</userId></user></users></department></departments>
===================== List Response ==================
Department{id=1, name='xx部门', users=[User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}]}
Department{id=1, name='xx部门', users=[User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}]}
Department{id=1, name='xx部门', users=[User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}]}
Department{id=1, name='xx部门', users=[User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}]}
Department{id=1, name='xx部门', users=[User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}]}
Department{id=1, name='xx部门', users=[User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}]}
Department{id=1, name='xx部门', users=[User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}]}
Department{id=1, name='xx部门', users=[User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}]}
Department{id=1, name='xx部门', users=[User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}]}
Department{id=1, name='xx部门', users=[User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}, User{userId=1, name='liuhan'}]}

Process finished with exit code 0

```

希望对大家有所帮助。

如有任何问题指出或探讨请提交issue或直接联系我（qq：393803588）