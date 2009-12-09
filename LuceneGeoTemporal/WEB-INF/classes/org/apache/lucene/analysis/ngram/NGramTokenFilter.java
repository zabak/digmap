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
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Tokenizes the input into n-grams of the given size(s).
 */
public class NGramTokenFilter extends TokenFilter {
    public static final int DEFAULT_MIN_NGRAM_SIZE = 1;
    public static final int DEFAULT_MAX_NGRAM_SIZE = 2;

    private int minGram, maxGram;
    private LinkedList ngrams;

    /**
     * Creates NGramTokenFilter with given min and max n-grams.
     * @param input TokenStream holding the input to be tokenized
     * @param minGram the smallest n-gram to generate
     * @param maxGram the largest n-gram to generate
     */
    public NGramTokenFilter(TokenStream input, int minGram, int maxGram) {
        super(input);
        if (minGram < 1) {
            throw new IllegalArgumentException("minGram must be greater than zero");
        }
        if (minGram > maxGram) {
            throw new IllegalArgumentException("minGram must not be greater than maxGram");
        }
        this.minGram = minGram;
        this.maxGram = maxGram;
        this.ngrams = new LinkedList();
    }

    /**
     * Creates NGramTokenFilter with default min and max n-grams.
     * @param input TokenStream holding the input to be tokenized
     */
    public NGramTokenFilter(TokenStream input) {
        this(input, DEFAULT_MIN_NGRAM_SIZE, DEFAULT_MAX_NGRAM_SIZE);
    }

    /** Returns the next token in the stream, or null at EOS. */
    public final Token next(final Token reusableToken) throws IOException {
        assert reusableToken != null;
        if (ngrams.size() > 0) {
            return (Token) ngrams.removeFirst();
        }

        Token nextToken = input.next();
        if (nextToken == null)
            return null;

        ngram(nextToken);
        if (ngrams.size() > 0)
            return (Token) ngrams.removeFirst();
        else
            return null;
    }

    public final Token next() throws IOException {
        Token aux = new Token("",0,0);
        return next(aux);
    }

    private void ngram(Token token) {
//        char[] termBuffer = token.termText().toCharArray();
//        Token tokWord = new Token(token.termText(),0,token.endOffset(),token.type());
//        ngrams.add(tokWord);
        int termLength = token.termText().length();
        int gramSize = minGram;
        while (gramSize <= maxGram) {
            int pos = 0;                        // reset to beginning of string
            while (pos+gramSize <= termLength) {     // while there is input
                Token tok = new Token(token.termText().substring(pos,pos+gramSize),token.startOffset()+pos,token.startOffset()+pos+gramSize,token.type());
                ngrams.add(tok);
                pos++;
            }
            gramSize++;                         // increase n-gram size
        }
    }
}