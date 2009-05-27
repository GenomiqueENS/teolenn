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

package fr.ens.transcriptome.teolenn.selector;

import java.io.IOException;
import java.util.logging.Logger;

import fr.ens.transcriptome.teolenn.Design;
import fr.ens.transcriptome.teolenn.Globals;
import fr.ens.transcriptome.teolenn.measurement.Measurement;
import fr.ens.transcriptome.teolenn.sequence.SequenceMeasurements;

public class TilingSelector extends SimpleSelector {

  private static Logger logger = Logger.getLogger(Globals.APP_NAME);
  private static final float MIN_SCORE = -1 * Float.MAX_VALUE;

  private boolean start1;
  private int windowLength;
  private int windowStep;

  /**
   * Set a parameter for the filter.
   * @param key key for the parameter
   * @param value value of the parameter
   */
  public void setInitParameter(final String key, final String value) {

    if (Design.START_1_PARAMETER_NAME.equals(key))
      this.start1 = Boolean.parseBoolean(value);

    else if ("_windowLength".equals(key))
      this.windowLength = Integer.parseInt(value.trim());

    else if ("_windowStep".equals(key))
      this.windowStep = Integer.parseInt(value.trim());

    super.setInitParameter(key, value);
  }

  public void init() throws IOException {
  }

  public void doSelection() throws IOException {

    boolean first = true;
    int indexScaffold = -1;
    int indexStartPosition = -1;

    String currentScafold = null;
    int infoLastIndexStartPosition = -1;
    int infoCountWindows = 1;
    int infoCountSelectedOligos = 0;

    final int windowLength = this.windowLength;
    int endWindow = windowLength + (start1 ? 1 : 0);
    int startWindow = start1 ? 1 : 0;

    float bestScore = MIN_SCORE;
    float nextBestScore = MIN_SCORE;
    int posNextBestScore = -1;
    int lastSelected = -1;
    int valuesLength = -1;

    // Object used to read oligo measurement
    SequenceMeasurements sm = null;
    Object[] values = null;

    final SequenceMeasurements smToWrite = new SequenceMeasurements();
    final SequenceMeasurements nextSmToWrite = new SequenceMeasurements();

    while ((sm = next()) != null) {

      if (first) {

        indexScaffold = sm.getIndexMeasurment("chromosome");
        indexStartPosition = sm.getIndexMeasurment("oligostart");
        values = sm.getArrayMeasurementValues();

        for (Measurement m : sm.getMeasurements()) {
          smToWrite.addMesurement(m);
          nextSmToWrite.addMesurement(m);
        }

        valuesLength = values.length;
        first = false;
      }

      if (indexScaffold < 0)
        throw new RuntimeException("No Scaffold field");
      if (indexStartPosition < 0)
        throw new RuntimeException("No Start field");

      final String chromosome = (String) values[indexScaffold];
      final int pos = (Integer) values[indexStartPosition];

      final int id = sm.getId();

      // System.out.println("id="
      // + id + "\tpos=" + pos + "\t" + endWindow + "\tbest=" + bestScore
      // + "\t" + MIN_SCORE + "\tlastSelected=" + lastSelected);

      if (currentScafold == null)
        currentScafold = chromosome;

      if (!currentScafold.equals(chromosome)) {

        // Write best
        if (bestScore > MIN_SCORE) {

          // Prevent writing the same oligo more than one time
          if (lastSelected != smToWrite.getId()) {

            writeSelectedSequenceMeasurements(smToWrite);
            lastSelected = smToWrite.getId();
            infoCountSelectedOligos++;
          }
        } else
          logger.severe("Bad case while selecting (1): " + bestScore);

        logger
            .fine(String
                .format(
                    "chromosome: %s\t%d windows (%.2f theoric), "
                        + "%d oligos selected, %d pb in chromosome, %d pb windows, %d pb step.",
                    currentScafold,
                    infoCountWindows,
                    ((infoLastIndexStartPosition + 1.0f - windowLength) / windowStep) + 1.0f,
                    infoCountSelectedOligos, infoLastIndexStartPosition,
                    windowLength, windowStep));

        infoCountSelectedOligos = 0;

        currentScafold = chromosome;
        endWindow = windowLength + (start1 ? 1 : 0);
        startWindow = start1 ? 1 : 0;
        infoCountWindows = 1;

        while (pos >= endWindow) {
          startWindow = endWindow + 1;
          endWindow += windowStep;
          infoCountWindows++;
        }

        bestScore = MIN_SCORE;
        nextBestScore = MIN_SCORE;
        posNextBestScore = -1;
      }

      if (pos >= endWindow) {
        // Write best
        if (bestScore > MIN_SCORE) {

          // Prevent writing the same oligo more than one time
          if (lastSelected != smToWrite.getId()) {

            writeSelectedSequenceMeasurements(smToWrite);
            lastSelected = smToWrite.getId();
            infoCountSelectedOligos++;
          }
        } else
          logger.severe("Bad case while selecting (2): " + bestScore);

        bestScore = MIN_SCORE;

        final int previousEndWindow = endWindow;

        while (pos >= endWindow) {
          startWindow = endWindow + 1;
          endWindow += windowStep;
          infoCountWindows++;
        }

        // Test if the best score is also the best score for next window
        if (endWindow - windowStep == previousEndWindow) {

          bestScore = nextBestScore;

          // Test if the best score is also the best score for next next
          // window
          if (posNextBestScore < startWindow + windowStep)
            nextBestScore = MIN_SCORE;
        }

      }

      final float score = sm.getScore();
      boolean bestScoreChanged = false;

      if (score > bestScore) {

        bestScore = score;
        smToWrite.setId(id);

        // Add the global score
        final Object[] valuesToWrite = new Object[valuesLength];
        System.arraycopy(values, 0, valuesToWrite, 0, valuesLength);
        // valuesToWrite[valuesLength] = score;

        smToWrite.setArrayMeasurementValues(valuesToWrite);
        bestScoreChanged = true;
      }

      if ((pos >= startWindow + windowStep) && score > nextBestScore) {

        nextBestScore = score;
        posNextBestScore = pos;
        nextSmToWrite.setId(id);

        if (bestScoreChanged) {

          nextSmToWrite.setArrayMeasurementValues(smToWrite
              .getArrayMeasurementValues());
        } else {

          // Add the global score
          final Object[] valuesToWrite = new Object[valuesLength];
          System.arraycopy(values, 0, valuesToWrite, 0, valuesLength);
          // valuesToWrite[valuesLength] = score;

          nextSmToWrite.setArrayMeasurementValues(valuesToWrite);
        }
      }

      infoLastIndexStartPosition = pos;
    }

    // Write best
    if (bestScore > MIN_SCORE) {

      // logger.fine("*** select "
      // + smToWrite.getId() + " for window " + infoCountWindows
      // + "\tbestScore=" + bestScore + " ***");

      // Prevent writing the same oligo more than one time
      if (lastSelected != smToWrite.getId())
        writeSelectedSequenceMeasurements(smToWrite);
    }

    logger
        .fine(String
            .format(
                "chromosome: %s\t%d windows (%.2f theoric), "
                    + "%d oligos selected, %d pb in chromosome, %d pb windows, %d pb step.",
                currentScafold, infoCountWindows,
                (float) infoLastIndexStartPosition / (float) windowLength,
                infoCountSelectedOligos, infoLastIndexStartPosition,
                windowLength, windowStep));

    close();

  }

}
