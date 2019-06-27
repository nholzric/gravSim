/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Nathan
 */
public class paintPanel extends javax.swing.JPanel{
    private ArrayList<AsteroidGraphic> theseAsteroids;
//    private boolean isBusy = false;

    private class AsteroidGraphic{
        private int r = 10;
        private int x;
        private int y;
        
        AsteroidGraphic(int x, int y){
            this.x = x;
            this.y = y;
        }
        
        public int getX(){
            return x;
        }
        public int getY(){
            return y;
        }
        public int getR(){
            return r;
        }
    }
    
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
        theseAsteroids = new ArrayList<AsteroidGraphic>();
    }

    public void updateChart(){
        this.repaint();
    }
    
    public void drawScene(ArrayList<gravsim.State> simState,
            double sceneMinX, double sceneMaxX, double sceneMinY, double sceneMaxY){
//        if(isBusy){
//            System.out.printf("paintPanel is busy in drawScene\n");
//            return;
//        }
//        isBusy = true;
        
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
        synchronized(this){
//            System.out.printf("drawScene\t (x=%f)\n",simState.get(0).getP().getX());
            theseAsteroids = new ArrayList<AsteroidGraphic>();
    //        Iterator<gravsim.State> it = simState.iterator();
    //        int objectIndex = 0;
    //        while(it.hasNext()){
            for(int i = 0; i < simState.size(); i++){
                gravsim.Coordinate thisP = simState.get(i).getP();
    //            gravsim.Coordinate thisP = it.next().getP();
                int centerX = xSceneToPixel.transformInt(thisP.getX());
                int centerY = ySceneToPixel.transformInt(thisP.getY());

                theseAsteroids.add(new AsteroidGraphic(centerX,centerY));

    //            ++objectIndex;
            }
        }
        
//        System.out.printf("drawScene: theseAsteroids.size() = %d\n", theseAsteroids.size());
//        isBusy = false;
        
        this.repaint();
    }
    
    @Override
    protected void paintComponent(java.awt.Graphics g){
//        if(isBusy){
//            System.out.printf("paintPanel is busy in paintComponent\n");
//            return;
//        }
//        isBusy = true;
        
        super.paintComponent(g);
        
//        System.out.printf("In paintComponent: theseAsteroids.size() = %d\n",theseAsteroids.size());
        synchronized(this){
//            System.out.printf("paintComponent theseAsteroids.size=%d\t", theseAsteroids.size());
//            if(theseAsteroids.size()>0)
//                    System.out.printf("(x=%d)\n",theseAsteroids.get(0).getX());
            Iterator<AsteroidGraphic> it = theseAsteroids.listIterator();
            it.forEachRemaining((ag)->{
                g.drawOval(ag.getX(),ag.getY(),ag.getR(),ag.getR());
    //            System.out.printf("(%d,%d)",ag.getX(),ag.getY());
            });
        }
//        System.out.printf("\n");
//        isBusy = false;
    }
}
