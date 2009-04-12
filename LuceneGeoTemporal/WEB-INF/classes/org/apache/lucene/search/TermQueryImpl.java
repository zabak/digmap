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

import org.apache.lucene.index.Term;


/** 
 * This is to force all TermQuery implementations to inherit
 * from the same class. 
 */
public abstract class TermQueryImpl extends Query {

	/** Prints a user-readable version of this query. */
	public abstract String toString(String field);

	/** Returns true iff <code>o</code> is equal to this. */
	public abstract boolean equals(Object o);

	/** Returns a hash code value for this object.*/
	public abstract int hashCode();

	/** Returns the term of this query. */
	public abstract Term getTerm();
	
	protected abstract Weight createWeight(Searcher searcher);
	
}
