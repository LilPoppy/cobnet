package com.cobnet.polyglot;

import com.cobnet.polyglot.options.PolyglotOptionDescriptors;
import org.graalvm.polyglot.Instrument;


public final class PolyglotInstrument {
	
	private final Instrument instrument;

	public PolyglotInstrument(Instrument instrument) {
		
		this.instrument = instrument;
	}
	
	public Instrument getInstrument() {
		
		return this.instrument;
	}
	
	public String getId() {
		
		return this.instrument.getId();
	}
	
	public String getName() {
		
		return this.instrument.getName();
	}
	
	public PolyglotOptionDescriptors getOptions() {
		
		return new PolyglotOptionDescriptors(this.instrument.getOptions());
	}
	
	public String getVersion() {
		
		return this.instrument.getVersion();
	}
	
	public <T> T lookup(Class<T> type) {
		
		return this.instrument.lookup(type);
	}
}
