package containmentcache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import com.google.common.base.Strings;

/**
 * Tests for containment caches.
 * @author afrechet
 */
public abstract class AContainmentCacheTests {
	
	/**
	 * Factory method for the containment cache to be tested.
	 * @param universe - the set of elements the cache is for. 
	 * @return - containment cache instance to be tested.
	 */
	protected abstract IContainmentCache<Integer> getCache(Set<Integer> universe);
	
	private ICacheSet<Integer> makeSet(int... elements)
	{
		Set<Integer> set = new HashSet<Integer>();
		for(int element : elements)
		{
			set.add(element);
		}
		return new CacheSet<Integer>(set);
	}
	
	private final static Set<Integer> UNIVERSE = Collections.unmodifiableSet(new HashSet<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10)));
	
	/**
	 * Creation tests.
	 */
	@Test 
	public void testEmptyTree()
	{
		final IContainmentCache<Integer> C = getCache(UNIVERSE);
		
		//Empty tree has size 0.
		assertEquals(C.size(), 0);
		
		//Empty tree does not contain empty set.
		ICacheSet<Integer> emptySet = makeSet();
		assertTrue(C.getSubsets(emptySet).isEmpty());
		assertTrue(C.getSupersets(emptySet).isEmpty());
		assertFalse(C.contains(emptySet));
		
		//Empty tree does not contain any subset.
		assertTrue(C.getSubsets(makeSet(1,2,3)).isEmpty());
		
		//Empty tree does not contain any superset.
		assertTrue(C.getSupersets(makeSet(1,2,3)).isEmpty());
	}
	
	/**
	 * Subset and superset tests.
	 **/
	
	@Test 
	public void testEmptySet()
	{
		final IContainmentCache<Integer> C = getCache(UNIVERSE);
		
		ICacheSet<Integer> S = makeSet();
		C.add(S);
		
		Collection<Set<Integer>> subsets;
		Collection<Set<Integer>> supersets;
		
		subsets = C.getSubsets(S);
		assertEquals(subsets.size(),1);
		assertTrue(subsets.contains(S.getElements()));
		supersets = C.getSupersets(S);
		assertEquals(supersets.size(),1);
		assertTrue(supersets.contains(S.getElements()));
		
		ICacheSet<Integer> R = makeSet(1,2,3,4);
		C.add(R);
		
		subsets = C.getSubsets(R);
		assertEquals(subsets.size(),2);
		assertEquals(C.getNumberSubsets(R),subsets.size());
		assertTrue(subsets.contains(S.getElements()));
		assertTrue(subsets.contains(R.getElements()));
		
		supersets = C.getSupersets(S);
		assertEquals(supersets.size(),2);
		assertEquals(C.getNumberSupersets(S),supersets.size());
		assertTrue(supersets.contains(S.getElements()));
		assertTrue(supersets.contains(R.getElements()));
		
	}
	
	@Test
	public void testIdempotence()
	{
		final IContainmentCache<Integer> C = getCache(UNIVERSE);
		
		ICacheSet<Integer> S = makeSet(1,2,3);
		
		C.add(S);
		
		Collection<Set<Integer>> subsets;
		Collection<Set<Integer>> supersets;
		
		assertEquals(C.size(), 1);
		
		subsets = C.getSubsets(S);
		assertTrue(subsets.contains(S.getElements()));
		assertEquals(subsets.size(),1); 
		assertEquals(C.getNumberSubsets(S),subsets.size());
		
		supersets = C.getSupersets(S);
		assertTrue(supersets.contains(S.getElements()));
		assertEquals(supersets.size(),1);
		assertEquals(C.getNumberSupersets(S),supersets.size());
		
		C.add(S);
		
		assertEquals(C.size(), 1);
		
		subsets = C.getSubsets(S);
		assertTrue(subsets.contains(S.getElements()));
		assertEquals(subsets.size(),1);
		assertEquals(C.getNumberSubsets(S),subsets.size());
		
		supersets = C.getSupersets(S);
		assertTrue(supersets.contains(S.getElements()));
		assertEquals(supersets.size(),1);
		assertEquals(C.getNumberSupersets(S),supersets.size());
		
	}
	
	@Test 
	public void testOneSubset()
	{
		final IContainmentCache<Integer> C = getCache(UNIVERSE);
		
		Collection<Set<Integer>> nosubsets = C.getSubsets(makeSet(1,2,3,4));
		assertTrue(nosubsets.isEmpty());
		
		ICacheSet<Integer> s1 = makeSet(1,2);
		C.add(s1);
		Collection<Set<Integer>> onesubsets = C.getSubsets(makeSet(1,2,3,4));
		assertEquals(onesubsets.size(),1);
		assertTrue(onesubsets.contains(s1.getElements()));
		assertEquals(C.getNumberSubsets(makeSet(1,2,3,4)),onesubsets.size());
	}
	
	@Test 
	public void testOneSuperset()
	{
		final IContainmentCache<Integer> C = getCache(UNIVERSE);
		
		Collection<Set<Integer>> nosupersets = C.getSupersets(makeSet(1,2));
		assertTrue(nosupersets.isEmpty());
		
		ICacheSet<Integer> s1 = makeSet(1,2,3,4);
		C.add(s1);
		Collection<Set<Integer>> onesubsets = C.getSupersets(makeSet(1,2));
		int numsupersets = C.getNumberSupersets(makeSet(1,2));
		assertEquals(numsupersets,1);
		assertEquals(onesubsets.size(),numsupersets);
		assertTrue(onesubsets.contains(s1.getElements()));
	}
	
	@Test
	public void testIntersectingSubsets()
	{
		final IContainmentCache<Integer> C = getCache(UNIVERSE);
	
		ICacheSet<Integer> s1 = makeSet(1,2);
		C.add(s1);	
		ICacheSet<Integer> s2 = makeSet(2,3);
		C.add(s2);
		
		Collection<Set<Integer>> subsets = C.getSubsets(makeSet(1,2,3,4));		
		int numsubsets = C.getNumberSubsets(makeSet(1,2,3,4));
		
		assertEquals(numsubsets,2);
		assertEquals(subsets.size(),numsubsets);
		assertTrue(subsets.contains(s1.getElements()));
		assertTrue(subsets.contains(s2.getElements()));
	}
	
	@Test
	public void testNestedSubsets()
	{
		final IContainmentCache<Integer> C = getCache(UNIVERSE);
	
		ICacheSet<Integer> s1 = makeSet(1);
		C.add(s1);	
		ICacheSet<Integer> s2 = makeSet(1,2);
		C.add(s2);
		
		Collection<Set<Integer>> subsets = C.getSubsets(makeSet(1,2,3,4));
		
		assertEquals(subsets.size(),2);
		assertTrue(subsets.contains(s1.getElements()));
		assertTrue(subsets.contains(s2.getElements()));
	}
	
	@Test 
	public void testNestedSupersets()
	{
		final IContainmentCache<Integer> C = getCache(UNIVERSE);
		
		ICacheSet<Integer> s1 = makeSet(1,2);
		C.add(s1);	
		ICacheSet<Integer> s2 = makeSet(1,2,3);
		C.add(s2);
		
		Collection<Set<Integer>> supersets = C.getSupersets(makeSet(1));
		
		assertEquals(supersets.size(),2);
		assertTrue(supersets.contains(s1.getElements()));
		assertTrue(supersets.contains(s2.getElements()));
	}
	
	/**
	 * Addition & removal tests.
	 */
	@Test
	public void testAddThenRemove()
	{
		final IContainmentCache<Integer> C = getCache(UNIVERSE);
		
		ICacheSet<Integer> S = makeSet(1,2,3);
		
		C.add(S);
		assertEquals(C.size(), 1);
		assertTrue(C.contains(S));
		
		C.remove(S);
		assertEquals(C.size(), 0);
		assertFalse(C.contains(S));
	}
	
	
	/**
	 * Smoke tests.
	 */
	@Test
	public void smokeTest()
	{
		System.out.println("Smoke tests");
		
		//Parameters
		final long seed = 1;
		final Random rand = new Random(seed);

		final int numtests = 1000;
		final int N = 5000;
		
		
		final List<Integer> universe = new ArrayList<Integer>();
		for(int i=0;i<N;i++)
		{
			universe.add(i);
		}
		System.out.println("Universe has "+N+" elements.");
		
		
		//Create cache and wrap with timer proxy.
		final IContainmentCache<Integer> cache = getCache(new HashSet<Integer>(universe));
		final ProxyTimer timer = new ProxyTimer(cache);
		@SuppressWarnings("unchecked")
		final IContainmentCache<Integer> C = (IContainmentCache<Integer>) Proxy.newProxyInstance(IContainmentCache.class.getClassLoader(), new Class[] {IContainmentCache.class}, timer);
		
		System.out.print("Load testing "+numtests+" times...");
		for(int t=0;t<numtests;t++)
		{
			if(t%(numtests/10) < (t-1)%(numtests/10))
			{
				System.out.print(((double) t)/((double) numtests)*100.0+"%...");
			}
			
			int M = rand.nextInt(10);
			//Add some load to the data structure.
			for(int m=0;m<M;m++)
			{
				Collections.shuffle(universe, rand);
				List<Integer> elements = universe.subList(0, rand.nextInt(universe.size()));
				ICacheSet<Integer> set = new CacheSet<Integer>(new HashSet<Integer>(elements));
				
				C.add(set);
			}
			
			//Test a certain set.
			Collections.shuffle(universe, rand);
			List<Integer> elements = universe.subList(0, rand.nextInt(universe.size()));
			ICacheSet<Integer> set = new CacheSet<Integer>(new HashSet<Integer>(elements));
			
			//Test addition.
			C.add(set);
			assertTrue(C.contains(set));
			assertTrue(C.getSubsets(set).contains(set.getElements()));
			assertTrue(C.getSupersets(set).contains(set.getElements()));
			int size = C.size();
			
			//Test subsets
			final Collection<Set<Integer>> subsets = C.getSubsets(set);
			
			//Test number subsets
			final int numsubsets = C.getNumberSubsets(set);
			assertEquals(subsets.size(), numsubsets);
			
			//Test supersets
			final Collection<Set<Integer>> supersets = C.getSupersets(set);
			
			//Test number supersets
			final int numsupersets = C.getNumberSupersets(set);
			assertEquals(numsupersets, supersets.size());
			
			
			//Test removal.
			C.remove(set);
			assertFalse(C.contains(set));
			assertFalse(C.getSubsets(set).contains(set.getElements()));
			assertFalse(C.getSupersets(set).contains(set.getElements()));
			assertEquals(C.size(), size-1);
		}
		
		System.out.println("");
		
		System.out.println("Runtime (ms) statistics:");
		
		System.out.printf("%-30s %-10s %-10s %-10s %-10s %-10s %-10s %-10s\n","Method","Mean","StdDev","Min","Q25","Median","Q75","Max");
		
		//System.out.printf("%s,%s,%s,%s,%s,%s,%s,%s\n","Method","Mean","StdDev","Min","Q25","Median","Q75","Max");
		
		final Map<Method,DescriptiveStatistics> stats = timer.getMethodStats();
		
		final List<Method> methods = new LinkedList<Method>(stats.keySet());
		Collections.sort(methods,new Comparator<Method>(){
			@Override
			public int compare(Method o1, Method o2) {
				return o1.getName().compareTo(o2.getName());
		}});
		
		for(Method method : methods)
		{
			final DescriptiveStatistics stat = stats.get(method);
			
			
			System.out.printf("%-30s %-10.3f %-10.3f %-10.3f %-10.3f %-10.3f %-10.3f %-10.3f\n",
					"\""+method.getName()+"\"",
					stat.getMean(),
					stat.getStandardDeviation(),
					stat.getMin(),
					stat.getPercentile(25),
					stat.getPercentile(50),
					stat.getPercentile(75),
					stat.getMax());
					
			/*
			System.out.printf("%s,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f\n",
					"\""+method.getName()+"\"",
					stat.getMean(),
					stat.getStandardDeviation(),
					stat.getMin(),
					stat.getPercentile(25),
					stat.getPercentile(50),
					stat.getPercentile(75),
					stat.getMax());
			*/
		}
		
		
		
		
	}
	

}
