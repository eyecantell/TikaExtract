import org.apache.tika.parser.*;
import org.apache.tika.sax.*;
import org.apache.tika.metadata.*;
import org.apache.tika.exception.*;
import org.apache.tika.extractor.*;
import org.xml.sax.*;
import java.io.*;
import java.nio.file.*;

public class TikaExtract {
    public static void main(String[] args) {
        String path = "/notice_data/notices/FederalContractNotice/675798f955264ac5ab15a296a2a6fcac/ATTACHMENT-B-SOW-GM-MMNC-.docx";
        extractFromDocument(path);
        //System.out.println(xhtml);
    }

    /* The extractFromDocument routine uses Apache Tika to:
            1. Extract content from the given document and save in <document_name>.tika.json
            2. Extract images from given document and save them in a directory <document_name>_embedded
    }*/

    private static String extractFromDocument(String path) {
        String xhtmlContents = "";

        AutoDetectParser parser = new AutoDetectParser();
        ContentHandler handler = new ToXMLContentHandler();
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        Path outputDir = Paths.get(path + ".extracts_dir");

        EmbeddedDocumentExtractor embeddedDocumentExtractor = new EmbeddedDocumentExtractor() {
            @Override
            public boolean shouldParseEmbedded(Metadata metadata) {
                return true;
            }

            @Override
            public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml)
                    throws SAXException, IOException {
                
                Files.createDirectories(outputDir);

                String fileName = metadata.get("resourceName");
                if (fileName == null) {
                    fileName = "embedded_" + System.currentTimeMillis();
                }
                Path outputPath = outputDir.resolve(fileName);
                Files.copy(stream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            }
        };

        context.set(EmbeddedDocumentExtractor.class, embeddedDocumentExtractor);
        context.set(AutoDetectParser.class, parser);

        try (InputStream stream = new FileInputStream(path)) {
            parser.parse(stream, handler, metadata, context);
            xhtmlContents = handler.toString();

            // Save xhtmlContents in a file within the output directory
            Path xhtmlFilePath = Paths.get(outputDir.toString(), "content.xhtml");
            System.out.println("Writing contents to " + xhtmlFilePath.toString());
            Files.write(xhtmlFilePath, xhtmlContents.getBytes());

            // Print all metadata
            /*System.out.println("Metadata:");
            for (String name : metadata.names()) {

                System.out.println(name + ": " + metadata.get(name));
            }*/
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }

        return xhtmlContents;
    }
}