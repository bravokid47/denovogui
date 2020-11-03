package com.compomics.denovogui.execution.jobs;

import com.compomics.denovogui.execution.Job;
import com.compomics.software.cli.CommandLineUtils;
import com.compomics.software.CompomicsWrapper;
import com.compomics.util.exceptions.ExceptionHandler;
import com.compomics.util.experiment.biology.modifications.Modification;
import com.compomics.util.experiment.biology.modifications.ModificationFactory;
import com.compomics.util.experiment.biology.modifications.ModificationType;
import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.parameters.UtilitiesUserParameters;
import com.compomics.util.parameters.identification.IdentificationParameters;
import com.compomics.util.parameters.identification.search.SearchParameters;
import com.compomics.util.parameters.identification.tool_specific.NovorParameters;
import com.compomics.util.waiting.WaitingHandler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * This job class runs the Novor software in wrapper mode.
 *
 * @author Harald Barsnes
 */
public class NovorJob extends Job {

    /**
     * The name of the Novor executable.
     */
    public final static String EXECUTABLE_FILE_NAME = "novor.jar";
    /**
     * The spectrumFile file.
     */
    private File spectrumFile;
    /**
     * The search parameters.
     */
    private SearchParameters searchParameters;
    /**
     * The path to the Novor executable.
     */
    private File novorFolder;
    /**
     * The output path.
     */
    private File outputFolder;
    /**
     * The command executed.
     */
    private String command = "";
    /**
     * The name of the Novor parameters file.
     */
    private String parameterFileName = "novor_params.txt";
    /**
     * The name of the Novor custom modifications file.
     */
    private String modsFileName = "novor_mods.txt";
    /**
     * The post translational modifications factory.
     */
    private ModificationFactory modFactory = ModificationFactory.getInstance();
    /**
     * The Novor to utilities PTM map. Key: Novor PTM short name, element:
     * utilities PTM name.
     */
    private HashMap<String, String> novorPtmMap;

    /**
     * Constructor for the NovorJob.
     *
     * @param novorFolder the path to the Novor executable
     * @param mgfFile the spectrum MGF file
     * @param outputFolder the output folder
     * @param searchParameters the search parameters
     * @param isCommandLine true if run from the command line, false if GUI
     * @param waitingHandler the waiting handler
     * @param exceptionHandler the exception handler
     */
    public NovorJob(File novorFolder, File mgfFile, File outputFolder, SearchParameters searchParameters, boolean isCommandLine, WaitingHandler waitingHandler, ExceptionHandler exceptionHandler) {
        this.novorFolder = novorFolder;
        this.spectrumFile = mgfFile;
        this.outputFolder = outputFolder;
        this.searchParameters = searchParameters;
        this.waitingHandler = waitingHandler;
        this.exceptionHandler = exceptionHandler;
        initJob(isCommandLine);
    }

    /**
     * Initializes the job, setting up the commands for the ProcessBuilder.
     */
    private void initJob(boolean isCommandLine) {

        try {
            // make sure that the novor jar file is executable
            File novorExecutable = new File(novorFolder.getAbsolutePath() + File.separator + EXECUTABLE_FILE_NAME);
            novorExecutable.setExecutable(true);

            // set java home
            UtilitiesUserParameters utilitiesUserParameters = UtilitiesUserParameters.loadUserParameters();
            CompomicsWrapper wrapper = new CompomicsWrapper();
            ArrayList<String> javaHomeAndOptions = wrapper.getJavaHomeAndOptions(utilitiesUserParameters.getDeNovoGuiPath());
            procCommands.add(javaHomeAndOptions.get(0)); // set java home

            // set java options
            if (!isCommandLine) {
                for (int i = 1; i < javaHomeAndOptions.size(); i++) {
                    procCommands.add(javaHomeAndOptions.get(i));
                }
            } else {
                // add the jvm arguments for denovogui to novor
                RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
                List<String> aList = bean.getInputArguments();
                for (String element : aList) {
                    procCommands.add(element);
                }
            }

            // add novor.jar
            procCommands.add("-jar");
            procCommands.add(CommandLineUtils.getCommandLineArgument(new File(novorFolder, EXECUTABLE_FILE_NAME)));

            // create the parameters file
            createParameterFile();

            // add the parameters
            procCommands.add("-p");
            procCommands.add(novorFolder.getAbsolutePath() + File.separator + parameterFileName);

            // add the custom modifications
            procCommands.add("-m");
            procCommands.add(novorFolder.getAbsolutePath() + File.separator + modsFileName);

            // add output folder
            String txtFileName = spectrumFile.getName().substring(0, spectrumFile.getName().lastIndexOf("."));
            outputFile = new File(outputFolder, txtFileName + ".novor.csv");
            procCommands.add("-o");
            procCommands.add(CommandLineUtils.getCommandLineArgument(outputFile));

            // force overwrite of output file
            procCommands.add("-f");

            // add the spectrum file
            procCommands.add(CommandLineUtils.getCommandLineArgument(spectrumFile));

            procCommands.trimToSize();

            // save command line
            for (String commandComponent : procCommands) {
                if (!command.equals("")) {
                    command += " ";
                }
                command += commandComponent;
            }

            writeCommand();
            
            // Set the description - yet not used
            setDescription("Novor");
            procBuilder = new ProcessBuilder(procCommands);
            procBuilder.directory(novorFolder);

            // set error out and std out to same stream
            procBuilder.redirectErrorStream(true);

        } catch (Exception e) {
            exceptionHandler.catchException(e);
            waitingHandler.appendReport("An error occurred running Novor. See error log for details. " + e.getMessage(), true, true);
            waitingHandler.setRunCanceled();
        }
    }

    /**
     * Cancels the job by destroying the process.
     */
    @Override
    public void cancel() {
        if (proc != null) {
            proc.destroy();
            log.info(">> De novo sequencing has been canceled.");
        }
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void writeCommand() {
        System.out.println(System.getProperty("line.separator") + System.getProperty("line.separator") + "Novor command: " + command + System.getProperty("line.separator"));
    }

    /**
     * Create the Novor parameters file.
     */
    private void createParameterFile() {

        // get the Novoe specific parameters
        NovorParameters novorParameters = (NovorParameters) searchParameters.getIdentificationAlgorithmParameter(Advocate.novor.getIndex());
        try {
            FileWriter parameterWriter = new FileWriter(novorFolder.getAbsolutePath() + File.separator + parameterFileName);
            BufferedWriter bufferedParameterWriter = new BufferedWriter(parameterWriter);

            bufferedParameterWriter.write("# Search parameters" + System.getProperty("line.separator"));

            // the enzyme
            bufferedParameterWriter.write("enzyme = Trypsin" + System.getProperty("line.separator"));

            // fragmentation method
            bufferedParameterWriter.write("fragmentation = " + novorParameters.getFragmentationMethod() + System.getProperty("line.separator"));

            // the instrument
            bufferedParameterWriter.write("massAnalyzer = " + novorParameters.getMassAnalyzer() + System.getProperty("line.separator"));

            // the fragment ion tolerance
            bufferedParameterWriter.write("fragmentIonErrorTol = ");
            if (searchParameters.getFragmentAccuracyType() == SearchParameters.MassAccuracyType.DA) {
                bufferedParameterWriter.write(searchParameters.getFragmentIonAccuracy() + "Da" + System.getProperty("line.separator"));
            } else {
                double convertedTolerance = IdentificationParameters.getDaTolerance(searchParameters.getFragmentIonAccuracy(), 1000);
                bufferedParameterWriter.write(convertedTolerance + "Da" + System.getProperty("line.separator")); // note: only dalton is currently supported
            }

            // the precursor ion tolerance
            bufferedParameterWriter.write("precursorErrorTol = " + searchParameters.getPrecursorAccuracy());
            if (searchParameters.getPrecursorAccuracyType() == SearchParameters.MassAccuracyType.DA) {
                bufferedParameterWriter.write("Da" + System.getProperty("line.separator"));
            } else {
                bufferedParameterWriter.write("ppm" + System.getProperty("line.separator"));
            }

            // add empty line
            bufferedParameterWriter.write(System.getProperty("line.separator"));

            // modifications
            FileWriter modsWriter = new FileWriter(novorFolder.getAbsolutePath() + File.separator + modsFileName);
            BufferedWriter bufferedModsWriter = new BufferedWriter(modsWriter);

            // create map for mapping back to the utilities ptms used
            novorPtmMap = new HashMap<String, String>();

            // variable modifications
            if (!searchParameters.getModificationParameters().getVariableModifications().isEmpty()) {
                bufferedParameterWriter.write("# Variable modifications" + System.getProperty("line.separator"));
                String variableModsAsString = "";

                for (String variableModification : searchParameters.getModificationParameters().getVariableModifications()) {
                    Modification mod = modFactory.getModification(variableModification);
                    addModification(bufferedModsWriter, mod);

                    // update the modifications string
                    if (!variableModsAsString.isEmpty()) {
                        variableModsAsString += ", ";
                    }
                    variableModsAsString += mod.getName();
                }

                // add the modification to the parameter file
                variableModsAsString = "variableModifications = " + variableModsAsString;
                bufferedParameterWriter.write(variableModsAsString + System.getProperty("line.separator") + System.getProperty("line.separator"));
            }

            // fixed modifications
            if (!searchParameters.getModificationParameters().getFixedModifications().isEmpty()) {
                bufferedParameterWriter.write("# Fixed modifications" + System.getProperty("line.separator"));
                String fixedModsAsString = "";

                for (String fixedModification : searchParameters.getModificationParameters().getFixedModifications()) {
                    Modification mod = modFactory.getModification(fixedModification);
                    addModification(bufferedModsWriter, mod);

                    // update the modifications string
                    if (!fixedModsAsString.isEmpty()) {
                        fixedModsAsString += ", ";
                    }
                    fixedModsAsString += mod.getName();
                }

                // add the modification to the parameter file
                fixedModsAsString = "fixedModifications = " + fixedModsAsString;
                bufferedParameterWriter.write(fixedModsAsString + System.getProperty("line.separator") + System.getProperty("line.separator"));
            }
            
            novorParameters.setNovorPtmMap(novorPtmMap);

            // close the mods writer
            bufferedModsWriter.close();
            modsWriter.close();

            // forbidden residues
            bufferedParameterWriter.write("# The residue which will not be used in de novo algorithm." + System.getProperty("line.separator"));
            bufferedParameterWriter.write("# I is disabled as default because it is the same as L" + System.getProperty("line.separator"));
            bufferedParameterWriter.write("# U is disabled because it is very rare" + System.getProperty("line.separator"));
            bufferedParameterWriter.write("forbiddenResidues = I,U" + System.getProperty("line.separator")); // @TODO: make this a user parameter?

            // close the parameters writer
            bufferedParameterWriter.close();
            modsWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, new String[]{"Unable to write file: '" + e.getMessage() + "'!",
                "Could not save Novr+ parameter file."}, "Novor Parameter File Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Converts a modification to the Novor format.
     *
     * @param bufferedModsWriter the writer to add the modification to
     * @param mod the current modification
     * @param modsAsString the current modifications as a string
     * @throws IOException thrown if an IOException occurs
     */
    private void addModification(BufferedWriter bufferedModsWriter, Modification mod) throws IOException {

        // modification id
        bufferedModsWriter.write(mod.getName() + ", ");

        // short name
        bufferedModsWriter.write(novorPtmMap.keySet().size() + ", ");
        novorPtmMap.put("" + novorPtmMap.keySet().size(), mod.getName());

        // long name
        bufferedModsWriter.write(mod.getName() + ", ");

        // the groups involved in the modification
        if (mod.getModificationType().isNTerm()) {
            if (mod.getModificationType() == ModificationType.modnaa_peptide 
                    || mod.getModificationType() == ModificationType.modnaa_protein) {
                bufferedModsWriter.write("nr-, ");
            } else {
                bufferedModsWriter.write("n--, ");
            }
        } else if (mod.getModificationType().isCTerm()) {
            if (mod.getModificationType() == ModificationType.modcaa_peptide 
                    || mod.getModificationType() == ModificationType.modcaa_protein) {
                bufferedModsWriter.write("-rc, ");
            } else {
                bufferedModsWriter.write("--c, ");
            }
        } else {
            bufferedModsWriter.write("-r-, ");
        }

        // the affected residues
        if (mod.getPattern() != null) {
            for (Character target : mod.getPattern().getAminoAcidsAtTarget()) {
                bufferedModsWriter.write(target);
            }
            bufferedModsWriter.write(", ");
        } else {
            bufferedModsWriter.write("*, ");
        }

        // the change of atoms
        bufferedModsWriter.write(", "); // @TOOD: we use this one instead of the mass?

        // the mass change
        bufferedModsWriter.write("" + mod.getRoundedMass());

        // add new line
        bufferedModsWriter.write(System.getProperty("line.separator"));
    }
}
