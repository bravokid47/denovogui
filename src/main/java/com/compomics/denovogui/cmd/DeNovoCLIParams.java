package com.compomics.denovogui.cmd;

import org.apache.commons.cli.Options;

/**
 * Command line option parameters for DeNovoCLI.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public enum DeNovoCLIParams {

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // IMPORTANT: Any change here must be reported in the wiki: 
    // https://github.com/compomics/denovogui/wiki/DeNovoCLI.
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    SPECTRUM_FILES("spectrum_files", "Spectrum files (mgf format), comma separated list or an entire folder.", true),
    OUTPUT_FOLDER("output_folder", "The output folder.", true),
    IDENTIFICATION_PARAMETERS("id_params", "A search parameters file. Can be generated from the GUI or using the IdentificationParametersCLI (see http://compomics.github.io/projects/compomics-utilities/wiki/identificationparameterscli.html for details).", false),
    THREADS("threads", "The number of threads to use for the processing. Default is the number of cores available.", false),
    PEPNOVO("pepnovo", "Turn the PepNovo+ sequencing on or off (1: on, 0: off, default is '1'). (At least one sequencing method has to be enabled.)", false),
    DIRECT_TAG("directag", "Turn the DirecTag sequencing on or off (1: on, 0: off, default is '1'). (At least one sequencing method has to be enabled.)", false),
    PNOVO("pnovo", "Turn the pNovo+ sequencing on or off (1: on, 0: off, default is '0'). (At least one sequencing method has to be enabled.)", false),
    NOVOR("novor", "Turn the Novor sequencing on or off (1: on, 0: off, default is '0'). (At least one sequencing method has to be enabled.)", false),
    PEPNOVO_LOCATION("pepnovo_folder", "The PepNovo+ executable, defaults to the OS dependent versions included with DeNovoGUI.", false),
    DIRECTAG_LOCATION("directag_folder", "The DirecTag executable, defaults to the OS dependent versions included with DeNovoGUI.", false),
    PNOVO_LOCATION("pnovo_folder", "The pNovo+ executable, defaults to the OS dependent versions included with DeNovoGUI.", false),
    NOVOR_LOCATION("novor_folder", "The Novor executable, defaults to the OS dependent versions included with DeNovoGUI.", false);

    /**
     * Short Id for the CLI parameter.
     */
    public String id;
    /**
     * Explanation for the CLI parameter.
     */
    public String description;
    /**
     * Boolean indicating whether the parameter is mandatory.
     */
    public boolean mandatory;

    /**
     * Private constructor managing the various variables for the enum
     * instances.
     *
     * @param id the id
     * @param description the description
     * @param mandatory is the parameter mandatory
     */
    private DeNovoCLIParams(String id, String description, boolean mandatory) {
        this.id = id;
        this.description = description;
        this.mandatory = mandatory;
    }

    /**
     * Creates the options for the command line interface based on the possible
     * values.
     *
     * @param aOptions the options object where the options will be added
     */
    public static void createOptionsCLI(Options aOptions) {
        for (DeNovoCLIParams identificationParametersCLIParams : values()) {
            aOptions.addOption(identificationParametersCLIParams.id, true, identificationParametersCLIParams.description);
        }
        
        // Path setup
        PathSettingsCLIParams.createOptionsCLI(aOptions);
    }

    /**
     * Returns the options as a string.
     *
     * @return the options as a string
     */
    public static String getOptionsAsString() {

        String output = "";
        String formatter = "%-35s";

        output += "Mandatory Parameters:\n\n";
        output += "-" + String.format(formatter, SPECTRUM_FILES.id) + " " + SPECTRUM_FILES.description + "\n";
        output += "-" + String.format(formatter, OUTPUT_FOLDER.id) + " " + OUTPUT_FOLDER.description + "\n";
        output += "-" + String.format(formatter, IDENTIFICATION_PARAMETERS.id) + " " + IDENTIFICATION_PARAMETERS.description + "\n";

        output += "\n\nOptional Parameters:\n\n";
        output += "-" + String.format(formatter, PEPNOVO.id) + " " + PEPNOVO.description + "\n";
        output += "-" + String.format(formatter, DIRECT_TAG.id) + " " + DIRECT_TAG.description + "\n";
        output += "-" + String.format(formatter, PNOVO.id) + " " + PNOVO.description + "\n";
        output += "-" + String.format(formatter, NOVOR.id) + " " + NOVOR.description + "\n";

        output += "\n\nOptional Advanced Parameters:\n\n";
        output += "-" + String.format(formatter, PEPNOVO_LOCATION.id) + " " + PEPNOVO_LOCATION.description + "\n";
        output += "-" + String.format(formatter, DIRECTAG_LOCATION.id) + " " + DIRECTAG_LOCATION.description + "\n";
        output += "-" + String.format(formatter, PNOVO_LOCATION.id) + " " + PNOVO_LOCATION.description + "\n";
        output += "-" + String.format(formatter, NOVOR_LOCATION.id) + " " + NOVOR_LOCATION.description + "\n";
        output += "-" + String.format(formatter, THREADS.id) + " " + THREADS.description + "\n";

        output += "\n\nOptional Temporary Folder:\n\n";
        output += "-" + String.format(formatter, PathSettingsCLIParams.ALL.id) + " " + PathSettingsCLIParams.ALL.description + "\n";

        return output;
    }
}
