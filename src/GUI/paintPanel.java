/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

/**
 *
 * @author Nathan
 */
public class paintPanel extends javax.swing.JPanel{
    private int ovalX = 20;
    private int ovalY = 20;
    private int ovalW = 20;
    private int ovalH = 20;
    private final int bounds = 50;

    public paintPanel(){

    }

    public void updateChart(){
        this.moveOval();
        this.repaint();
    }

    private void moveOval(){
        java.util.Random rand = new java.util.Random();
        ovalX = rand.nextInt(bounds);
        ovalY = rand.nextInt(bounds);
        ovalW = rand.nextInt(bounds);
        ovalH = rand.nextInt(bounds);
    }

    @Override
    protected void paintComponent(java.awt.Graphics g){
        super.paintComponent(g);
        g.drawOval(ovalX, ovalY, ovalW, ovalH);
    }
}
