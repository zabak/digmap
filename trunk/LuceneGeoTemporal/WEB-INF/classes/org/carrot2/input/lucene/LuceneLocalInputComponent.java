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

import java.io.IOException;
import java.util.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.carrot2.core.*;
import org.carrot2.core.clustering.*;
import org.carrot2.core.profiling.ProfiledLocalInputComponentBase;
import pt.utl.ist.lucene.LgteDocumentWrapper;
import pt.utl.ist.lucene.LgteHits;
import pt.utl.ist.lucene.LgteIndexSearcherWrapper;
import pt.utl.ist.lucene.LgteQuery;
import pt.utl.ist.lucene.level1query.QueryParams;

/**
 * Implements a local input component that reads data from Lucene index. Use
 * {@link org.carrot2.input.lucene.LuceneLocalInputComponentFactory} to obtain
 * instances of this component or provide a valid {@link LuceneSearchConfig}
 * object.
 */
public class LuceneLocalInputComponent extends ProfiledLocalInputComponentBase implements RawDocumentsProducer {
    /** The default number of requested results */
    public final static int DEFAULT_REQUESTED_RESULTS = 100;

    /** 
     * A request-context parameter overriding the default search configuration.
     * The value of this parameter must be an instance of {@link LuceneSearchConfig}. 
     */
    public final static String LUCENE_CONFIG = "org.carrot2.input.lucene.config";

    /** Capabilities required from the next component in the chain */
    private final static Set SUCCESSOR_CAPABILITIES = toSet(RawDocumentsConsumer.class);

    /** This component's capabilities */
    private final static Set COMPONENT_CAPABILITIES = toSet(RawDocumentsProducer.class);

    /**
     * All information required to perform a search in Lucene.
     */
    private final LuceneSearchConfig luceneSettings;

    /** Current query. */
    private String query;

    /** Current RawDocumentsConsumer to feed */
    private RawDocumentsConsumer rawDocumentConsumer;

    /** Current request context */
    private RequestContext requestContext;

    /**
     * No direct instantiation.
     * 
     * @deprecated Use {@link #LuceneLocalInputComponent(LuceneSearchConfig)}.
     */
    protected LuceneLocalInputComponent(LgteIndexSearcherWrapper searcher, Analyzer analyzer,
        String [] searchFields, String titleField, String summaryField,
        String urlField)
    {
        this(new LuceneSearchConfig(
                searcher, analyzer, searchFields, titleField,
                summaryField, urlField));
    }

    /**
     * Create an instance of the component with a set
     * of predefined settings.
     */
    public LuceneLocalInputComponent(LuceneSearchConfig settings)
    {
        this.luceneSettings = settings;
    }

    /**
     * Create an empty instance of this component. You will need to pass
     * Lucene configuration at query time using {@link #LUCENE_CONFIG}.
     */
    public LuceneLocalInputComponent()
    {
        this.luceneSettings = null;
    }

    /*
     * @see org.carrot2.core.LocalInputComponent#setQuery(java.lang.String)
     */
    public void setQuery(String query)
    {
        this.query = query;
    }

    /*
     * @see org.carrot2.core.LocalComponent#getComponentCapabilities()
     */
    public Set getComponentCapabilities()
    {
        return COMPONENT_CAPABILITIES;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#getRequiredSuccessorCapabilities()
     */
    public Set getRequiredSuccessorCapabilities()
    {
        return SUCCESSOR_CAPABILITIES;
    }

    /*
     * @see org.carrot2.core.LocalComponent#flushResources()
     */
    public void flushResources()
    {
        super.flushResources();
        query = null;
        rawDocumentConsumer = null;
    }

    /*
     * @see org.carrot2.core.LocalInputComponent#setNext(org.carrot2.core.LocalComponent)
     */
    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        if (next instanceof RawDocumentsConsumer)
        {
            rawDocumentConsumer = (RawDocumentsConsumer) next;
        }
        else
        {
            rawDocumentConsumer = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.carrot2.core.LocalComponent#startProcessing(org.carrot2.core.RequestContext)
     */
    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);

        this.requestContext = requestContext;

        // See if the required attributes are present in the query
        // context:
        Map params = requestContext.getRequestParameters();
        try
        {
            // Produce results.
            pushResults(params, luceneSettings);
        }
        catch (ParseException e)
        {
            throw new ProcessingException("Query parse exception", e);
        }
        catch (IOException e)
        {
            throw new ProcessingException("Query execution exception", e);
        }
    }

    /*
     * @see org.carrot2.core.LocalComponent#getName()
     */
    public String getName()
    {
        return "Lucene Input";
    }

    /**
     * 
     */
    private void pushResults(Map params, LuceneSearchConfig luceneSettings) throws ParseException, IOException,
        ProcessingException
    {
        // assemble request parameters
        final int requestedDocuments = getIntFromRequestContext(
                requestContext, LocalInputComponent.PARAM_REQUESTED_RESULTS, DEFAULT_REQUESTED_RESULTS);
        final int startAt = getIntFromRequestContext(
                requestContext, LocalInputComponent.PARAM_START_AT, 0);

        // check if there is an override for lucene settings in the context.
        if (params.containsKey(LUCENE_CONFIG)) {
            luceneSettings = (LuceneSearchConfig) params.get(LUCENE_CONFIG); 
        }
        
        if (luceneSettings == null) {
            throw new ProcessingException("Lucene input component not configured. Need LuceneSettings.");
        }
        
        // Create a boolean query that combines all fields
        final BooleanQuery booleanQuery = new BooleanQuery();

        for (int i = 0; i < luceneSettings.searchFields.length; i++) {
            final QueryParser queryParser = new QueryParser(luceneSettings.searchFields[i], luceneSettings.analyzer);
            queryParser.setOperator(QueryParser.DEFAULT_OPERATOR_AND);
            Query queryComponent = queryParser.parse(query);
            booleanQuery.add(queryComponent,true,false);
            //TODO: Test with Changing the query behaviour 
            //booleanQuery.add(queryComponent, new BooleanClause().Occur.SHOULD);
        }

        // Perform query

        final LgteHits hits = luceneSettings.searcher.search(booleanQuery);
        final int endAt = Math.min(hits.length(), startAt + requestedDocuments);

        // Pass the actual document count
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_TOTAL_MATCHING_DOCUMENTS,
            new Integer(endAt - startAt));

        // Pass the query
        requestContext.getRequestParameters().put(
            LocalInputComponent.PARAM_QUERY, query);

        // This improves document fetching when document count is > 100
        // The reason for this is 'specific' implementation of Lucene's
        // Hits class. The number 100 is hardcoded in Hits.
        if (endAt > 100)
        {
            hits.id(endAt - 1);
        }

        // Get results from the index
        for (int i = startAt; i < endAt; i++)
        {
            final LgteDocumentWrapper doc = hits.doc(i);
            final RawDocumentSnippet rawDocument = new RawDocumentSnippet(
                new Integer(hits.id(i)), 
                doc.get(luceneSettings.titleField), 
                doc.get(luceneSettings.summaryField), 
                doc.get(luceneSettings.urlField), 
                hits.score(i));
            rawDocumentConsumer.addDocument(rawDocument);
        }
    }
}