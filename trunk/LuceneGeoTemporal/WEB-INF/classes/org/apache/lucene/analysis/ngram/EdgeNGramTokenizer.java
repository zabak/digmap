package org.apache.lucene.analysis.ngram;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter.Side;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import pt.utl.ist.lucene.analyzer.LgteStemAnalyzer;

/**
 * Tokenizes the input from an edge into n-grams of given size(s).
 *
 * This tokenizer create n-grams from the beginning edge or ending edge of a input token.
 * MaxGram can't be larger than 1024 because of limitation.
 *
 */
public class EdgeNGramTokenizer extends Tokenizer {
  public static final Side DEFAULT_SIDE = Side.FRONT;
  public static final int DEFAULT_MAX_GRAM_SIZE = 1;
  public static final int DEFAULT_MIN_GRAM_SIZE = 1;

  // Replace this with an enum when the Java 1.5 upgrade is made, the impl will be simplified
  /** Specifies which side of the input the n-gram should be generated from */
  public static class Side {
    private String label;

    /** Get the n-gram from the front of the input */
    public static Side FRONT = new Side("front");

    /** Get the n-gram from the end of the input */
    public static Side BACK = new Side("back");

    // Private ctor
    private Side(String label) { this.label = label; }


    public String getLabel() { return label; }

    // Get the appropriate Side from a string
    public static Side getSide(String sideName) {
      if (FRONT.getLabel().equals(sideName)) {
        return FRONT;
      }
      else if (BACK.getLabel().equals(sideName)) {
        return BACK;
      }
      return null;
    }
  }

  private int minGram;
  private int maxGram;
  private int gramSize;
  private Side side;
  private boolean started = false;
  private int inLen;
  private String inStr;


  /**
   * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range
   *
   * @param input Reader holding the input to be tokenized
   * @param side the {@link Side} from which to chop off an n-gram
   * @param minGram the smallest n-gram to generate
   * @param maxGram the largest n-gram to generate
   */
  public EdgeNGramTokenizer(Reader input, Side side, int minGram, int maxGram) {
    super(input);

    if (side == null) {
      throw new IllegalArgumentException("sideLabel must be either front or back");
    }

    if (minGram < 1) {
      throw new IllegalArgumentException("minGram must be greater than zero");
    }

    if (minGram > maxGram) {
      throw new IllegalArgumentException("minGram must not be greater than maxGram");
    }

    this.minGram = minGram;
    this.maxGram = maxGram;
    this.side = side;
  }
  /**
   * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range
   *
   * @param input Reader holding the input to be tokenized
   * @param sideLabel the name of the {@link Side} from which to chop off an n-gram
   * @param minGram the smallest n-gram to generate
   * @param maxGram the largest n-gram to generate
   */
  public EdgeNGramTokenizer(Reader input, String sideLabel, int minGram, int maxGram) {
    this(input, Side.getSide(sideLabel), minGram, maxGram);
  }
  
  public final Token next() throws IOException {
	  Token aux = new Token("",0,0);
	  return next(aux);
  }

  /** Returns the next token in the stream, or null at EOS. */
  public final Token next(Token reusableToken) throws IOException {
    assert reusableToken != null;
    // if we are just starting, read the whole input
    if (!started) {
      started = true;
      char[] chars = new char[1024];
      input.read(chars);
      inStr = new String(chars).trim();  // remove any leading or trailing spaces
      inLen = inStr.length();
      gramSize = minGram;
    }

    // if the remaining input is too short, we can't generate any n-grams
    if (gramSize > inLen) {
      return null;
    }

    // if we have hit the end of our n-gram size range, quit
    if (gramSize > maxGram) {
      return null;
    }

    // grab gramSize chars from front or back
    int start = side == Side.FRONT ? 0 : inLen - gramSize;
    int end = start + gramSize;
    reusableToken = new Token(inStr.substring( start, gramSize), start, end,reusableToken.type());
    gramSize++;
    return reusableToken;
  }

    public static void main(String[] args) throws IOException
    {
        EdgeNGramTokenizer tokenizer; //= new EdgeNGramTokenizer(new StringReader("Jorge"), Side.FRONT,3,3);
        Token t;
//        while((t = tokenizer.next()) != null)
//        {
//            System.out.println(t.termText());
//        }
        Analyzer a = new LgteStemAnalyzer(4,4,EdgeNGramTokenFilter.Side.FRONT);
        TokenStream ts = a.tokenStream("contentsN4",new StringReader("\n" +
                "  the similarity laws for aerothermoelastic testing are presented\n" +
                "in the range .  these are obtained by\n" +
                "making nondimensional the appropriate governing equations of\n" +
                "the individual external aerodynamic flow, heat conduction to\n" +
                "the interior, and stress deflection problems which make up the\n" +
                "combined aerothermoelastic problem .\n" +
                "  for the general aerothermoelastic model, where the model is\n" +
                "placed in a high stagnation temperature wind tunnel, similitude\n" +
                "is shown to be very difficult to achieve for a scale ratio other\n" +
                "than unity .  the primary conflict occurs between the freestream\n" +
                "mach number reynolds number aeroelastic\n" +
                "parameter heat conduction parameter and\n" +
                "thermal expansion parameter .\n" +
                "  means of dealing with this basic conflict are presented .  these\n" +
                "include (1) looking at more specialized situations, such as the\n" +
                "behavior of wing structures and of thin solid plate lifting surfaces,\n" +
                "and panel flutter, where the aerothermoelastic similarity parameters\n" +
                "assume less restrictive forms, (2) the use of /incomplete\n" +
                "aerothermoelastic/ testing in which the pressure and/or heating\n" +
                "rates are estimated in advance and applied artificially to the\n" +
                "model, and (3) the use of /restricted purpose/ models investigating\n" +
                "separately one or another facet of the complete aerothermoelastic\n" +
                "problem .\n" +
                "  some numerical examples of modeling for the general aerothermoelastic\n" +
                "case as well as for the specialized situations mentioned\n" +
                "in (1) above are given .\n" +
                "  finally, extension of the aerothermoelastic similarity laws to\n" +
                "higher speeds and temperatures is discussed .\n" +
                "  \n" +
                "similarity laws for aerothermoelastic testing .\n" +
                " \n" +
                "similarity laws for aerothermoelastic testing .\n" +
                "  \n" +
                "dugundji,j.\n" +
                " \n" +
                "dugundji,j.\n" +
                "  ".replace("\n"," ")));
         while((t = ts.next()) != null)
        {
            System.out.println(t.termText());
        }
        tokenizer = new EdgeNGramTokenizer(new StringReader("Jo"), Side.FRONT,5,5);

        while((t = tokenizer.next()) != null)
        {
            System.out.println(t.termText());
        }
    }
}
