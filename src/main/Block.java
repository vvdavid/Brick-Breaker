/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Rectangle;

/**
 *
 * @author David Eduardo Vazquez Vargas 20
 */
public class Block {

    boolean enabled = true;
    int x, y, width = 80, heigth = 50;
    public Rectangle rectangle;

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
        rectangle = new Rectangle(x, y, width, heigth);
    }
}
