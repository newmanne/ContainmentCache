package containmentcache.ubt;

import java.util.Set;

import containmentcache.AContainmentCacheTests;

public class UBTreeTests extends AContainmentCacheTests{

	@Override
	protected UBTree<Integer,Object> getCache(Set<Integer> universe) {
		return new UBTree<Integer,Object>();
	}

}
