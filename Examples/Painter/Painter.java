package Examples.Painter;

import HAL.Gui.GridWindow;
import HAL.Interfaces.KeyResponse;
import HAL.Util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static HAL.Util.*;

public class Painter extends GridWindow {



    public Painter(String title, int xDim, int yDim, int scaleFactor) {
        super(title, xDim, yDim, scaleFactor);
    }


    public int[][] seeBMPImage(String BMPFileName) throws IOException {
        BufferedImage image = ImageIO.read(getClass().getResource(BMPFileName));

        int[][] array2D = new int[image.getWidth()][image.getHeight()];

        for (int xPixel = 0; xPixel < image.getWidth(); xPixel++)
        {
            for (int yPixel = 0; yPixel < image.getHeight(); yPixel++)
            {
                int color = image.getRGB(xPixel, yPixel);

//                -16777216
//                -13948117
//                -15395563
//                -2894893
//                -8421505
//                -1447447
//
//                Color.BLACK.getRGB()
                if (color <= -10000000) {
                    array2D[xPixel][yPixel] = 1;
//                    this.SetPix(xPixel,yDim-yPixel-1, Util.WHITE);
                } else {
                    array2D[xPixel][yPixel] = 0; // ?
                }
            }
        }
        return array2D;
    }

    public static void main(String[] args) {



//        String name = "./FUTRFW.bmp";
        String name = "./font.bmp";

        try {

            int letter_width = 14;
            int letter_spacing_width = 4;

            int letter_height = 20;


            Painter win=new Painter("painter",letter_width,letter_height,2);
            int[][] array = win.seeBMPImage(name);


            for (int row_letter = 0; row_letter < 26; row_letter++) {

                int offset_x = 12 + row_letter * (letter_width + letter_spacing_width);
                int offset_y = 4;

                int[][] letter_arraynew = new int[letter_width][letter_height];

                int actual_x = 0;

                for (int x_pixel = offset_x; x_pixel < letter_width + offset_x; x_pixel++) {
                    int actual_y = 0;
                    for (int y_pixel = offset_y; y_pixel < letter_height + offset_y; y_pixel++) {
                        if (array[x_pixel][y_pixel] == 1) {
                            win.SetPix(actual_x, win.yDim - actual_y - 1, Util.WHITE);
                        }
                        actual_y++;
                    }
                    actual_x++;
                }

                win.TickPause((row_letter == 0) ? 25000 : 500);
                win.ClearAll();

            }

        } catch (Exception e) {
            // Handle it.
            System.out.println("uh oh");
        }


//        Painter win=new Painter("painter",3,5,100);
//        win.AddMouseListeners(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                System.out.println(win.ClickXsq(e)+"");
//                int i=win.I(win.ClickXsq(e),win.ClickYsq(e));
//                if(win.IsSet(i)){
//                    win.SetPix(i,BLACK);
//                }
//                else{
//                    win.SetPix(i,RED);
//                }
//                System.out.println(win.ToShort());
//            }
//        });
//        win.AddKeyResponses(new KeyResponse() {
//            @Override
//            public void Response(char c, int keyCode) {
//                System.out.println(keyCode);
//                win.SetChar(c,0,4,GREEN,BLUE);
//            }
//        },null);
        //public void RunEvent(KeyEvent e) {
        //    if(e.getID()==KeyEvent.KEY_RELEASED){
        //        switch (e.getKeyChar()){
        //            case 'c':win.Clear(BLACK);break;
        //            case 's': System.out.println(win.ToShort());break;
        //        }
        //    }
        //}

    }

    void ClearAll(){
        for (int i = 0; i < this.length; i++) {
            SetPix(i,Util.BLACK);
        }
    }
}