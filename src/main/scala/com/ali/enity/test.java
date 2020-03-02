package com.ali.enity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class test {
    public static void main(String[] args) {
        Student a=new Student("gjh",45.2);
//        System.out.println(a);

        List<Student> list=new ArrayList<>();
        list.add(a);
        list.add(new Student("wl",34.2));

        String listjson=JSON.toJSONString(list);
        System.out.println("listjson=>"+listjson);
//        JSONObject stu=new JSONObject(JSON.toJSONString(a));

//        对象转换为字符串
        String b=JSON.toJSONString(a);
//字符串转换为json对象
        Student stu1=JSON.parseObject(b,Student.class);
        System.out.println(stu1);
        System.out.println(b);

//        list后的对象字符串转换为对象
        List<Student> list1=JSON.parseArray(listjson,Student.class);
        for (Student c:list1
             ) {
            System.out.println(c);
        }
    }
}
