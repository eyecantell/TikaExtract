# Use the Remote Development extension and /devcontainer/devcontainer.json 
# Alternately for debugging, build from the samscrape_project directory using (but remember there will be no mounts)
#    docker build -t samscrape-container .
# Run using: 
#    docker run -it samscrape-container /bin/bash

# Use a base image with Python already installed
FROM python:3.10-slim

# Update apt 
RUN apt-get -y update

# Install git
RUN apt-get -y install git

# Install curl
RUN apt-get -y install curl
RUN apt-get -y install tree

# Install java runtime env
RUN apt-get -y install default-jdk
RUN apt-get -y install maven
RUN apt-get -y install gradle

# Install Tesseract for OCR in Tika
RUN apt-get -y install tesseract-ocr

# Add .local/bin to PATH
ENV PATH=/home/developer/.local/bin:$PATH

# Set the working directory
WORKDIR /workspace/TikaExtract

# Create the developer user
RUN useradd -s /bin/bash -m developer
USER developer