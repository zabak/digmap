package org.apache.lucene.search.regexp;

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

import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.search.spans.Spans;
import org.apache.lucene.search2.TermQuery;
import org.apache.lucene.util.ToStringUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;

/**
 * A SpanQuery version of {@link RegexQuery} allowing regular expression
 * queries to be nested within other SpanQuery subclasses.
 */
public class SpanRegexQuery extends SpanQuery implements RegexQueryCapable {
  private RegexCapabilities regexImpl = new JavaUtilRegexCapabilities();
  private Term term;

  public SpanRegexQuery(Term term) {
    this.term = term;
  }

  public Term getTerm() { return term; }

  public Query rewrite(IndexReader reader) throws IOException {
    RegexQuery orig = new RegexQuery(term);
    orig.setRegexImplementation(regexImpl);

    // RegexQuery (via MultiTermQuery).rewrite always returns a BooleanQuery
    BooleanQuery bq = (BooleanQuery) orig.rewrite(reader);

    BooleanClause[] clauses = bq.getClauses();
    SpanQuery[] sqs = new SpanQuery[clauses.length];
    for (int i = 0; i < clauses.length; i++) {
      BooleanClause clause = clauses[i];

      // Clauses from RegexQuery.rewrite are always TermQuery's
      TermQuery tq = (TermQuery) clause.query;

      sqs[i] = new SpanTermQuery(tq.getTerm());
      sqs[i].setBoost(tq.getBoost());
    }

    SpanOrQuery query = new SpanOrQuery(sqs);
    query.setBoost(orig.getBoost());

    return query;
  }

  public Spans getSpans(IndexReader reader) throws IOException {
    throw new UnsupportedOperationException("Query should have been rewritten");
  }

  public String getField() {
    return term.field();
  }

  public Collection getTerms() {
    Collection terms = new ArrayList();
    terms.add(term);
    return terms;
  }

  /* generated by IntelliJ IDEA */
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final SpanRegexQuery that = (SpanRegexQuery) o;

    if (!regexImpl.equals(that.regexImpl)) return false;
    if (!term.equals(that.term)) return false;

    return true;
  }

  /* generated by IntelliJ IDEA */
  public int hashCode() {
    int result;
    result = regexImpl.hashCode();
    result = 29 * result + term.hashCode();
    return result;
  }

  public String toString(String field) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("spanRegexQuery(");
    buffer.append(term);
    buffer.append(")");
    buffer.append(ToStringUtils.boost(getBoost()));
    return buffer.toString();
  }

  public void setRegexImplementation(RegexCapabilities impl) {
    this.regexImpl = impl;
  }

  public RegexCapabilities getRegexImplementation() {
    return regexImpl;
  }
}
