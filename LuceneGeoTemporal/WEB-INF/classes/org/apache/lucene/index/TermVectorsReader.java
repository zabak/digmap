package org.apache.lucene.index;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.InputStream;
import org.apache.log4j.Logger;

import java.io.IOException;

/** TODO: relax synchro!
 */
class TermVectorsReader {
  private FieldInfos fieldInfos;

  private InputStream tvx;
  private InputStream tvd;
  private InputStream tvf;
  private InputStream tvl;  // doc length
  private int size;

  TermVectorsReader(Directory d, String segment, FieldInfos fieldInfos)
    throws IOException {
    if (d.fileExists(segment + TermVectorsWriter.TVX_EXTENSION)) {
      tvx = d.openFile(segment + TermVectorsWriter.TVX_EXTENSION);
      checkValidFormat(tvx);
      tvd = d.openFile(segment + TermVectorsWriter.TVD_EXTENSION);
      checkValidFormat(tvd);
      tvf = d.openFile(segment + TermVectorsWriter.TVF_EXTENSION);
      checkValidFormat(tvf);
      tvl = d.openFile(segment + TermVectorsWriter.TVL_EXTENSION);
      checkValidFormat(tvl);
      size = (int) tvx.length() / 8;
    }

    this.fieldInfos = fieldInfos;
  }
  
  private void checkValidFormat(InputStream in) throws IOException
  {
    int format = in.readInt();
    if (format > TermVectorsWriter.FORMAT_VERSION)
    {
      throw new IOException("Incompatible format version: " + format + " expected " 
              + TermVectorsWriter.FORMAT_VERSION + " or less");
    }
    
  }

  synchronized void close() throws IOException {
    // why don't we trap the exception and at least make sure that
    // all streams that we can close are closed?
    if (tvx != null) tvx.close();
    if (tvd != null) tvd.close();
    if (tvf != null) tvf.close();
    if (tvl != null) tvl.close();
  }

  /**
   * 
   * @return The number of documents in the reader
   */
  int size() {
    return size;
  }

  /**
   * Retrieve the term vector for the given document and field
   * @param docNum The document number to retrieve the vector for
   * @param field The field within the document to retrieve
   * @return The TermFreqVector for the document and field or null
   */ 
  synchronized TermFreqVector get(int docNum, String field) {
    // Check if no term vectors are available for this segment at all
    int fieldNumber = fieldInfos.fieldNumber(field);
    TermFreqVector result = null;
    if (tvx != null) {
      try {
        //We need to account for the FORMAT_SIZE at when seeking in the tvx
        //We don't need to do this in other seeks because we already have the file pointer
        //that was written in another file
        tvx.seek((docNum * 8L) + TermVectorsWriter.FORMAT_SIZE);
        //System.out.println("TVX Pointer: " + tvx.getFilePointer());
        long position = tvx.readLong();

        tvd.seek(position);
        int fieldCount = tvd.readVInt();
        //System.out.println("Num Fields: " + fieldCount);
        // There are only a few fields per document. We opt for a full scan
        // rather then requiring that they be ordered. We need to read through
        // all of the fields anyway to get to the tvf pointers.
        int number = 0;
        int found = -1;
        for (int i = 0; i < fieldCount; i++) {
          number += tvd.readVInt();
          if (number == fieldNumber) found = i;
        }
  
        // This field, although valid in the segment, was not found in this document
        if (found != -1) {
          // Compute position in the tvf file
          position = 0;
          for (int i = 0; i <= found; i++)
          {
            position += tvd.readVLong();
          }
          result = readTermVector(field, position);
        }
        else {
          //System.out.println("Field not found");
        }
          
      } catch (Exception e) {
        //e.printStackTrace();
      }
    }
    else
    {
      logger.info("No tvx file for Field:" + field);
    }
    return result;
  }

    private static final Logger logger = Logger.getLogger(TermVectorsReader.class);


  /** Return all term vectors stored for this document or null if the could not be read in. */
  synchronized TermFreqVector[] get(int docNum) {
    TermFreqVector[] result = null;
    // Check if no term vectors are available for this segment at all
    if (tvx != null) {
      try {
        //We need to offset by
        tvx.seek((docNum * 8L) + TermVectorsWriter.FORMAT_SIZE);
        long position = tvx.readLong();

        tvd.seek(position);
        int fieldCount = tvd.readVInt();

        // No fields are vectorized for this document
        if (fieldCount != 0) {
          int number = 0;
          String[] fields = new String[fieldCount];

          for (int i = 0; i < fieldCount; i++) {
            number += tvd.readVInt();
            fields[i] = fieldInfos.fieldName(number);
          }
  
          // Compute position in the tvf file
          position = 0;
          long[] tvfPointers = new long[fieldCount];
          for (int i = 0; i < fieldCount; i++) {
            position += tvd.readVLong();
            tvfPointers[i] = position;
          }

          result = readTermVectors(fields, tvfPointers);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else
    {
      logger.info("No tvx file");
    }
    return result;
  }

  private SegmentTermVector[] readTermVectors(String fields[], long tvfPointers[])
          throws IOException {
    SegmentTermVector res[] = new SegmentTermVector[fields.length];
    for (int i = 0; i < fields.length; i++) {
      res[i] = readTermVector(fields[i], tvfPointers[i]);
    }
    return res;
  }

  /**
   * 
   * @param field The field to read in
   * @param tvfPointer The pointer within the tvf file where we should start reading
   * @return The TermVector located at that position
   * @throws IOException
   */ 
  private SegmentTermVector readTermVector(String field, long tvfPointer)
          throws IOException {

    // Now read the data from specified position
    //We don't need to offset by the FORMAT here since the pointer already includes the offset
    tvf.seek(tvfPointer);

    int numTerms = tvf.readVInt();
    //System.out.println("Num Terms: " + numTerms);
    // If no terms - return a constant empty termvector
    if (numTerms == 0) return new SegmentTermVector(field, null, null);

    int length = numTerms + tvf.readVInt();

    String terms[] = new String[numTerms];
    
    int termFreqs[] = new int[numTerms];

    int start = 0;
    int deltaLength = 0;
    int totalLength = 0;
    char [] buffer = {};
    String previousString = "";
    for (int i = 0; i < numTerms; i++) {
      start = tvf.readVInt();
      deltaLength = tvf.readVInt();
      totalLength = start + deltaLength;
      if (buffer.length < totalLength)
      {
        buffer = new char[totalLength];
        for (int j = 0; j < previousString.length(); j++)  // copy contents
          buffer[j] = previousString.charAt(j);
      }
      tvf.readChars(buffer, start, deltaLength);
      terms[i] = new String(buffer, 0, totalLength);
      previousString = terms[i];
      termFreqs[i] = tvf.readVInt();
    }
    SegmentTermVector tv = new SegmentTermVector(field, terms, termFreqs);
    return tv;
  }

  /**
   * Get the sum of the lengths of all fields in a document
   * @param docNum The document number 
   * @return The total length of the fields within the document
   */ 
  synchronized int getDocLength(int docNum) {
    int numTerms = 0;
    if (tvl != null) {
      try {
        tvl.seek((docNum * 4L) + TermVectorsWriter.FORMAT_SIZE);
        numTerms = tvl.readInt();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    else
    {
      System.out.println("No tvl file");
    }
    return numTerms;
  }
  
  /**
   * Get the length (in terms) of a field.
   * @param docNum The document number 
   * @param field The field within the document
   * @return The length of the field within this document
 * @throws IOException
   */ 
  synchronized public int getFieldLength(String field, int docNum) throws IOException {
    return getFieldLength(fieldInfos.fieldNumber(field), docNum);
  }
 
  synchronized private int getFieldLength(int fieldNumber, int docNum) throws IOException {
  	int result = 0;
    if (tvx != null) {
      try {
        tvx.seek((docNum * 8L) + TermVectorsWriter.FORMAT_SIZE);
        long position = tvx.readLong();
        tvd.seek(position);
        int fieldCount = tvd.readVInt();
        int number = 0;
        int found = -1;
        for (int i = 0; i < fieldCount; i++) {
          number += tvd.readVInt();
          if (number == fieldNumber) found = i;
        }
        if (found != -1) {
          position = 0;
          for (int i = 0; i <= found; i++) {
            position += tvd.readVLong();
          }
          result = readFieldLength(position);
        }          
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      logger.info("No tvx file for field number: " + fieldNumber);
    }
    return result;
  }

  /**
   * @param tvfPointer The pointer within the tvf file where we should start reading
   * @return The field size (number of non-unique terms)
   * @throws IOException
   */ 
  private int readFieldLength(long tvfPointer) throws IOException {
  	int totalFreq = 0;
    tvf.seek(tvfPointer);
    int numTerms = tvf.readVInt();
    if (numTerms != 0) {
	    totalFreq = numTerms + tvf.readVInt();
    }
    return totalFreq;
  }

}
