package com.storechain.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import com.storechain.interfaces.TreeNode;

public class MultiwayTree<T extends Comparable<T>> extends AbstractList<MultiwayTree<T>> implements TreeNode<T>, Comparable<MultiwayTree<T>> {
	
	private final T value;
	
	private final List<MultiwayTree<T>> children = new ArrayList<>();
	
	public MultiwayTree(T value) {
		this.value = value;
	}

	public T getValue() {
		
		return value;
	}
	
    public int getBreadth() {
    	
    	var children = new ArrayList<Integer>();
    	
    	this.bfs(child -> children.add(child.getLevel()));
    	
    	int depth = children.stream().max((x, y) -> Integer.compare(x, y)).orElse(0);
    	
    	int result = 0;
    	
    	for(int i = 0; i < depth; i++) {
    		
    		result = Math.max(result, Collections.frequency(children, i));
    	}

    	return result;
    }
	
    public int getDepth() {
    	
       int result = 0;

       for (MultiwayTree<T> n : children) {
    	   
    	   result = Math.max(result, n.getDepth());
       }

       return result + 1;
    }
    
    public int getLevel() {
    	
    	return 0;
    }
    
    public Collection<? extends MultiwayTree<T>> getChildrenByLevel(int level) {
    	
    	List<MultiwayTree<T>> children = new ArrayList<>();
    	
    	this.bfs(child -> 
    	{
    		if(child.getLevel() == level) {
    			
    			children.add(child);
    		}
    		
    		return true;
    	});
    	
    	return Collections.unmodifiableList(children);
    }
    
    public void bfs(Function<? super MultiwayTree<T>, Boolean> predicated) {
    	
        Queue<MultiwayTree<T>> queue = new LinkedList<>();
        
        queue.offer(this);
        
        while(!queue.isEmpty()) {
        	
            int len = queue.size();
            
            for(int i = 0; i < len; i++) {
            	
            	MultiwayTree<T> node = queue.poll();
                
            	if(!predicated.apply(node)) {
            		
            		return;
            	}
            	
                for (MultiwayTree<T> item : node.children) {
                	
                    queue.offer(item);
                }
            }
        }
    }
    
	@Override
    public void add(int index, MultiwayTree<T> node) {
		
		this.checkNode(CommonConverter.adapt(n -> super.add(index, n)), node);
    }
	
    public MultiwayTree<T> set(int index, MultiwayTree<T> node) {
    	
    	return this.checkNode(n -> super.set(index, n), node);
    }
    
    private <E> E checkNode(Function<MultiwayTree<T>, E> predicated, MultiwayTree<T> node) {
    	
    	if(node instanceof MultiwayTreeNode<T>) {
    		
    		MultiwayTreeNode<T> t = (MultiwayTreeNode<T>) node;
    		
    		if(t.getParent() == null) {
    			
    			t.parent = this;
    		}
    		
    		if(t.parent != this) {
    			
    			throw new IllegalArgumentException("Please ensure the parent is the same as operating instance.");
    		}
    		
    		return predicated.apply(node);
    	}
    	
    	throw new IllegalArgumentException("Please use " + MultiwayTreeNode.class.getName() + " instead.");
    }
    
    public MultiwayTree<T> getChildByLevel(int level) {
    	
    	MultiwayTree<T> node = this;
    	
    	try {
    		
        	for(int i = 0; i < level; i++) {
        		
        		node = node.get(0);
        	}
        	
    	} catch(IndexOutOfBoundsException ex) {
    		
    		throw new IndexOutOfBoundsException("Index " + level + " out of bounds for length " + (this.getDepth() - 1));
    	}

    	return node;
    }
    

	@Override
	public int compareTo(MultiwayTree<T> o) {
		
		if(this == o) {
			
			return 0;
		}
		
		return Integer.compare(this.getDepth(), o.getDepth());
	}

	@Override
	protected List<MultiwayTree<T>> getList() {
		return this.children;
	}
	
	public static MultiwayTreeNode<String> from(String text, String regex, int limit) {
		
		String[] nodes = text.split(regex, limit);
		
		if(nodes.length > 0) {
			
			MultiwayTreeNode<String> root = new MultiwayTreeNode<String>(nodes[0]);
			
			if(nodes.length > 1) {
				
				text = text.substring(nodes[0].length() + 1, text.length());
				
				if(text.length() > 0) {
					
					MultiwayTreeNode<String> node = from(text, regex, limit <= 0 ? limit : limit - 1);
					
					if(node != null) {
						
						root.add(node);
					}
				}
			}

			return root;
		}
	
		return null;
	}
	
	public static MultiwayTreeNode<String> from(String text, String regex) {
		
		return from(text, regex, -1);
	}
	
	public static MultiwayTreeNode<String> from(String text) {
		
		return from(text, "\\.");
	}
	
	public static class MultiwayTreeNode<T extends Comparable<T>> extends MultiwayTree<T> {

		protected MultiwayTree<T> parent;
		
		public MultiwayTreeNode(T value, MultiwayTreeNode<T> parent) {
			super(value);
			this.parent = parent;
		}
		
		public MultiwayTreeNode(T value) {
			this(value, null);
		}
		
		@Override
		public int getLevel() {
			
			if(this.parent == null) {
				
				return 0;
			}

			return this.parent.getLevel() + 1;
		}
		
		public MultiwayTree<T> getParent() {
			
			return this.parent;
		}
		
	}
	
}
