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

package fr.ens.transcriptome.oligo.filter;

import fr.ens.transcriptome.oligo.Sequence;

/**
 * This class define a filter that filters nothing but replace all 'X' of the
 * sequences by 'N'.
 * @author Laurent Jourdren
 */
public class RemoveXSequenceFilter implements SequenceFilter {

  /**
   * Tests whether or not the specified sequence should be accepted.
   * @param sequence Sequence to test
   * @return allways true
   */
  public boolean accept(Sequence sequence) {

    if (sequence == null)
      return false;

    final String s = sequence.getSequence();
    if (s != null)
      sequence.setSequence(s.replace('X', 'N'));

    return true;
  }

}
