package com.pinyougou.user;

import java.util.Random;

import org.junit.Test;

public class UserRun {
	public String ranDom() {
		int [] arr = new int[6];
		String stringRandom="";
		for(int i =0;i<6;i++){
			int random = new Random().nextInt(9) + 1;
			arr[i]=random;
		}
		for( int k=0;k<arr.length;k++){
		stringRandom+=arr[k]+"";
		}
		return stringRandom;
		
	}
	@Test
	public void demo(){
		String ranDom = ranDom();
		if(ranDom.length()==6){
			System.out.println(ranDom);
		}else{
			demo();
		}
	}

}
