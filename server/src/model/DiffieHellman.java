package model;

import javax.crypto.*;

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

    public int calculateMyResult(){
        int result = ((int) Math.pow(g, secret)) % p;
       this.myResult = result;
       return result;  
    }

    public long calculateSecretKey(){
        System.out.println("partnerResult: " + this.partnerResult);
        System.out.println("secret: " + this.secret);
        System.out.println("p: " + this.p);
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
