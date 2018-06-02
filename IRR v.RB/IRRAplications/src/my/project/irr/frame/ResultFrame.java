/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package my.project.irr.frame;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import my.project.irr.process.ProcessIRR;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import my.project.irr.common.HeaderRenderer;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author junjun
 */
public class ResultFrame extends javax.swing.JFrame {

    /**
     * Creates new form ResultFrame
     */
    ProcessIRR hasil;
    DefaultTableModel model;
    ArrayList<Integer> header = new ArrayList<>();

    public ResultFrame(ProcessIRR hasil) {
        initComponents();
        this.hasil = hasil;

        setData();
        setColumn();
        setRow();
        setGrafik();
        setIRR();
    }

    private void setRow() {
        for (int i = 0; i < hasil.getAngsuran(); i++) {
            Object[] row = new Object[header.size() + 1];
            row[0] = i + 1;
            for (int j = 0; j < header.size(); j++) {
                row[j + 1] = toCurrency(hasil.getHasil().get(header.get(j)).get(i));
            }
            model.addRow(row);
        }

        for (int i = 0; i < 3; i++) {
            Object[] row = new Object[header.size() + 1];
            if (i == 0) {
                row[0] = "Total PV";
            } else if (i == 1) {
                row[0] = "Modal";
            } else if (i == 2) {
                row[0] = "NPV";
            }
            for (int j = 0; j < header.size(); j++) {
                row[j + 1] = toCurrency(hasil.getHasil().get(header.get(j)).get(hasil.getAngsuran() + i));
            }
            model.addRow(row);
        }

    }
    
    private void setData(){
        model = (DefaultTableModel) jTable1.getModel();
        lblHargaTunai1.setText("Rp " + toCurrency(hasil.getHargaTunai()));
        lblDP1.setText("Rp " + toCurrency(hasil.getDP()));
        lblCicilan1.setText("Rp " + toCurrency(hasil.getCicilan()));
        lblAngsuran1.setText(String.valueOf(hasil.getAngsuran()) + "x");
    }
    
    private void setIRR(){
        int index = header.size();
        ArrayList<Double> listNPV1 = hasil.getHasil().get(header.get(index - 2));
        ArrayList<Double> listNPV2 = hasil.getHasil().get(header.get(index - 1));
        int i1 = header.get(index - 2);
        int i2 = header.get(index - 1);
        double npv1 = listNPV1.get(hasil.getAngsuran() + 2);
        double npv2 = listNPV2.get(hasil.getAngsuran() + 2);
        double irr = i1 + (npv1/(npv1-npv2)*(i2-i1));
        lblIRR1.setText(decimalFormat(irr) + " %");
        lbli11.setText("= " + header.get(index - 2).toString()+ " %");
        lbli22.setText("= " + header.get(index - 1).toString()+ " %");
        lblNPV11.setText("= Rp " + toCurrency(listNPV1.get(hasil.getAngsuran() + 2)));
        lblNPV21.setText("= Rp " + toCurrency(listNPV2.get(hasil.getAngsuran() + 2)));        
    }

    private void setColumn() {
        Map<Integer, ArrayList<Double>> treeMap = new TreeMap<>(hasil.getHasil());
        for (Map.Entry col : treeMap.entrySet()) {
            model.addColumn(col.getKey().toString() + " %");
            header.add((Integer) col.getKey());
        }
        TableColumn column = null;
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 0; i < header.size() + 1; i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            column = jTable1.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setMaxWidth(70);
                column.setMinWidth(70);
            } else {
                column.setMaxWidth(90);
                column.setMinWidth(90);
            }
        }

        JTableHeader tableHeader = jTable1.getTableHeader();
        tableHeader.setDefaultRenderer(new HeaderRenderer(jTable1));
    }
    
    private void setGrafik(){
        final XYDataset dataset = createDataset();
        final JFreeChart chart = createChart(dataset);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(300, 170));
        jPanel3.add(chartPanel);
        jPanel3.repaint();
        jPanel3.revalidate();
    }

    private String toCurrency(double num) {
        DecimalFormat formater = new DecimalFormat("###,###,###");
        return formater.format(num);
    }

    private XYDataset createDataset() {
        final XYSeries series = new XYSeries("Third");
        Map<Integer, Double> treeMap = new TreeMap<>(hasil.getSummary());
        for (Map.Entry col : treeMap.entrySet()) {
            series.add(Double.valueOf(col.getKey().toString()), Double.valueOf(col.getValue().toString()));
        }
        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    private JFreeChart createChart(final XYDataset dataset) {
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "NPV", // chart title
                "Tingkat Bunga", // x axis label
                "NPV", // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true, // tooltips
                false // urls
                );

        chart.setBackgroundPaint(Color.white);

        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);

        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);
        return chart;

    }
    
    public static String decimalFormat(double num) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(new Double(num)).replace(",", ".");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblHargaTunai = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblNPV1 = new javax.swing.JLabel();
        lblNPV2 = new javax.swing.JLabel();
        lblNPV21 = new javax.swing.JLabel();
        lblNPV11 = new javax.swing.JLabel();
        lbli2 = new javax.swing.JLabel();
        lblIRR = new javax.swing.JLabel();
        lbli1 = new javax.swing.JLabel();
        lbli11 = new javax.swing.JLabel();
        lbli22 = new javax.swing.JLabel();
        lblIRR1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        lblDP = new javax.swing.JLabel();
        lblDP1 = new javax.swing.JLabel();
        lblAngsuran1 = new javax.swing.JLabel();
        lblAngsuran = new javax.swing.JLabel();
        lblHargaTunai1 = new javax.swing.JLabel();
        lblCicilan1 = new javax.swing.JLabel();
        lblCicilan = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Result");

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblHargaTunai.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblHargaTunai.setForeground(new java.awt.Color(255, 255, 255));
        lblHargaTunai.setText("Harga Tunai");
        jPanel1.add(lblHargaTunai, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 25));

        jTabbedPane1.setBackground(new java.awt.Color(204, 51, 0));
        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTabbedPane1.setForeground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Angsuran"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setAlignmentX(0.1F);
        jTable1.setAlignmentY(0.1F);
        jTable1.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTable1.setEnabled(false);
        jTable1.setSelectionBackground(new java.awt.Color(204, 51, 0));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 633, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Hasil", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel3.setLayout(new java.awt.CardLayout());
        jTabbedPane1.addTab("Grafik", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/project/irr/image/IRR.png"))); // NOI18N
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 190, -1, -1));

        lblNPV1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblNPV1.setText("NPV1");
        jPanel4.add(lblNPV1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 50, 25));

        lblNPV2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblNPV2.setText("NPV2");
        jPanel4.add(lblNPV2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 50, 25));

        lblNPV21.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblNPV21.setText("NPV2");
        jPanel4.add(lblNPV21, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 90, 150, 25));

        lblNPV11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblNPV11.setText("NPV1");
        jPanel4.add(lblNPV11, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 60, 150, 25));

        lbli2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lbli2.setText("i2");
        jPanel4.add(lbli2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 50, 25));

        lblIRR.setFont(new java.awt.Font("Arial", 2, 14)); // NOI18N
        lblIRR.setText("IRR =");
        jPanel4.add(lblIRR, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 280, 50, 25));

        lbli1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lbli1.setText("i1");
        jPanel4.add(lbli1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 50, 25));

        lbli11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lbli11.setText("NPV1");
        jPanel4.add(lbli11, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 120, 150, 25));

        lbli22.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lbli22.setText("NPV2");
        jPanel4.add(lbli22, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 150, 150, 25));

        lblIRR1.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        lblIRR1.setForeground(new java.awt.Color(204, 0, 0));
        lblIRR1.setText("IRR");
        jPanel4.add(lblIRR1, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 280, 210, 25));

        jLabel4.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel4.setText("Berikut perhitungan Internal Rate of Return :");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, -1, 30));

        jTabbedPane1.addTab("IRR", jPanel4);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 130, 640, 400));

        jPanel5.setBackground(new java.awt.Color(51, 51, 51));

        lblDP.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblDP.setForeground(new java.awt.Color(255, 255, 255));
        lblDP.setText("DP");

        lblDP1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblDP1.setForeground(new java.awt.Color(255, 255, 255));
        lblDP1.setText("Rp -");

        lblAngsuran1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblAngsuran1.setForeground(new java.awt.Color(255, 255, 255));
        lblAngsuran1.setText("35x");

        lblAngsuran.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblAngsuran.setForeground(new java.awt.Color(255, 255, 255));
        lblAngsuran.setText("Lama Cicilan");

        lblHargaTunai1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblHargaTunai1.setForeground(new java.awt.Color(255, 255, 255));
        lblHargaTunai1.setText("Rp -");

        lblCicilan1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblCicilan1.setForeground(new java.awt.Color(255, 255, 255));
        lblCicilan1.setText("Rp -");

        lblCicilan.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        lblCicilan.setForeground(new java.awt.Color(255, 255, 255));
        lblCicilan.setText("Cicilan Perbulan");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(lblCicilan, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblCicilan1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblHargaTunai1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblDP, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblAngsuran, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblAngsuran1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDP1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(lblHargaTunai1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDP, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDP1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAngsuran, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAngsuran1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCicilan1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCicilan, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(403, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 320, 530));

        jPanel6.setBackground(new java.awt.Color(204, 51, 0));

        jPanel7.setBackground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel2.setFont(new java.awt.Font("Arial", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("APLIKASI LEASING");

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Mencari nilai Internal Rate of Return");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(0, 73, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel3)))
                .addContainerGap(462, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 0, 320, 530));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 639, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setSize(new java.awt.Dimension(647, 555));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
         * Set the Nimbus look and feel
         */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ResultFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /*
         * Create and display the form
         */
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                   UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ResultFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(ResultFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(ResultFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(ResultFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                new ResultFrame(null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblAngsuran;
    private javax.swing.JLabel lblAngsuran1;
    private javax.swing.JLabel lblCicilan;
    private javax.swing.JLabel lblCicilan1;
    private javax.swing.JLabel lblDP;
    private javax.swing.JLabel lblDP1;
    private javax.swing.JLabel lblHargaTunai;
    private javax.swing.JLabel lblHargaTunai1;
    private javax.swing.JLabel lblIRR;
    private javax.swing.JLabel lblIRR1;
    private javax.swing.JLabel lblNPV1;
    private javax.swing.JLabel lblNPV11;
    private javax.swing.JLabel lblNPV2;
    private javax.swing.JLabel lblNPV21;
    private javax.swing.JLabel lbli1;
    private javax.swing.JLabel lbli11;
    private javax.swing.JLabel lbli2;
    private javax.swing.JLabel lbli22;
    // End of variables declaration//GEN-END:variables
}
