package com.search.string.test;

import org.junit.Test;

public class SpiltStrng {
	
	@Test
	public void demo(){
		String s="5-100";
		String s2="100";
		String[] split = s.split("-");
		System.out.println(split[0]+"--"+split[1]);
		String[] split2 = s2.split("-");
		System.out.println(split2[0]);
	}

}
