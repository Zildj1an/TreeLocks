
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Peterson implements Lock{

	private AtomicBoolean flag[] = new AtomicBoolean[2];
	private AtomicInteger victim;

	public Peterson() {
		flag[0] = new AtomicBoolean();
		flag[1] = new AtomicBoolean();
		victim = new AtomicInteger();
	}

	@Override
	public void lock(){
		int i = ((ThreadId)Thread.currentThread()).getThreadId(); 
		tree_lock(i);
	}

	@Override
	public void unlock(){
 		int i = ((ThreadId)Thread.currentThread()).getThreadId(); 
		tree_unlock(i);
	}

	public void tree_lock(int i) {
		int j = 1-i;
		flag[i].set(true);
		victim.set(i);
		while(flag[j].get() && victim.get() == i);
//			System.out.println("Thread " + i + " waiting");
	}

	public void tree_unlock(int i) {
		flag[i].set(false);
	}
}
