/**
 * 
 */

import java.io.Serializable;

/**
 * @author Arthur Hsu
 * Created: Sep 24, 2012
 *
 */
public class Interval implements Comparable<Interval>, Serializable {
	static final long serialVersionUID = 61393452847L; 
	
	public static enum INTERVAL_TYPE {CLOSED_INTERVAL, OPEN_INTERVAL, RIGHT_OPEN_INTERVAL};
	
	public int start, end;
	public static INTERVAL_TYPE type;
	public static boolean showBracket=false;
	
	public Interval() {
		start = -1;
		end = -1;
		type = INTERVAL_TYPE.CLOSED_INTERVAL;
	}
	
	public Interval(int start, int end) {
		this(start, end, INTERVAL_TYPE.CLOSED_INTERVAL);
	}
	
	public Interval(int start, int end, INTERVAL_TYPE type) {
		this.start = Math.min(start, end);
		this.end = Math.max(start, end);
		Interval.type = type;
	}
	
	public int compareTo(Interval other) {		
		switch (type) {
		case CLOSED_INTERVAL:
			if (this.start < other.start) return -1;
			else if (this.start > other.start) return 1;
			else {
	        	if (this.end<other.end) return -1;
	        	else if (this.end==other.end) return 0;
	        	else return 1;
	        }
		case OPEN_INTERVAL:
			return 0;
		case RIGHT_OPEN_INTERVAL:
			return 0;
		default:
			System.err.println("undefined interval type");
			System.exit(1);
			return 0;
		}
	}
	
	public boolean intersects(Interval other) {
		return this.intersects(other, 0);
	}
	
	public boolean contains(Interval other) {
		return this.contains(other, 0);
	}
	
	public boolean adjoins(Interval other) {
		return this.adjoins(other, 0);
	}
	
	public boolean intersects(Interval other, int flank) {
		switch (Interval.type) {
		case CLOSED_INTERVAL:
			return intersectsClosedInterval(this.start-flank, this.end+flank, other.start, other.end);
		case OPEN_INTERVAL:
			return intersectsOpenInterval(this.start-flank, this.end+flank, other.start, other.end);
		case RIGHT_OPEN_INTERVAL:
			return intersectsRightOpenInterval(this.start-flank, this.end+flank, other.start, other.end);
		default:
			System.err.println("undefined interval type");
			System.exit(1);
			return false;
		}
	}
	
	public boolean contains(Interval other, int flank) {
		return containsInterval(this.start-flank, this.end+flank, other.start, other.end);
	}
	
	public boolean adjoins(Interval other, int flank) {
		switch (Interval.type) {
		case CLOSED_INTERVAL:
			return adjoinsClosedInterval(this.start-flank, this.end+flank, other.start, other.end);
		case OPEN_INTERVAL:
			return adjoinsOpenInterval(this.start-flank, this.end+flank, other.start, other.end);
		case RIGHT_OPEN_INTERVAL:
			return adjoinsRightOpenInterval(this.start-flank, this.end+flank, other.start, other.end);
		default:
			System.err.println("undefined interval type");
			System.exit(1);
			return false;
		}
	}
	
	@Override
	public String toString() {
		if (showBracket) {
			switch (Interval.type) {
			case CLOSED_INTERVAL:
				return "[" + this.start + "-" + this.end + "]";
			case OPEN_INTERVAL:
				return "(" + this.start + "-" + this.end + ")";
			case RIGHT_OPEN_INTERVAL:
				return "[" + this.start + "-" + this.end + ")";
			default:
				System.err.println("undefined interval type");
				System.exit(1);
				return null;
			}
		} else return this.start + "-" + this.end;
	}
	
	
	
	public static boolean intersectsClosedInterval(int s1, int e1, int s2, int e2) {
		if (s2 <= e1 && e2 >= s1) return true;
		else return false;
	}
	
	public static boolean adjoinsClosedInterval(int s1, int e1, int s2, int e2) {
		if (intersectsClosedInterval(s1,e1,s2,e2)) return false;
		if (Math.abs(s2-e1)==1 || Math.abs(s1-e2)==1) return true;
		else return false;
	}
	
	public static boolean intersectsOpenInterval(int s1, int e1, int s2, int e2) {
		if (s2 < e1 && e2 > s1) return true;
		else return false;
	}
	
	public static boolean adjoinsOpenInterval(int s1, int e1, int s2, int e2) {
		if (intersectsOpenInterval(s1,e1,s2,e2)) return false;
		if (s1<s2 && s2==e1-1) return true;
		else if (s1>s2 && s1==e2-1) return true;
		else return false;
	}
	
	public static boolean intersectsRightOpenInterval(int s1, int e1, int s2, int e2) {
		if (s2 < e1-1 && e2-1 >= s1) return true;
		else return false;
	}
	
	public static boolean adjoinsRightOpenInterval(int s1, int e1, int s2, int e2) {
		if (intersectsRightOpenInterval(s1,e1,s2,e2)) return false;
		if (s1<s2 && s2==e1) return true;
		else if (s1>s2 && s1==e2) return true;
		else return false;
	}
	
	public static boolean containsInterval(int s1, int e1, int s2, int e2) {
		if (s1<=s2 && e2 <= e1) return true;
		else return false;
	}
}
