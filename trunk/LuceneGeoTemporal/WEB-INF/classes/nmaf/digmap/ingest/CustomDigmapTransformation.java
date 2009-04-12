package nmaf.digmap.ingest;

import nmaf.marc.Record;

import org.dom4j.Document;

public abstract class CustomDigmapTransformation {

	public abstract Document costumizeTransformation(Record rec, Document digmapDom);	
}
