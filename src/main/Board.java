package main;

import asset.Colors;

import java.awt.*;

public class Board {
    final  int MAX_COL = 8;
    final  int MAX_ROW = 8;

    public static final int SQUARE_SIZE=100;
    public static final int HALF_SQUARE_SIZE=SQUARE_SIZE/2;

    public void draw(Graphics2D g2){
        boolean c=true;
        for (int row=0;row<MAX_COL;row++){
            c=!c;
            for(int col=0;col<MAX_ROW;col++){
                g2.setColor(c? Colors.MATERIAL_BROWN:Colors.SAND);
                c=!c;
//                if(c){
//                    g2.setColor(Color.black);
//                    c=false;
//                }else {
//                    g2.setColor(Color.white);
//                    c=true;
//                }
                g2.fillRect(col*SQUARE_SIZE , row*SQUARE_SIZE,SQUARE_SIZE,SQUARE_SIZE);
            }
        }
    }

}
