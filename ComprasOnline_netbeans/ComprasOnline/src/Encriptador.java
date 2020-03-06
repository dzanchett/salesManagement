
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author diego
 */
public class Encriptador {
    
    //Declaração de váriaveis.
    private static MessageDigest md;
    
    //Método que recebe um string senha e retorna uma string da senha criptografada com MD5.
    public static String encriptaSenha(String senha) throws NoSuchAlgorithmException{
        md = MessageDigest.getInstance("MD5");
        byte[] senhaBytes = senha.getBytes();
        md.reset();
        byte[] digested = md.digest(senhaBytes);
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i<digested.length; i++){
            sb.append(Integer.toHexString(0xff & digested[i]));
        }
        
        return sb.toString();
    }
    
}
