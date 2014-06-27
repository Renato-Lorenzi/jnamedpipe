package br.com.rml.jnamedpipe.api;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

import br.com.rml.jnamedpipe.api.helper.UnsupportedInputStream;
import br.com.rml.jnamedpipe.api.helper.UnsupportedOutputStream;

public class NamedPipeClient implements NamedPipeStream {

	private RandomAccessFile pipe;

	public NamedPipeClient(String pipeName) {
		super();
		try {
			pipe = new RandomAccessFile(pipeName, "rw");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public InputStream getInputStream() {
		return new UnsupportedInputStream() {

			@Override
			public int read(byte[] b) throws IOException {
				return pipe.read(b);
			}

			@Override
			public int read() throws IOException {
				return pipe.read();
			}

		};
	}

	public OutputStream getOutputStream() {
		return new UnsupportedOutputStream() {

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				write(Arrays.copyOfRange(b, off, len));
			}

			@Override
			public void write(byte[] b) throws IOException {
				pipe.write(b);
			}

			@Override
			public void write(int b) throws IOException {
				pipe.writeByte(b);
			}
		};
	}

	public void close() throws IOException {
		pipe.close();
	}
}
