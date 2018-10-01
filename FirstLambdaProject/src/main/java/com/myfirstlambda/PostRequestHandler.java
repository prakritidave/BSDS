package com.myfirstlambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PostRequestHandler implements RequestHandler<Map<String, String>, Map<String, String>> {

  @Override
  public Map<String, String> handleRequest(Map<String, String> input, Context context){


    Map<String, String>output = new HashMap<>();
    String val = input.get("key");
    int len = val.length();
    String str = ""+len;
    output.put("key", str);
    return output;

    //    Iterator itr  = input.entrySet().iterator();
//    for(Map.Entry<String, String> entry: input.entrySet()){
//      String key = entry.getKey();
//      String val = entry.getValue();
//      int len = val.length();
//      String valLen = ""+len;
//      System.out.println(valLen);
//      output.put(key, valLen);
//    }


  }
}
