package org.hypknowsys.util;

import java.awt.Point;

public class Test {
	public static void main(String args[]) {
		System.out.println(new Point() {
			{
				x = -1;
				y = -1;
			}
		}.getLocation());

		System.out.println(new Point(-1, 0) {
			{
				y = -1;
			}
		}.getLocation());
	}
}
