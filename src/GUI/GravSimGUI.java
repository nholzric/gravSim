/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.SwingWorker;
import javax.swing.JOptionPane;
import javax.swing.text.PlainDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import javax.swing.text.Document;
import java.util.Iterator;
import static java.lang.Math.ceil;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import gravsim.Gravsim;
import gravsim.BodyFactory;
import gravsim.BodyInterface;
import gravsim.MythiumBodyConcreteFactory;
import gravsim.GravityBodyConcreteFactory;
import gravsim.Coordinate;
import java.util.Set;

/**
 *
 * @author Nathan
 */
public class GravSimGUI extends javax.swing.JFrame {

    private gravsim.Gravsim mySim;
    private SimulationResults simResults;
    
    private final String simulationTimeLabel = "Simulation Time: ";
    
    /**
     * Creates new form GravSimGUI
     */
    public GravSimGUI() {
        initComponents();
        this.setLocationRelativeTo(null); //puts new window in center of screen
        
        setDefaultValues();
        setToolTips();
        
        testJFreeChart();
    }
    
    private void testJFreeChart(){
        XYSeries series = new XYSeries("Test Series");
        series.add(1,1);
        series.add(1,2);
        series.add(2,1);
        series.add(2,2);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        
        JFreeChart chart = ChartFactory.createScatterPlot("Test", "X Axis", "Y Axis", dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        jPanel_analysisGraph.setLayout(new java.awt.BorderLayout());
        jPanel_analysisGraph.add(chartPanel,java.awt.BorderLayout.CENTER);
        jPanel_analysisGraph.validate();
    }
    
    //https://stackoverflow.com/questions/11093326/restricting-jtextfield-input-to-integers
    private numericDocumentFilter myScientificFilter = new numericDocumentFilter(new checkScientific());
    private numericDocumentFilter myIntFilter = new numericDocumentFilter(new checkInt());
    interface textCheck{
        public boolean test(String text);
    }
    class checkScientific implements textCheck{
        @Override
        public boolean test(String text){
            //allow empty text (hopefully temporarily)
            if(text.isEmpty())
                return true;
            
            try{
                Double.parseDouble(text);
                return true;
            } catch(NumberFormatException e){
                //allow typing scientific notation (with negative exponents)
                if( text.endsWith("E") || text.endsWith("e") || text.endsWith("-"))
                    return true;
                
                return false;
            }
        }
    }
    class checkInt implements textCheck{
        @Override
        public boolean test(String text){
            //allow empty text (hopefully temporarily)
            if(text.isEmpty())
                return true;
            
            try{
                Integer.parseInt(text);
                return true;
            } catch(NumberFormatException e){
                return false;
            }
        }
    }
    class numericDocumentFilter extends DocumentFilter {
        textCheck myTest;
        numericDocumentFilter(textCheck thisTest){
            myTest = thisTest;
        }
        
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attrs)
                throws BadLocationException {
            
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.insert(offset, text);
            
            if(myTest.test(sb.toString())){
                super.insertString(fb,offset,text,attrs);
            }
            else{
//                System.out.printf("Failed numericDocumentFilter.insertString: %s\n",sb.toString());
//                JOptionPane.showMessageDialog(frame,
//                    "Current population is empty",
//                    "Unable to save population",
//                    JOptionPane.WARNING_MESSAGE);
            }
        }
        
        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException{
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);
            
            if(myTest.test(sb.toString())){
                super.replace(fb,offset,length,text,attrs);
            }
            else{
//                System.out.printf("Failed numericDocumentFilter.replace: %s\n",sb.toString());
            }
        }
        
        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.delete(offset, offset + length);

            if(myTest.test(sb.toString())){
                super.remove(fb, offset, length);
            }
            else{
//                System.out.printf("Failed numericDocumentFilter.remove: %s\n",sb.toString());
            }
        }
    }
    
    private void setDefaultValues(){
        jTextField_centralMass.setText("1.898E27");
        jTextField_startTime.setText("0");
        jTextField_timeStep.setText("306.8064");
        jTextField_endTime.setText("3068064");
        jCheckBox_useMythium.setSelected(false);
        jTextField_baselineRadius.setText("670900.0");
        jTextField_rangeRadius.setText("6709.0");
        jTextField_baselineMass.setText("1500E15");
        jTextField_rangeMass.setText("1500E14");
        jTextField_baselineMythium.setText("0");
        jTextField_rangeMythium.setText("0");
        jTextField_numberOfObjects.setText("100");
        jTextField_randomSeed.setText("0");
        
        //make table empty by default
        DefaultTableModel tableModel = (DefaultTableModel) jTable_population.getModel();
        tableModel.setRowCount(0);
        
        //set Document Filter for fields which support scientific notation
        ((PlainDocument) jTextField_centralMass.getDocument()).setDocumentFilter(myScientificFilter);
        ((PlainDocument) jTextField_startTime.getDocument()).setDocumentFilter(myScientificFilter);
        ((PlainDocument) jTextField_timeStep.getDocument()).setDocumentFilter(myScientificFilter);
        ((PlainDocument) jTextField_endTime.getDocument()).setDocumentFilter(myScientificFilter);
        ((PlainDocument) jTextField_baselineRadius.getDocument()).setDocumentFilter(myScientificFilter);
        ((PlainDocument) jTextField_rangeRadius.getDocument()).setDocumentFilter(myScientificFilter);
        ((PlainDocument) jTextField_baselineMass.getDocument()).setDocumentFilter(myScientificFilter);
        ((PlainDocument) jTextField_rangeMass.getDocument()).setDocumentFilter(myScientificFilter);
        ((PlainDocument) jTextField_baselineMythium.getDocument()).setDocumentFilter(myScientificFilter);
        ((PlainDocument) jTextField_rangeMythium.getDocument()).setDocumentFilter(myScientificFilter);
        
        //set Document Filter for fields which only support ints
        ((PlainDocument) jTextField_numberOfObjects.getDocument()).setDocumentFilter(myIntFilter);
        ((PlainDocument) jTextField_randomSeed.getDocument()).setDocumentFilter(myIntFilter);
    }
    private void setToolTips(){
        jTextField_centralMass.setToolTipText("Mass around which objects orbit");
        jTextField_startTime.setToolTipText("Simulation start time");
        jTextField_timeStep.setToolTipText("Suggested timestep of simulation");
        jTextField_endTime.setToolTipText("End time of simulation");
        jCheckBox_useMythium.setToolTipText("Toggles mythium force");
        
        jTextField_baselineRadius.setToolTipText("Average orbital radius of generated population");
        jTextField_rangeRadius.setToolTipText("Range of deviation from baseline radius");
        jTextField_baselineMass.setToolTipText("Average mass of generated population");
        jTextField_rangeMass.setToolTipText("Range of deviation from baseline mass");
        jTextField_baselineMythium.setToolTipText("Average mythium mass of generated population");
        jTextField_rangeMythium.setToolTipText("Range of deviation from baseline mythium mass");
        jTextField_numberOfObjects.setToolTipText("Number of objects to randomly generate");
        
        jTextField_randomSeed.setToolTipText("Random seed for population generation");
        jButton_generatePopulation.setToolTipText("Use above population parameters to randomly generate a population of objects");
        jButton_loadPopulation.setToolTipText("Load population from file");
        jButton_savePopulation.setToolTipText("Save population to file");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem2 = new javax.swing.JMenuItem();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel_config = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_centralMass = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jCheckBox_useMythium = new javax.swing.JCheckBox();
        jTextField_startTime = new javax.swing.JTextField();
        jTextField_timeStep = new javax.swing.JTextField();
        jTextField_endTime = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextField_baselineRadius = new javax.swing.JTextField();
        jTextField_rangeRadius = new javax.swing.JTextField();
        jTextField_baselineMass = new javax.swing.JTextField();
        jTextField_rangeMass = new javax.swing.JTextField();
        jTextField_baselineMythium = new javax.swing.JTextField();
        jTextField_rangeMythium = new javax.swing.JTextField();
        jTextField_numberOfObjects = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        jLabel14 = new javax.swing.JLabel();
        jTextField_randomSeed = new javax.swing.JTextField();
        jButton_generatePopulation = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton_loadPopulation = new javax.swing.JButton();
        jButton_savePopulation = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable_population = new javax.swing.JTable();
        jPanel_simulationResults = new javax.swing.JPanel();
        jPanel_analysis = new javax.swing.JPanel();
        jComboBox_uniqueObjects = new javax.swing.JComboBox<>();
        jPanel_analysisGraph = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        paintPanel_test = new GUI.paintPanel();
        jButton_playAnimation = new javax.swing.JButton();
        jSlider_animationControl = new javax.swing.JSlider();
        jButton_stepForward = new javax.swing.JButton();
        jButton_stepBackward = new javax.swing.JButton();
        jLabel_simulationTime = new javax.swing.JLabel();
        jProgressBar_simProgress = new javax.swing.JProgressBar();
        jLabel15 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem_loadSimFile = new javax.swing.JMenuItem();
        jMenuItem_saveSimFile = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem_runSimulation = new javax.swing.JMenuItem();

        jMenuItem2.setText("jMenuItem2");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel1.setText("Simulation Controls");

        jLabel2.setText("Central Mass (kg)");

        jTextField_centralMass.setText("jTextField1");

        jLabel3.setText("Start Time (s)");

        jLabel4.setText("Time Step (s)");

        jLabel5.setText("End Time (s)");

        jCheckBox_useMythium.setText("Use Mythium");

        jTextField_startTime.setText("jTextField2");

        jTextField_timeStep.setText("jTextField3");

        jTextField_endTime.setText("jTextField4");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel6.setText("Population Generation");

        jLabel7.setText("Baseline Radius (km)");

        jLabel8.setText("Range Radius (km)");

        jLabel9.setText("Baseline Mass (kg)");

        jLabel10.setText("Range Mass (kg)");

        jLabel11.setText("Baseline Mythium");

        jLabel12.setText("Range Mythium");

        jLabel13.setText("Number of Objects");

        jTextField_baselineRadius.setText("jTextField5");

        jTextField_rangeRadius.setText("jTextField6");

        jTextField_baselineMass.setText("jTextField7");

        jTextField_rangeMass.setText("jTextField8");

        jTextField_baselineMythium.setText("jTextField9");

        jTextField_rangeMythium.setText("jTextField10");

        jTextField_numberOfObjects.setText("jTextField11");
        jTextField_numberOfObjects.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jLabel14.setText("Random Seed");

        jTextField_randomSeed.setText("jTextField12");

        jButton_generatePopulation.setText("Generate Population");
        jButton_generatePopulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generatePopulationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jCheckBox_useMythium)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextField_centralMass, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                                    .addComponent(jTextField_startTime)
                                    .addComponent(jTextField_timeStep)
                                    .addComponent(jTextField_endTime)))
                            .addComponent(jLabel6)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel9)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel13))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextField_baselineRadius, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jTextField_numberOfObjects, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                                        .addComponent(jTextField_rangeMythium, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextField_baselineMythium, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextField_rangeMass, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextField_baselineMass, javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTextField_rangeRadius, javax.swing.GroupLayout.Alignment.LEADING))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(jTextField_randomSeed, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton_generatePopulation)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField_centralMass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jTextField_startTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jTextField_timeStep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jTextField_endTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCheckBox_useMythium)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jTextField_baselineRadius, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(jTextField_rangeRadius, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jTextField_baselineMass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jTextField_rangeMass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(jTextField_baselineMythium, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12)
                    .addComponent(jTextField_rangeMythium, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jTextField_numberOfObjects, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jTextField_randomSeed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_generatePopulation)))
        );

        jButton_loadPopulation.setText("Load Population");
        jButton_loadPopulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_loadPopulationActionPerformed(evt);
            }
        });

        jButton_savePopulation.setText("Save Population");
        jButton_savePopulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_savePopulationActionPerformed(evt);
            }
        });

        jTable_population.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Name", "Gravity Mass", "Mythium Mass", "X Position", "Y Position", "X Velocity", "Y Velocity"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class, java.lang.Double.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable_population);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton_loadPopulation)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_savePopulation)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton_loadPopulation)
                    .addComponent(jButton_savePopulation)))
        );

        javax.swing.GroupLayout jPanel_configLayout = new javax.swing.GroupLayout(jPanel_config);
        jPanel_config.setLayout(jPanel_configLayout);
        jPanel_configLayout.setHorizontalGroup(
            jPanel_configLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_configLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_configLayout.setVerticalGroup(
            jPanel_configLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_configLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_configLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_configLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Config", jPanel_config);

        jPanel_simulationResults.setLayout(new java.awt.GridLayout(1, 0));

        jComboBox_uniqueObjects.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel_analysisGraphLayout = new javax.swing.GroupLayout(jPanel_analysisGraph);
        jPanel_analysisGraph.setLayout(jPanel_analysisGraphLayout);
        jPanel_analysisGraphLayout.setHorizontalGroup(
            jPanel_analysisGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel_analysisGraphLayout.setVerticalGroup(
            jPanel_analysisGraphLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel_analysisLayout = new javax.swing.GroupLayout(jPanel_analysis);
        jPanel_analysis.setLayout(jPanel_analysisLayout);
        jPanel_analysisLayout.setHorizontalGroup(
            jPanel_analysisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_analysisLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel_analysisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_analysisGraph, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel_analysisLayout.createSequentialGroup()
                        .addComponent(jComboBox_uniqueObjects, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 403, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel_analysisLayout.setVerticalGroup(
            jPanel_analysisLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_analysisLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel_analysisGraph, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox_uniqueObjects, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(235, 235, 235))
        );

        jPanel_simulationResults.add(jPanel_analysis);

        paintPanel_test.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        javax.swing.GroupLayout paintPanel_testLayout = new javax.swing.GroupLayout(paintPanel_test);
        paintPanel_test.setLayout(paintPanel_testLayout);
        paintPanel_testLayout.setHorizontalGroup(
            paintPanel_testLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 475, Short.MAX_VALUE)
        );
        paintPanel_testLayout.setVerticalGroup(
            paintPanel_testLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );

        jButton_playAnimation.setText("Play");
        jButton_playAnimation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_playAnimationActionPerformed(evt);
            }
        });

        jSlider_animationControl.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_animationControlStateChanged(evt);
            }
        });

        jButton_stepForward.setText(">");
        jButton_stepForward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_stepForwardActionPerformed(evt);
            }
        });

        jButton_stepBackward.setText("<");
        jButton_stepBackward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_stepBackwardActionPerformed(evt);
            }
        });

        jLabel_simulationTime.setText("Simulation Time: ---");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paintPanel_test, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel_simulationTime, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton_stepBackward)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton_stepForward))
                            .addComponent(jSlider_animationControl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton_playAnimation)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(paintPanel_test, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSlider_animationControl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_playAnimation))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton_stepForward)
                        .addComponent(jButton_stepBackward))
                    .addComponent(jLabel_simulationTime))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_simulationResults.add(jPanel1);

        jTabbedPane1.addTab("Simulation Result", jPanel_simulationResults);

        jLabel15.setText("Simulation Progress");

        jMenu1.setText("File");

        jMenuItem_loadSimFile.setText("Load Sim File");
        jMenuItem_loadSimFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_loadSimFileActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem_loadSimFile);

        jMenuItem_saveSimFile.setText("Save Sim File");
        jMenuItem_saveSimFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_saveSimFileActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem_saveSimFile);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Simulation");

        jMenuItem_runSimulation.setText("Run Simulation");
        jMenuItem_runSimulation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem_runSimulationActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem_runSimulation);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar_simProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 591, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel15)
                    .addComponent(jProgressBar_simProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //reads parameters from GUI to initialize mySim object
    private void initializeMySim(){
        //build new Gravsim object
        gravsim.BodyFactory thisBodyFactory;
        if(jCheckBox_useMythium.isSelected())
            thisBodyFactory = new gravsim.MythiumBodyConcreteFactory();
        else
            thisBodyFactory = new gravsim.GravityBodyConcreteFactory();
        
        //https://stackoverflow.com/questions/11093326/restricting-jtextfield-input-to-integers
        double centralMass = Double.parseDouble(jTextField_centralMass.getText());
        long randomSeed = Long.parseLong(jTextField_randomSeed.getText());
        
        mySim = new Gravsim(thisBodyFactory,centralMass,randomSeed);
    }
    private void populatePopulationTable(ArrayList<BodyInterface> bodyList){
        DefaultTableModel tableModel = (DefaultTableModel) jTable_population.getModel();
        tableModel.setRowCount(bodyList.size());
        for(int i = 0; i < bodyList.size(); i++){
            if(i > jTable_population.getRowCount()){  
            }
            tableModel.setValueAt(bodyList.get(i).getName(), i, 0);
            tableModel.setValueAt(bodyList.get(i).getGravityMass(), i, 1);
            tableModel.setValueAt(bodyList.get(i).getMythiumMass(), i, 2);
            tableModel.setValueAt(bodyList.get(i).getPosition().getX(), i, 3);
            tableModel.setValueAt(bodyList.get(i).getPosition().getY(), i, 4);
            tableModel.setValueAt(bodyList.get(i).getVelocity().getX(), i, 5);
            tableModel.setValueAt(bodyList.get(i).getVelocity().getY(), i, 6);
        }
    }
    
    //Read population inputs to generate random population
    private void jButton_generatePopulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generatePopulationActionPerformed
        
        initializeMySim();
        
        //get parameters of population from GUI
        //https://stackoverflow.com/questions/11093326/restricting-jtextfield-input-to-integers
        double baselineRadius = Double.parseDouble(jTextField_baselineRadius.getText());
        double rangeRadius = Double.parseDouble(jTextField_rangeRadius.getText());
        double baselineMass = Double.parseDouble(jTextField_baselineMass.getText());
        double rangeMass = Double.parseDouble(jTextField_rangeMass.getText());
        double baselineMythium = Double.parseDouble(jTextField_baselineMythium.getText());
        double rangeMythium = Double.parseDouble(jTextField_rangeMythium.getText());
        
        //parameters not currently exposed to user
        double baselineAngle = 0.005*Math.PI;
        double rangeAngle = 0.005*Math.PI;
        
        //use mySim to build population
        int numberOfObjects = Integer.parseInt(jTextField_numberOfObjects.getText());
        for(int i = 0; i < numberOfObjects; i++){
            mySim.addNewRandomBody(
                    baselineMass-rangeMass,baselineMass+rangeMass,
                    baselineMythium-rangeMythium,baselineMythium+rangeMythium,
                    baselineRadius-rangeRadius,baselineRadius+rangeRadius,
                    baselineAngle-rangeAngle,baselineAngle+rangeAngle);
        }
        
        //get population from mySim to display in GUI
        populatePopulationTable(mySim.getBodyList());
    }//GEN-LAST:event_jButton_generatePopulationActionPerformed
    
    //loads population from file
    private void jButton_loadPopulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_loadPopulationActionPerformed
        JFileChooser c = new JFileChooser();
        c.setFileSelectionMode(JFileChooser.FILES_ONLY);
        c.setFileFilter(new FileNameExtensionFilter(".txt","txt"));
        int returnValue = c.showOpenDialog(jPanel_config);
        if(returnValue != JFileChooser.APPROVE_OPTION)
            return;
        java.io.File file = c.getSelectedFile();
        
        initializeMySim();
        
        mySim.importPopulation(file.getPath());
        
        populatePopulationTable(mySim.getBodyList());
    }//GEN-LAST:event_jButton_loadPopulationActionPerformed
    private void jButton_savePopulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_savePopulationActionPerformed
        if( isPopulationEmpty() ){
            JOptionPane.showMessageDialog(this,
                    "Current population is empty",
                    "Unable to save population",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser c = new JFileChooser();
        c.setFileSelectionMode(JFileChooser.FILES_ONLY);
        c.setFileFilter(new FileNameExtensionFilter(".txt","txt"));
        int returnValue = c.showSaveDialog(jPanel_config);
        if(returnValue != JFileChooser.APPROVE_OPTION)
            return;
        java.io.File file = c.getSelectedFile();
        
        //load bodies described in table to mySim
        readPopulationToSim();
        
        mySim.exportPopulation(file.getPath());
    }//GEN-LAST:event_jButton_savePopulationActionPerformed

    //clear simulation (make a new one) and add bodies described in table to new simulation
    private void readPopulationToSim(){
        initializeMySim(); //sets BodyFactory and Central Mass (and random seed which isn't used here)
        DefaultTableModel tableModel = (DefaultTableModel) jTable_population.getModel();
        for(int i = 0; i < jTable_population.getRowCount(); i++){
            String name = (String) tableModel.getValueAt(i,0);
            double gravityMass = (double) tableModel.getValueAt(i,1);
            double mythiumMass = (double) tableModel.getValueAt(i,2);
            Coordinate position = new Coordinate(
                (double) tableModel.getValueAt(i,3),
                (double) tableModel.getValueAt(i,4));
            Coordinate velocity = new Coordinate(
                (double) tableModel.getValueAt(i,5),
                (double) tableModel.getValueAt(i,6));
            mySim.addNewBody(name,position,velocity,gravityMass,mythiumMass);
        }
    }
    
    private void jMenuItem_loadSimFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_loadSimFileActionPerformed
        JFileChooser c = new JFileChooser();
        c.setFileSelectionMode(JFileChooser.FILES_ONLY);
        c.setFileFilter(new FileNameExtensionFilter(".txt","txt"));
        int returnValue = c.showOpenDialog(jPanel_config);
        if(returnValue != JFileChooser.APPROVE_OPTION)
            return;
        java.io.File file = c.getSelectedFile();
        
        Gravsim.simParameters theseParameters = Gravsim.loadSimFile(file.getPath());
        
        jTextField_centralMass.setText(String.valueOf(theseParameters.centralMass));
        jTextField_startTime.setText(String.valueOf(theseParameters.startTime));
        jTextField_timeStep.setText(String.valueOf(theseParameters.timeStep));
        jTextField_endTime.setText(String.valueOf(theseParameters.endTime));
        
        jCheckBox_useMythium.setSelected(theseParameters.useMythium);
        
        jTextField_baselineRadius.setText(String.valueOf(theseParameters.myRandPopulationSpecs.baselineRadius));
        jTextField_rangeRadius.setText(String.valueOf(theseParameters.myRandPopulationSpecs.rangeRadius));
        jTextField_baselineMass.setText(String.valueOf(theseParameters.myRandPopulationSpecs.baselineMass));
        jTextField_rangeMass.setText(String.valueOf(theseParameters.myRandPopulationSpecs.rangeMass));
        jTextField_baselineMythium.setText(String.valueOf(theseParameters.myRandPopulationSpecs.baselineMythium));
        jTextField_rangeMythium.setText(String.valueOf(theseParameters.myRandPopulationSpecs.rangeMythium));
        jTextField_numberOfObjects.setText(String.valueOf(theseParameters.myRandPopulationSpecs.numObjects));
        jTextField_randomSeed.setText(String.valueOf(theseParameters.myRandPopulationSpecs.numObjects));
    }//GEN-LAST:event_jMenuItem_loadSimFileActionPerformed
    private void jMenuItem_saveSimFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_saveSimFileActionPerformed
        JFileChooser c = new JFileChooser();
        c.setFileSelectionMode(JFileChooser.FILES_ONLY);
        c.setFileFilter(new FileNameExtensionFilter(".txt","txt"));
        int returnValue = c.showSaveDialog(jPanel_config);
        if(returnValue != JFileChooser.APPROVE_OPTION)
            return;
        java.io.File file = c.getSelectedFile();
        
        Gravsim.simParameters theseParameters = new Gravsim.simParameters();
        theseParameters.centralMass = Double.parseDouble(jTextField_centralMass.getText());
        theseParameters.startTime = Double.parseDouble(jTextField_startTime.getText());
        theseParameters.timeStep = Double.parseDouble(jTextField_timeStep.getText());
        theseParameters.endTime = Double.parseDouble(jTextField_endTime.getText());
        theseParameters.useMythium = jCheckBox_useMythium.isSelected();
        
        Gravsim.randomPopulationParameters thesePopParameters = new Gravsim.randomPopulationParameters();
        thesePopParameters.baselineRadius = Double.parseDouble(jTextField_baselineRadius.getText());
        thesePopParameters.rangeRadius = Double.parseDouble(jTextField_rangeRadius.getText());
        thesePopParameters.baselineMass = Double.parseDouble(jTextField_baselineMass.getText());
        thesePopParameters.rangeMass = Double.parseDouble(jTextField_rangeMass.getText());
        thesePopParameters.baselineMythium = Double.parseDouble(jTextField_baselineMythium.getText());
        thesePopParameters.rangeMythium = Double.parseDouble(jTextField_rangeMythium.getText());
        thesePopParameters.numObjects = Integer.parseInt(jTextField_numberOfObjects.getText());
        thesePopParameters.randomSeed = Long.parseLong(jTextField_randomSeed.getText());
        
        theseParameters.myRandPopulationSpecs = thesePopParameters;
        Gravsim.exportSimFile(file.getPath(),theseParameters);
    }//GEN-LAST:event_jMenuItem_saveSimFileActionPerformed

    //https://docs.oracle.com/javase/tutorial/uiswing/examples/components/ProgressBarDemoProject/src/components/ProgressBarDemo.java
    class SimulationTask extends SwingWorker<Void,Void>{
        //Main task - executed in background thread
        @Override
        public Void doInBackground(){
            JFileChooser c = new JFileChooser();
            c.setFileSelectionMode(JFileChooser.FILES_ONLY);
            c.setFileFilter(new FileNameExtensionFilter(".txt","txt"));
            int returnValue = c.showSaveDialog(jPanel_config);
            if(returnValue != JFileChooser.APPROVE_OPTION)
                return null;
            java.io.File file = c.getSelectedFile();
            
            //sets BodyFactory, Central Mass, (and random seed which isn't used here) and loads bodies described in table to mySim
            readPopulationToSim();

            //NOTE: the following code is heavily mirrored in Gravsim.main()
            try{
                java.io.BufferedWriter myLogWriter = new java.io.BufferedWriter(new java.io.OutputStreamWriter(new java.io.FileOutputStream(new java.io.File(file.getPath()))));

                //write log file header
                myLogWriter.write("Time\tMoon\tX\tY\tdX\tdY\tddX\tddY\n");

                //write initial position
                mySim.getPositionStrings().stream().forEach((s)-> {
                    try{
                        myLogWriter.write(String.format("0.0\t%s\n",s));
                    }catch(java.io.IOException e){
                        System.out.println("Error writing line.");
                    }
                });

                double thisTime = Double.parseDouble(jTextField_startTime.getText());
                double suggestedTimeStep = Double.parseDouble(jTextField_timeStep.getText());
                double maxSimTime = Double.parseDouble(jTextField_endTime.getText());

                simResults = new SimulationResults();
                
                //main loop
                int step = 0;
                double thisTimeStep = suggestedTimeStep;
                int estimatedNumberOfSteps = Double.valueOf(ceil((maxSimTime-thisTime)/thisTimeStep)).intValue();
                jProgressBar_simProgress.setString("");
                jProgressBar_simProgress.setStringPainted(false);
                jProgressBar_simProgress.setMinimum(step);
                jProgressBar_simProgress.setMaximum(estimatedNumberOfSteps);
                jSlider_animationControl.setMinimum(0);
                while(thisTime < maxSimTime){
                    thisTimeStep = mySim.doTimestep(suggestedTimeStep);
                    thisTime += thisTimeStep;
                    simResults.addRecord(mySim.getSimState(),thisTime);
                    
                    ++step;
                    Iterator<String> it = mySim.getPositionStrings().iterator();
                    while(it.hasNext()){
                        myLogWriter.write(String.format("%f\t%s\n",thisTime,it.next()));
                    }
                    jProgressBar_simProgress.setValue(step);
                    jSlider_animationControl.setMaximum(step);
                }

                myLogWriter.close();
                jProgressBar_simProgress.setString("Simulation Complete");
                jProgressBar_simProgress.setStringPainted(true);
            }
            catch(java.io.IOException e){
                System.out.println("Error writing file.");
            }
            
            populateUniqueObjectsDropdown(simResults.getUniqueObjects());
            
            return null;
        }
    }
    
    private void jMenuItem_runSimulationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem_runSimulationActionPerformed
        if( isPopulationEmpty() ){
            JOptionPane.showMessageDialog(this,
                    "Populate population to run a simulation",
                    "Unable to run simulation",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SimulationTask task = new SimulationTask();
        task.execute();
    }//GEN-LAST:event_jMenuItem_runSimulationActionPerformed

    private void populateUniqueObjectsDropdown(Set<String> theseObjects){
        jComboBox_uniqueObjects.removeAllItems();
        
        Iterator<String> it = theseObjects.iterator();
        it.forEachRemaining((s)->{
            jComboBox_uniqueObjects.addItem(s);
        });
    }
    
    private void jButton_playAnimationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_playAnimationActionPerformed
        if(!Animator.getAnimateTask(this).isAnimating()){
            Animator.getAnimateTask(this).execute();
        }else{
            Animator.getAnimateTask(this).stopAnimation();
        }
    }//GEN-LAST:event_jButton_playAnimationActionPerformed

    private void jSlider_animationControlStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_animationControlStateChanged
        if(!Animator.getAnimateTask(this).isAnimating()){
            Animator.getAnimateTask(this).drawScene(jSlider_animationControl.getValue());
        }
    }//GEN-LAST:event_jSlider_animationControlStateChanged

    private void jButton_stepBackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_stepBackwardActionPerformed
        jSlider_animationControl.setValue(jSlider_animationControl.getValue()-1);
    }//GEN-LAST:event_jButton_stepBackwardActionPerformed

    private void jButton_stepForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_stepForwardActionPerformed
        jSlider_animationControl.setValue(jSlider_animationControl.getValue()+1);
    }//GEN-LAST:event_jButton_stepForwardActionPerformed

    private boolean isPopulationEmpty(){
        return jTable_population.getRowCount()==0;
    }
    
    private static class SceneBounds{
        double minX;
        double maxX;
        double minY;
        double maxY;
        static final double defaultBufferSize = 1.05;
        
        public SceneBounds(ArrayList<gravsim.State> simState, double bufferSize){
            this.minX = Double.POSITIVE_INFINITY;
            this.maxX = Double.NEGATIVE_INFINITY;
            this.minY = Double.POSITIVE_INFINITY;
            this.maxY = Double.NEGATIVE_INFINITY;
            Iterator<gravsim.State> it = simState.iterator();
            while(it.hasNext()){
                gravsim.Coordinate thisP = it.next().getP();
                if(thisP.getX() < minX)
                    minX = thisP.getX();
                if(thisP.getX() > maxX)
                    maxX = thisP.getX();
                if(thisP.getY() < minY)
                    minY = thisP.getY();
                if(thisP.getY() > maxY)
                    maxY = thisP.getY();
            }

            double xBuffer = bufferSize*(maxX-minX);
            maxX += xBuffer;
            minX -= xBuffer;
            double yBuffer = bufferSize*(maxY-minY);
            maxY += yBuffer;
            minY -= yBuffer;
        }
        public SceneBounds(ArrayList<gravsim.State> simState){
            this(simState, defaultBufferSize);
        }
    }
    private static class Animator extends SwingWorker<Void,Void>{
        private boolean continueAnimating = false;
        private final int minAnimationRefreshPeriod = 10; //milliseconds
        
        private static Animator animateTaskSingleton = new Animator();
        private static GravSimGUI thisGUI;
        private Animator(){}
        public static Animator getAnimateTask(GravSimGUI useThisGUI){
            thisGUI = useThisGUI;
            return animateTaskSingleton;
        }
        
        @Override
        public Void doInBackground(){
            System.out.printf("Starting Animator.doInBackground()\n");
            this.continueAnimating = true;
            
//            System.out.printf("inAnimateTask.doInBackground: minValue = %d, thisValue = %d, maxValue = %d\n",
//                    jSlider_animationControl.getMinimum(),jSlider_animationControl.getValue(),jSlider_animationControl.getMaximum());
            
            int thisIndex;
            while(continueAnimating &&
                    this.thisGUI.jSlider_animationControl.getValue() < this.thisGUI.jSlider_animationControl.getMaximum()){
                
                thisIndex = this.thisGUI.jSlider_animationControl.getValue();
                this.drawScene(thisIndex);

                this.thisGUI.jSlider_animationControl.setValue(thisIndex +1);
                
                try{
//                    System.out.printf("Animator Waiting\n");
                    java.util.concurrent.TimeUnit.MILLISECONDS.sleep(this.minAnimationRefreshPeriod);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            
            System.out.printf("Finished Animator.doInBackground()\n");
            
            animateTaskSingleton = new Animator();
            
            return null;
        }
        
        public void stopAnimation(){
            this.continueAnimating = false;
        }
        
        public boolean isAnimating(){
            return this.continueAnimating;
        }
        
        public void drawScene(int thisIndex){
            //don't animate a state that is outside the bounds
            if(thisIndex >= this.thisGUI.simResults.simStatesSize())
                thisIndex = this.thisGUI.simResults.simStatesSize()-1;
            
            ArrayList<gravsim.State> simState = this.thisGUI.simResults.getState(thisIndex);
            SceneBounds thisSceneBounds = new SceneBounds(simState);

            this.thisGUI.paintPanel_test.drawScene(simState,
                thisSceneBounds.minX, thisSceneBounds.maxX,
                thisSceneBounds.minY, thisSceneBounds.maxY);
            
            this.thisGUI.jLabel_simulationTime.setText(String.format("%s%f",
                    this.thisGUI.simulationTimeLabel,this.thisGUI.simResults.getTime(thisIndex)));
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GravSimGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GravSimGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GravSimGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GravSimGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GravSimGUI().setVisible(true);
            }
        });
    }
    


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_generatePopulation;
    private javax.swing.JButton jButton_loadPopulation;
    private javax.swing.JButton jButton_playAnimation;
    private javax.swing.JButton jButton_savePopulation;
    private javax.swing.JButton jButton_stepBackward;
    private javax.swing.JButton jButton_stepForward;
    private javax.swing.JCheckBox jCheckBox_useMythium;
    private javax.swing.JComboBox<String> jComboBox_uniqueObjects;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_simulationTime;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem_loadSimFile;
    private javax.swing.JMenuItem jMenuItem_runSimulation;
    private javax.swing.JMenuItem jMenuItem_saveSimFile;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel_analysis;
    private javax.swing.JPanel jPanel_analysisGraph;
    private javax.swing.JPanel jPanel_config;
    private javax.swing.JPanel jPanel_simulationResults;
    private javax.swing.JProgressBar jProgressBar_simProgress;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSlider jSlider_animationControl;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable_population;
    private javax.swing.JTextField jTextField_baselineMass;
    private javax.swing.JTextField jTextField_baselineMythium;
    private javax.swing.JTextField jTextField_baselineRadius;
    private javax.swing.JTextField jTextField_centralMass;
    private javax.swing.JTextField jTextField_endTime;
    private javax.swing.JTextField jTextField_numberOfObjects;
    private javax.swing.JTextField jTextField_randomSeed;
    private javax.swing.JTextField jTextField_rangeMass;
    private javax.swing.JTextField jTextField_rangeMythium;
    private javax.swing.JTextField jTextField_rangeRadius;
    private javax.swing.JTextField jTextField_startTime;
    private javax.swing.JTextField jTextField_timeStep;
    private GUI.paintPanel paintPanel_test;
    // End of variables declaration//GEN-END:variables
}
