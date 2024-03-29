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

package fr.ens.transcriptome.teolenn.sequence;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import fr.ens.transcriptome.teolenn.measurement.Measurement;
import fr.ens.transcriptome.teolenn.util.FileUtils;

/**
 * This class define a writer for statistics of sequences measurements.
 * @author Laurent Jourdren
 */
public class SequenceMeasurementsStatWriter {

  private Writer writer;

  /**
   * Write statistics.
   * @throws IOException if an error occurs while reading data
   */
  public void write(final SequenceMeasurements sm) throws IOException {

    if (sm == null)
      throw new NullPointerException("SequenceMesurement is null");

    String[] mNames = sm.getArrayMesurementNames();
    List<Measurement> listM = new ArrayList<Measurement>();
    for (int i = 0; i < mNames.length; i++)
      listM.add(sm.getMeasurement(mNames[i]));

    Set<String> pNames = new LinkedHashSet<String>();
    Map<Measurement, Properties> properties =
        new HashMap<Measurement, Properties>();

    for (Measurement m : sm.getMeasurements())
      properties.put(m, m.computeStatistics());

    for (Map.Entry<Measurement, Properties> value : properties.entrySet()) {

      final Properties p2 = value.getValue();
      if (p2 != null)
        for (Object name : p2.keySet())
          pNames.add((String) name);
    }

    for (int i = 0; i < mNames.length; i++) {
      writer.append("\t");
      writer.append(mNames[i]);
    }
    writer.append("\n");

    List<String> pNamesSorted = new ArrayList<String>(pNames);
    Collections.sort(pNamesSorted);

    for (String pName : pNamesSorted) {

      writer.append(pName);

      for (Measurement m : listM) {

        writer.append("\t");

        final Properties ps = properties.get(m);
        if (ps != null) {
          final String v = ps.getProperty(pName);
          if (v != null)
            writer.append(v);
        }
      }

      writer.append("\n");
    }

    writer.close();
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param file file to write
   */
  public SequenceMeasurementsStatWriter(final File file) throws IOException {

    if (file == null)
      throw new NullPointerException("File is null");

    this.writer = FileUtils.createBufferedWriter(file);
  }

}
