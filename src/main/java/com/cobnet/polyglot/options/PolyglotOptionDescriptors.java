package com.cobnet.polyglot.options;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import org.graalvm.options.OptionDescriptors;
import org.graalvm.options.OptionDescriptor;

public class PolyglotOptionDescriptors implements Iterable<PolyglotOptionDescriptor> {

	private final OptionDescriptors descriptors;
	
	public static final PolyglotOptionDescriptors EMPTY = new PolyglotOptionDescriptors(OptionDescriptors.EMPTY);
	
	public PolyglotOptionDescriptors(OptionDescriptors descriptors) {
		
		this.descriptors = descriptors;
	}

	public OptionDescriptors getDescriptors() {
		
		return descriptors;
	}
	
	public static PolyglotOptionDescriptors	createAs(List<OptionDescriptor> descriptors) {
		
		return new PolyglotOptionDescriptors(OptionDescriptors.create(descriptors));
	}
	
	public static PolyglotOptionDescriptors	create(List<PolyglotOptionDescriptor> descriptors) {
		
		return PolyglotOptionDescriptors.createAs(descriptors.stream().map(descriptor -> descriptor.getDescriptor()).toList());
	}
	
	public static PolyglotOptionDescriptors createUnion(OptionDescriptors... descriptors) {
		
		return new PolyglotOptionDescriptors(OptionDescriptors.createUnion(descriptors));
	}
	
	public static PolyglotOptionDescriptors createUnion(PolyglotOptionDescriptors... descriptors) {
		
		return PolyglotOptionDescriptors.createUnion(Arrays.stream(descriptors).map(descriptor -> descriptor.getDescriptors()).toArray(OptionDescriptors[]::new));
	}
	
	public PolyglotOptionDescriptor get(String optionName) {
		
		return new PolyglotOptionDescriptor(this.descriptors.get(optionName));
	}
	
	public Iterator<PolyglotOptionDescriptor> iterator() {
		
		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.descriptors.iterator(), Spliterator.ORDERED), false).map(descriptor -> new PolyglotOptionDescriptor(descriptor)).iterator();
	}
	
}
