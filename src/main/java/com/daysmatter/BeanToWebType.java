package com.daysmatter;

import com.daysmatter.data.vo.DaysMatterConfigVO;
import com.daysmatter.data.vo.DaysMatterVO;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * @author 22234
 * @date 2023-08-04 15:07:47
 */
public class BeanToWebType {

    public static void beanToWebType(Class clazz) {
        for (Field field : clazz.getDeclaredFields()){
            try {
                if(field.getName().equals("serialVersionUID")){//跳过序列化版本uid
                    continue;
                }
                String str = field.getName()+"?: ";
                if(Number.class.isAssignableFrom(field.getType())){
                    str += "number;";
                }else {
                    str += "string;";
                }
                System.out.println(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[] args) {
        beanToWebType(DaysMatterConfigVO.class);
    }
}
