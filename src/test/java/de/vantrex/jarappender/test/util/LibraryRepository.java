/*
 * Copyright (c) 2022. Drapuria
 */

package de.vantrex.jarappender.test.util;

import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public enum LibraryRepository {

    MAVEN_CENTRAL("https://repo1.maven.org/maven2/");

    private final String url;

    LibraryRepository(String url) {
        this.url = url;
    }

    private URLConnection openConnection(String path) throws IOException {
        URL url = new URL(this.url + path);
        return url.openConnection();
    }

    private URLConnection openConnection(String path, String mavenRepoPath) throws IOException {
        URL url = new URL(mavenRepoPath + path);
        return url.openConnection();
    }

    public byte[] downloadRaw(String path, String mavenRepoPath) throws Exception {
        try {
            HttpURLConnection connection = (HttpURLConnection) openConnection(path, mavenRepoPath);
            connection.setDoOutput(false);
            connection.setRequestMethod("GET");
            connection
                    .setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
            try (InputStream in = connection.getInputStream()) {
                byte[] bytes = ByteStreams.toByteArray(in);
                if (bytes.length == 0) {
                    throw new Exception("Empty stream");
                }
                return bytes;
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public byte[] download(String dependency, String mavenRepoPath) throws Exception {

        // compute a hash for the downloaded file

        return downloadRaw(dependency, mavenRepoPath);
    }

    public void download(String dependency, Path file, String mavenRepoPath) throws Exception {
        try {
            Files.write(file, download(dependency, mavenRepoPath));
        } catch (IOException e) {
            throw new Exception(e);
        }
    }

    public byte[] downloadRaw(String dependency) throws Exception {
        try {
            URLConnection connection = openConnection(dependency);
            try (InputStream in = connection.getInputStream()) {
                byte[] bytes = ByteStreams.toByteArray(in);
                if (bytes.length == 0) {
                    throw new Exception("Empty stream");
                }
                return bytes;
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    public byte[] download(String dependency) throws Exception {
        return downloadRaw(dependency);
    }

    public void download(String dependency, Path file) throws Exception {
        try {
            Files.write(file, download(dependency));
        } catch (IOException e) {
            throw new Exception(e);
        }
    }
}