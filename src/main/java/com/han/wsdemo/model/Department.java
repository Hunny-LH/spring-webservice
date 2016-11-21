package com.han.wsdemo.model;

import java.io.Serializable;
import java.util.List;

/**
 * 部门实体
 *
 * @author <a href="mailto: 393803588@qq.com">刘涵(Hanl)</a>
 *         By 2016/11/21
 */
public class Department implements Serializable {
    private static final long serialVersionUID = 8538757123847016384L;

    private long id;

    private String name;

    private List<User> users;

    public long getId() {
        return id;
    }

    public Department setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public Department setName(String name) {
        this.name = name;
        return this;
    }

    public List<User> getUsers() {
        return users;
    }

    public Department setUsers(List<User> users) {
        this.users = users;
        return this;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", users=" + users +
                '}';
    }
}
