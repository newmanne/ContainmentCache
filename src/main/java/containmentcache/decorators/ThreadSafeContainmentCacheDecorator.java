package containmentcache.decorators;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.jcip.annotations.ThreadSafe;
import containmentcache.ICacheEntry;
import containmentcache.IContainmentCache;
import containmentcache.ILockableContainmentCache;

/**
 * Decorator that makes a containment cache thread safe through the use of an read/write lock.
 * 
 * @author afrechet
 *
 * @param <E> - type of elements in set representing entry.
 * @param <C> - type of cache entry.
 */
@ThreadSafe
public class ThreadSafeContainmentCacheDecorator<E,C extends ICacheEntry<E>> implements ILockableContainmentCache<E, C> {

	private final IContainmentCache<E,C> fCache;
	private final ReadWriteLock fLock;
	
	public ThreadSafeContainmentCacheDecorator(IContainmentCache<E,C> cache, ReadWriteLock lock) {
		fCache = cache;
		fLock = lock;
	}
	
	/**
	 * Make the given cache thread safe with a simple unfair reentrant read write lock.
	 * @param cache - cache to decorate.
	 * @return decorated thread safe cache.
	 */
	public static <E,C extends ICacheEntry<E>> ThreadSafeContainmentCacheDecorator<E,C> makeThreadSafe(IContainmentCache<E,C> cache)
	{
		final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
		return new ThreadSafeContainmentCacheDecorator<E,C>(cache,lock);
	}
	
	@Override
	public Lock getReadLock()
	{
		return fLock.readLock();
	}
	
	@Override
	public void add(C set) {
		
		fLock.writeLock().lock();
		try
		{
			fCache.add(set);
		}
		finally
		{
			fLock.writeLock().unlock();
		}
	}

	@Override
	public void remove(C set) {
		fLock.writeLock().lock();
		try
		{
			fCache.remove(set);
		}
		finally
		{
			fLock.writeLock().unlock();
		}
	}

	@Override
	public boolean contains(C set) {
		fLock.readLock().lock();
		try
		{
			return fCache.contains(set);
		}
		finally
		{
			fLock.readLock().unlock();
		}
	}
	
	@Override
	public Iterable<C> getSets() {
		fLock.readLock().lock();
		try
		{
			return fCache.getSets();
		}
		finally
		{
			fLock.readLock().unlock();
		}
	}

	@Override
	public Iterable<C> getSubsets(ICacheEntry<E> set) {
		fLock.readLock().lock();
		try
		{
			return fCache.getSubsets(set);
		}
		finally
		{
			fLock.readLock().unlock();
		}
	}

	@Override
	public int getNumberSubsets(ICacheEntry<E> set) {
		fLock.readLock().lock();
		try
		{
			return fCache.getNumberSubsets(set);
		}
		finally
		{
			fLock.readLock().unlock();
		}
	}

	@Override
	public Iterable<C> getSupersets(ICacheEntry<E> set) {
		fLock.readLock().lock();
		try
		{
			return fCache.getSupersets(set);
		}
		finally
		{
			fLock.readLock().unlock();
		}
	}

	@Override
	public int getNumberSupersets(ICacheEntry<E> set) {
		fLock.readLock().lock();
		try
		{
			return fCache.getNumberSupersets(set);
		}
		finally
		{
			fLock.readLock().unlock();
		}
	}

	@Override
	public int size() {
		fLock.readLock().lock();
		try
		{
			return fCache.size();
		}
		finally
		{
			fLock.readLock().unlock();
		}
	}

}
