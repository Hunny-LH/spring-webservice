package com.han.wsdemo.service;

import com.han.wsdemo.model.Department;

import java.util.List;

/**
 * @author <a href="mailto: 393803588@qq.com">刘涵(Hanl)</a>
 *         By 2016/11/21
 */
public interface BizService {

    /**
     * 获取实体列表
     *
     * @return
     */
    List<Department> getDeptList();

}
