package jomm.utils;


import javax.activation.MimetypesFileTypeMap;
import java.io.File;

/**
 * @author Jorge Machado
 * @date 26/Fev/2008
 * @time 12:46:16
 */
public class MimeTypeGuesser {
    private MimetypesFileTypeMap typeMap;
    private static MimeTypeGuesser instance;

    private MimeTypeGuesser() {
        typeMap = new MimetypesFileTypeMap(Thread.currentThread().getContextClassLoader().getResourceAsStream("jomm/utils/mime.types"));
    }

    public static synchronized MimeTypeGuesser getInstance() {
        if (instance == null) {
            instance = new MimeTypeGuesser();
        }
        return instance;
    }

    public String guessMimeType(String file) {
        return guessMimeType(new File(file));
    }

    public String guessMimeType(File file) {
        return typeMap.getContentType(file);
    }
}
