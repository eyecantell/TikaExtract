import org.apache.tika.parser.*;
import org.apache.tika.sax.*;
import org.apache.tika.metadata.*;
import org.apache.tika.exception.*;
import org.apache.tika.extractor.*;
import org.apache.tika.parser.pdf.*;
import org.xml.sax.*;
import java.io.*;
import java.nio.file.*;

public class TikaExtract {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: java TikaExtract <file_path>");
            System.exit(1);
        }

        String filePath = args[0];
        extractFromDocument(filePath);
    }

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
                
                System.out.println("Writing extracted item '" + fileName + "' to " + outputDir.toString());
                Path outputPath = outputDir.resolve(fileName);
                Files.copy(stream, outputPath, StandardCopyOption.REPLACE_EXISTING);
            }
        };

        context.set(EmbeddedDocumentExtractor.class, embeddedDocumentExtractor);
        context.set(AutoDetectParser.class, parser);

        // Add PDF-specific configuration
        PDFParserConfig pdfConfig = new PDFParserConfig();
        pdfConfig.setExtractInlineImages(true);
        pdfConfig.setExtractUniqueInlineImagesOnly(false);
        context.set(PDFParserConfig.class, pdfConfig);

        try (InputStream stream = new FileInputStream(path)) {
            parser.parse(stream, handler, metadata, context);
            xhtmlContents = handler.toString();

            Path xhtmlFilePath = Paths.get(outputDir.toString(), "content.xhtml");
            System.out.println("Writing contents to " + xhtmlFilePath.toString());
            Files.write(xhtmlFilePath, xhtmlContents.getBytes());

        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }

        return xhtmlContents;
    }
}