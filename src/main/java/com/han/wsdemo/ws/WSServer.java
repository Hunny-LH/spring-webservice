package com.han.wsdemo.ws;

import com.han.wsdemo.model.Department;
import com.han.wsdemo.service.BizService;
import com.han.wsdemo.utils.XMLUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.jws.WebService;
import java.util.List;

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
}
