/*
 *                      Nividic development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.oligo.measurement;

import fr.ens.transcriptome.oligo.Sequence;

/**
 * This class define a measurement that returns the scaffolds of sequences.
 * @author Laurent Jourdren
 */
public final class ScaffoldMeasurement extends StringMeasurement {

  /**
   * Calc the measurement of a sequence.
   * @param sequence the sequence to use for the measurement
   * @return an String Object
   */
  protected String calcStringMeasurement(final Sequence sequence) {

    String seqName = sequence.getName();

    return seqName.substring(seqName.indexOf("_") + 1, seqName.indexOf(":"));
  }

  /**
   * Get the description of the measurement.
   * @return the description of the measurement
   */
  public String getDescription() {

    return "Get the scaffold of the sequence";
  }

  /**
   * Get the name of the measurement.
   * @return the name of the measurement
   */
  public String getName() {

    return "Scaffold";
  }

}
