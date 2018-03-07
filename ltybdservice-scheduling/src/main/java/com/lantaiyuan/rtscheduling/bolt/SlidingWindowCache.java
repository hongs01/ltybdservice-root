package com.lantaiyuan.rtscheduling.bolt;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SlidingWindowCache <T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private Map<Integer, List<T>> tupMap = new HashMap<Integer, List<T>>();

	private int headSlot;

	private int tailSlot;

	private int slotNum;

	public SlidingWindowCache(int slotNum) {
	/*	if (slotNum <2) {
			//throw new IllegalArgumentException("Window length in slots must be at least two (you requested " + slotNum + ")");
		}else{*/

		this.slotNum = slotNum;

		for (int i = 0; i < slotNum; i++) {
			tupMap.put(i, null);
		}
		headSlot = 0;
		tailSlot = (headSlot + 1) % this.slotNum;
		//}
	}

	public void add(T t) {
		List<T> objs = tupMap.get(headSlot);
		if (objs == null) {
			objs = new ArrayList<T>();
		}
		objs.add(t);
		tupMap.put(headSlot, objs);
	}

	/**
	 * 获取窗口内的消息，并向前移动窗口
	 * 
	 * @return
	 */
	public List<T> getAndAdvanceWindow() {
		int i = headSlot;

		List<T> windowedTuples = new ArrayList<T>();
		if (tupMap.get(i) != null) {
			windowedTuples.addAll(tupMap.get(i));
		}

		while ((i = slotAfter(i)) != headSlot) {
			if (tupMap.get(i) != null) {
				windowedTuples.addAll(tupMap.get(i));
			}
		}

		advanceHead();
		return windowedTuples;
	}

	/**
	 * 向前移动窗口
	 */
	private void advanceHead() {
//		printList(tupMap.get(headSlot));
		headSlot = tailSlot;
		wipeSlot(headSlot);
		tailSlot = slotAfter(tailSlot);
	}

	public int slotAfter(int slot) {
		return (slot + 1) % slotNum;
	}

	public void wipeSlot(int slot) {
		tupMap.put(slot, null);
	}
}