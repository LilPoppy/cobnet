package com.storechain.polyglot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.io.ByteSequence;

import com.storechain.polyglot.io.PolyglotByteSequence;
import com.storechain.utils.ScriptEngineManager;

public final class PolyglotSource {
	
	private final Source source;
	
	public PolyglotSource(Source source) {
		
		this.source = source;
	}

	public Source getSource() {
		
		return source;
	}
	
	public static PolyglotSource create(String language, CharSequence source) {
		
		return new PolyglotSource(Source.create(language, source));
	}
	
	public static PolyglotSource readFromFile(String language, File file) throws FileNotFoundException, IOException {
		
		try(FileReader reader = new FileReader(file)) {
			
			char[] chars = new char[(int) file.length()];
			reader.read(chars);
			
			return create(language, new String(chars));
		}
	}
	
	public static PolyglotSource readFromFIle(String language, String file) throws FileNotFoundException, IOException {
		
		return readFromFile(language, new File(file));
	}
	
	public static PolyglotSource readFromResource(String language, String path, Charset charset, String delimiterPattern) {
		
        try (Scanner scanner = new Scanner(ScriptEngineManager.class.getResourceAsStream(path), charset)) {
        	
        	return create(language, scanner.useDelimiter(delimiterPattern).next());
        }
	}
	
	public static PolyglotSource readFromResource(String language, String path) {
		
		return readFromResource(language, path, StandardCharsets.UTF_8, "\\A");
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.source.equals(obj);
	}
	
	public static String findLanguage(File file) throws IOException {
		
		return Source.findLanguage(file);
	}
	
	public static String findLanguage(String mimeType) throws IOException {
		
		return Source.findLanguage(mimeType);
	}
	
	public static String findLanguage(URL url) throws IOException {
		
		return Source.findLanguage(url);
	}
	
	public static String findMimeType(File file) throws IOException {
		
		return Source.findMimeType(file);
	}
	
	public static String findMimeType(URL url) throws IOException {
		
		return Source.findMimeType(url);
	}
	
	public PolyglotByteSequence	getBytes() {
		
		return new PolyglotByteSequence(this.source.getBytes());
	}
	
	public CharSequence	getCharacters() {
		
		return this.source.getCharacters();
	}
	
	public CharSequence getCharacters(int lineNumber) {
		
		return this.source.getCharacters(lineNumber);
	}
	
	public int getColumnNumber(int offset) {
		
		return this.source.getColumnNumber(offset);
	}
	
	public String getLanguage() {
		
		return this.source.getLanguage();
	}
	
	public int getLength() {
		
		return this.source.getLength();
	}
	
	public int getLineCount() {
		
		return this.source.getLineCount();
	}
	
	public int getLineLength(int lineNumber) {
		
		return this.source.getLineLength(lineNumber);
	}
	
	public int getLineNumber(int offset) {
		
		return this.source.getLineNumber(offset);
	}
	
	public int getLineStartOffset(int lineNumber) {
		
		return this.source.getLineStartOffset(lineNumber);
	}
	
	public String getMimeType() {
		
		return this.source.getMimeType();
	}
	
	public String getName() {
		
		return this.source.getName();
	}
	
	public String getPath() {
		
		return this.source.getPath();
	}
	
	public Reader getReader() {
		
		return this.source.getReader();
	}
	
	public URI getURI() {
		
		return this.source.getURI();
	}
	
	public URL getURL() {
		
		return this.source.getURL();
	}
	
	public boolean hasBytes() {
		
		return this.source.hasBytes();
	}
	
	public boolean hasCharacters() {
		
		return this.source.hasCharacters();
	}
	
	public boolean isInteractive() {
		
		return this.source.isInteractive();
	}
	
	public boolean isInternal() {
		
		return this.source.isInternal();
	}
	
	public static PolyglotSourceBuilder newBuilder(String language, ByteSequence bytes, String name) {
		
		return new PolyglotSourceBuilder(Source.newBuilder(language, bytes, name));
	}
	
	public static PolyglotSourceBuilder newBuilder(String language, PolyglotByteSequence bytes, String name) {
		
		return PolyglotSource.newBuilder(language, bytes.getSequence(), name);
	}
	
	public static PolyglotSourceBuilder newBuilder(String language, CharSequence characters, String name) {
		
		return new PolyglotSourceBuilder(Source.newBuilder(language, characters, name));
	}
	
	public static PolyglotSourceBuilder newBuilder(String language, File file) {
		
		return new PolyglotSourceBuilder(Source.newBuilder(language, file));
	}
	
	public static PolyglotSourceBuilder newBuilder(String language, Reader source, String name) {
		
		return new PolyglotSourceBuilder(Source.newBuilder(language, source, name));
	}
	
	public static PolyglotSourceBuilder newBuilder(String language, URL url) {
		
		return new PolyglotSourceBuilder(Source.newBuilder(language, url));
	}
	
	@Override
	public String toString() {
		
		return this.source.toString();
	}
	
	@Override
	public int hashCode() {
		
		return this.source.hashCode();
	}
	
	public static final class PolyglotSourceBuilder {
		
		private Source.Builder builder;
		
		public PolyglotSourceBuilder(Source.Builder builder) {
			
			this.builder = builder;
		}

		public Source.Builder getBuilder() {
			return builder;
		}
		
		public PolyglotSource build() throws IOException {
			
			return new PolyglotSource(this.builder.build());
		}
		
		public PolyglotSource buildLiteral() throws IOException {
			
			return new PolyglotSource(this.builder.buildLiteral());
		}
		
		public PolyglotSourceBuilder cached(boolean cached) {
			
			this.builder = this.builder.cached(cached);
			
			return this;
		}
		
		public PolyglotSourceBuilder content(ByteSequence bytes) {
			
			this.builder = this.builder.content(bytes);
			
			return this;
		}
		
		public PolyglotSourceBuilder content(PolyglotByteSequence bytes) {
			
			return this.content(bytes.getSequence());
		}
		
		public PolyglotSourceBuilder content(CharSequence characters) {
			
			this.builder = this.builder.content(characters);
			
			return this;
		}
		
		public PolyglotSourceBuilder content(String code) {
			
			this.builder = this.builder.content(code);
			
			return this;
		}
		
		public PolyglotSourceBuilder encoding(Charset encoding) {
			
			this.builder = this.builder.encoding(encoding);
			
			return this;
		}
		
		public PolyglotSourceBuilder interactive(boolean interactive) {
			
			this.builder = this.builder.interactive(interactive);
			
			return this;
		}
		
		public PolyglotSourceBuilder internal(boolean internal) {
			
			this.builder = this.builder.internal(internal);
			
			return this;
		}
		
		public PolyglotSourceBuilder mimeType(String mimeType) {
			
			this.builder = this.builder.mimeType(mimeType);
			
			return this;
		}
		
		public PolyglotSourceBuilder name(String newName) {
			
			this.builder = this.builder.name(newName);
			
			return this;
		}
		
		public PolyglotSourceBuilder uri(URI newUri) {
			
			this.builder = this.builder.uri(newUri);
			
			return this;
		}
		
	}
}
