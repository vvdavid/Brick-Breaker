package main;

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Main extends javax.swing.JFrame {

    public static boolean debug;

    public static void main(String[] args) throws InterruptedException {
        debug = args.length == 1 ? Boolean.parseBoolean(args[0]) : false;
        new Main().setVisible(true);
//        Thread.sleep(1000 * 10);
//        System.exit(0);
//    JFrame frame = new JFrame("test");
//    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//    
//    JPanel panel = new JPanel();
////    panel.setSize(150, 300);
//    panel.setPreferredSize(new Dimension(200, 200));
//    
//    frame.add(panel);
//    frame.pack();
//    
//    frame.setVisible(true);
    }

    public Main() {
        initComponents();
        setContentPane(new Gameplay());

//        System.out.println(g.getSize());
//        System.out.println(g.getPreferredSize());
        
//        add(g);
        pack();
        setLocationRelativeTo(null);
    }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rómpelo!");
        getContentPane().setLayout(null);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
