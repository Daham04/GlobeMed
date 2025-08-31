package gui;

import java.sql.ResultSet;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import model.MySQL;

interface ReportElement {

    void accept(ReportVisitor visitor);
}

class Patient implements ReportElement {

    private String nic;
    private String fullName;
    private String gender;
    private String mobile;
    private String age;
    private String address1;
    private String address2;

    public Patient(String nic, String fullName, String gender, String mobile, String age, String address1, String address2) {
        this.nic = nic;
        this.fullName = fullName;
        this.gender = gender;
        this.mobile = mobile;
        this.age = age;
        this.address1 = address1;
        this.address2 = address2;
    }

    // getters
    public String getNic() {
        return nic;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getMobile() {
        return mobile;
    }

    public String getAge() {
        return age;
    }

    public String getAddress1() {
        return address1;
    }

    public String getAddress2() {
        return address2;
    }

    @Override
    public void accept(ReportVisitor visitor) {
        visitor.visit(this);
    }
}

class Medicine implements ReportElement {

    private String nic;
    private String medicine;
    private String date;

    public Medicine(String nic, String medicine, String date) {
        this.nic = nic;
        this.medicine = medicine;
        this.date = date;
    }

    public String getNic() {
        return nic;
    }

    public String getMedicine() {
        return medicine;
    }

    public String getDate() {
        return date;
    }

    @Override
    public void accept(ReportVisitor visitor) {
        visitor.visit(this);
    }
}

class Surgery implements ReportElement {

    private String nic;
    private String surgery;
    private String date;

    public Surgery(String nic, String surgery, String date) {
        this.nic = nic;
        this.surgery = surgery;
        this.date = date;
    }

    public String getNic() {
        return nic;
    }

    public String getSurgery() {
        return surgery;
    }

    public String getDate() {
        return date;
    }

    @Override
    public void accept(ReportVisitor visitor) {
        visitor.visit(this);
    }
}

interface ReportVisitor {

    void visit(Patient patient);

    void visit(Medicine medicine);

    void visit(Surgery surgery);
}

class JTableReportVisitor implements ReportVisitor {

    private JTable patientTable;
    private JTable medicineTable;
    private JTable surgeryTable;

    public JTableReportVisitor(JTable patientTable, JTable medicineTable, JTable surgeryTable) {
        this.patientTable = patientTable;
        this.medicineTable = medicineTable;
        this.surgeryTable = surgeryTable;
    }

    @Override
    public void visit(Patient patient) {
        DefaultTableModel model = (DefaultTableModel) patientTable.getModel();
        model.addRow(new Object[]{
            patient.getNic(), patient.getFullName(), patient.getGender(),
            patient.getMobile(), patient.getAge(), patient.getAddress1(), patient.getAddress2()
        });
    }

    @Override
    public void visit(Medicine medicine) {
        DefaultTableModel model = (DefaultTableModel) medicineTable.getModel();
        model.addRow(new Object[]{medicine.getNic(), medicine.getMedicine(), medicine.getDate()});
    }

    @Override
    public void visit(Surgery surgery) {
        DefaultTableModel model = (DefaultTableModel) surgeryTable.getModel();
        model.addRow(new Object[]{surgery.getNic(), surgery.getSurgery(), surgery.getDate()});
    }
}

class PatientDataLoader {

    private PatientDataImplementor implementor;

    public PatientDataLoader(PatientDataImplementor implementor) {
        this.implementor = implementor;
    }

    public void loadData(JTable patientTable, JTable medicineTable, JTable surgeryTable, String nicFilter) {
        try {
            ResultSet rs = implementor.fetchPatientData();

            // clear tables
            ((DefaultTableModel) patientTable.getModel()).setRowCount(0);
            ((DefaultTableModel) medicineTable.getModel()).setRowCount(0);
            ((DefaultTableModel) surgeryTable.getModel()).setRowCount(0);

            JTableReportVisitor visitor = new JTableReportVisitor(patientTable, medicineTable, surgeryTable);

            while (rs.next()) {
                String nic = rs.getString("p.patient_nic");

                if (nicFilter != null && !nicFilter.isEmpty() && !nic.equalsIgnoreCase(nicFilter)) {
                    continue; // skip non-matching rows
                }

                // Create domain objects
                Patient patient = new Patient(
                        nic,
                        rs.getString("p.first_name") + " " + rs.getString("p.last_name"),
                        rs.getString("g.gender"),
                        rs.getString("p.mobile"),
                        rs.getString("p.age"),
                        rs.getString("p.addres_line1"),
                        rs.getString("p.address_line2")
                );
                patient.accept(visitor);

                Medicine medicine = new Medicine(
                        nic,
                        rs.getString("m.medicine") != null ? rs.getString("m.medicine") : "N/A",
                        rs.getString("medicine_date") != null ? rs.getString("medicine_date") : "N/A"
                );
                medicine.accept(visitor);

                Surgery surgery = new Surgery(
                        nic,
                        rs.getString("s.surgery") != null ? rs.getString("s.surgery") : "N/A",
                        rs.getString("surgery_date") != null ? rs.getString("surgery_date") : "N/A"
                );
                surgery.accept(visitor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

interface BillingData {

    ResultSet getBillingData(String nic) throws Exception;
}

class MySQLBillingData implements BillingData {

    @Override
    public ResultSet getBillingData(String billingID) throws Exception {
        String query = "SELECT * FROM `billing` "
                + "INNER JOIN `appointment` ON `appointment`.`id` = `billing`.`appointment_id`"
                + "INNER JOIN `patient` ON `billing`.`patient_patient_nic` = `patient`.`patient_nic`"
                + "INNER JOIN `staff` ON `staff`.`id` = `billing`.`staff_id`"
                + "INNER JOIN `payment_method` ON `payment_method`.`id` = `billing`.`payment_method_id`"
                + (billingID.isEmpty() ? "" : "WHERE billing.id = '" + billingID + "'");
        return MySQL.execute(query);
    }
}

interface BillingDataVisitor {

    void visit(BillingData data, JTable table, String nic);
}

class BillingTableVisitor implements BillingDataVisitor {

    @Override
    public void visit(BillingData data, JTable table, String billingID) {
        try {
            ResultSet rs = data.getBillingData(billingID);
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("billing.id"));
                row.add(rs.getString("billing.appointment_id"));
                row.add(rs.getString("staff.first_name") +" "+ rs.getString("staff.last_name"));
                row.add(rs.getString("billing.date"));
                row.add(rs.getString("patient.patient_nic"));
                row.add(rs.getString("billing.total_amount"));
                model.addRow(row);
            }
            table.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class BillingDataLoader {

    private BillingData data;
    private BillingDataVisitor visitor;

    public BillingDataLoader(BillingData data, BillingDataVisitor visitor) {
        this.data = data;
        this.visitor = visitor;
    }

    public void loadBillingData(JTable table, String nic) {
        visitor.visit(data, table, nic);
    }
}

public class ReportsPanel extends javax.swing.JPanel {

    public ReportsPanel() {
        initComponents();
        loadActivity();
        addSearchFilter(jTextField1, jTable1, jTable2, jTable4);
        loadBilling("");

        // add search filter
        jTextField2.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                String billingID = jTextField2.getText().trim();
                loadBilling(billingID);
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }
        });
    }

    public void loadActivity() {
        PatientDataLoader loader = new PatientDataLoader(new MySQLPatientData());
        loader.loadData(jTable1, jTable4, jTable2, "");
    }

    private void addSearchFilter(JTextField txtSearch, JTable jTable1, JTable jTable2, JTable jTable4) {
        TableRowSorter<DefaultTableModel> sorter1 = new TableRowSorter<>((DefaultTableModel) jTable1.getModel());
        TableRowSorter<DefaultTableModel> sorter2 = new TableRowSorter<>((DefaultTableModel) jTable2.getModel());
        TableRowSorter<DefaultTableModel> sorter4 = new TableRowSorter<>((DefaultTableModel) jTable4.getModel());

        jTable1.setRowSorter(sorter1);
        jTable2.setRowSorter(sorter2);
        jTable4.setRowSorter(sorter4);

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                String text = txtSearch.getText().trim();
                if (text.length() == 0) {
                    sorter1.setRowFilter(null);
                    sorter2.setRowFilter(null);
                    sorter4.setRowFilter(null);
                } else {
                    sorter1.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0)); // col 0 = NIC
                    sorter2.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
                    sorter4.setRowFilter(RowFilter.regexFilter("(?i)" + text, 0));
                }
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filter();
            }
        });
    }

    private void loadBilling(String billingID) {
        BillingData data = new MySQLBillingData();
        BillingDataVisitor visitor = new BillingTableVisitor();
        BillingDataLoader loader = new BillingDataLoader(data, visitor);
        loader.loadBillingData(jTable5, billingID);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        kGradientPanel3 = new com.k33ptoo.components.KGradientPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField2 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable5 = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(239, 239, 236));

        kGradientPanel3.setToolTipText("Product Management");
        kGradientPanel3.setkBorderRadius(0);
        kGradientPanel3.setkEndColor(new java.awt.Color(33, 52, 72));
        kGradientPanel3.setkStartColor(new java.awt.Color(138, 189, 192));
        kGradientPanel3.setName("[138,189,192] "); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Reports");
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        jLabel2.setIconTextGap(10);

        javax.swing.GroupLayout kGradientPanel3Layout = new javax.swing.GroupLayout(kGradientPanel3);
        kGradientPanel3.setLayout(kGradientPanel3Layout);
        kGradientPanel3Layout.setHorizontalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 1035, Short.MAX_VALUE)
                .addGap(44, 44, 44))
        );
        kGradientPanel3Layout.setVerticalGroup(
            kGradientPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
        );

        jLabel5.setBackground(new java.awt.Color(33, 52, 72));
        jLabel5.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(33, 52, 72));
        jLabel5.setText("Patient Details");

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

        jLabel4.setBackground(new java.awt.Color(33, 52, 72));
        jLabel4.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(33, 52, 72));
        jLabel4.setText("NIC :");

        jButton1.setBackground(new java.awt.Color(239, 239, 236));
        jButton1.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(33, 52, 72));
        jButton1.setText("Generate Patient Report");
        jButton1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

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

        jPanel3.setBackground(new java.awt.Color(239, 239, 236));
        jPanel3.setLayout(new java.awt.GridLayout());

        jPanel4.setBackground(new java.awt.Color(239, 239, 236));

        jLabel7.setBackground(new java.awt.Color(33, 52, 72));
        jLabel7.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(33, 52, 72));
        jLabel7.setText("Medicine History");

        jTable4.setBackground(new java.awt.Color(239, 239, 236));
        jTable4.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTable4.setForeground(new java.awt.Color(33, 52, 72));
        jTable4.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable4.setGridColor(new java.awt.Color(239, 239, 236));
        jScrollPane4.setViewportView(jTable4);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(387, 387, 387))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.add(jPanel4);

        jPanel5.setBackground(new java.awt.Color(239, 239, 236));
        jPanel5.setPreferredSize(new java.awt.Dimension(513, 32));

        jLabel8.setBackground(new java.awt.Color(33, 52, 72));
        jLabel8.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(33, 52, 72));
        jLabel8.setText("Surgery History");

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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
            .addComponent(jLabel8)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.add(jPanel5);

        jTextField2.setBackground(new java.awt.Color(239, 239, 236));
        jTextField2.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(33, 52, 72));
        jTextField2.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField2KeyReleased(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(33, 52, 72));
        jLabel9.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(33, 52, 72));
        jLabel9.setText("Billing ID:");

        jButton4.setBackground(new java.awt.Color(239, 239, 236));
        jButton4.setFont(new java.awt.Font("Bahnschrift", 1, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(33, 52, 72));
        jButton4.setText("Generate Finance Report");
        jButton4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTable5.setBackground(new java.awt.Color(239, 239, 236));
        jTable5.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTable5.setForeground(new java.awt.Color(33, 52, 72));
        jTable5.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Billing ID", "Appointment ID", "Issued By", "Date", "Patient NIC", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable5.setGridColor(new java.awt.Color(239, 239, 236));
        jScrollPane5.setViewportView(jTable5);

        jLabel10.setBackground(new java.awt.Color(33, 52, 72));
        jLabel10.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(33, 52, 72));
        jLabel10.setText("Billings Details");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 1073, Short.MAX_VALUE)
                    .addComponent(jScrollPane5)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel10)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(kGradientPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed

    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyReleased

    }//GEN-LAST:event_jTextField1KeyReleased

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField2KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2KeyReleased

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTable jTable5;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private com.k33ptoo.components.KGradientPanel kGradientPanel2;
    private com.k33ptoo.components.KGradientPanel kGradientPanel3;
    // End of variables declaration//GEN-END:variables
}
