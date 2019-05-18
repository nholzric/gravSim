/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.ArrayList;

/**
 *
 * @author Nathan
 */
public class paintPanel extends javax.swing.JPanel{
    //TEST/////////////////////////
    private int ovalX = 20;
    private int ovalY = 20;
    private int ovalW = 20;
    private int ovalH = 20;
    private final int bounds = 50;
    ///////////////////////////////

    private class LinearTransform{
        private double m;
        private double b;
        
        LinearTransform(double x1, double y1, double x2, double y2){
            m = (y1-y2)/(x1-x2);
            b = y1 - m*x1;
        }
        
        public double transform(double x){
            return m*x + b;
        }
        
        public int transformInt(double x){
            return (int) Math.round(transform(x));
        }
    }
    
    public paintPanel(){

    }

    //TEST///////////////////////////////////////////////
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
    ////////////////////////////////////////////////////
    
    public void drawScene(ArrayList<gravsim.State> simState,
            double sceneMinX, double sceneMaxX, double sceneMinY, double sceneMaxY){
        int pixelMaxX = this.getSize().width;
        int pixelMaxY = this.getSize().height;
        int pixelMinX = 0;
        int pixelMinY = 0;
        double pixelHWRatio = (pixelMaxY-pixelMinY)/(pixelMaxX-pixelMinX);
        double sceneHeight = sceneMaxY - sceneMinY;
        double sceneWidth = sceneMaxX - sceneMinX;
        double sceneHWRatio = sceneHeight/sceneWidth;
        
        //adjust requested scene to match size of window
        if(pixelHWRatio < sceneHWRatio){
            //Scene is too tall therefore increase scene width
            double additionalWidth = sceneHeight/pixelHWRatio - sceneWidth;
            sceneMinX = sceneMinX - additionalWidth/2;
            sceneMaxX = sceneMaxX + additionalWidth/2;
        }
        else{
            //Scene is too wide therefore increase scene height
            double additionalHeight = sceneWidth*pixelHWRatio - sceneWidth;
            sceneMinY = sceneMinY - additionalHeight/2;
            sceneMaxY = sceneMaxY + additionalHeight/2;
        }
        
        LinearTransform xSceneToPixel = new LinearTransform(sceneMinX,(double) pixelMinX,sceneMaxX,(double) pixelMaxY);
        LinearTransform ySceneToPixel = new LinearTransform(sceneMinY,(double) pixelMinY,sceneMaxY,(double) pixelMaxY);
        
        
    }
    
    @Override
    protected void paintComponent(java.awt.Graphics g){
        super.paintComponent(g);
        
        //TEST/////////////////////////////////
        g.drawOval(ovalX, ovalY, ovalW, ovalH);
//        System.out.printf("paintPanel Size: %f, %f\n",this.getSize().getHeight(),this.getSize().getWidth());
        ///////////////////////////////////////
    }
}
