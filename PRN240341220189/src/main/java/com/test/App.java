package com.test;

import java.io.File;
import java.io.FileReader;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	  if (args.length != 2) {
              System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <JSON file path>");
              return;
          }

          String prnNumber = args[0];
          String jsonFilePath = args[1];

//          String prnNumber = "240350000046";
//          String jsonFilePath = "E:\\Udemy\\test.json";


//          System.out.println("PRN Number: " + prnNumber);
//          System.out.println("JSON File Path: " + jsonFilePath);

          try {
              // Read the JSON file
              FileReader reader = new FileReader(new File(jsonFilePath));
              StringBuilder jsonStringBuilder = new StringBuilder();
              int i;
              while ((i = reader.read()) != -1) {
                  jsonStringBuilder.append((char) i);
              }
              reader.close();

              String jsonString = jsonStringBuilder.toString();
   //           System.out.println("JSON Content: " + jsonString);

              JSONObject jsonObject = new JSONObject(jsonString);

              // Find the first instance of the key "destination"
              String destinationValue = findDestinationValue(jsonObject);
              if (destinationValue == null) {
                  System.out.println("Key 'destination' not found in the JSON file.");
                  return;
              }

              // Generate a random 8-character alphanumeric string
              String randomString = generateRandomString(8);

              // Concatenate the PRN number, destination value, and random string
              String concatenatedString = prnNumber + destinationValue + randomString;
//              System.out.println("Concatenated String: " + concatenatedString);

              // Generate MD5 hash
              String md5Hash = generateMD5Hash(concatenatedString);
//              System.out.println("MD5 Hash: " + md5Hash);

              // Output the result in the format <hash>;<random string>
              System.out.println(md5Hash + ";" + randomString);

          } catch (Exception e) {
              System.err.println("An error occurred:");
              e.printStackTrace();
          }
      }


      private static String findDestinationValue(JSONObject jsonObject) {
          Iterator<String> keys = jsonObject.keys();
          while (keys.hasNext()) {
              String key = keys.next();
              Object value = jsonObject.get(key);

              if (key.equals("destination")) {
                  return value.toString();
              }

              if (value instanceof JSONObject) {
                  String result = findDestinationValue((JSONObject) value);
                  if (result != null) {
                      return result;
                  }
              } else if (value instanceof JSONArray) {
                  JSONArray array = (JSONArray) value;
                  for (int i = 0; i < array.length(); i++) {
                      Object element = array.get(i);
                      if (element instanceof JSONObject) {
                          String result = findDestinationValue((JSONObject) element);
                          if (result != null) {
                              return result;
                          }
                      }
                  }
              }
          }
          return null;
      }

      private static String generateRandomString(int length) {
          SecureRandom random = new SecureRandom();
          StringBuilder sb = new StringBuilder(length);
          for (int i = 0; i < length; i++) {
              sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".charAt(random.nextInt(62)));
          }
          return sb.toString();
      }

      private static String generateMD5Hash(String input) throws Exception {
          MessageDigest md = MessageDigest.getInstance("MD5");
          byte[] messageDigest = md.digest(input.getBytes());
          StringBuilder sb = new StringBuilder();
          for (byte b : messageDigest) {
              sb.append(String.format("%02x", b));
          }
          return sb.toString();
   
    }
}
