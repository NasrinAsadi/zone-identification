/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fusionapp;

/**
 *
 * @author ZeytoonCo
 */

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;

public class NewClass {

  public static void main(final String[] args) {
    JFrame frame = new JFrame("Test");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.getContentPane().setBackground(Color.red);
    frame.setPreferredSize(new Dimension(400, 300));
    frame.pack();
   frame.setVisible(true);
  }

  private NewClass() {
  }
}
