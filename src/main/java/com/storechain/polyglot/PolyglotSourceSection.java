package com.storechain.polyglot;

import org.graalvm.polyglot.SourceSection;

public final class PolyglotSourceSection {
	
	private final SourceSection section;
	
	public PolyglotSourceSection(SourceSection section) {
		
		this.section = section;
	}

	public SourceSection getSection() {
		
		return section;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.section.equals(obj);
	}
	
	public CharSequence getCharacters() {
		
		return this.section.getCharacters();
	}
	
	public int getCharEndIndex() {
		
		return this.section.getCharEndIndex();
	}
	
	public int getCharIndex() {
		
		return this.section.getCharIndex();
	}
	
	public int getCharLength() {
		
		return this.section.getCharLength();
	}

	public int getEndColumn() {
		
		return this.section.getEndColumn();
	}
	
	public int getEndLine() {
		
		return this.section.getEndLine();
	}
	
	public PolyglotSource getSource() {
		
		return new PolyglotSource(this.section.getSource());
	}
	
	public int getStartColumn() {
		
		return this.section.getStartColumn();
	}
	
	public int getStartLine() {
		
		return this.section.getStartLine();
	}
	
	public boolean hasCharIndex() {
		
		return this.section.hasCharIndex();
	}
	
	public boolean hasColumns() {
		
		return this.section.hasColumns();
	}
	
	@Override
	public int hashCode() {
		
		return this.section.hashCode();
	}
	
	public boolean hasLines() {
		
		return this.section.hasLines();
	}
	
	public boolean isAvailable() {
		
		return this.section.isAvailable();
	}
	
	@Override
	public String toString() {
		
		return this.section.toString();
	}
}
