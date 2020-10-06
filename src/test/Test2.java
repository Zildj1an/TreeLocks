/**
 * 
 * @author Balaji Arun
 */
public class Test2 {
	
	private static final String LOCK_ONE = "LockOne";
	private static final String LOCK_TWO = "LockTwo";
	private static final String PETERSON = "Peterson";
	private static final String FILTER   = "Filter";
	private static final String BAKERY   = "Bakery";
	private static final String TREE     = "TreePeterson";

	private static String[] q1_test;

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InterruptedException {
		
		//String lockClass = (args.length == 0 ? TREE : args[0]);
		int threadCount=(args.length <= 1 ? 8 : Integer.parseInt(args[1]));
		int totalIters = (args.length <= 2 ? 6400 : Integer.parseInt(args[2]));
		int iters = totalIters / threadCount;

		String lockClass;
		q1_test = new String[3];
		q1_test[0] = TREE; //q1_test[1] = FILTER; //q1_test[2] = PETERSON;

		System.out.printf("Report of experiment with %d threads:\n",threadCount);

		for (int j = 0; j < 1; j++){
			
			lockClass = q1_test[j];

			System.out.printf("---------------------------------------\n");

			for (int i = 0; i < 3; i++) {
				run(lockClass, threadCount, iters);
			}
		}
	}

	private static void 
        run(String lockClass, int threadCount, int iters) throws InterruptedException, ClassNotFoundException, IllegalAccessException, InstantiationException {

		final Counter counter = new SharedCounter(0, (Lock)Class.forName("edu.vt.ece.locks." + lockClass).newInstance());
		final TestThread2[] threads = new TestThread2[threadCount];
		TestThread2.reset();

		for(int t=0; t<threadCount; t++) {
			threads[t] = new TestThread2(counter, iters);
		}

		for(int t=0; t<threadCount; t++) {
			threads[t].start();
		}

		long totalTime = 0;
		for(int t=0; t<threadCount; t++) {
			threads[t].join();
			totalTime += threads[t].getElapsedTime();
		}
		System.out.println(lockClass +": Average time per thread is "+totalTime/threadCount+"ms");
	}
}
