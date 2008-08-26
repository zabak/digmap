package org.apache.lucene.search;

/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import pt.utl.ist.lucene.Model;
import pt.utl.ist.lucene.ModelManager;

/**
 * This is a wrapper for actual term queriy object, which
 * are instatiated according to the system property
 * RetrievalModel
 * A Query that matches documents containing a term.
 * This may be combined with other terms with a {@link BooleanQuery}.
 */
public class TermQuery extends Query
{

    private TermQueryImpl termQueryImpl;

    public TermQuery(Term term)
    {
        Model model = ModelManager.getInstance().getModel();
        if(model == null)
        {
            String retModel = System.getProperty("RetrievalModel");
            model = Model.parse(retModel);

            if (model == null)
            {
                System.err.println("No retrieval model selected - defaulting to VectorSpace");
                System.setProperty("RetrievalModel", "VectorSpace");
                model = Model.VectorSpaceModel;
            }
        }

        switch ( model ) {
            case VectorSpaceModel: termQueryImpl = new TermQueryVectorSpace(term); break;
            case LanguageModel: termQueryImpl = new TermQueryLanguageModel(term) ; break;
            case DLHHypergeometricDFRModel:
            case InExpC2DFRModel :
            case InExpB2DFRModel :
            case IFB2DFRModel :
            case InL2DFRModel :
            case PL2DFRModel :
            case BB2DFRModel :
            case OkapiBM25Model :
            default:termQueryImpl = new TermQueryDFR(term,model);
        }
    }

    public String toString()
    {
        return "wrapper around: " + termQueryImpl.toString();
    }

    /**
     *
     * @return Returns the term of this query.
     */
    public Term getTerm()
    {
        return termQueryImpl.getTerm();
    }

    /**
     * Prints a user-readable version of this query.
     */
    public String toString(String field)
    {
        return termQueryImpl.toString();
    }

    /**
     * Returns true iff <code>o</code> is equal to this.
     */
    public boolean equals(Object o)
    {
        return termQueryImpl.equals(o);
    }

    /**
     * Returns a hash code value for this object.
     */
    public int hashCode()
    {
        return termQueryImpl.hashCode();
    }

    protected Weight createWeight(Searcher searcher)
    {
        return termQueryImpl.createWeight(searcher);
    }

    public void setBoost(float b)
    {
        termQueryImpl.setBoost(b);
    }

    public float getBoost()
    {
        return termQueryImpl.getBoost();
    }

    public Weight weight(Searcher searcher) throws IOException
    {
        return termQueryImpl.weight(searcher);
    }

    public Query rewrite(IndexReader reader) throws IOException
    {
        return termQueryImpl.rewrite(reader);
    }

    public Query combine(Query[] queries)
    {
        return termQueryImpl.combine(queries);
    }

    /**
     * Returns a clone of this query.
     * @return an exception
     */
    public Object clone()
    {
        throw new RuntimeException("Clone not supported: ");
    }

}
