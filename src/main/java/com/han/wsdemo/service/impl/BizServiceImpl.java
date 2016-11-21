package com.han.wsdemo.service.impl;

import com.han.wsdemo.model.Department;
import com.han.wsdemo.model.User;
import com.han.wsdemo.service.BizService;
import com.han.wsdemo.utils.XMLUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 业务服务
 *
 * @author <a href="mailto: 393803588@qq.com">刘涵(Hanl)</a>
 *         By 2016/11/21
 */
@Service
public class BizServiceImpl implements BizService {

    /**
     * 模拟业务数据，返回一个列表
     *
     * @return
     */
    @Override
    public List<Department> getDeptList() {
        return Stream.generate(this::makeDept)
                .limit(10)
                .collect(Collectors.toList());
    }

    private Department makeDept() {
        return new Department()
                .setId(1)
                .setName("xx部门")
                .setUsers(Stream.generate(this::makeUser).limit(10).collect(Collectors.toList()));
    }

    private User makeUser() {
        return new User()
                .setUserId(1)
                .setName("liuhan");
    }

    public static void main(String[] args) {
        BizService bizService = new BizServiceImpl();

        List<Department> list = bizService.getDeptList();

        System.out.println(XMLUtils.convertToXML("departments", list));
    }
}
