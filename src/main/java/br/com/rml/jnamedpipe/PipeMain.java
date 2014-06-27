package br.com.rml.jnamedpipe;

import static br.com.rml.jnamedpipe.JNAKernel32.PIPE_ACCESS_DUPLEX;
import static br.com.rml.jnamedpipe.JNAKernel32.PIPE_READMODE_MESSAGE;
import static br.com.rml.jnamedpipe.JNAKernel32.PIPE_TYPE_MESSAGE;
import static br.com.rml.jnamedpipe.JNAKernel32.PIPE_UNLIMITED_INSTANCES;
import static br.com.rml.jnamedpipe.JNAKernel32.PIPE_WAIT;

import java.io.RandomAccessFile;

import com.sun.jna.ptr.IntByReference;

public class PipeMain {

	static final String PIPE_NAME = "\\\\.\\pipe\\mynamedpipe";
	private static JNAKernel32 k32lib;

	public static void main(String[] args) throws InterruptedException {
		k32lib = JNAKernel32.INSTANCE;
		new Thread() {
			public void run() {
				execServer();
			};
		}.start();

		Thread.sleep(3000);
		execClient();
	}

	private static void execClient() {
		try {

			RandomAccessFile[] pipe = new RandomAccessFile[2];

			for (int i = 0; i < 1; i++) {

				// Connect to the named pipe
				pipe[i] = new RandomAccessFile(PIPE_NAME, "rw"); // or
				Thread.sleep(3000);
			}
			// "w"
			// or
			// for
			// bidirectional
			// (Windows
			// only)
			// "rw"

			String req = "Request text";
			// Write request to the pipe
			pipe[0].write(req.getBytes());
			byte[] bytes = new byte[5];
			pipe[0].read(bytes);
			System.out.println(new String(bytes));
			// req = "Request text2";
			// pipe[1].write(req.getBytes());

			// Read response from pipe
			// String res = pipe.readLine();

			// Close the pipe
			pipe[0].close();
			// pipe[1].close();

			// do something with res
		} catch (Exception e) {
			// do something
			e.printStackTrace();
		}
	}

	private static void execServer() {
		while (true) {
			final int hPipe = k32lib.CreateNamedPipe(PIPE_NAME, // pipe
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
						IntByReference cbBytesRead = new IntByReference();

						byte[] pchRequest = new byte[512];
						boolean fSuccess = k32lib.ReadFile(hPipe, // handle to pipe
								pchRequest, // buffer to receive data
								512, // size of buffer
								cbBytesRead, // number of bytes read
								0); // not overlapped I/O

						if (!fSuccess || cbBytesRead.getValue() == 0) {
							System.out.println("error");
						} else {
							System.out.println(hPipe + " Received: " + new String(pchRequest));
						}

						k32lib.WriteFile(hPipe, "12345".getBytes(), 5, cbBytesRead, 0);
					};
				}.start();
			}
		}
	}
}
