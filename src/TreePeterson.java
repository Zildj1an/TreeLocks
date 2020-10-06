
/*
	author Carlos Bilbao
	bilbao@vt.edu
*/

public class TreePeterson implements Lock {

	private PeterNode root;
	private int num_threads, num_nodes, tree_height, max_left;

    	public TreePeterson() {
        	this(8);
    	}

   	public TreePeterson(int n) 
	{
		int i = 0, aux;
		num_threads = n;
		/* Round up integer division for num nodes */
		num_nodes = number_nodes(num_threads); 

	 	tree_height = (int)Math.floor(log2(num_nodes)); 
		
		/* Generate tree */	
		System.out.printf("Generating %d nodes (height %d)...\n",num_nodes,tree_height);	

		root = new PeterNode(null,0);
		root.left_son(build_subtree(root,1,1));
		root.right_son(build_subtree(root,(num_nodes/2)+1,1));
	
		System.out.println("GENERATED TREE:");
		drawTree(root,0);
		System.out.println("-------------------");
	}

	@Override
    	public void lock() 
	{
		int i = ((ThreadId)Thread.currentThread()).getThreadId();
		PeterNode node = findNode(root,i);

		if (node == null){
			System.out.printf("Node %d not found\n",i);
			return;
		}		

		System.out.printf("Thread %d is going to start locking.\n",i);
	
		/* Traverse upside down */
		while (node != null){
			/* Wait for space in the lock */
			while (!node.insert_thread(i));	
			if (!node.lock(i)){
				continue;	
			}
			node = node.get_dad();
		}
    	}

    	@Override
    	public void unlock() 
	{
		int i = ((ThreadId)Thread.currentThread()).getThreadId();
		PeterNode node = root;
		int depth = 1;

		System.out.printf("Thread %d is going to start unlocking.\n",i);

		/* Unlock the locks traversed */
		while (true){
			if (!node.unlock(i)){
				while (!node.insert_thread(i));	
				continue;
			}
		
			/* Everything is unlocked */	
			if (is_node(node,i)) break;

			if (should_go_there(node.get_right(),i,depth)){
				node = node.get_right();
			}
			else node = node.get_left();
			depth++;
		}	
	}

	/* I do:
				0
				/\
			       1  4          		
			      /\  /\
			     2 3  5 6   NUM_NODES= 6
	  
	 As I force a balanced tree, the depth will be 1 + floor(log2(n)) */

	private PeterNode build_subtree(PeterNode parent,int key,int depth)
	{
		int left_key,right_key;	

		PeterNode node = new PeterNode(parent,key);

		/* Base Case */
		if (depth == tree_height) {
			max_left = key;
			return node;
		}

		node.left_son(build_subtree(node,key + 1, depth + 1));
		node.right_son(build_subtree(node,max_left + 1, depth + 1));

		return node;
	}

	private boolean should_go_there(PeterNode node, int id, int depth){
	
		if (depth == tree_height){
			return is_node(node,id);
		}

		if (is_node(node,id)) return true;

		return (should_go_there(node.get_right(),id,depth+1) || 
			should_go_there(node.get_left(),id,depth+1));
	}

	public void drawTree(PeterNode node, int depth){

		System.out.printf(" %d",node.get_key());

		if (depth == tree_height) {
			System.out.printf(" leaf\n");
			return;
		}

		System.out.printf(" ->");
		drawTree(node.get_left(),depth+1);
		System.out.printf(" %d ->",node.get_key());
		drawTree(node.get_right(),depth+1);
	} 

	private boolean is_node(PeterNode node, int id){
		int key = node.get_key();
		return ((key == id) || (id > num_nodes && key + num_nodes + 1 == id));
	}

	private PeterNode findNode(PeterNode node,int id){

		if (is_node(node,id)) return node;

		if (node.get_right() != null){
			if (node.get_right().get_key() <= id) 
			return findNode(node.get_right(),id);
		}
		
		if (node.get_left() != null){
			return findNode(node.get_left(),id);
		}
		return null;
	}

	private int number_nodes(int threads){

		int filled_nodes = (Math.abs(threads)+1)/ 2;	
		int i = 0, max = 2, MAX_TOP=10;

		/* Ranges:
		   Nodes:  Perfect balanced binary tree:
		   1-2     2
		   3-6	   6
		   7-14    14
		   15-30   30
		   ...
		*/

		for (; filled_nodes >= max && i < MAX_TOP; ++i){
			max = (max+1)*2;
		}
	
		return max;
	}

	private static int log2(int N) 
	{ 		  
		return (int)(Math.log(N) / Math.log(2)); 
	} 
}
