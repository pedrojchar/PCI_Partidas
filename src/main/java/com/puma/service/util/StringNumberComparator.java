package com.puma.service.util;

import java.util.Comparator;

public class StringNumberComparator implements Comparator<String>{
	public int compare(String strNumber1, String strNumber2) {
		
        //convert String to int first
        int number1 = Integer.parseInt( strNumber1.split(" ")[1] );
        int number2 = Integer.parseInt( strNumber2.split(" ")[1] );
        
        //compare numbers
        if( number1 > number2 ){
            return 1;
        }else if( number1 < number2 ){
            return -1;
        }else{
            return 0;
        }
    }
}
