package com.compomics.denovogui.gui;

import com.compomics.denovogui.DeNovoSequencingHandler;
import com.compomics.denovogui.io.FileProcessor;
import com.compomics.util.Util;
import com.compomics.util.experiment.identification.SearchParameters;
import java.io.File;

/**
 * Dialog for selecting DeNovoGUI results to display.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class SelectResultsDialog extends javax.swing.JDialog {

    /**
     * Indicates whether the user pressed the cancel button.
     */
    private boolean canceled = false;
    /**
     * The output file selected.
     */
    private File outFile = null;
    /**
     * The mgf file selected.
     */
    private File mgfFile = null;
    /**
     * The search parameters.
     */
    private SearchParameters searchParameters = null;
    /**
     * The last selected folder.
     */
    private String lastSelectedFolder = null;

    /**
     * Creates a new SelectResultsDialog.
     *
     * @param parent the parent
     * @param lastSelectedFolder the last selected folder
     */
    public SelectResultsDialog(java.awt.Frame parent, String lastSelectedFolder) {
        super(parent, true);
        this.lastSelectedFolder = lastSelectedFolder;
        initComponents();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * Indicates whether the user pressed the cancel button.
     *
     * @return whether the user pressed the cancel button
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Returns the output file selected by the user.
     *
     * @return the output file selected by the user
     */
    public File getOutFile() {
        return outFile;
    }

    /**
     * Returns the mgf file selected by the user.
     *
     * @return the mgf file selected by the user
     */
    public File getMgfFile() {
        return mgfFile;
    }

    /**
     * Returns the search parameters selected by the user.
     *
     * @return the search parameters selected by the user
     */
    public SearchParameters getSearchParameters() {
        return searchParameters;
    }

    /**
     * Returns the last selected folder
     *
     * @return the last selected folder
     */
    public String getLastSelectedFolder() {
        return lastSelectedFolder;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        outputPanel = new javax.swing.JPanel();
        browseOutButton = new javax.swing.JButton();
        outTxt = new javax.swing.JTextField();
        resultsLabel = new javax.swing.JLabel();
        spectraLabel = new javax.swing.JLabel();
        settingsLabel = new javax.swing.JLabel();
        browseMgfButton = new javax.swing.JButton();
        browseParametersButton = new javax.swing.JButton();
        mgfTxt = new javax.swing.JTextField();
        paramtersTxt = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Open Results");
        setResizable(false);

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

        outputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select DenovoGUI Output"));
        outputPanel.setOpaque(false);

        browseOutButton.setText("Browse");
        browseOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseOutButtonActionPerformed(evt);
            }
        });

        outTxt.setEditable(false);

        resultsLabel.setText("PepNovo Result");

        spectraLabel.setText("Spectra");

        settingsLabel.setText("Settings");

        browseMgfButton.setText("Browse");
        browseMgfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseMgfButtonActionPerformed(evt);
            }
        });

        browseParametersButton.setText("Browse");
        browseParametersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseParametersButtonActionPerformed(evt);
            }
        });

        mgfTxt.setEditable(false);

        paramtersTxt.setEditable(false);

        javax.swing.GroupLayout outputPanelLayout = new javax.swing.GroupLayout(outputPanel);
        outputPanel.setLayout(outputPanelLayout);
        outputPanelLayout.setHorizontalGroup(
            outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(outputPanelLayout.createSequentialGroup()
                        .addGroup(outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(outputPanelLayout.createSequentialGroup()
                                .addComponent(resultsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(outTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, outputPanelLayout.createSequentialGroup()
                                .addComponent(settingsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(paramtersTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addGroup(outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(browseOutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(browseParametersButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(outputPanelLayout.createSequentialGroup()
                        .addComponent(spectraLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mgfTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseMgfButton)))
                .addContainerGap())
        );

        outputPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {browseMgfButton, browseOutButton, browseParametersButton});

        outputPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {resultsLabel, settingsLabel, spectraLabel});

        outputPanelLayout.setVerticalGroup(
            outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(outputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(browseOutButton)
                    .addComponent(outTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(resultsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spectraLabel)
                    .addComponent(browseMgfButton)
                    .addComponent(mgfTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(outputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(settingsLabel)
                    .addComponent(browseParametersButton)
                    .addComponent(paramtersTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(outputPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(outputPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Opens a file chooser to select the out files.
     *
     * @param evt
     */
    private void browseOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseOutButtonActionPerformed

        File selectedFile = Util.getUserSelectedFile(this, ".out", "DeNovoGUI result file (.out)", "Select Output File", lastSelectedFolder, true);

        if (selectedFile != null) {

            outFile = selectedFile;
            lastSelectedFolder = outFile.getParent();
            outTxt.setText(outFile.getName());

            // try to find the mgf file
            File tempMgfFile = FileProcessor.getMgfFile(outFile);
            if (tempMgfFile.exists()) {
                mgfFile = tempMgfFile;
                mgfTxt.setText(mgfFile.getName());
            }

            // try to find the parameters file
            File tempParamtersFile = new File(outFile.getParent(), DeNovoSequencingHandler.parametersFileName);
            if (tempParamtersFile.exists()) {
                try {
                    searchParameters = SearchParameters.getIdentificationParameters(tempParamtersFile);
                    paramtersTxt.setText(tempParamtersFile.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_browseOutButtonActionPerformed

    /**
     * Opens a file chooser to select the mgf files.
     *
     * @param evt
     */
    private void browseMgfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseMgfButtonActionPerformed

        File selectedFile = Util.getUserSelectedFile(this, ".mgf", "Spectrum files (.mgf)", "Select Spectrum File", lastSelectedFolder, true);

        if (selectedFile != null) {
            mgfFile = selectedFile;
            lastSelectedFolder = mgfFile.getParent();
            mgfTxt.setText(mgfFile.getName());
        }
    }//GEN-LAST:event_browseMgfButtonActionPerformed

    /**
     * Opens a file chooser to select the parameter file.
     *
     * @param evt
     */
    private void browseParametersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseParametersButtonActionPerformed

        File selectedFile = Util.getUserSelectedFile(this, ".parameters", "DeNovoGUI settings (.parameters)", "Select Settings File", lastSelectedFolder, true);

        if (selectedFile != null) {
            File tempParamtersFile = selectedFile;
            try {
                searchParameters = SearchParameters.getIdentificationParameters(tempParamtersFile);
                lastSelectedFolder = tempParamtersFile.getParent();
                paramtersTxt.setText(tempParamtersFile.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_browseParametersButtonActionPerformed

    /**
     * Cancel the opening and close the dialog.
     *
     * @param evt
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        canceled = true;
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Close the dialog and open the results.
     *
     * @param evt
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton browseMgfButton;
    private javax.swing.JButton browseOutButton;
    private javax.swing.JButton browseParametersButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JTextField mgfTxt;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField outTxt;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JTextField paramtersTxt;
    private javax.swing.JLabel resultsLabel;
    private javax.swing.JLabel settingsLabel;
    private javax.swing.JLabel spectraLabel;
    // End of variables declaration//GEN-END:variables
}
