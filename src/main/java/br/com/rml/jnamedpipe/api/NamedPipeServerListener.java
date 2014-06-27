package br.com.rml.jnamedpipe.api;

public interface NamedPipeServerListener {
	void newConnection(NamedPipeStream stream);
}
