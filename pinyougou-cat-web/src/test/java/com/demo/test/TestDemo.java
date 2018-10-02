package com.demo.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.xalan.xsltc.compiler.util.IntType;
import org.junit.Test;

public class TestDemo {
  @Test
  public void demo(){
	  List<Integer> list = new ArrayList();
	  for(int i=0;i<10;i++){
		  list.add(i);
	  }
	  System.out.println(list.size());
	  int i = 0;
	 while (list.isEmpty()) {
		i++;
		if(list.get(i).equals(5)){
			list.remove(list.get(i));
			
		}
	}
	
	  System.out.println(list);
	  System.out.println(list.get(0));
  }
}
