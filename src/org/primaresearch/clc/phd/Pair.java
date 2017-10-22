/*
 * @PRImA_Header@
 */
package org.primaresearch.clc.phd;



/**
 * Simple pair of objects
 * @author clc
 *
 * @param <Left> Object
 * @param <Right> Object
 */
public class Pair<Left extends Object, Right extends Object> {
	public Left left;
	public Right right;

	public Pair() {
	}
	
	public Pair(Left left, Right right) {
		this.left = left;
		this.right = right;
	}
	
}
