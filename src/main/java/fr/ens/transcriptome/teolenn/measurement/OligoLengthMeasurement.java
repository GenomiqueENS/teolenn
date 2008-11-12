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

package fr.ens.transcriptome.teolenn.measurement;

import fr.ens.transcriptome.teolenn.Sequence;

/**
 * This class define a measurement that give the length of sequences.
 * @author Laurent Jourdren
 */
public final class OligoLengthMeasurement extends IntegerMeasurement {

  /**
   * Calc the measurement of a sequence.
   * @param sequence the sequence to use for the measurement
   * @return an int value
   */
  protected int calcIntMeasurement(final Sequence sequence) {

    String seqName = sequence.getName();

    int startPos = seqName.indexOf(":subseq(");
    int startPos2 = seqName.indexOf(",", startPos);
    int endPos = seqName.indexOf(")", startPos2);

    return Integer.parseInt(seqName.substring(startPos2 + 1, endPos));
  }

  /**
   * Get the name of the measurement.
   * @return the name of the measurement
   */
  public String getName() {

    return "Length";
  }

  /**
   * Get the description of the measurement.
   * @return the description of the measurement
   */
  public String getDescription() {

    return "Get the length of the sequence";
  }

}