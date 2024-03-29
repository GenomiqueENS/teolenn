/*
 *                  Teolenn development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU General Public License version 2 or later. This
 * should be distributed with the code. If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Teolenn project and its aims,
 * or to join the Teolenn Google group, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/teolenn
 *
 */

package fr.ens.transcriptome.teolenn.sequence.filter;

import fr.ens.transcriptome.teolenn.sequence.Sequence;

/**
 * This class define a filter that filters nothing but replace all 'X' of the
 * sequences by 'N'.
 * @author Laurent Jourdren
 */
public class RemoveXSequenceFilter implements SequenceFilter {

  /** Sequence filter name. */
  public static final String SEQUENCE_FILTER_NAME = "removex";
  
  /**
   * Get the name of the filter.
   * @return the name of the module
   */
  public String getName() {

    return SEQUENCE_FILTER_NAME;
  }

  /**
   * Get the description of the filter.
   * @return the description of the filter
   */
  public String getDescription() {

    return "Filter nothing but replace all 'X' of the sequences by 'N'.";
  }
  
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

  /**
   * Set a parameter for the filter.
   * @param key key for the parameter
   * @param value value of the parameter
   */
  public void setInitParameter(final String key, final String value) {
  }

  /**
   * Run the initialization phase of the parameter.
   */
  public void init() {
  }
  
  //
  // Constructor
  //

  /**
   * Public constructor.
   */
  public RemoveXSequenceFilter() {
  }
  
}
