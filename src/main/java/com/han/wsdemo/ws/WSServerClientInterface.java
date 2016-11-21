package com.han.wsdemo.ws;

import com.han.wsdemo.model.Department;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.List;

/**
 * @author <a href="mailto: 393803588@qq.com">刘涵(Hanl)</a>
 *         By 2016/11/21
 */
@WebService
public interface WSServerClientInterface {
    @WebMethod(operationName = "getAsXml")
    String getDeptListAsXML();
    @WebMethod(operationName = "getAsList")
    List<Department> getDeptList();
}
