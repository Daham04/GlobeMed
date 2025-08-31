package gui;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.MySQL;
import model.UserBean;

// --- Implementor interface ---
interface PatientDataImplementor {

    ResultSet fetchPatientData() throws Exception;
}

// --- Concrete Implementor ---
class MySQLPatientData implements PatientDataImplementor {

    @Override
    public ResultSet fetchPatientData() throws Exception {
        return MySQL.execute(
                "SELECT p.patient_nic, p.first_name, p.last_name, p.mobile, p.age, "
                + "p.addres_line1, p.address_line2, g.gender, "
                + "m.medicine, mh.date AS medicine_date, "
                + "s.surgery, ps.date AS surgery_date "
                + "FROM patient p "
                + "INNER JOIN gender g ON p.gender_id = g.id "
                + "LEFT JOIN patient_has_medicine pm ON pm.patient_patient_nic = p.patient_nic "
                + "LEFT JOIN medicine m ON m.id = pm.medicine_id "
                + "LEFT JOIN medicine_history mh ON mh.id = pm.medicine_history_id "
                + "LEFT JOIN patient_has_surgery ps ON ps.patient_patient_nic = p.patient_nic "
                + "LEFT JOIN surgery s ON s.id = ps.surgery_id"
        );
    }
}

// --- Abstraction ---
abstract class PatientRecord {

    protected PatientDataImplementor implementor;

    public PatientRecord(PatientDataImplementor implementor) {
        this.implementor = implementor;
    }

    public abstract void loadPatients(JTable tablePatients, JTable tableSurgeries, JTable tableMedicines);
}

// --- Refined Abstraction for security ---
class SecurePatientRecord extends PatientRecord {

    protected String userRole;

    public SecurePatientRecord(PatientDataImplementor implementor, String userRole) {
        super(implementor);
        this.userRole = userRole;
    }

    protected boolean isAuthorized() {
        return "Doctor".equalsIgnoreCase(userRole) || "Nurse".equalsIgnoreCase(userRole);
    }

    @Override
    public void loadPatients(JTable tablePatients, JTable tableSurgeries, JTable tableMedicines) {
        if (!isAuthorized()) {
            JOptionPane.showMessageDialog(null, "Access Denied: You are not authorized to view patient data.");
            return;
        }

        try {
            ResultSet patientSet = implementor.fetchPatientData();

            DefaultTableModel model1 = (DefaultTableModel) tablePatients.getModel();
            model1.setRowCount(0);

            DefaultTableModel model2 = (DefaultTableModel) tableSurgeries.getModel();
            model2.setRowCount(0);

            DefaultTableModel model3 = (DefaultTableModel) tableMedicines.getModel();
            model3.setRowCount(0);

            Set<String> addedPatients = new HashSet<>();
            Set<String> addedSurgeries = new HashSet<>();
            Set<String> addedMedicines = new HashSet<>();

            while (patientSet.next()) {
                String patientNIC = patientSet.getString("p.patient_nic");

                // --- Patient Table ---
                if (!addedPatients.contains(patientNIC)) {
                    var vector1 = new Vector<>();
                    vector1.add(patientNIC);
                    vector1.add(patientSet.getString("p.first_name") + " " + patientSet.getString("p.last_name"));
                    vector1.add(patientSet.getString("g.gender"));
                    vector1.add(patientSet.getString("p.mobile"));
                    vector1.add(patientSet.getString("p.age"));
                    vector1.add(patientSet.getString("p.addres_line1"));
                    vector1.add(patientSet.getString("p.address_line2"));
                    model1.addRow(vector1);
                    addedPatients.add(patientNIC);
                }

                // --- Surgery Table ---
                String sName = patientSet.getString("s.surgery") != null ? patientSet.getString("s.surgery") : "N/A";
                String sDate = patientSet.getString("surgery_date") != null ? patientSet.getString("surgery_date") : "N/A";
                String surgeryKey = patientNIC + "-" + sName + "-" + sDate;
                if (!addedSurgeries.contains(surgeryKey)) {
                    var vector2 = new Vector<>();
                    vector2.add(patientNIC);
                    vector2.add(sName);
                    vector2.add(sDate);
                    model2.addRow(vector2);
                    addedSurgeries.add(surgeryKey);
                }

                // --- Medicine Table ---
                String mName = patientSet.getString("m.medicine") != null ? patientSet.getString("m.medicine") : "N/A";
                String mDate = patientSet.getString("medicine_date") != null ? patientSet.getString("medicine_date") : "N/A";
                String medicineKey = patientNIC + "-" + mName + "-" + mDate;
                if (!addedMedicines.contains(medicineKey)) {
                    var vector3 = new Vector<>();
                    vector3.add(patientNIC);
                    vector3.add(mName);
                    vector3.add(mDate);
                    model3.addRow(vector3);
                    addedMedicines.add(medicineKey);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class SearchablePatientRecord extends SecurePatientRecord {

    public SearchablePatientRecord(PatientDataImplementor implementor, String userRole) {
        super(implementor, userRole);
    }

    public void loadPatients(JTable tablePatients, JTable tableSurgeries, JTable tableMedicines, String patientNIC) {
        if (!isAuthorized()) {
            JOptionPane.showMessageDialog(null, "Access Denied: You are not authorized to view patient data.");
            return;
        }

        try {
            // Fetch all data
            ResultSet patientSet = implementor.fetchPatientData();

            DefaultTableModel model1 = (DefaultTableModel) tablePatients.getModel();
            DefaultTableModel model2 = (DefaultTableModel) tableSurgeries.getModel();
            DefaultTableModel model3 = (DefaultTableModel) tableMedicines.getModel();

            model1.setRowCount(0);
            model2.setRowCount(0);
            model3.setRowCount(0);

            Set<String> addedPatients = new HashSet<>();
            Set<String> addedSurgeries = new HashSet<>();
            Set<String> addedMedicines = new HashSet<>();

            while (patientSet.next()) {
                String nicValue = patientSet.getString("p.patient_nic");

                // --- Patient Table ---
                if (!addedPatients.contains(nicValue)) {
                    Vector<Object> vector1 = new Vector<>();
                    vector1.add(nicValue);
                    vector1.add(patientSet.getString("p.first_name") + " " + patientSet.getString("p.last_name"));
                    vector1.add(patientSet.getString("g.gender"));
                    vector1.add(patientSet.getString("p.mobile"));
                    vector1.add(patientSet.getString("p.age"));
                    vector1.add(patientSet.getString("p.addres_line1"));
                    vector1.add(patientSet.getString("p.address_line2"));
                    model1.addRow(vector1);
                    addedPatients.add(nicValue);
                }

                // --- Surgery Table ---
                String sName = patientSet.getString("s.surgery") != null ? patientSet.getString("s.surgery") : "N/A";
                String sDate = patientSet.getString("surgery_date") != null ? patientSet.getString("surgery_date") : "N/A";
                String surgeryKey = nicValue + "-" + sName + "-" + sDate;
                if (!addedSurgeries.contains(surgeryKey)) {
                    Vector<Object> vector2 = new Vector<>();
                    vector2.add(nicValue);
                    vector2.add(sName);
                    vector2.add(sDate);
                    model2.addRow(vector2);
                    addedSurgeries.add(surgeryKey);
                }

                // --- Medicine Table ---
                String mName = patientSet.getString("m.medicine") != null ? patientSet.getString("m.medicine") : "N/A";
                String mDate = patientSet.getString("medicine_date") != null ? patientSet.getString("medicine_date") : "N/A";
                String medicineKey = nicValue + "-" + mName + "-" + mDate;
                if (!addedMedicines.contains(medicineKey)) {
                    Vector<Object> vector3 = new Vector<>();
                    vector3.add(nicValue);
                    vector3.add(mName);
                    vector3.add(mDate);
                    model3.addRow(vector3);
                    addedMedicines.add(medicineKey);
                }
            }

            // --- Apply RowSorter for filtering
            TableRowSorter<DefaultTableModel> sorter1 = new TableRowSorter<>(model1);
            TableRowSorter<DefaultTableModel> sorter2 = new TableRowSorter<>(model2);
            TableRowSorter<DefaultTableModel> sorter3 = new TableRowSorter<>(model3);

            tablePatients.setRowSorter(sorter1);
            tableSurgeries.setRowSorter(sorter2);
            tableMedicines.setRowSorter(sorter3);

            if (patientNIC != null && !patientNIC.isEmpty()) {
                sorter1.setRowFilter(RowFilter.regexFilter("(?i)" + patientNIC, 0));
                sorter2.setRowFilter(RowFilter.regexFilter("(?i)" + patientNIC, 0));
                sorter3.setRowFilter(RowFilter.regexFilter("(?i)" + patientNIC, 0));
            } else {
                // No NIC: show all data
                sorter1.setRowFilter(null);
                sorter2.setRowFilter(null);
                sorter3.setRowFilter(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class PatientPanel extends javax.swing.JPanel {

    private UserBean userBean;

    public PatientPanel(UserBean userBean) {
        this.userBean = userBean;
        initComponents();
        loadActivity();
    }

    public void loadActivity() {

        String currentUserRole = userBean.getUserRole();

        // Bridge connection
        PatientDataImplementor implementor = new MySQLPatientData();
        PatientRecord record = new SecurePatientRecord(implementor, currentUserRole);

        record.loadPatients(jTable1, jTable2, jTable3);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        kGradientPanel2 = new com.k33ptoo.components.KGradientPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        setBackground(new java.awt.Color(239, 239, 236));

        jLabel4.setBackground(new java.awt.Color(33, 52, 72));
        jLabel4.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(33, 52, 72));
        jLabel4.setText("NIC :");

        jTextField1.setBackground(new java.awt.Color(239, 239, 236));
        jTextField1.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(33, 52, 72));
        jTextField1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(239, 239, 236));
        jButton1.setFont(new java.awt.Font("Bahnschrift", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(33, 52, 72));
        jButton1.setText("Search");
        jButton1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jTable1.setBackground(new java.awt.Color(239, 239, 236));
        jTable1.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTable1.setForeground(new java.awt.Color(33, 52, 72));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Patient Nic", "Patient Name", "Gender", "Mobile", "Age", "Addres Line1", "Addres Line2"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setGridColor(new java.awt.Color(239, 239, 236));
        jScrollPane1.setViewportView(jTable1);

        jLabel5.setBackground(new java.awt.Color(33, 52, 72));
        jLabel5.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(33, 52, 72));
        jLabel5.setText("Patient Details");

        kGradientPanel2.setToolTipText("Product Management");
        kGradientPanel2.setkBorderRadius(0);
        kGradientPanel2.setkEndColor(new java.awt.Color(33, 52, 72));
        kGradientPanel2.setkStartColor(new java.awt.Color(138, 189, 192));
        kGradientPanel2.setName("[138,189,192] "); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Patient Management");
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        jLabel2.setIconTextGap(10);

        javax.swing.GroupLayout kGradientPanel2Layout = new javax.swing.GroupLayout(kGradientPanel2);
        kGradientPanel2.setLayout(kGradientPanel2Layout);
        kGradientPanel2Layout.setHorizontalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        kGradientPanel2Layout.setVerticalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
        );

        jPanel1.setBackground(new java.awt.Color(239, 239, 236));
        jPanel1.setLayout(new java.awt.GridLayout(1, 0));

        jPanel2.setBackground(new java.awt.Color(239, 239, 236));

        jLabel6.setBackground(new java.awt.Color(33, 52, 72));
        jLabel6.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(33, 52, 72));
        jLabel6.setText("Medicine History");

        jTable3.setBackground(new java.awt.Color(239, 239, 236));
        jTable3.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTable3.setForeground(new java.awt.Color(33, 52, 72));
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Patient Nic", "Medicine", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.setGridColor(new java.awt.Color(239, 239, 236));
        jScrollPane3.setViewportView(jTable3);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(387, 387, 387))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(jPanel2);

        jPanel3.setBackground(new java.awt.Color(239, 239, 236));
        jPanel3.setPreferredSize(new java.awt.Dimension(513, 32));

        jLabel7.setBackground(new java.awt.Color(33, 52, 72));
        jLabel7.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(33, 52, 72));
        jLabel7.setText("Surgery History");

        jScrollPane2.setRequestFocusEnabled(false);

        jTable2.setBackground(new java.awt.Color(239, 239, 236));
        jTable2.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTable2.setForeground(new java.awt.Color(33, 52, 72));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Patient Nic", "Surgery", "Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setGridColor(new java.awt.Color(239, 239, 236));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 523, Short.MAX_VALUE)
            .addComponent(jLabel7)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel1.add(jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(kGradientPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4))
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed

    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String patientNIC = jTextField1.getText().trim();
        PatientDataImplementor implementor = new MySQLPatientData();
        SearchablePatientRecord record = new SearchablePatientRecord(implementor, userBean.getUserRole());
        record.loadPatients(jTable1, jTable2, jTable3, patientNIC);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased
        String patientNIC = jTextField1.getText().trim();

        PatientDataImplementor implementor = new MySQLPatientData();
        SearchablePatientRecord record = new SearchablePatientRecord(implementor, userBean.getUserRole());
        record.loadPatients(jTable1, jTable2, jTable3, patientNIC);
    }//GEN-LAST:event_jTextField1KeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    private com.k33ptoo.components.KGradientPanel kGradientPanel2;
    // End of variables declaration//GEN-END:variables
}
