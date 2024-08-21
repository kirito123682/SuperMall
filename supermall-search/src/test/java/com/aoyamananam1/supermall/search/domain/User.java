package com.aoyamananam1.supermall.search.domain;

import lombok.Data;
import lombok.ToString;


public class User {
    private Integer uerId;
    private String name;
    private Integer age;

    public Integer getUerId() {
        return uerId;
    }

    public void setUerId(Integer uerId) {
        this.uerId = uerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public User(){}

    public User(String name, Integer uerId, Integer age) {
        this.name = name;
        this.uerId = uerId;
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "uerId=" + uerId +
                ", name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
