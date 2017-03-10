package com.baojie.worker.looptable;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicIntegerFieldUpdaterDemo {
	class DemoData{
	       public volatile int value1 = 1;
	       volatile int value2 = 2;
	       protected volatile int value3 = 3;
	       private volatile int value4 = 4;
		public int getValue1() {
			return value1;
		}
		public void setValue1(int value1) {
			this.value1 = value1;
		}
		public int getValue2() {
			return value2;
		}
		public void setValue2(int value2) {
			this.value2 = value2;
		}
		public int getValue3() {
			return value3;
		}
		public void setValue3(int value3) {
			this.value3 = value3;
		}
		public int getValue4() {
			return value4;
		}
		public void setValue4(int value4) {
			this.value4 = value4;
		}
	       
	   }
	    AtomicIntegerFieldUpdater<DemoData> getUpdater(String fieldName) {
	        return AtomicIntegerFieldUpdater.newUpdater(DemoData.class, fieldName);
	    }
	    void doit() {
	        DemoData data = new DemoData();
	        System.out.println("1 ==> "+getUpdater("value1").getAndSet(data, 10));
	        System.out.println(data.getValue1());
	        System.out.println("3 ==> "+getUpdater("value2").incrementAndGet(data));
	        System.out.println("2 ==> "+getUpdater("value3").decrementAndGet(data));
	        System.out.println("true ==> "+getUpdater("value4").compareAndSet(data, 4, 5));
	    }
	    public static void main(String[] args) {
	        AtomicIntegerFieldUpdaterDemo demo = new AtomicIntegerFieldUpdaterDemo();
	        demo.doit();
	    }
}
