/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ucitel;

/**
 *
 * @author strafeldap
 */
public class CryptoUtils {

  public static void main(String arg[]) {
    /*try {
      // quick way to do input from the keyboard, now deprecated...
      java.io.StreamTokenizer Input=new java.io.StreamTokenizer(System.in);
      //
      System.out.print("Input your secret password : ");
      Input.nextToken();
      String secret = new String(CryptoUtils.encrypt(Input.sval));
      System.out.println("the encrypted result : " + secret);
      boolean ok = true;
      String s = "";
      while (ok) {
          System.out.print("Now try to enter a password : " );
          Input.nextToken();
          s = new String(CryptoUtils.encrypt(Input.sval));
          if (secret.equals(s)){
             System.out.println("You got it!");
             ok = false;
             }
          else
             System.out.println("Wrong, try again...!");
      }
    }
    catch (Exception e){
      e.printStackTrace();
    }*/

  }

  public static byte[] encrypt(String x)   throws Exception
  {
     java.security.MessageDigest d =null;
     d = java.security.MessageDigest.getInstance("SHA-1");
     d.reset();
     d.update(x.getBytes());
     return  d.digest();
  }
}
