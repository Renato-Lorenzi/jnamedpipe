package br.com.rml.jnamedpipe.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NamedPipeStream {

	public abstract InputStream getInputStream();

	public abstract OutputStream getOutputStream();

	public abstract void close() throws IOException;

}