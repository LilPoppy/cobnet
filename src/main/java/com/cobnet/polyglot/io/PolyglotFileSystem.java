package com.cobnet.polyglot.io;

import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.util.Map;
import java.util.Set;

import org.graalvm.polyglot.io.FileSystem;

public final class PolyglotFileSystem {

	private final FileSystem system;
	
	public PolyglotFileSystem(FileSystem system) {
		
		this.system = system;
	}

	public FileSystem getSystem() {
		
		return system;
	}
	
	public void checkAccess(Path path, Set<? extends AccessMode> modes, LinkOption... linkOptions) throws IOException {
		
		this.system.checkAccess(path, modes, linkOptions);
	}
	
	public void copy(Path source, Path target, CopyOption... options) throws IOException {
		
		this.system.copy(source, target, options);
	}
	
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
		
		this.system.createDirectory(dir, attrs);
	}
	
	public void createLink(Path link, Path existing) throws IOException {
		
		this.system.createLink(link, existing);
	}
	
	public void createSymbolicLink(Path link, Path target, FileAttribute<?>... attrs) throws IOException {
		
		this.system.createSymbolicLink(link, target, attrs);
	}
	
	public void delete(Path path) throws IOException {
		
		this.system.delete(path);
	}
	
	public Charset getEncoding(Path path) {
		
		return this.system.getEncoding(path);
	}
	
	public String getMimeType(Path path) {
		
		return this.system.getMimeType(path);
	}
	
	public String getPathSeparator() {
		
		return this.system.getPathSeparator();
	}
	
	public String getSeparator() {
		
		return this.system.getSeparator();
	}
	
	public Path getTempDirectory() {
		
		return this.system.getTempDirectory();
	}
	
	public boolean isSameFile(Path path1, Path path2, LinkOption... options) throws IOException {
		
		return this.system.isSameFile(path1, path2, options);
	}
	
	public void move(Path source, Path target, CopyOption... options) throws IOException {
		
		this.system.move(source, target, options);
	}
	
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
		
		return this.system.newByteChannel(path, options, attrs);
	}
	
	public static PolyglotFileSystem newDefaultFileSystem() {
		
		return new PolyglotFileSystem(FileSystem.newDefaultFileSystem());
	}
	
	public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
		
		return this.system.newDirectoryStream(dir, filter);
	}
	
	public Path parsePath(String path) {
		
		return this.system.parsePath(path);
	}
	
	public Path parsePath(URI uri) {
		
		return this.system.parsePath(uri);
	}
	
	public Map<String,Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
		
		return this.system.readAttributes(path, attributes, options);
	}
	
	public Path	readSymbolicLink(Path link) throws IOException {
		
		return this.system.readSymbolicLink(link);
	}
	
	public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
		
		this.system.setAttribute(path, attribute, value, options);
	}
	
	public void	setCurrentWorkingDirectory(Path currentWorkingDirectory) {
		
		this.system.setCurrentWorkingDirectory(currentWorkingDirectory);
	}
	
	public Path	toAbsolutePath(Path path) {
		
		return this.system.toAbsolutePath(path);
	}
	
	public Path	toRealPath(Path path, LinkOption... linkOptions) throws IOException {
		
		return this.system.toRealPath(path, linkOptions);
	}
}
