package com.bots.advanced.PSO;

import java.util.Random;

public class Random_Generator{

    public double create_random_velocity(double bound,int coefficient){

        return (create_random(bound)*create_sign())*coefficient;
    }

    public double create_random(double bound) {
        Random rand = new Random();
        return rand.nextDouble(bound);
    }

    public int create_sign(){

        Random rand=new Random();
        int random=rand.nextInt(2);
        if (random==0){

            return -1;
        }
        return 1;

    }
}