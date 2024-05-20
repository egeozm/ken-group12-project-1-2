package com.gui;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;

public class Debugger {
    public static void printMatrix(double[][] a){
        for(int i = 0; i < a.length; i++){
            for (int j = 0; j < a[i].length; j++){
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }
    public static void printMatrix(Material[][] a){
        for(int i = 0; i < a.length; i++){
            for (int j = 0; j < a[i].length; j++){
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }
    public static void printMatrix(String[][] a){
        for(int i = 0; i < a.length; i++){
            for (int j = 0; j < a[i].length; j++){
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }
    public static void printArray(Vector3[] a){
        for(int i = 0; i < a.length; i++){
            System.out.println(a[i].x + " " + a[i].y + " " + a[i].z);
        }
    }
}
