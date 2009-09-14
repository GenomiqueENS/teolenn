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

package fr.ens.transcriptome.teolenn.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import fr.ens.transcriptome.teolenn.TeolennException;
import fr.ens.transcriptome.teolenn.sequence.Sequence;

/**
 * This class allow to read fasta sequence of the oligonucleotides generated by
 * teolenn.
 * @author Laurent Jourdren
 */
public class OligoSequenceResource {

  /** The name of the resource. */
  public static final String RESOURCE_NAME = "oligo";

  /** The charset to use. */
  private static final String CHARSET = "ISO-8859-1";

  private File oligosDir;
  private String chr;
  private String oligosExtension;
  private int oligoLength;
  private boolean start1;
  private int positionConstant;
  private FileChannel inChannel;
  private ByteBuffer bb;

  /**
   * Get a sequence from teolenn generated fasta file
   * @param chromosome Chromosome of the sequence
   * @param oligoStartPos the start position of the oligonucleotide in the
   *          genome
   * @return a Sequence Object
   * @throws IOException if an error occurs while reading fasta file
   */
  public Sequence getSequence(final String chr, final int oligoStartPos)
      throws IOException {

    return getSequence(chr, oligoStartPos, null);
  }

  /**
   * Get a sequence from teolenn generated fasta file
   * @param chromosome Chromosome of the sequence
   * @param oligoStartPos the start position of the oligonucleotide in the
   *          genome
   * @param sequence the result to avoid creating a new object
   * @return a Sequence Object
   * @throws IOException if an error occurs while reading fasta file
   */
  public Sequence getSequence(final String chromosome, final int oligoStartPos,
      final Sequence sequence) throws IOException {

    if (chromosome == null || oligoStartPos < 0)
      return null;

    if (!chromosome.equals(this.chr)) {

      if (this.inChannel != null)
        this.inChannel.close();

      this.chr = chromosome;
      File f = new File(oligosDir, chromosome + oligosExtension);

      FileInputStream fis = new FileInputStream(f);
      this.inChannel = fis.getChannel();

      this.positionConstant = calcPositionConstant();
    }

    final long pos = getFilePos(oligoStartPos, this.start1);
    final int length = this.positionConstant + getDigits(oligoStartPos);

    if (this.bb == null || this.bb.capacity() != length)
      this.bb = ByteBuffer.allocate(length);
    else
      bb.clear();

    final int read = this.inChannel.read(bb, pos);

    if (read != length)
      throw new IOException("Error while reading sequence "
          + chromosome + "," + oligoStartPos + ").");

    return string2Sequence(new String(bb.array(), CHARSET), sequence);
  }

  /**
   * Convert the two line (with CR) of a fasta sequence to a Sequence object
   * @param s The String to convert
   * @param sequence The sequence output if you want reuse it
   * @return a Sequence object
   */
  private static final Sequence string2Sequence(final String s,
      final Sequence sequence) {

    final Sequence result;

    if (sequence == null)
      result = new Sequence();
    else
      result = sequence;

    final int i1 = s.indexOf('\n');
    if (i1 == -1)
      return null;

    final int i2 = s.indexOf('\n', i1 + 1);
    if (i2 == -1)
      return null;

    result.setName(s.substring(1, i1));
    result.setSequence(s.substring(i1 + 1, i2));

    return result;
  }

  /**
   * Close the file.
   * @throws IOException if an error occurs while closing file
   */
  public void close() throws IOException {

    if (this.inChannel == null)
      return;

    this.inChannel.close();
    this.inChannel = null;
  }

  /**
   * Compute the constant part of the position.
   * @return The constant parrt of the position
   */
  private int calcPositionConstant() {

    return 5
        + chr.length() + ":subseq(".length() + +getDigits(this.oligoLength)
        + oligoLength;
  }

  /**
   * Compute the variable part of the position
   * @param oligoStartPos The start of the oligonucleotide in the chormosome
   * @param start1 if the first position in the chromosome is 1
   * @return the
   */
  private static final long calcPositionVariable(final int oligoStartPos,
      final boolean start1) {

    long count = start1 ? 9 : 10;
    long toAdd = 9;
    long sumToAdd = 10;
    final int digits = getDigits(oligoStartPos);

    if (digits == 1)
      return oligoStartPos;

    for (int j = 2; j < digits; j++) {
      toAdd *= 10;
      count += toAdd * j;
      sumToAdd += toAdd;
    }

    count += (oligoStartPos - sumToAdd) * digits;

    return count;
  }

  /**
   * Get the position in file of the start of the oligonucleotide.
   * @param oligoStartPos The start of the oligonucleotide in the chormosome
   * @param start1 if the first position in the chromosome is 1
   * @return
   */
  private long getFilePos(final int oligoStartPos, final boolean start1) {

    return this.positionConstant
        * (start1 ? oligoStartPos - 1 : oligoStartPos)
        + calcPositionVariable(oligoStartPos, start1);
  }

  /**
   * Get the number of digits in a number.
   * @param n The input number
   * @return the number of digits of the number
   */
  private static final int getDigits(final int n) {

    int count = 1;
    int val = 10;

    while (n >= val) {
      count++;
      val *= 10;
    }

    return count;
  }

  /**
   * Get the resource.
   * @return a OligoSequenceResource Object if it has been already created
   * @throws TeolennException if the resource doesn't exists
   */
  public static OligoSequenceResource getRessource() throws TeolennException {

    final Resources rs = Resources.getResources();

    if (rs.isResource(RESOURCE_NAME))
      return (OligoSequenceResource) rs.getResource(RESOURCE_NAME);

    throw new TeolennException(
        "OligoSequenceResource has not been initialized.");
  }

  /**
   * Create the resource.
   * @param oligosDir the directory of the oligonucleotides
   * @param extension the extension of the fasta file
   * @param oligoLength the length of the oligonucleotides
   * @param start1 if the first position in the chromosome is 1
   */
  public static OligoSequenceResource getRessource(final File oligosDir,
      final String extension, final int oligoLength, final boolean start1)
      throws TeolennException {

    final Resources rs = Resources.getResources();

    if (rs.isResource(RESOURCE_NAME))
      return (OligoSequenceResource) rs.getResource(RESOURCE_NAME);

    final OligoSequenceResource result =
        new OligoSequenceResource(oligosDir, extension, oligoLength, start1);

    rs.setResource(RESOURCE_NAME, result);

    return result;
  }

  //
  // Constructor
  // 

  /**
   * Private constructor.
   * @param oligosDir the directory of the oligonucleotides
   * @param extension the extension of the fasta file
   * @param oligoLength the length of the oligonucleotides
   * @param start1 if the first position in the chromosome is 1
   */
  private OligoSequenceResource(final File oligosDir, final String extension,
      final int oligoLength, final boolean start1) throws TeolennException {

    if (oligosDir == null)
      throw new TeolennException("Invalid directory for oligonucleotides: "
          + oligosDir);

    if (extension == null)
      throw new TeolennException("Invalid extension for oligonucleotides: "
          + oligosExtension);

    if (oligoLength <= 0)
      throw new TeolennException("Invalid length for oligonucleotides: "
          + oligoLength);

    this.oligosDir = oligosDir;
    this.oligosExtension = extension;
    this.oligoLength = oligoLength;
    this.start1 = start1;

  }

}
