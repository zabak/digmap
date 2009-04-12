package nmaf.digmap.ingest;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;

import nmaf.digmap.DigmapServices;
import nmaf.digmap.gis.GeoParser;
import nmaf.marc.Field;
import nmaf.marc.Record;
import nmaf.marc.RecordType;
import nmaf.marc.xml.MarcXChangeDom4jBuilder;
import nmaf.util.Dom4jUtil;
import nmaf.util.FileUtil;
import nmaf.util.structure.Tuple;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

public abstract class CustomizedImporter {
	String baseFile;
	boolean useGeoParser;
	boolean useGeoParserGazeteerIds;
	
	public CustomizedImporter() {
	}
	
	public CustomizedImporter(String baseFile, boolean useGeoParser, boolean useGeoParserGazeteerIds) {
		super();
		this.baseFile = baseFile;
		this.useGeoParser = useGeoParser;
		this.useGeoParserGazeteerIds = useGeoParserGazeteerIds;
	}

	public abstract void runImport() throws Exception;
	
	public abstract void clean() throws Exception;
	


	public String getBaseFile() {
		return baseFile;
	}

	public void setBaseFile(String baseFile) {
		this.baseFile = baseFile;
	}

	public boolean isUseGeoParser() {
		return useGeoParser;
	}

	public void setUseGeoParser(boolean useGeoParser) {
		this.useGeoParser = useGeoParser;
	}

	public boolean isUseGeoParserGazeteerIds() {
		return useGeoParserGazeteerIds;
	}

	public void setUseGeoParserGazeteerIds(boolean useGeoParserGazeteerIds) {
		this.useGeoParserGazeteerIds = useGeoParserGazeteerIds;
	}	
}
