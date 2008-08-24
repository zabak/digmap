
/*
 * Carrot2 project.
 *
 * Copyright (C) 2002-2006, Dawid Weiss, Stanisław Osiński.
 * Portions (C) Contributors listed in "carrot2.CONTRIBUTORS" file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the repository checkout or at:
 * http://www.carrot2.org/carrot2.LICENSE
 */

package org.carrot2.input.lucene;

import org.apache.lucene.analysis.Analyzer;
import pt.utl.ist.lucene.LgteIndexSearcherWrapper;

/**
 * All settings required to perform a search in Lucene:
 * location of the index, searched fields, analyzer etc.
 * 
 * @author Dawid Weiss
 */
public final class LuceneSearchConfig {
	
    /** Lucene Searcher */
    final LgteIndexSearcherWrapper searcher;

    /** Lucene Analyzer */
    final Analyzer analyzer;

    /** Lucene fields to be searched */
    final String [] searchFields;

    /** Content fields */
    final String titleField;
    final String summaryField;
    final String urlField;

    public LuceneSearchConfig(LgteIndexSearcherWrapper searcher, Analyzer analyzer,
            String [] searchFields, String titleField, String
            summaryField, String urlField)
    {
        this.searcher = searcher;
        this.analyzer = analyzer;
        this.searchFields = searchFields;
        this.titleField = titleField;
        this.summaryField = summaryField;
        this.urlField = urlField;
    }
}
