import java.util.concurrent.atomic.AtomicInteger;

public class PeterNode {

	private Peterson peter_lock;
	private AtomicInteger key,thread0, thread1;

	PeterNode left, right, peter_parent;

	public PeterNode(PeterNode dad, int key){
		peter_lock = new Peterson();
		right = null;
		left  = null;
		peter_parent = dad;
		this.key = new AtomicInteger(key);
		thread0  = new AtomicInteger(-1);
		thread1  = new AtomicInteger(-1);
	}

	public boolean is_full(){
		return (thread0.get() != -1 && thread1.get() != -1);
	}

	public boolean insert_thread(int id)
	{	
		AtomicInteger aux = new AtomicInteger(0);
		
		if (is_full()) return false;

		if (thread0.get() == -1) {
			thread0.set(id);
		}
		else if (thread1.get() == -1){
			thread1.set(id);
			aux.set(1);
		}
		else return false;

		System.out.printf("LOCK %d assigned thread%d to t %d\n",key.get(),aux.get(),id);

		/* Check everything went smoothy */
		if (aux.get() == 0) return (thread0.get() == id);
		else return (thread1.get() == id);
	}

	public int get_key(){
		return key.get();
	}

	public boolean lock(int i)
	{
		AtomicInteger aux = new AtomicInteger(0);
	
		if (thread1.get() == i) aux.set(1);
		else if (thread0.get() != i){
			//System.out.printf("Tried lock with unregistered!\n");
			return false;
		}
			
		peter_lock.tree_lock(aux.get());
		
		System.out.printf("Thread %d acquires lock %d.\n",i,key.get());
	
		return true;
	}

	public boolean unlock(int id)
	{
		AtomicInteger aux = new AtomicInteger(0);
		
		/* Remove thread */
		if (thread1.get() == id) {
			aux.set(1);
			thread1.set(-1);
		}
		else {
			if (thread0.get() != id){
				//System.out.printf("Tried unlock unregistered!\n");
				return false;
			}
			thread0.set(-1);
		}
		System.out.printf("Thread %d unlocks lock %d.\n",id,key.get());
		peter_lock.tree_unlock(aux.get());
		return true;
	}

	public void left_son(PeterNode son){
		left = son;
	}	

	public PeterNode get_left(){
		return left;
	}
	
	public void right_son(PeterNode son){
		right = son;
	}

	public PeterNode get_right(){
		return right;
	}

	public PeterNode get_dad(){
		return peter_parent;
	}
}

