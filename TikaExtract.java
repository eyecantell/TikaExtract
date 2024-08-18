import org.apache.tika.parser.*;
import org.apache.tika.sax.*;
import org.apache.tika.metadata.*;
import org.apache.tika.exception.*;
import org.apache.tika.parser.pdf.*;
import org.xml.sax.*;

import java.io.*;
import java.nio.file.*;

public class TikaExtract {
    // Your existing methods go here

    public static void main(String[] args) {
        String path = "/path/to/your/document.pdf";
        String xhtml = parseDocument(path);
        System.out.println(xhtml);
    }

    private static void setPdfConfig(ParseContext context) {
        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(true);
        pdfConfig.setExtractUniqueInlineImagesOnly(true);
    
        context.set(PDFParserConfig.class, pdfConfig);
    }
    
    private static String parseDocument(String path) {
        String xhtmlContents = "";
    
        AutoDetectParser parser = new AutoDetectParser();
        ContentHandler handler = new ToXMLContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        EmbeddedDocumentExtractor embeddedDocumentExtractor = 
                new EmbeddedDocumentExtractor() {
            @Override
            public boolean shouldParseEmbedded(Metadata metadata) {
                return true;
            }
            @Override
            public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml)
                    throws SAXException, IOException {
                Path outputDir = new File(path + "_").toPath();
                Files.createDirectories(outputDir);
    
                Path outputPath = new File(outputDir.toString() + "/" + metadata.get(Metadata.RESOURCE_NAME_KEY)).toPath();
                Files.deleteIfExists(outputPath);
                Files.copy(stream, outputPath);
            }
        };
    
        context.set(EmbeddedDocumentExtractor.class, embeddedDocumentExtractor);
        context.set(AutoDetectParser.class, parser);
    
        setPdfConfig(context);
    
        try (InputStream stream = new FileInputStream(path)) {
            parser.parse(stream, handler, metadata, context);
            xhtmlContents = handler.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException | TikaException e) {
            e.printStackTrace();
        }
    
        return xhtmlContents;
    }
}