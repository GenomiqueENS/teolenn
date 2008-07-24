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

package fr.ens.transcriptome.oligo.util;

/**
 * This class implements a loopHandler with guided self scheduling.
 * @author Laurent Jourdren
 */
public abstract class GuidedLoopHandler extends PoolLoopHandler {
  protected int minSize;

  public GuidedLoopHandler(int start, int end, int min, int threads) {

    super(start, end, threads);
    minSize = min;
  }

  protected synchronized LoopRange loopGetRange() {

    if (curLoop >= endLoop)
      return null;

    LoopRange ret = new LoopRange();
    ret.start = curLoop;
    int sizeLoop = (endLoop - curLoop) / numThreads;
    curLoop += (sizeLoop > minSize) ? sizeLoop : minSize;
    ret.end = (curLoop < endLoop) ? curLoop : endLoop;
    return ret;

  }
}
