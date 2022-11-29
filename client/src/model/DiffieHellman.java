package model;

import javax.crypto.*;
 

/**

 * This class implements the logic behind the diffieHellman algorithm.

 */
public class DiffieHellman {
	
    private int p; //Modulus
    private int g; //base
    private int secret; //Secret number
    private int partnerResult;
    private int myResult;
    private long key;

    public DiffieHellman (){
        p = 0;
        g = 0;
        secret = 0;
        partnerResult = 0;
        myResult = 0;
        key = 0;
    }

    /**
	* Calculates the result of g to the power of the secret number modulo p, also called A or B. 
	* <p>
	* The result of the operation is stored in variable myResult, and
    * is intended to be sent to the communication partner as part of
    * the symetrical key setup process
	*
	* @return   An integer representing the result of g to the power of the secret number modulo p
	*/
    public int calculateMyResult(){
        int result = ((int) Math.pow(g, secret)) % p;
       this.myResult = result;
       return result;  
    }

    /**
	* Calculates the shared secretkey by calculating the partnerResult to the power of the secret and then calculate the modulo of it
	* <p>
	* The result of the operation is stored in variable result, and
    * is intended to be used as the key to encrypt the data
    *
	* @return      the result of partner result to the power of secret and then the modulos of it
	*/
    public long calculateSecretKey(){
        System.out.println(((long) Math.pow(partnerResult, secret)));
        long result = ((long) Math.pow(partnerResult, secret)) % p;
        this.key = result;

        return result;
    }

    public int getP(){
        return this.p;
    }

    public int getG(){
        return this.g;
    }

    public int getSecret(){
        return this.secret;
    }

    public int getPartnerResult(){
        return this.partnerResult;
    }

    public int getMyResult(){
        return this.myResult;
    }

    public long getSecretKey(){
        return this.key;
    }

    public void setG(int g){
        this.g = g;
    }

    public void setP(int p){
        this.p = p;
    }

    public void setSecret(int secret) {
        this.secret = secret;
    }

    public void setPartnerResult(int partnerResult) {
        this.partnerResult = partnerResult;
    }
}
