package com.compomics.denovogui.io;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.Advocate;
import com.compomics.util.experiment.identification.Identification;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.identification.SpectrumIdentificationAssumption;
import com.compomics.util.experiment.identification.spectrum_assumptions.TagAssumption;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.identification.amino_acid_tags.Tag;
import com.compomics.util.experiment.massspectrometry.Precursor;
import com.compomics.util.experiment.massspectrometry.Spectrum;
import com.compomics.util.experiment.massspectrometry.SpectrumFactory;
import com.compomics.util.experiment.refinementparameters.PepnovoAssumptionDetails;
import com.compomics.util.waiting.WaitingHandler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshallerException;

/**
 * This class allows exporting the results in a text file.
 *
 * @author Marc Vaudel
 */
public class TextExporter {

    /**
     * Separator used for the export.
     */
    private static final String separator = "\t";
    /**
     * Separator used for the export.
     */
    private static final String separator2 = ";";

    /**
     * Exports the peptide matching results to a given file.
     *
     * @param destinationFile the destination file
     * @param identification the identification object containing identification
     * details
     * @param searchParameters the search parameters used for the search
     * @param waitingHandler waiting handler displaying progress to the user and
     * allowing to cancel the process
     * @param scoreThreshold de novo score threshold
     * @param greaterThan use greater than threshold
     * @param aNumberOfMatches the maximum number of matches to export per
     * spectrum
     *
     * @throws IOException thrown if an IO exception occurs
     * @throws SQLException thrown if an SQL exception occurs
     * @throws ClassNotFoundException thrown if a ClassNotFoundException occurs
     * @throws MzMLUnmarshallerException thrown if a precursor cannot be
     * extracted from a spectrum
     * @throws InterruptedException thrown if the process is interrupted
     */
    public static void exportPeptides(File destinationFile, Identification identification, SearchParameters searchParameters,
            WaitingHandler waitingHandler, Double scoreThreshold, boolean greaterThan, Integer aNumberOfMatches)
            throws IOException, SQLException, ClassNotFoundException, MzMLUnmarshallerException, InterruptedException {

        FileWriter f = new FileWriter(destinationFile);

        double threshold = 0;
        if (scoreThreshold != null) {
            threshold = scoreThreshold;
        }
        int numberOfMatches = 10;
        if (aNumberOfMatches != null) {
            numberOfMatches = aNumberOfMatches;
        }

        try {
            BufferedWriter b = new BufferedWriter(f);

            try {
                b.write("File Name" + separator + "Spectrum Title" + separator + "Measured m/z" + separator + "Measured Charge" + separator
                        + "Rank" + separator + "Protein(s)" + separator + "Peptide" + separator + "Peptide Variable Modifications" + separator + "Modified Sequence"
                        + separator + "Tag" + separator + "Longest Amino Acid Sequence" + separator + "Tag Variable Modifications" + separator + "Modified tag sequence" + separator
                        + "PepNovo RankScore" + separator + "PepNovo Score" + separator + "DirecTag E-value" + separator + "pNovo+ Score" + separator + "N-Gap" + separator + "C-Gap" + separator
                        + "Theoretic m/z" + separator + "Identification Charge" + separator + "Tag Mass Error (Da)" + separator + "Tag Mass Error (ppm)" + separator
                        + "Peptide Mass Error (Da)" + separator + "Peptide Mass Error (ppm)" + separator + "Isotope");
                b.newLine();

                if (waitingHandler != null) {
                    waitingHandler.setWaitingText("Exporting Spectra - Writing File. Please Wait...");
                    // reset the progress bar
                    waitingHandler.resetSecondaryProgressCounter();
                    waitingHandler.setMaxSecondaryProgressCounter(identification.getSpectrumIdentificationSize());
                }

                for (String mgfFile : identification.getSpectrumFiles()) {
                    for (String spectrumKey : identification.getSpectrumIdentification(mgfFile)) {
                        if (identification.matchExists(spectrumKey)) {

                            String spectrumDetails = "";

                            String spectrumTitle = Spectrum.getSpectrumTitle(spectrumKey);
                            spectrumDetails += mgfFile + separator + spectrumTitle + separator;

                            Precursor precursor = SpectrumFactory.getInstance().getPrecursor(spectrumKey);
                            spectrumDetails += precursor.getMz() + separator + precursor.getPossibleChargesAsString() + separator;

                            ArrayList<PeptideAssumption> assumptions = new ArrayList<PeptideAssumption>();
                            HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> assumptionsMap = identification.getAssumptions(spectrumKey);

                            for (int algorithmId : assumptionsMap.keySet()) {
                                HashMap<Double, ArrayList<SpectrumIdentificationAssumption>> advocateMap = assumptionsMap.get(algorithmId);
                                if (advocateMap != null) {
                                    ArrayList<Double> scores = new ArrayList<Double>(advocateMap.keySet());
                                    Collections.sort(scores, Collections.reverseOrder());
                                    for (Double score : scores) {
                                        for (SpectrumIdentificationAssumption assumption : advocateMap.get(score)) {
                                            if (assumption instanceof PeptideAssumption) {
                                                PeptideAssumption peptideAssumption = (PeptideAssumption) assumption;
                                                assumptions.add(peptideAssumption);
                                            }
                                        }
                                    }
                                }
                            }

                            // export all matches above the score threshold up to the given user selected amount
                            for (int i = 0; i < assumptions.size() && i < numberOfMatches; i++) {

                                PeptideAssumption peptideAssumption = assumptions.get(i);

                                boolean passesThreshold;

                                if (greaterThan) {
                                    passesThreshold = peptideAssumption.getScore() >= threshold;
                                } else { // less than
                                    passesThreshold = peptideAssumption.getScore() <= threshold;
                                }

                                if (passesThreshold) {

                                    b.write(spectrumDetails);
                                    b.write(peptideAssumption.getRank() + separator);

                                    Peptide peptide = peptideAssumption.getPeptide();
                                    String proteinText = "";
                                    ArrayList<String> proteins = peptide.getParentProteinsNoRemapping();
                                    Collections.sort(proteins);
                                    for (String accession : proteins) {
                                        if (!proteinText.equals("")) {
                                            proteinText += separator2;
                                        }
                                        proteinText += accession;
                                    }
                                    b.write(proteinText + separator);

                                    b.write(peptide.getSequence() + separator);
                                    b.write(getPeptideModificationsAsString(peptide) + separator);
                                    b.write(peptide.getTaggedModifiedSequence(searchParameters.getPtmSettings(), false, false, true, false) + separator);

                                    TagAssumption tagAssumption = new TagAssumption();
                                    tagAssumption = (TagAssumption) peptideAssumption.getUrParam(tagAssumption);
                                    Tag tag = tagAssumption.getTag();
                                    b.write(tag.asSequence() + separator);
                                    b.write(tag.getLongestAminoAcidSequence() + separator);
                                    b.write(Tag.getTagModificationsAsString(tag) + separator);
                                    b.write(tag.getTaggedModifiedSequence(searchParameters.getPtmSettings(), false, false, true, false) + separator);
                                    if (tagAssumption.getAdvocate() == Advocate.pepnovo.getIndex()) {
                                        PepnovoAssumptionDetails pepnovoAssumptionDetails = new PepnovoAssumptionDetails();
                                        pepnovoAssumptionDetails = (PepnovoAssumptionDetails) tagAssumption.getUrParam(pepnovoAssumptionDetails);
                                        b.write(pepnovoAssumptionDetails.getRankScore() + separator);
                                        b.write(tagAssumption.getScore() + separator + separator + separator);
                                    } else if (tagAssumption.getAdvocate() == Advocate.direcTag.getIndex()) {
                                        b.write(separator + separator + tagAssumption.getScore() + separator + separator);
                                    } else if (tagAssumption.getAdvocate() == Advocate.pNovo.getIndex()) {
                                        b.write(separator + separator + separator + tagAssumption.getScore() + separator);
                                    }
                                    b.write(tag.getNTerminalGap() + separator);
                                    b.write(tag.getCTerminalGap() + separator);
                                    b.write(tag.getMass() + separator);
                                    b.write(tagAssumption.getIdentificationCharge().value + separator);
                                    double massDeviation = tagAssumption.getDeltaMass(precursor.getMz(), false);
                                    b.write(massDeviation + separator);
                                    massDeviation = tagAssumption.getDeltaMass(precursor.getMz(), true);
                                    b.write(massDeviation + separator);
                                    massDeviation = peptideAssumption.getDeltaMass(precursor.getMz(), false);
                                    b.write(massDeviation + separator);
                                    massDeviation = peptideAssumption.getDeltaMass(precursor.getMz(), true);
                                    b.write(massDeviation + separator);
                                    b.write(peptideAssumption.getIsotopeNumber(precursor.getMz()) + separator);
                                    b.newLine();
                                }
                            }
                            if (waitingHandler != null) {
                                waitingHandler.increaseSecondaryProgressCounter();
                                if (waitingHandler.isRunCanceled()) {
                                    return;
                                }
                            }
                        }
                    }
                }
            } finally {
                b.close();
            }
        } finally {
            f.close();
        }
    }

    /**
     * Exports the identification results to a given file.
     *
     * @param destinationFile the destination file
     * @param identification the identification object containing identification
     * details
     * @param searchParameters the search parameters used for the search
     * @param waitingHandler waiting handler displaying progress to the user and
     * allowing to cancel the process
     * @param scoreThreshold de novo score threshold
     * @param greaterThan use greater than threshold
     * @param aNumberOfMatches the maximum number of matches to export per
     * spectrum
     *
     * @throws IOException thrown if an IO exception occurs
     * @throws SQLException thrown if an SQL exception occurs
     * @throws ClassNotFoundException thrown if a ClassNotFoundException occurs
     * @throws MzMLUnmarshallerException thrown if a precursor cannot be
     * extracted from a spectrum
     * @throws InterruptedException thrown if the process is interrupted
     */
    public static void exportTags(File destinationFile, Identification identification, SearchParameters searchParameters,
            WaitingHandler waitingHandler, Double scoreThreshold, boolean greaterThan, Integer aNumberOfMatches)
            throws IOException, SQLException, ClassNotFoundException, MzMLUnmarshallerException, InterruptedException {

        FileWriter f = new FileWriter(destinationFile);

        double threshold = 0;
        if (scoreThreshold != null) {
            threshold = scoreThreshold;
        }
        int numberOfMatches = 10;
        if (aNumberOfMatches != null) {
            numberOfMatches = aNumberOfMatches;
        }

        try {
            BufferedWriter b = new BufferedWriter(f);

            try {
                b.write("File Name" + separator + "Spectrum Title" + separator + "Measured m/z" + separator + "Measured Charge" + separator
                        + "Rank" + separator + "Tag" + separator + "Longest AminoAcid sequence" + separator + "Variable Modifications" + separator + "Modified Sequence" + separator
                        + "PepNovo RankScore" + separator + "PepNovo Score" + separator + "DirecTag E-value" + separator + "pNovo+ Score" + separator + "N-Gap" + separator + "C-Gap" + separator
                        + "Theoretic m/z" + separator + "Identification Charge");
                b.newLine();

                if (waitingHandler != null) {
                    waitingHandler.setWaitingText("Exporting Spectra - Writing File. Please Wait...");
                    // reset the progress bar
                    waitingHandler.resetSecondaryProgressCounter();
                    waitingHandler.setMaxSecondaryProgressCounter(identification.getSpectrumIdentificationSize());
                }

                for (String mgfFile : identification.getSpectrumFiles()) {
                    for (String spectrumKey : identification.getSpectrumIdentification(mgfFile)) {
                        if (identification.matchExists(spectrumKey)) {

                            String spectrumTitle = Spectrum.getSpectrumTitle(spectrumKey);
                            String spectrumDetails = mgfFile + separator + spectrumTitle + separator;

                            Precursor precursor = SpectrumFactory.getInstance().getPrecursor(spectrumKey);
                            spectrumDetails += precursor.getMz() + separator + precursor.getPossibleChargesAsString() + separator;

                            ArrayList<TagAssumption> assumptions = new ArrayList<TagAssumption>();
                            HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> assumptionsMap = identification.getAssumptions(spectrumKey);

                            for (int algorithmId : assumptionsMap.keySet()) {
                                HashMap<Double, ArrayList<SpectrumIdentificationAssumption>> advocateMap = assumptionsMap.get(algorithmId);
                                if (advocateMap != null) {
                                    ArrayList<Double> scores = new ArrayList<Double>(advocateMap.keySet());
                                    Collections.sort(scores, Collections.reverseOrder());
                                    for (Double score : scores) {
                                        for (SpectrumIdentificationAssumption assumption : advocateMap.get(score)) {
                                            if (assumption instanceof TagAssumption) {
                                                TagAssumption tagAssumption = (TagAssumption) assumption;
                                                assumptions.add(tagAssumption);
                                            }
                                        }
                                    }
                                }
                            }

                            int rank = 0;

                            // export all matches above the score threshold up to the given user selected amount
                            for (int i = 0; i < assumptions.size() && i < numberOfMatches; i++) {

                                TagAssumption tagAssumption = assumptions.get(i);

                                boolean passesThreshold;

                                if (greaterThan) {
                                    passesThreshold = tagAssumption.getScore() >= threshold;
                                } else { // less than
                                    passesThreshold = tagAssumption.getScore() <= threshold;
                                }

                                if (passesThreshold) {
                                    Tag tag = tagAssumption.getTag();
                                    b.write(spectrumDetails);
                                    b.write(++rank + separator);
                                    b.write(tag.asSequence() + separator);
                                    b.write(tag.getLongestAminoAcidSequence() + separator);
                                    b.write(Tag.getTagModificationsAsString(tag) + separator);
                                    b.write(tag.getTaggedModifiedSequence(searchParameters.getPtmSettings(), false, false, true, false) + separator);
                                    if (tagAssumption.getAdvocate() == Advocate.pepnovo.getIndex()) {
                                        PepnovoAssumptionDetails pepnovoAssumptionDetails = new PepnovoAssumptionDetails();
                                        pepnovoAssumptionDetails = (PepnovoAssumptionDetails) tagAssumption.getUrParam(pepnovoAssumptionDetails);
                                        b.write(pepnovoAssumptionDetails.getRankScore() + separator);
                                        b.write(tagAssumption.getScore() + separator + separator + separator);
                                    } else if (tagAssumption.getAdvocate() == Advocate.direcTag.getIndex()) {
                                        b.write(separator + separator + tagAssumption.getScore() + separator + separator);
                                    } else if (tagAssumption.getAdvocate() == Advocate.pNovo.getIndex()) {
                                        b.write(separator + separator + separator + tagAssumption.getScore() + separator);
                                    }
                                    b.write(tag.getNTerminalGap() + separator);
                                    b.write(tag.getCTerminalGap() + separator);
                                    b.write(tag.getMass() + separator);
                                    b.write(tagAssumption.getIdentificationCharge().value + separator);
                                    b.newLine();
                                }
                            }
                            if (assumptions.isEmpty()) {
                                b.newLine(); //This should not happen. Should.
                            }
                            if (waitingHandler != null) {
                                waitingHandler.increaseSecondaryProgressCounter();
                                if (waitingHandler.isRunCanceled()) {
                                    return;
                                }
                            }
                        }

                        b.newLine();
                    }
                }
            } finally {
                b.close();
            }
        } finally {
            f.close();
        }
    }

    /**
     * Exports the BLAST-compatible identification results to a given file.
     *
     * @param destinationFile the destination file
     * @param identification the identification object containing identification
     * details
     * @param searchParameters the search parameters used for the search
     * @param waitingHandler waiting handler displaying progress to the user and
     * allowing to cancel the process.
     * @param scoreThreshold de novo score threshold
     * @param greaterThan use greater than threshold
     * @param aNumberOfMatches the maximum number of matches to export per
     * spectrum
     *
     * @throws IOException thrown if an IO exception occurs
     * @throws SQLException thrown if an SQL exception occurs
     * @throws ClassNotFoundException thrown if a ClassNotFoundException occurs
     * @throws MzMLUnmarshallerException thrown if a precursor cannot be
     * extracted from a spectrum
     * @throws InterruptedException thrown if the process is interrupted
     */
    public static void exportBlastPSMs(File destinationFile, Identification identification, SearchParameters searchParameters, WaitingHandler waitingHandler,
            Double scoreThreshold, boolean greaterThan, Integer aNumberOfMatches) throws IOException, SQLException, ClassNotFoundException, MzMLUnmarshallerException, InterruptedException {

        FileWriter f = new FileWriter(destinationFile);
        double threshold = 0;
        if (scoreThreshold != null) {
            threshold = scoreThreshold;
        }
        int numberOfMatches = 10;
        if (aNumberOfMatches != null) {
            numberOfMatches = aNumberOfMatches;
        }

        try {
            BufferedWriter b = new BufferedWriter(f);

            try {
                if (waitingHandler != null) {
                    waitingHandler.setWaitingText("Exporting Spectra - Writing File. Please Wait...");
                    // reset the progress bar
                    waitingHandler.resetSecondaryProgressCounter();
                    waitingHandler.setMaxSecondaryProgressCounter(identification.getSpectrumIdentificationSize());
                }

                for (String mgfFile : identification.getSpectrumFiles()) {
                    for (String spectrumKey : identification.getSpectrumIdentification(mgfFile)) {
                        if (identification.matchExists(spectrumKey)) {

                            String spectrumDetails = ">";
                            String spectrumTitle = Spectrum.getSpectrumTitle(spectrumKey);
                            spectrumDetails += mgfFile + separator2 + spectrumTitle + separator2;
                            Precursor precursor = SpectrumFactory.getInstance().getPrecursor(spectrumKey);
                            spectrumDetails += precursor.getMz() + separator2 + precursor.getPossibleChargesAsString() + separator2;
                            ArrayList<TagAssumption> assumptions = new ArrayList<TagAssumption>();
                            HashMap<Integer, HashMap<Double, ArrayList<SpectrumIdentificationAssumption>>> assumptionsMap = identification.getAssumptions(spectrumKey);

                            for (int algorithmId : assumptionsMap.keySet()) {
                                HashMap<Double, ArrayList<SpectrumIdentificationAssumption>> advocateMap = assumptionsMap.get(algorithmId);
                                if (advocateMap != null) {
                                    ArrayList<Double> scores = new ArrayList<Double>(advocateMap.keySet());
                                    Collections.sort(scores, Collections.reverseOrder());
                                    for (Double score : scores) {
                                        for (SpectrumIdentificationAssumption assumption : advocateMap.get(score)) {
                                            if (assumption instanceof TagAssumption) {
                                                TagAssumption tagAssumption = (TagAssumption) assumption;
                                                assumptions.add(tagAssumption);
                                            }
                                        }
                                    }
                                }
                            }

                            // export all matches above the score threshold up to the given user selected amount
                            for (int i = 0; i < assumptions.size() && i < numberOfMatches; i++) {

                                TagAssumption tagAssumption = assumptions.get(i);

                                boolean passesThreshold;

                                if (greaterThan) {
                                    passesThreshold = tagAssumption.getScore() >= threshold;
                                } else { // less than
                                    passesThreshold = tagAssumption.getScore() <= threshold;
                                }

                                if (passesThreshold) {
                                    b.write(spectrumDetails);
                                    if (tagAssumption.getAdvocate() == Advocate.pepnovo.getIndex()) {
                                        PepnovoAssumptionDetails pepnovoAssumptionDetails = new PepnovoAssumptionDetails();
                                        pepnovoAssumptionDetails = (PepnovoAssumptionDetails) tagAssumption.getUrParam(pepnovoAssumptionDetails);
                                        b.write(pepnovoAssumptionDetails.getRankScore() + separator2);
                                    } else {
                                        b.write(separator2);
                                    }
                                    b.write(tagAssumption.getScore() + "");
                                    b.newLine();
                                    b.write(tagAssumption.getTag().getLongestAminoAcidSequence());
                                    b.newLine();
                                }
                            }

                            if (assumptions.isEmpty()) {
                                b.newLine(); //This should not happen. Should.
                            }

                            if (waitingHandler != null) {
                                waitingHandler.increaseSecondaryProgressCounter();
                                if (waitingHandler.isRunCanceled()) {
                                    return;
                                }
                            }
                        }
                    }
                }
            } finally {
                b.close();
            }
        } finally {
            f.close();
        }
    }

    /**
     * Returns the peptide modifications as a string.
     *
     * @param peptide the peptide
     * @return the peptide modifications as a string
     */
    public static String getPeptideModificationsAsString(Peptide peptide) {

        StringBuilder result = new StringBuilder();

        if (peptide.isModified()) {
            HashMap<String, ArrayList<Integer>> modMap = new HashMap<String, ArrayList<Integer>>(peptide.getNModifications());
            for (ModificationMatch modificationMatch : peptide.getModificationMatches()) {
                if (modificationMatch.isVariable()) {
                    if (!modMap.containsKey(modificationMatch.getTheoreticPtm())) {
                        modMap.put(modificationMatch.getTheoreticPtm(), new ArrayList<Integer>());
                    }
                    modMap.get(modificationMatch.getTheoreticPtm()).add(modificationMatch.getModificationSite());
                }
            }

            boolean first = true, first2;
            ArrayList<String> mods = new ArrayList<String>(modMap.keySet());

            Collections.sort(mods);
            for (String mod : mods) {
                if (first) {
                    first = false;
                } else {
                    result.append(", ");
                }
                first2 = true;
                result.append(mod);
                result.append(" (");
                for (int aa : modMap.get(mod)) {
                    if (first2) {
                        first2 = false;
                    } else {
                        result.append(", ");
                    }
                    result.append(aa);
                }
                result.append(")");
            }
        }

        return result.toString();
    }
}
