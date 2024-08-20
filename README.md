# TikaExtract
Using Apache Tika to extract images and text from various document types

## Usage

To run the TikaExtract application, follow these steps:

1. Ensure you have Java installed on your system.
2. Clone the TikaExtract repo (possibly with git submodule add)
3. Run the script:

   ```bash
   extract_from_document.sh <path_to_document>
   ```

   This will extract the content and embedded items (e.g. images) of the given document into a directory named <document_name>.extracts_data
