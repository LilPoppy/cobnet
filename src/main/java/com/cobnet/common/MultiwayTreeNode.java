package com.cobnet.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.cobnet.common.wrapper.AbstractListWrapper;
import com.cobnet.interfaces.TreeNode;

public class MultiwayTreeNode<T> extends AbstractListWrapper<MultiwayTreeNode<T>> implements TreeNode<T> {
	
	private final T value;
	
	private final transient List<MultiwayTreeNode<T>> children = new ArrayList<>();
	
	public MultiwayTreeNode(T value) {
		this.value = value;
	}

	public T getValue() {
		
		return value;
	}
	
	public MultiwayTreeNode<T> getLeft() {
		
		MultiwayTreeNode<T> parent = this.getParent();
		
		if(parent == null) {
			
			return null;
		}
		
		int index = parent.children.indexOf(this);
		
		if(index == 0) {
			
			return null;
		}
		
		return parent.get(index - 1);
	}
	
	public MultiwayTreeNode<T> getRight() {
		
		MultiwayTreeNode<T> parent = this.getParent();
		
		if(parent == null) {
			
			return null;
		}
		
		int index = parent.children.indexOf(this);
		
		if(index == parent.children.size() - 1) {
			
			return null;
		}
		
		return parent.get(index + 1);
	}
	
	public MultiwayTreeNode<T> getParent() {
		
		if(!(this instanceof MultiwayTreeNodeChild<T>) || this.isRoot()) {
			
			return null;
		}
		
		return ((MultiwayTreeNodeChild<T>)this).getParent();
	}
	
    public int getBreadth() {
    	
    	var children = new ArrayList<Integer>();
    	
    	this.bfs((child,index) -> children.add(child.getLevel()));
    	
    	int depth = children.stream().max((x, y) -> Integer.compare(x, y)).orElse(0);
    	
    	int result = 0;
    	
    	for(int i = 0; i < depth; i++) {
    		
    		result = Math.max(result, Collections.frequency(children, i));
    	}

    	return result;
    }
	
    public int getDepth() {
    	
       int result = 0;

       for (MultiwayTreeNode<T> n : children) {
    	   
    	   result = Math.max(result, n.getDepth());
       }

       return result + 1;
    }
    
    public int getLevel() {
    	
    	return 0;
    }
    
    public Collection<? extends MultiwayTreeNode<T>> getChildrenByLevel(int level) {
    	
    	List<MultiwayTreeNode<T>> children = new ArrayList<>();
    	
    	this.bfs((child, index) -> 
    	{
    		if(child.getLevel() == level) {
    			
    			children.add(child);
    		}
    		
    		return true;
    	});
    	
    	return Collections.unmodifiableList(children);
    }
    
    public Optional<MultiwayTreeNode<T>> getFirstChildByValue(T value) {
    	
    	return this.children.stream().filter(child -> child.getValue().equals(value)).findFirst();
    }
    
    @Override
    public boolean contains(Object obj) {
    	
    	for(var child : this) {
    		
    		if(child.value.equals(obj)) {
    			
    			return true;
    		}
    	}
    	
    	return false;
    }

    
    public void dfs(BiFunction<? super MultiwayTreeNode<T>, Integer, Boolean> predicate, Runnable new_line) {
    	
		Stack<MultiwayTreeNode<T>> stack = new Stack<>();
		
		stack.add(this);
		
		int index = 0;
		
		while (!stack.isEmpty()) {

			MultiwayTreeNode<T> node = stack.pop();
			
			index++;
			
        	if((predicate == null && new_line == null) || !predicate.apply(node, index)) {
        		
        		return;
        	}
        		
    		for(int j = node.children.size() - 1; j >= 0; j--) {
    			
    			stack.add(node.children.get(j));
    		}
    		
			if(stack.isEmpty() || node.getLevel() + 1 == stack.peek().getLevel()) {
				
	            if(new_line != null) {
	            	new_line.run();
	            }
			}

        	
		}

    }
    
    public void dfs(BiFunction<? super MultiwayTreeNode<T>, Integer, Boolean> predicate) {
    	
    	this.dfs(predicate, null);
    }
    
    public void bfs(BiFunction<? super MultiwayTreeNode<T>, Integer, Boolean> predicate, Runnable new_line) {
    	
        Queue<MultiwayTreeNode<T>> queue = new LinkedList<>();
        
        queue.offer(this);
        
        int index = 0;
        
        while(!queue.isEmpty()) {
        	
            int len = queue.size();
            
            for(int i = 0; i < len; i++) {
            	
            	index++;
            	MultiwayTreeNode<T> node = queue.poll();
                
            	if((predicate == null && new_line == null) || !predicate.apply(node, index)) {

            		return;
            	}
            	
                for (MultiwayTreeNode<T> item : node.children) {
                	
                    queue.offer(item);
                }
                
            }
            
            if(new_line != null) {
            	
            	new_line.run();
            }
        }
    }
    
    public void bfs(BiFunction<? super MultiwayTreeNode<T>, Integer, Boolean> predicate) {
    	
    	this.bfs(predicate, null);
    }
    
	@Override
    public void add(int index, MultiwayTreeNode<T> node) {
		
		this.checkNode(FunctionalConverter.adapt(n -> super.add(index, n)), node);
    }
	
    public MultiwayTreeNode<T> set(int index, MultiwayTreeNode<T> node) {
    	
    	return this.checkNode(n -> super.set(index, n), node);
    }
    
    private <E> E checkNode(Function<MultiwayTreeNode<T>, E> predicated, MultiwayTreeNode<T> node) {
    	
    	if(node instanceof MultiwayTreeNodeChild<T>) {
    		
    		MultiwayTreeNodeChild<T> t = (MultiwayTreeNodeChild<T>) node;
    		
    		if(t.getParent() == null) {
    			
    			t.parent = this;
    		}
    		
    		if(t.parent != this) {
    			
    			throw new IllegalArgumentException("Please ensure the parent is the same as operating instance.");
    		}
    		
    		return predicated.apply(node);
    	}
    	
    	throw new IllegalArgumentException("Please use " + MultiwayTreeNodeChild.class.getName() + " instead.");
    }
    
    public MultiwayTreeNode<T> getChildByLevel(int level) {
    	
    	MultiwayTreeNode<T> node = this;
    	
    	try {
    		
        	for(int i = 0; i < level; i++) {
        		
        		node = node.get(0);
        	}
        	
    	} catch(IndexOutOfBoundsException ex) {
    		
    		throw new IndexOutOfBoundsException("Index " + level + " out of bounds for length " + (this.getDepth() - 1));
    	}

    	return node;
    }
    
    public boolean swap(MultiwayTreeNode<T> sibling) {
    	
    	if(this == sibling) {
    		
    		return true;
    	}
    	
    	MultiwayTreeNode<T> parent = this.getParent();
    	
		if(parent == null || this.isRoot()) {
			
			return false;
		}
		
		MultiwayTreeNode<T> siblingParent = sibling.getParent();
		
		if(siblingParent == null || sibling.isRoot()) {
			
			return false;
		}
		
		if(parent != siblingParent) {
			
			return false;
		}
		
		if(this.getLevel() != sibling.getLevel()) {
			
			return false;
		}
		
		int index = parent.indexOf(this);
		
		parent.set(parent.indexOf(sibling), this);
		parent.set(index, sibling);
		
		return true;
    }
    
    public void flip() {
    	
        for (MultiwayTreeNode<T> child: this.children) {
        	
            child.flip();
        }
 
        int n = this.size();
        
        for (int i = 0, j = n - 1; i < j; i++, j--)
        {
        	MultiwayTreeNode<T> temp = this.children.get(i);
        	this.children.set(i, this.children.get(j));
        	this.children.set(j, temp);
        }
    }
    
    public MultiwayTreeNodeChild<T> asChild() {
    	
    	if(!this.isRoot() || this.getParent() != null || this instanceof MultiwayTreeNodeChild<T>) {
    		
    		return (MultiwayTreeNodeChild<T>) this;
    	}
    	
    	MultiwayTreeNodeChild<T> node = new MultiwayTreeNodeChild<T>(this.getValue());
    	
    	for(MultiwayTreeNode<T> child : this) {
    		
    		if(child instanceof MultiwayTreeNodeChild<T>) {
    			
    			((MultiwayTreeNodeChild<T>) child).parent = node;
    			node.add(child);
    		}	
    	}
    	
    	this.clear();
    	
    	return node;
    }
    
    public MultiwayTreeNode<T> asRoot() {
    	
    	if(!(this instanceof MultiwayTreeNodeChild<T>)) {
    		
    		return this;
    	}
    	
    	((MultiwayTreeNodeChild<T>) this).unparent();
    	
    	MultiwayTreeNode<T> node = new MultiwayTreeNode<T>(this.getValue());
    	
    	for(MultiwayTreeNode<T> child : this) {
    		
    		if(child instanceof MultiwayTreeNodeChild<T>) {
    			
    			((MultiwayTreeNodeChild<T>) child).parent = node;
    			node.add(child);
    		}	
    	}
    	
    	this.clear();
    	
    	return node;
    }

	@Override
	protected List<MultiwayTreeNode<T>> getList() {
		return this.children;
	}
	
	@Override
	public String toString() {
		
		return this.children.stream().map(child -> child.getValue()).toList().toString();
	}
	
	
	public static <T extends Comparable<T>> MultiwayTreeNodeChild<T> from(T[] instances) {
		
		if(instances.length > 0) {
			
			var root = new MultiwayTreeNodeChild<T>(instances[0]);
			
			if(instances.length > 1) {
				
				MultiwayTreeNodeChild<T> node = from(instances);
				
				if(node != null) {
					
					root.add(node);
				}
			}
			
			return root;
		}

		return null;
	}
	
	public static MultiwayTreeNodeChild<String> from(String text, String regex, int limit) {
		
		String[] nodes = text.split(regex, limit);
		
		if(nodes.length > 0) {
			
			MultiwayTreeNodeChild<String> root = new MultiwayTreeNodeChild<String>(nodes[0]);
			
			if(nodes.length > 1) {
				
				text = String.join(regex, Arrays.copyOfRange(nodes, 1, nodes.length));
				
				if(text.length() > 0) {
					
					MultiwayTreeNodeChild<String> node = from(text, regex, limit <= 0 ? limit : limit - 1);
					
					if(node != null) {
						
						root.add(node);
					}
				}
			}

			return root;
		}
	
		return null;
	}
	
	public static MultiwayTreeNodeChild<String> from(String text, String regex) {
		
		return from(text, regex, -1);
	}
	
	public static MultiwayTreeNodeChild<String> from(String text) {
		
		return from(text, "\\.");
	}
}
