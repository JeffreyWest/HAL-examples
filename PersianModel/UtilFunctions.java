package PersianModel;

import HAL.Gui.UIGrid;

//        UtilFunctions.SetString(vis, "twitter.com/mathoncbro", true, Util.RED, Util.BLUE, 4);


public final class UtilFunctions {

    public static void SetString(UIGrid vis, String s, boolean placeOnTop, int color, int bkColor, int sf) {
        //on top line and first char in line, don't draw bk, else draw bk to left & above
        int charWidth = 4, charLength = 5,deltaX = 0;

        // bound between grid boundaries, accounting for scale factor
        int maxY = vis.yDim - 1 - ((charLength-1)*sf + sf-1);
        int deltaY = placeOnTop ? maxY : 0;

        if (s.length() > 0) {
            SetChar(vis, s.charAt(0), deltaX, deltaY, color, bkColor, sf);
            for (int i = 1; i < s.length(); i++) {
                DrawVertCharBar(vis, deltaX + i * charWidth-1, deltaY, bkColor,sf);
                SetChar(vis, s.charAt(i), deltaX + i * charWidth, deltaY, color, bkColor, sf);
            }
        }
    }

    private static void SetChar(UIGrid vis, char c, int deltaX, int deltaY, int color, int bkColor, int sf) {
        final short[] alphabet = new short[]{
                32319//box
                , 32767//full
                , 0//space
                , 928//!
                , 24600//"
                , 32095//#
                , 21482//$
                , 9362//%
                , 8126//&
                , 768//'
                , 558//(
                , 14880//)
                , 20756//*
                , 4548//+
                , 65//,
                , 4228//-
                , 32//.
                , 24707///
                , 31279//0
                , 1000//1
                , 9907//2
                , 10929//3
                , 31900//4
                , 19133//5
                , 24239//6
                , 25235//7
                , 32447//8
                , 31421//9
                , 320//:
                , 321//;
                , 17732//<
                , 10570//=
                , 4433//>
                , 25264//?
                , 13998//@
                , 16015//A
                , 10943//B
                , 17966//C
                , 14911//D
                , 22207//E
                , 21151//F
                , 24238//G
                , 31903//H
                , 18417//I
                , 30754//J
                , 27807//K
                , 1087//L
                , 32159//M
                , 32223//N
                , 14894//O
                , 8863//P
                , 15982//Q
                , 14031//R
                , 19113//S
                , 17392//T
                , 31806//U
                , 28796//V
                , 31967//W
                , 27803//X
                , 24824//Y
                , 26291//Z
                , 17983//[
                , 2184//\
                , 32305//]
                , 8712//^
                , 1057//_
                , 272//`
                , 7595//a
                , 6463//b
                , 9510//c
                , 32038//d
                , 13670//e
                , 20964//f
                , 14757//g
                , 7455//h
                , 736//i
                , 22562//j
                , 9439//k
                , 2033//l
                , 15823//m
                , 7439//n
                , 6438//o
                , 4423//p
                , 7492//q
                , 8431//r
                , 10725//s
                , 10216//t
                , 15406//u
                , 14446//v
                , 15599//w
                , 9417//x
                , 14505//y
                , 13803//z
                , 18276//{
                , 864//|
                , 4977//}
                , 17160//~
                , 32767//full
        };

        if (c > alphabet.length + 30) {
            c = 0;
        }

        short s = (c <= 30 && c < alphabet.length + 30) ? alphabet[0] : alphabet[c - 30];
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 5; y++) {
                for (int x_sf = 0; x_sf < sf; x_sf++) {
                    for (int y_sf = 0; y_sf < sf; y_sf++) {
                        if (((s >> (x * 5 + y)) & 1) == 0) {
                            vis.SetPix((x + deltaX)*sf + x_sf, y*sf + deltaY + y_sf, bkColor);
                        } else {
                            vis.SetPix((x + deltaX)*sf + x_sf, y*sf + deltaY + y_sf, color);
                        }
                    }
                }
            }
        }
    }

    private static void DrawVertCharBar(UIGrid vis, int x, int deltaY, int color, int sf) {
        // coords go from 0 to (x,y)Dim-1
        for (int dy = 0; dy < 5; dy++) {
            for (int x_sf = 0; x_sf < sf; x_sf++) {
                for (int y_sf = 0; y_sf < sf; y_sf++) {
                    vis.SetPix(x*sf + x_sf, dy*sf + y_sf + deltaY, color);
                }
            }
        }
    }

}
