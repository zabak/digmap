package org.apache.lucene.ilps;

import java.sql.SQLException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Searcher;

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

class LuceneRetrieverXML extends LuceneRetriever {
		protected XMLStructureReader xr = null;

    LuceneRetrieverXML() {
			super();
			xr = new XMLStructureReader();
    }

    LuceneRetrieverXML(Searcher searcher, Analyzer analyzer, int maxResults) {
			super(searcher, analyzer, maxResults);
			xr = new XMLStructureReader();
    }

		/**
		 * Retruns the name of a docuement with the id docID. 
		 * @param docID 
		 * @return name of the document ... to be fed to the retrieval file
		 */
		public String getDocumentName(String docID){
			String name = null;
			try {
				name =
					xr.getElementFileName(Integer.valueOf(docID).intValue())
						+ xr.getElementPath(Integer.valueOf(docID).intValue());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return name;
		}
}


