package com.han.wsdemo.model;

import java.io.Serializable;

/**
 * 人员实体
 *
 * @author <a href="mailto: 393803588@qq.com">刘涵(Hanl)</a>
 *         By 2016/11/21
 */
public class User implements Serializable {
    private static final long serialVersionUID = -3665941277268240867L;

    private long userId;

    private String name;

    public long getUserId() {
        return userId;
    }

    public User setUserId(long userId) {
        this.userId = userId;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                '}';
    }
}
