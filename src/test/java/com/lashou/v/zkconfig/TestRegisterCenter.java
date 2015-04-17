package com.lashou.v.zkconfig;

import com.lashou.v.zkconfig.common.ElementCollection;
import com.lashou.v.zkconfig.common.RegisterCollection;
import com.lashou.v.zkconfig.core.BeanRegisterCenter;

public class TestRegisterCenter {

	public static void main(String[] args) {
		TestObj instance = new TestObj();//实例化对象
//		Class<?> clazz = instance.getClass();
		
		BeanRegisterCenter brc = new BeanRegisterCenter();
		brc.register(instance);
		
		System.out.println(RegisterCollection.getRegisterCollection());
		System.out.println(ElementCollection.getElementCollection());

	}

}
