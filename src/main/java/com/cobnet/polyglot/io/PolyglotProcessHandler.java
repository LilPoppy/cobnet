package com.cobnet.polyglot.io;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.io.ProcessHandler;

public class PolyglotProcessHandler {

	private final ProcessHandler handler;
	
	public PolyglotProcessHandler(ProcessHandler handler) {
		
		this.handler = handler;
	}

	public ProcessHandler getHandler() {
		
		return handler;
	}
	
	public Process start(ProcessHandler.ProcessCommand command) throws IOException {
		
		return this.handler.start(command);
	}
	
	public Process start(PolyglotProcessComand command) throws IOException { 
		
		return this.start(command.getProcessCommand());
	}
	
	public static final class PolyglotProcessComand {
		
		private final ProcessHandler.ProcessCommand command;
		
		public PolyglotProcessComand(ProcessHandler.ProcessCommand command) {
			
			this.command = command;
		}

		public ProcessHandler.ProcessCommand getProcessCommand() {
			
			return command;
		}
		
		public List<String> getCommand() {
			
			return this.command.getCommand();
		}
		
		public String getDirectory() {
			
			return this.command.getDirectory();
		}
		
		public Map<String,String> getEnvironment() {
			
			return this.command.getEnvironment();
		}
		
		public PolyglotRedirect getErrorRedirect() {
			
			return new PolyglotRedirect(this.command.getErrorRedirect());
		}
		
		public PolyglotRedirect getInputRedirect() {
			
			return new PolyglotRedirect(this.command.getInputRedirect());
		}
		
		public PolyglotRedirect getOutputRedirect() {
			
			return new PolyglotRedirect(this.command.getOutputRedirect());
		}
		
		public boolean isRedirectErrorStream() {
			
			return this.command.isRedirectErrorStream();
		}
		
	}
	
	public static final class PolyglotRedirect {
		
		private final ProcessHandler.Redirect redirect;
		
		public static final PolyglotRedirect INHERIT = new PolyglotRedirect(ProcessHandler.Redirect.INHERIT);
		
		public static final PolyglotRedirect PIPE = new PolyglotRedirect(ProcessHandler.Redirect.PIPE);
		
		public PolyglotRedirect(ProcessHandler.Redirect redirect) {
			
			this.redirect = redirect;
		}

		public ProcessHandler.Redirect getRedirect() {
			
			return redirect;
		}
		
		@Override
		public boolean equals(Object obj) {
			
			return this.redirect.equals(obj);
		}
		
		@Override
		public int hashCode() {
			
			return this.redirect.hashCode();
		}
		
		@Override
		public String toString() {
			
			return this.redirect.toString();
		}
		
	}
}
