package com.storechain.polyglot;

import org.graalvm.polyglot.EnvironmentAccess;

public final class PolyglotEnvironmentAccess {

	private final EnvironmentAccess access;
	
	public final static PolyglotEnvironmentAccess INHERIT = new PolyglotEnvironmentAccess(EnvironmentAccess.INHERIT);
	
	public final static PolyglotEnvironmentAccess NONE = new PolyglotEnvironmentAccess(EnvironmentAccess.NONE);
	
	public PolyglotEnvironmentAccess(EnvironmentAccess access) {
		
		this.access = access;
	}

	public EnvironmentAccess getAccess() {
		
		return access;
	}
}
