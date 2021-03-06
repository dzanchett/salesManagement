
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showMessageDialog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author diego
 */
public class TelaEditarProduto extends javax.swing.JFrame {
    
    private static JComboBox<String> categoria;
    private static Map mapaCategorias;
    private String idProduto;

    /**
     * Creates new form TelaEditarProduto
     */
    public TelaEditarProduto(Map mapaCategorias, String idProduto, String nomeProduto, String categoriaProduto) {      
        initComponents();
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        
        this.categoria = categoriaField;
        this.mapaCategorias = mapaCategorias;
        this.idProduto = idProduto;
        
        atualizarCategoriaField();
        
        nomeProdutoField.setText(nomeProduto);
        categoriaField.setSelectedItem(categoriaProduto);
    }
    
    private static void atualizarCategoriaField(){
        mapaCategorias = BancoDeDados.getCategorias();
        Set chaveSet = mapaCategorias.keySet();
        Iterator it = chaveSet.iterator();
        
        categoria.removeAllItems();
        
        while(it.hasNext()){
            String item = (String) it.next();
            categoria.addItem(item);
        }
        
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
        jLabel1 = new javax.swing.JLabel();
        nomeProdutoField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        categoriaField = new javax.swing.JComboBox<>();
        cadastrarButton = new javax.swing.JButton();
        adicionarCategoriaButton = new javax.swing.JButton();
        apagarButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.addHierarchyListener(new java.awt.event.HierarchyListener() {
            public void hierarchyChanged(java.awt.event.HierarchyEvent evt) {
                jPanel1HierarchyChanged(evt);
            }
        });

        jLabel1.setText("Nome do produto:");

        jLabel2.setText("Categoria:");

        categoriaField.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        categoriaField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                categoriaFieldMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                categoriaFieldMouseClicked(evt);
            }
        });

        cadastrarButton.setText("Atualizar");
        cadastrarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cadastrarButtonActionPerformed(evt);
            }
        });

        adicionarCategoriaButton.setText("Adicionar Categoria");
        adicionarCategoriaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adicionarCategoriaButtonActionPerformed(evt);
            }
        });

        apagarButton.setText("Apagar");
        apagarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apagarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1)
                            .addComponent(nomeProdutoField)
                            .addComponent(jLabel2)
                            .addComponent(categoriaField, 0, 400, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cadastrarButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(apagarButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(adicionarCategoriaButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nomeProdutoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(categoriaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cadastrarButton)
                    .addComponent(adicionarCategoriaButton)
                    .addComponent(apagarButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void adicionarCategoriaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adicionarCategoriaButtonActionPerformed
        // TODO add your handling code here:
        JFrame adicionarCategoriaFrame = new TelaAdicionarCategoria();        
        adicionarCategoriaFrame.setVisible(true);
    }//GEN-LAST:event_adicionarCategoriaButtonActionPerformed

    private void jPanel1HierarchyChanged(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_jPanel1HierarchyChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel1HierarchyChanged

    private void categoriaFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categoriaFieldMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_categoriaFieldMouseClicked

    private void categoriaFieldMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categoriaFieldMousePressed
        // TODO add your handling code here:
        atualizarCategoriaField();
    }//GEN-LAST:event_categoriaFieldMousePressed

    private void cadastrarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cadastrarButtonActionPerformed
        // TODO add your handling code here:
        try {
            String nomeProduto = nomeProdutoField.getText();
            int idCategoria = (int) mapaCategorias.get(categoriaField.getSelectedItem().toString());
            BancoDeDados.atualizarProdutos(new Integer(idProduto), nomeProduto, idCategoria);
            TelaLogin.atualizaTabelaProduto();
            dispose();
        } catch (SQLException ex) {
            Logger.getLogger(TelaEditarProduto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TelaEditarProduto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cadastrarButtonActionPerformed

    private void apagarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apagarButtonActionPerformed
        try {
            // TODO add your handling code here:
            int ret = BancoDeDados.apagarProdutos(new Integer(idProduto));
            if(ret == 1){
                TelaLogin.atualizaTabelaProduto();
                TelaLogin.atualizarCategoriaField();
                dispose();
            }else{
                showMessageDialog(null, "Esse produto não pode ser apagado pois é uma chave estrangeira em uso.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TelaEditarProduto.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(TelaEditarProduto.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_apagarButtonActionPerformed

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
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaEditarProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaEditarProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaEditarProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaEditarProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                //new TelaEditarProduto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton adicionarCategoriaButton;
    private javax.swing.JButton apagarButton;
    private javax.swing.JButton cadastrarButton;
    private javax.swing.JComboBox<String> categoriaField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField nomeProdutoField;
    // End of variables declaration//GEN-END:variables
}
