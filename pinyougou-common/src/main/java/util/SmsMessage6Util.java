package util;

import java.util.Random;

public class SmsMessage6Util {
	
	public static String smsMessageRandom(){
		String ranDom = sms6();
		if(ranDom.length()==6){
			return ranDom;
		}else{
			smsMessageRandom();
		}
		return ranDom;
	}
	public static String sms6() {
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

}
