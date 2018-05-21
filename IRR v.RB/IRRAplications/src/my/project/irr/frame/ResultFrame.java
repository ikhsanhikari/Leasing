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
        lblDP = new javax.swing.JLabel();
        lblAngsuran = new javax.swing.JLabel();
        lblCicilan = new javax.swing.JLabel();
        lblHargaTunai1 = new javax.swing.JLabel();
        lblDP1 = new javax.swing.JLabel();
        lblAngsuran1 = new javax.swing.JLabel();
        lblCicilan1 = new javax.swing.JLabel();
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Result");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblHargaTunai.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblHargaTunai.setText("Harga Tunai");
        jPanel1.add(lblHargaTunai, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, 25));

        lblDP.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblDP.setText("DP");
        jPanel1.add(lblDP, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 80, 25));

        lblAngsuran.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblAngsuran.setText("Angsuran");
        jPanel1.add(lblAngsuran, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 70, 80, 25));

        lblCicilan.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblCicilan.setText("Cicilan / Bulan");
        jPanel1.add(lblCicilan, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 80, 25));

        lblHargaTunai1.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        lblHargaTunai1.setText("Rp -");
        jPanel1.add(lblHargaTunai1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 10, 170, 25));

        lblDP1.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        lblDP1.setText("Rp -");
        jPanel1.add(lblDP1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 170, 25));

        lblAngsuran1.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        lblAngsuran1.setText("35x");
        jPanel1.add(lblAngsuran1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 70, 170, 25));

        lblCicilan1.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        lblCicilan1.setText("Rp -");
        jPanel1.add(lblCicilan1, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 100, 170, 25));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

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
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 591, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Hasil", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setLayout(new java.awt.CardLayout());
        jTabbedPane1.addTab("Grafik", jPanel3);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/project/irr/image/IRR.png"))); // NOI18N
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, -1, -1));

        lblNPV1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblNPV1.setText("NPV1");
        jPanel4.add(lblNPV1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, 50, 25));

        lblNPV2.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblNPV2.setText("NPV2");
        jPanel4.add(lblNPV2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 50, 50, 25));

        lblNPV21.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        lblNPV21.setText("NPV2");
        jPanel4.add(lblNPV21, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 50, 150, 25));

        lblNPV11.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        lblNPV11.setText("NPV1");
        jPanel4.add(lblNPV11, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 20, 150, 25));

        lbli2.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbli2.setText("i2");
        jPanel4.add(lbli2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, 50, 25));

        lblIRR.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblIRR.setText("IRR");
        jPanel4.add(lblIRR, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 240, 50, 25));

        lbli1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lbli1.setText("i1");
        jPanel4.add(lbli1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 50, 25));

        lbli11.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        lbli11.setText("NPV1");
        jPanel4.add(lbli11, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 80, 150, 25));

        lbli22.setFont(new java.awt.Font("Times New Roman", 1, 12)); // NOI18N
        lbli22.setText("NPV2");
        jPanel4.add(lbli22, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 110, 150, 25));

        lblIRR1.setFont(new java.awt.Font("Times New Roman", 0, 12)); // NOI18N
        lblIRR1.setText("IRR");
        jPanel4.add(lblIRR1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 240, 50, 25));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/project/irr/image/all new rush 2018.jpg"))); // NOI18N
        jLabel2.setText("jLabel2");
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 30, 240, -1));

        jTabbedPane1.addTab("IRR", jPanel4);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 130, 620, 390));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/my/project/irr/image/Logo-Honda.png"))); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 10, -1, -1));

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

        setSize(new java.awt.Dimension(655, 567));
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
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
