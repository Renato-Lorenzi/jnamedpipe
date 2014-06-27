package br.com.rml.jnamedpipe.api;

import static br.com.rml.jnamedpipe.JNAKernel32.PIPE_ACCESS_DUPLEX;
import static br.com.rml.jnamedpipe.JNAKernel32.PIPE_READMODE_MESSAGE;
import static br.com.rml.jnamedpipe.JNAKernel32.PIPE_TYPE_MESSAGE;
import static br.com.rml.jnamedpipe.JNAKernel32.PIPE_UNLIMITED_INSTANCES;
import static br.com.rml.jnamedpipe.JNAKernel32.PIPE_WAIT;

import java.io.IOException;

import br.com.rml.jnamedpipe.JNAKernel32;

public class NamedPipeServer {
	private JNAKernel32 k32lib = JNAKernel32.INSTANCE;
	private String pipeName;
	private NamedPipeServerListener serverListener;

	public NamedPipeServer(String pipeName, NamedPipeServerListener serverListener) {
		super();
		this.pipeName = pipeName;
		this.serverListener = serverListener;
	}

	public void start() {
		new Thread() {

			@Override
			public void run() {
				while (true) {
					final int hPipe = k32lib.CreateNamedPipe(pipeName, // pipe
							// name
							PIPE_ACCESS_DUPLEX, // read/write access
							PIPE_TYPE_MESSAGE | // message type pipe
									PIPE_READMODE_MESSAGE | // message-read mode
									PIPE_WAIT, // blocking mode
							PIPE_UNLIMITED_INSTANCES, // max. instances
							512, // output buffer size
							512, // input buffer size
							0, // client time-out
							0); // default security attribute

					boolean connected = k32lib.ConnectNamedPipe(hPipe, 0);
					if (connected) {
						new Thread() {
							public void run() {
								NamedPipeServerStream stream = new NamedPipeServerStream(hPipe);
								try {
									serverListener.newConnection(stream);
								} finally {
									try {
										stream.close();
									} catch (IOException e) {
										throw new RuntimeException(e);
									}
								}

							};
						}.start();
					}
				}
			}
		}.start();
	}
}
